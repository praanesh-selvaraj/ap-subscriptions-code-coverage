package com.kmp.aeroparker.application.web;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.builder.ConfigBuilder;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.model.AnalyticsLocations;
import com.kmp.aeroparker.application.model.DetailsConfig;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.payment.handler.PaymentHandler;
import com.kmp.aeroparker.application.processor.SubscriptionAnalyticsProcessor;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesContent;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesDisplay;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(value = "/subscriptions/{affCode}")
public class DetailsController extends AbstractController
{
	private static final String IRELAND = "Ireland";
	private static final String UNITED_STATES = "united states";
	private static final String HEADER_DETAILS_STEP = "Your details";
	private static final String DISABLED = "DISABLED";
	private final SubscriptionConfigBean requestBean;
	private final SubscriptionControllerService service;
	private final ConfigBuilder configBuilder;
	private final PaymentHandler paymentHandler;
	private final LanguageFieldsList languageFieldsList;
	private final Localise localise;
	private final SubscriptionAnalyticsProcessor analyticsProcessor;

	@GetMapping(value = "/details")
	public String details(final Model model, @RequestParam(value = "customerGuid") final String customerGuid,
			@RequestParam(value = "startDate") final String startDate, @ModelAttribute("bookingData") final SubscriptionBookingData bookingData,
			@RequestParam(value = "paymentError", required = false) final String error)
	{
		// on refresh check if empty then load the purchase data
		int affId = requestBean.getAffiliateId();
		int currentLanguageId = requestBean.getCurrentLanguageId();
		int defaultLanguageId = requestBean.getDefaultLanguageId();
		model.addAttribute("customerGuid", customerGuid);
		model.addAttribute("startDate", startDate);
		// convert the json array string to a pojo
		// build the basket
		// fetch the purchase data using the customer guid each time. this
		// should avoid the issue with the refresh
		Basket basket = service.getBasket(customerGuid, currentLanguageId, defaultLanguageId, requestBean.getAffiliate(),
				requestBean.getAffiliateSubscriptionSettings());
		if (basket != null)
		{
			// Initialize basket to ensure priceIncludePennyPlaceholdersHtml reflects current (discounted) grand total
			basket.initialize(localise);
			// get the payment step filed and the mandatory fields, set them in
			// the model
			DetailsConfig config = configBuilder.build(requestBean.getSiteId(), affId, requestBean.getAffiliateConfig(), basket.getAllProductIds().get(0));
			model.addAttribute("currentStage", 3);
			model.addAttribute("config", config);
			model.addAttribute("isMultiPurchase", requestBean.isMultiPurchase());
			model.addAttribute("basket", basket);
			Map<String, Object> paymentAttributes = paymentGatewaySetup(basket.getGrandTotal());

			model.addAllAttributes(paymentAttributes);
			model.addAttribute("subscriptionProductId",basket.getAllProductIds().get(0));

			// Pass promo code to pre-populate input if promo is applied
			if (basket.getPromoDiscount() != null && basket.getPromoDiscount().compareTo(BigDecimal.ZERO) > 0)
			{
				model.addAttribute("appliedPromoCode", basket.getPromoCode());
			}

			model.addAttribute("bookingData", bookingData == null ? new SubscriptionBookingData() : bookingData);
			model.addAttribute("header", HEADER_DETAILS_STEP);
			Locations location = requestBean.getLocation();
			String locationName = location.getName();
			model.addAttribute("showCountyDropDown", StringUtil.isEqualAtLeastOne(locationName, UNITED_STATES, IRELAND));
			model.addAttribute("locationName", locationName);
			model.addAttribute("counties", service.getCountiesIfRequired(config.showCounties(), requestBean.getSite()
					.getTitle()));
			model.addAttribute("countries", service.getCountriesMap(requestBean.getSiteTitle(), languageFieldsList, true));
			model.addAttribute("enableAccountOnDetailsStep", requestBean.enableAccountOnDetailsStep());
			model.addAttribute("step4CreateAccountTitle", requestBean.getAffiliateContent().getCreateAccountTitle());
			model.addAttribute("step4CreateAccountContent", requestBean.getAffiliateContent().getCreateAccountContent());
			int affiliateId = requestBean.getAffiliateId();
			model.addAttribute("affiliateId", affiliateId);
			if (!StringUtil.isEmpty(error))
			{
				model.addAttribute("paymentError", error);
			}
			if (config.getPaymentStepFields().contains(47))
			{
				model.addAttribute("useViesVatValidation", true);
			}
			boolean skipProductSelection = service.shouldSkipProductSelection(requestBean.getAffiliateSubscriptionSettings());
			model.addAttribute("skipProductSelection", skipProductSelection);
			setTermsAndCondition(model);
			setRightToCancel(model, basket.getGrandTotal());
			setVehicleLookupDetails(model);
			analyticsProcessor.processSubscriptionAnalytics(model, basket, requestBean.getCurrency(), 
					AnalyticsLocations.SUBSCRIPTION_PAYMENT_DETAIL.getId(), affiliateId);
			log.debug("Displaying details step");
			return "subscription-details";
		}
		return "redirect:dates";
	}

