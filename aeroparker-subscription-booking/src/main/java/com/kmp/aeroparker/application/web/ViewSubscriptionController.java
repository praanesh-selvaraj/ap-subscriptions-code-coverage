package com.kmp.aeroparker.application.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.CarParkService;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.payment.handler.PaymentHistoryHandler;
import com.kmp.aeroparker.application.utils.SubscriptionDetailsUtil;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionPaymentHistory;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class ViewSubscriptionController extends AbstractController
{
	private static final String HEADER_SUBSCRIPTION_SEARCH = "Subscription Search";
	private static final String HEADER_SUBSCRIPTION_DETAILS = "Subscription Details";
	private final SubscriptionConfigBean requestBean;
	private final BookingService bookingService;
	private final ContactService contactService;
	private final SubscriptionDetailsUtil subscriptionUtils;
	private final PaymentHistoryHandler paymentHistoryHandler;
	private final CarParkService carParkService;
	private final SubscriptionControllerService controllerService;
	private final SubscriptionService subscriptionService;
	private final AffiliateService affiliateService;

	@GetMapping(value = "/view-booking")
	public String viewSubscription(final Model model,
			@RequestParam(value = "reference", required = false) final String reference,
			@RequestParam(value = "email", required = false) final String email,
			@RequestParam(value = "submitted", defaultValue = "0") final boolean submitted,
			@RequestParam(value = "errorMessage", required = false) final String errorMessage,
			final HttpSession session)
	{
		if (!StringUtil.isEmpty(reference) && !StringUtil.isEmpty(email))
		{
			// Adding the reference to the session
			session.setAttribute("reference", reference);
			
			// Temp fix to remove dupes from SubscriptionAllBookingData. Payment custom values
			// are causing dupes will need to be fixed in another ticket
			// Fetch will need to be reverted to use fetchAllSubscriptionBookingDataByReferenceAndEmail() when fixed
			List<SubscriptionAllBookingData> bookingDataList =
					bookingService.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(reference, email);
			if (!bookingDataList.isEmpty())
			{
				Map<String, SubscriptionPaymentHistory> paymentHistoryMap = getPaymentHistory(bookingDataList);
				SubscriptionAllBookingData firstBookingData = bookingDataList.get(0);

				model.addAttribute("header", HEADER_SUBSCRIPTION_DETAILS);
				model.addAttribute("bookingDataList", bookingDataList);
				model.addAttribute("paymentHistoryMap", paymentHistoryMap);
				// All subscriptions have the same booking, so these will match for all items in the list
				model.addAttribute("showVehicleDetails", subscriptionUtils.showVehicleDetails(firstBookingData));
				model.addAttribute("customerAddress", subscriptionUtils.getCustomerAddress(firstBookingData));
				boolean showCardDetails = !StringUtil.isEmpty(firstBookingData.getCardNumber());
				model.addAttribute("showCardDetails", showCardDetails);
				model.addAttribute("showResendConfEmail", requestBean.getAffiliateConfig()
						.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_EMAIL_CONFIRMATIONS));
				model.addAttribute("bookingCarParks", carParkService.fetchAllBookingCarParks(reference));
				int productId = Optional.ofNullable(firstBookingData.getProductId()).orElse(0);
				SubscriptionProduct subscriptionProduct = subscriptionService.fetchSubscriptionProductById(productId);
				model.addAttribute("showCarParks", subscriptionProduct != null ? BooleanUtils.isNotTrue(subscriptionProduct.getHideCarPark()) : false);
				model.addAttribute("showRenewal", controllerService.isSubscriptionRenewalEnabled(requestBean.getSiteId()));
				AffiliateConfig configValues = affiliateService.fetchAffiliateConfigValues(requestBean.getAffiliateId());
				model.addAttribute("disallowChangePersonalDetails",
						configValues.getConfigValue_Boolean(AffiliateConfigKeys.DISALLOW_CHANGE_PERSONAL_DETAILS));
				if (showCardDetails)
				{
					subscriptionUtils.formatCardExpiryDate(firstBookingData);
				}
				subscriptionUtils.formatMinimumTerm(bookingDataList);
				if (StringUtils.hasText(errorMessage))
				{
					model.addAttribute("errorMessage", errorMessage);
				}
				log.debug("Displaying Subscription Details");
				return "subscription-booking-details";
			}
			else
			{
				model.addAttribute("errorMessage", "We could not find a booking for the details provided.");
			}
		}
		
		if (submitted)
		{
			model.addAttribute("errorMessage",
					"We could not find a booking for the details provided.");
		}

		// Add reference to model for prepopulating the form field
		if (!StringUtil.isEmpty(reference))
		{
			model.addAttribute("reference", reference);
		}

		model.addAttribute("header", HEADER_SUBSCRIPTION_SEARCH);
		log.debug("Displaying Subscription Search");
		return "subscription-search";
	}

	@PostMapping(value = "/view-booking")
	public String validateRefAndEmail(final RedirectAttributes redirectAttributes,
			@RequestParam(value = "reference") final String reference,
			@RequestParam(value = "email") final String email)
	{
		SubscriptionBooking booking =
				bookingService.fetchBookingByReferenceAffiliateIdAndEmail(reference, requestBean.getAffiliateId(), email);
		if (booking != null)
		{
			redirectAttributes.addAttribute("reference", reference);
			redirectAttributes.addAttribute("email", email);
		}

		redirectAttributes.addAttribute("submitted", 1);
		return "redirect:view-booking";
	}

	private Map<String, SubscriptionPaymentHistory> getPaymentHistory(List<SubscriptionAllBookingData> bookingDataList)
	{
		Map<String, SubscriptionPaymentHistory> paymentHistoryMap = new HashMap<>();
		for (SubscriptionAllBookingData bookingData : bookingDataList)
		{
			if (!SubscriptionPeriodType.isFixedTicket(bookingData.getSubscriptionType()))
			{
				SubscriptionPaymentHistory paymentHistory = paymentHistoryHandler.processHistory(bookingData,
						requestBean.getAffiliateId(), requestBean.getSiteId());
				if (paymentHistory != null)
				{
					paymentHistoryMap.put(bookingData.getProductName(), paymentHistory);
				}
			}
		}
		return paymentHistoryMap;
	}
}
