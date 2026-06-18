package com.kmp.aeroparker.application.web;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.availability.PriceDetails;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseAgreement;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.application.validator.SubscriptionValidator;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionDiscountedRenewal;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionRenewalService
{
	private final SubscriptionControllerService service;
	private final SubscriptionConfigBean requestBean;
	private final Localise localiseService;
	private final SubscriptionValidator validator;

	private static final String PERCENTAGE_DISCOUNT_TYPE = "percentage";

	public Map<String, Object> validateSubscription(SubscriptionBookingData bookingData,
			String affCode)
	{
		Map<String, Object> response = new HashMap<>();

		SubscriptionBookingDetails bookingDetails =
				validator.fetchBookingDetails(bookingData.getEmail(), bookingData.getCustomerGuid(), response);
		if (bookingDetails == null)
		{
			return response;
		}

		if (!validator.validateBookingDetails(bookingDetails, bookingData.getCustomerGuid(), response))
		{
			return response;
		}

		SubscriptionDiscountedRenewal renewalConfig =
				service.fetchSubscriptionDiscountedRenewalProductId(bookingDetails.getSubscriptionProductId());
		if (!validator.validateRenewalConfiguration(bookingDetails.getSubscriptionProductId(), renewalConfig, response))
		{
			return response;
		}

		Basket basket = fetchAndApplyDiscount(bookingData.getCustomerGuid(), bookingData.getEmail(), bookingDetails);
		SubscriptionPurchaseRequest purchaseRequest = basket.getPurchaseRequestList()
				.get(0);

		if (!validator.checkRenewalWindow(bookingDetails.getStartDate()
				.toLocalDate(), purchaseRequest.getMinimumTerm(), renewalConfig, response))
		{
			return response;
		}

		return prepareSubscriptionRenewalResponse(basket, purchaseRequest, bookingData);
	}

	private Map<String, Object> prepareSubscriptionRenewalResponse(Basket basket,
			SubscriptionPurchaseRequest purchaseRequest, SubscriptionBookingData bookingData)
	{
		Map<String, Object> response = new HashMap<>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(requestBean.getDateFormat());

		BigDecimal updatedGrandTotal = basket.getGrandTotal();
		bookingData.setAmount(updatedGrandTotal);

		response.put("newStartDate", purchaseRequest.getStartDate()
				.format(dateFormatter));
		response.put("newEndDate", purchaseRequest.getEndDate()
				.format(dateFormatter));
		response.put("grandTotal", updatedGrandTotal.toString());
		response.put("priceIncludePennyPlaceholdersHtml", basket.getPriceIncludePennyPlaceholdersHtml());

		return response;
	}

	private void processRenewal(Basket basket)
	{
		BigDecimal total = BigDecimal.ZERO;

		for (SubscriptionPurchaseRequest purchaseRequest : basket.getPurchaseRequestList())
		{
			SubscriptionPurchaseAgreement agreement = purchaseRequest.getPurchaseAgreement();
			if (agreement == null)
			{
				log.info("Purchase agreement is null for productId: {}", purchaseRequest.getProductId());
				continue;
			}

			PriceDetails priceDetails = agreement.getPriceDetails();
			if (priceDetails == null)
			{
				log.info("Price details are null for productId: {}", purchaseRequest.getProductId());
				continue;
			}

			SubscriptionDiscountedRenewal renewalConfig =
					service.fetchSubscriptionDiscountedRenewalProductId(purchaseRequest.getProductId());
			if (renewalConfig == null || !renewalConfig.getDiscountedRenewalEnabled())
			{
				log.info("No renewal config or discount not enabled for productId: {}", purchaseRequest.getProductId());
				continue;
			}

			BigDecimal originalPrice = purchaseRequest.getOriginalGrandTotal();
			BigDecimal discountedAmount = getDiscountAmount(originalPrice, renewalConfig);

			priceDetails.setDiscountedAmount(discountedAmount.setScale(2, RoundingMode.HALF_UP));
			agreement.setPriceDetails(priceDetails);

			total = total.add(discountedAmount);

			LocalDate expiryDate = purchaseRequest.getStartDate()
					.plusMonths(purchaseRequest.getMinimumTerm()
							.getIntValue());
			LocalDate renewalStartDate = expiryDate.minusDays(renewalConfig.getRenewalDays());
			LocalDate renewalEndDate = expiryDate.plusDays(renewalConfig.getRenewalDays());

			log.info("ProductId: {} - Renewal Start Date: {}, Renewal End Date: {}", purchaseRequest.getProductId(),
					renewalStartDate, renewalEndDate);
		}

		basket.initialize(localiseService);
	}

	private BigDecimal getDiscountAmount(BigDecimal originalPrice, SubscriptionDiscountedRenewal renewalConfig)
	{
		BigDecimal discountAmount = BigDecimal.ZERO;
		if (originalPrice == null || renewalConfig == null || renewalConfig.getDiscountAmount() == null)
		{
			log.error("Cannot apply discount. Original price or discount configuration is null.");
			return discountAmount;
		}

		if (PERCENTAGE_DISCOUNT_TYPE.equals(renewalConfig.getDiscountType()))
		{
			discountAmount = originalPrice.multiply(renewalConfig.getDiscountAmount())
					.divide(BigDecimal.valueOf(100));
		}
		else
		{
			discountAmount = renewalConfig.getDiscountAmount();
		}
		return discountAmount;
	}

	public Basket fetchAndApplyDiscount(String customerGuid, String email, SubscriptionBookingDetails bookingDetails)
	{
		Basket basket =
				service.getBasket(customerGuid, requestBean.getCurrentLanguageId(), requestBean.getDefaultLanguageId(),
						requestBean.getAffiliate(), requestBean.getAffiliateSubscriptionSettings());
		if (basket == null)
		{
			log.error("Basket is null for customerGuid: {}", customerGuid);
		}

		processRenewal(basket);

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(requestBean.getDateFormat());
		LocalDate currentDate = LocalDate.now(ZoneId.of(requestBean.getTimeZone()));

		for (SubscriptionPurchaseRequest request : basket.getPurchaseRequestList())
		{
			SubscriptionMinimumTerm minimumTerm = request.getMinimumTerm();
			LocalDate expiryDate = bookingDetails.getStartDate()
					.toLocalDate()
					.plusMonths(minimumTerm.getIntValue());

			LocalDate newStartDate;
			if (expiryDate.isBefore(currentDate))
			{
				newStartDate = currentDate;
			}
			else
			{
				newStartDate = expiryDate.plusDays(1);
			}

			LocalDate newEndDate = newStartDate.plusMonths(minimumTerm.getIntValue());

			request.setStartDate(newStartDate);
			request.setEndDate(newEndDate);
			request.setLocalisedStartDate(newStartDate.format(dateFormatter));
			request.setLocalisedEndDate(newEndDate.format(dateFormatter));
		}
		return basket;
	}
}