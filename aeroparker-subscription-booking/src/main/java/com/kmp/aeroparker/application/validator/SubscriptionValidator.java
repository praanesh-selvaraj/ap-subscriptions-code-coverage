package com.kmp.aeroparker.application.validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.application.web.SubscriptionControllerService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionDiscountedRenewal;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SubscriptionValidator
{

	private final SubscriptionControllerService service;
	private final SubscriptionConfigBean requestBean;
	private final SubscriptionService subService;

	public SubscriptionBookingDetails fetchBookingDetails(String email, String customerGuid,
			Map<String, Object> response)
	{
		SubscriptionBookingDetails bookingDetails = service.fetchSubscriptionBookingDetailsByEmail(email);
		if (bookingDetails == null)
		{
			log.info("No Booking Details for customer with email {} and guid", email, customerGuid);
		}
		return bookingDetails;
	}

	public boolean validateBookingDetails(SubscriptionBookingDetails bookingDetails, String customerGuid,
			Map<String, Object> response)
	{
		Integer productId = bookingDetails.getSubscriptionProductId();
		LocalDate startDate = Optional.ofNullable(bookingDetails.getStartDate())
				.map(date -> date.toLocalDate())
				.orElse(null);

		if (productId == null || startDate == null)
		{
			response.put("error", "Product ID or Start Date missing from subscription details.");
			log.info("Product ID or Start Date was missing from subscription details for guid {}", customerGuid);
			return false;
		}
		return true;
	}

	public boolean validateRenewalConfiguration(Integer productId, SubscriptionDiscountedRenewal renewalConfig,
			Map<String, Object> response)
	{
		if (renewalConfig == null || !renewalConfig.getDiscountedRenewalEnabled())
		{
			log.info("No renewal configuration found or renewal discount is disabled for product Id {}", productId);
			return false;
		}
		if (renewalConfig.getDontApplyToNewBookings())
		{
			log.info("Renewal config with id : {} not supported for new bookings.", renewalConfig.getId());
			return false;
		}
		return true;
	}

	public boolean checkRenewalWindow(LocalDate startDate, SubscriptionMinimumTerm minimumTerm,
			SubscriptionDiscountedRenewal renewalConfig, Map<String, Object> response)
	{
		LocalDate currentDate = LocalDate.now(ZoneId.of(requestBean.getTimeZone()));
		LocalDate expiryDate = startDate.plusMonths(minimumTerm.getIntValue());
		LocalDate renewalStartDate = expiryDate.minusDays(renewalConfig.getRenewalDays());
		LocalDate renewalEndDate = expiryDate.plusDays(renewalConfig.getRenewalDays());

		if (currentDate.isBefore(renewalStartDate))
		{
			response.put("error", "You already have an active subscription that is not yet due for renewal.");
			log.info("Already an active subscription that is not yet due for renewal");
			return false;
		}
		else if (currentDate.isAfter(renewalEndDate))
		{
			response.put("message", "Your subscription has expired, but no renewal discount can be applied.");
			log.info("Subscription has expired, no renewal discount can be applied");
			return false;
		}
		return true;
	}

	public void prepareResponse(Basket basket, SubscriptionPurchaseRequest purchaseRequest,
			Map<String, Object> response, SubscriptionBookingData bookingData)
	{
		BigDecimal updatedGrandTotal = basket.getGrandTotal();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(requestBean.getDateFormat());
		bookingData.setAmount(updatedGrandTotal);

		response.put("newStartDate", purchaseRequest.getStartDate()
				.format(dateFormatter));
		response.put("newEndDate", purchaseRequest.getEndDate()
				.format(dateFormatter));
		response.put("grandTotal", updatedGrandTotal.toString());
		response.put("priceIncludePennyPlaceholdersHtml", basket.getPriceIncludePennyPlaceholdersHtml());
	}

	/**
	 * Check if the renewal is within the configured renewal period and returns true if it is, if there is no renewal
	 * period configured the check is skipped
	 * 
	 * @param reference
	 * @param originalEndDateString
	 * @return a boolean denoting whether the renewal is within the renewal period
	 */
	public boolean validateRenewalPeriod(String reference, String originalEndDateString)
	{
		log.info("Validating if the renewal for {} is within the configured renewal period", reference);
		boolean isValidPeriod = true;
		SubscriptionBookingDetails bookingDetails = subService.fetchSubscriptionBookingDetailsByRef(reference);
		if (bookingDetails != null)
		{
			isValidPeriod = processRenewalPeriod(isValidPeriod, bookingDetails, originalEndDateString);
		}
		return isValidPeriod;
	}

	private boolean processRenewalPeriod(boolean isValidPeriod, SubscriptionBookingDetails bookingDetails,
			String originalEndDateString)
	{
		SubscriptionProductTerms productTerms =
				subService.fetchSubscriptionProductTermsBySubProductId(bookingDetails.getSubscriptionProductId());
		if (productTerms != null && productTerms.getRenewalEligibilityPeriod() != null
				&& !StringUtil.isNullOrEmpty(originalEndDateString))
		{
			try
			{
				Integer productRenewalPeriod = productTerms.getRenewalEligibilityPeriod();
				LocalDate originalEndDate = LocalDate.parse(originalEndDateString, DateTimeFormatter.ISO_DATE);
				LocalDate currentDate = LocalDate.now();
				isValidPeriod = !currentDate.isBefore(originalEndDate.minusDays(productRenewalPeriod))
						&& !currentDate.isAfter(originalEndDate.plusDays(productRenewalPeriod));
			}
			catch (DateTimeParseException e)
			{
				// If somehow the date is in an invalid format, we return false here as there was an availability
				// period configured so we shouldn't skip it
				log.error("Booking end date could not be parsed: {}", e.getMessage(), e);
				isValidPeriod = false;
			}
		}
		return isValidPeriod;
	}
}