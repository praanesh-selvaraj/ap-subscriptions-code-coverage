package com.kmp.aeroparker.application.web;

import com.kmp.aeroparker.application.builder.PromotionBuilder;
import com.kmp.aeroparker.application.db.service.PromotionService;
import com.kmp.aeroparker.application.external.api.SubscriptionBookingRequestHandler;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.external.api.datatypes.Promotion;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionPromotionResponse;
import com.kmp.aeroparker.application.model.promotion.PromotionRequest;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PromotionsPromoCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class SubscriptionPromotionController
{
	private final SubscriptionConfigBean requestBean;
	private final SubscriptionBookingRequestHandler requestHandler;
	private final PromotionService promotionService;
	private final PromotionBuilder promotionBuilder;
	private final SubscriptionRenewalService renewalService;
	private final Localise localise;
	private final LanguageFieldsList languageFieldsList;
	private final SubscriptionControllerService service;

	@PostMapping(value = "/validate-promotion", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> validatePromotion(@RequestBody PromotionRequest request,
			@RequestParam(name = "customerGuid", required = false) String guid)
	{
		log.info("Validating promotion code");
		Map<String, Object> response = new HashMap<>();
		boolean isValid = false;
		String errorMessage = null;

		int subscriptionProductId = request.getSubscriptionProductId();
		String promoCode = request.getPromoCode();
		String email = request.getEmail();

		SubscriptionBookingData bookingData = new SubscriptionBookingData();
		bookingData.setEmail(email);
		bookingData.setCustomerGuid(guid);
		try
		{
			// Validate required parameters
			if (subscriptionProductId < 1 || StringUtil.isNullOrEmpty(promoCode))
			{
				log.error("Missing required parameters: subscriptionProductId or promoCode");
				errorMessage = languageFieldsList.getTranslation("Invalid request parameters");
			}
			else if (StringUtil.isNullOrEmpty(guid))
			{
				log.error("No GUID found in session");
				errorMessage = languageFieldsList.getTranslation("Session expired, please refresh");
			}
			else
			{
				// Check if renewal discount is already applied
				Map<String, Object> discountResponse = renewalService.validateSubscription(bookingData, "");
				if (discountResponse.containsKey("newStartDate"))
				{
					log.info("Promotion won't be applied since a renewal discount has already been applied.");
					errorMessage = languageFieldsList.getTranslation("Unable to validate promo code");
				}
				else
				{
					// Validate promo code with external API
					SubscriptionPromotionResponse promotionResponse = requestHandler.validatePromotion(
							requestBean.getAffiliateConfig(), subscriptionProductId, promoCode, email);

					if (promotionResponse != null && promotionResponse.getPromotion() != null)
					{
						Promotion promotion = promotionResponse.getPromotion();
						isValid = promotion.getValid();

						if (isValid)
						{
							BigDecimal discountedPrice = promotion.getDiscountedPrice();
							response.put("promoCode", promotion.getPromoCode());
							response.put("discount", promotion.getDiscount());
							response.put("discountedPrice", discountedPrice);
							response.put("discountFormatted", localise.price(promotion.getDiscount()
									.floatValue()));
							response.put("discountedPriceFormatted",
									localise.priceIncludePennyPlaceholders(discountedPrice.floatValue(), true)
											.replace("{pennies}",
													"<span class='booking-summary__item__val--total__pence'>")
											.replace("{/pennies}", "</span>"));
							response.put("noPaymentRequired", discountedPrice.compareTo(BigDecimal.ZERO) == 0);

							// Add success message with promo code
							String successMessage = languageFieldsList.getTranslation("Promo code") + " \""
									+ promotion.getPromoCode() + "\" " + languageFieldsList.getTranslation("applied");
							response.put("successMessage", successMessage);

							// Add updated Right to Cancel text with discounted price
							String rightToCancelText = getRightToCancelText(discountedPrice);
							if (rightToCancelText != null)
							{
								response.put("rightToCancelText", rightToCancelText);
							}

							log.info("Promo code {} validated successfully for GUID {}", promotion.getPromoCode(),
									guid);
							PromotionsPromoCodes promotionPromoCode =
									promotionService.fetchPromoByPromocodeAndSiteId(promoCode, requestBean.getSiteId());
							SubscriptionSessionPromotion sessionPromotion =
									promotionBuilder.buildSessionPromotion(guid, promotionResponse, promotionPromoCode);
							promotionService.saveSessionPromotion(sessionPromotion);
							log.debug("Saved session promotion for GUID {}", guid);
						}
						else
						{
							errorMessage = languageFieldsList.getTranslation("Invalid or expired promo code");
							log.info("Promo code {} is invalid or expired", promoCode);
						}
					}
					else
					{
						log.error("Failed to get promotion response from external API");
						errorMessage = languageFieldsList.getTranslation("Unable to validate promo code at this time");
					}
				}
			}
		}
		catch (Exception e)
		{
			log.error("Error validating promotion code", e);
			errorMessage = languageFieldsList.getTranslation("An error occurred while validating the promo code");
		}

		// Build final response
		response.put("valid", isValid);
		if (errorMessage != null)
		{
			response.put("errorMessage", errorMessage);
		}
		if (!isValid)
		{
			// Save the promo when invalid to avoid unwanted redemptions
			promotionService.saveSessionPromotion(promotionBuilder.buildInvalidSessionPromotion(promoCode, guid));
		}
		return ResponseEntity.ok(response);
	}

	private String getRightToCancelText(BigDecimal discountedPrice)
	{
		return service.formatRightToCancelText(requestBean.getAffiliateSubscription(), discountedPrice);
	}
}