	private void setTermsAndCondition(final Model model)
	{
		boolean showTermsAndConditions = requestBean.getAffiliateConfig()
				.getConfigValue_Boolean(AffiliateConfigKeys.DISPLAY_TCS_SECTION);
		model.addAttribute("showTermsAndConditions", showTermsAndConditions);
		boolean useTermsUrl = false;

		AffiliatesContent affiliatesContent = requestBean.getAffiliateContent();
		String termsAndConditions = "";
		if (affiliatesContent != null)
		{
			if (showTermsAndConditions)
			{
				termsAndConditions = StringEscapeUtils.unescapeHtml4(affiliatesContent.getTermsAndConditions());
				if (StringUtil.isEmpty(termsAndConditions))
				{
					AffiliatesDisplay affiliatesDisplay = requestBean.getAffiliatesDisplay();
					termsAndConditions = affiliatesDisplay == null ? "" : affiliatesDisplay.getTermsUrl();
					useTermsUrl = true;
				}
				model.addAttribute("useTermsUrl", useTermsUrl);
				model.addAttribute("termsAndConditions", termsAndConditions);
			}
			model.addAttribute("personalData", StringEscapeUtils.unescapeHtml4(affiliatesContent.getPersonalData()));
		}
	}

	private void setRightToCancel(final Model model, final BigDecimal amount)
	{
		String rightToCancel = service.formatRightToCancelText(requestBean.getAffiliateSubscription(), amount);
		if (rightToCancel != null)
		{
			model.addAttribute("rightToCancel", rightToCancel);
		}
	}

	private void setVehicleLookupDetails(final Model model)
	{
		VehiclelookupAffiliateLogins vehicleLookupLogin = service.getVehicleLookupLogin(requestBean.getAffiliateId());

		boolean isShowVehicleLookup = requestBean.getAffiliateConfig()
				.getConfigValue_Boolean(AffiliateConfigKeys.SHOWVEHICLELOOKUP);
		model.addAttribute("lookupMode", vehicleLookupLogin != null ? vehicleLookupLogin.getLookupMode() : DISABLED);
		model.addAttribute("shouldShowVehicleLookup", isShowVehicleLookup && vehicleLookupLogin != null);
		model.addAttribute("unrecognisedPlateValidation",
				vehicleLookupLogin != null ? vehicleLookupLogin.getUnrecPlateValidation() : false);
	}

	@PostMapping(value = "/details")
	public String redirectToBooking(final RedirectAttributes redirectAttributes, @ModelAttribute final SubscriptionBookingData bookingData,
			@RequestParam(value = "customerGuid") final String customerGuid, @RequestParam(value = "startDate") final String startDate)
	{
		// Validate Right to Cancel checkbox if configured
		AffiliateSubscription affSubscription = requestBean.getAffiliateSubscription();
		if (affSubscription != null && !StringUtil.isEmpty(affSubscription.getRightToCancel()))
		{
			if (!bookingData.isRightToCancel())
			{
				log.warn("Right to Cancel not accepted");
				redirectAttributes.addFlashAttribute("bookingData", bookingData);
				redirectAttributes.addAttribute("customerGuid", customerGuid);
				redirectAttributes.addAttribute("startDate", startDate);
				redirectAttributes.addAttribute("paymentError", languageFieldsList.getTranslation("You must accept the Right to Cancel Agreement to continue."));
				return "redirect:details";
			}
		}

		// booking data holds all user data, all data on payment step, super
		// cool spring maps everything into the pojo
		bookingData.setCurrentLanguageId(requestBean.getCurrentLanguageId());
		redirectAttributes.addFlashAttribute("bookingData", bookingData);
		// direct to booking, to process the actual booking
		log.debug("Redirecting to booking");
		return "redirect:booking";
	}

	private Map<String, Object> paymentGatewaySetup(final BigDecimal totalPrice)
	{
		Map<String, Object> params = new HashMap<>();
		if (totalPrice.compareTo(BigDecimal.ZERO) > 0)
		{
			log.debug("Building params for payment gateway");
			PaymentGatewayParameters paymentHandlerParams = PaymentGatewayParameters.builder()
					.withAffiliate(requestBean.getAffiliate())
					.withSite(requestBean.getSite())
					.withLanguage(requestBean.getCurrentLanguage())
					.withCurrency(requestBean.getCurrency())
					.withAmount(totalPrice)
					.build();
			params = paymentHandler.setUpTransaction(paymentHandlerParams);
		}
		else
		{
			params.put("noPaymentRequired", true);
		}

		return params;
	}
}