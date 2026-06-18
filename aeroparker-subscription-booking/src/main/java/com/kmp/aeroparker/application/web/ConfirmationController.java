package com.kmp.aeroparker.application.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kmp.aeroparker.application.builder.ConfirmationBuilder;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.ConfirmationDetails;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.application.processor.SubscriptionAnalyticsProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.html.utils.HtmlUtil;
import com.kmp.aeroparker.subscription.security.utils.SecurityUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class ConfirmationController extends AbstractController
{
	private static final String HEADER_CONFIRMATION = "Confirmation";
	private final SubscriptionConfigBean requestBean;
	private final ConfirmationBuilder builder;
	private final BookingService service;
	private final SubscriptionService subscriptionService;
	private final SubscriptionAnalyticsProcessor analyticsProcessor;

	@GetMapping(value = "/confirmation")
	public String confirmation(final Model model, @RequestParam(value = "guid") final String guid)
	{
		log.info("Processing confirmation guid: {}", guid);
		SubscriptionBookingQuery bookingQuery = createBookingQuery(requestBean, "");
		SubscriptionBookingRecord bookingRecord = service.fetchSubscriptionBooking(guid, bookingQuery.getAffiliate()
				.getId());
		ConfirmationDetails confirmationDetails = new ConfirmationDetails();
		String encryptedReference = "";
		if (bookingRecord != null)
		{
			HashMap<Integer, String> confirmationMessages = populateConfirmationMessageIfRequired(bookingRecord);
			confirmationDetails = builder.build(bookingQuery, bookingRecord, confirmationMessages);
			int bookingId = bookingRecord.getBooking() != null ? bookingRecord.getBooking()
					.getId() : 0;
			encryptedReference = service.fetchEncryptedReferenceByBookingId(bookingId);
		}
		analyticsProcessor.processSubscriptionAnalytics(model, bookingRecord, requestBean.getCurrency());
		model.addAttribute("confirmationDetails", confirmationDetails);
		model.addAttribute("currentStage", 4);
		model.addAttribute("header", HEADER_CONFIRMATION);
		model.addAttribute("encryptedReference", encryptedReference);
		boolean skipProductSelection =
				requestBean.getAffiliateSubscriptionSettings() != null && requestBean.getAffiliateSubscriptionSettings()
						.getSkipProductSelectionIfOneIsAvailable();
		model.addAttribute("skipProductSelection", skipProductSelection);
		log.info("Displaying confirmation for guid: {}", guid);
		return "subscription-confirmation";
	}

	@GetMapping(value = "/renewal-confirmation")
	public String renewalConfirmation(final Model model,
			@RequestParam(value = "encryptedReference") final String encryptedReference)
	{
		String reference = SecurityUtil.decryptString(encryptedReference);
		log.info("Displaying renewal confirmation for reference: {}", reference);
		SubscriptionBookingQuery bookingQuery = createBookingQuery(requestBean, "");
		SubscriptionBooking booking = service.fetchBookingByEncryptedReference(encryptedReference);
		SubscriptionBookingRecord bookingRecord =
				service.fetchSubscriptionBooking(booking == null ? 0 : booking.getId(), bookingQuery.getAffiliate()
						.getId());
		ConfirmationDetails confirmationDetails = new ConfirmationDetails();
		if (bookingRecord != null)
		{
			HashMap<Integer, String> confirmationMessages = populateConfirmationMessageIfRequired(bookingRecord);
			confirmationDetails = builder.build(bookingQuery, bookingRecord, confirmationMessages);
		}
		else
		{
			log.error("Failed to fetch booking for reference: {} with encrypted reference: {}", reference, encryptedReference);
		}
		analyticsProcessor.processSubscriptionAnalytics(model, bookingRecord, requestBean.getCurrency());
		model.addAttribute("confirmationDetails", confirmationDetails);
		model.addAttribute("header", HEADER_CONFIRMATION);
		model.addAttribute("encryptedReference", encryptedReference);
		// Determine if product selection step was skipped for breadcrumb display
		boolean skipProductSelection = requestBean.getAffiliateSubscriptionSettings() != null
				&& requestBean.getAffiliateSubscriptionSettings().getSkipProductSelectionIfOneIsAvailable();
		model.addAttribute("skipProductSelection", skipProductSelection);
		log.info("Displaying confirmation for reference: {}", reference);
		return "subscription-confirmation";
	}

	private HashMap<Integer, String> populateConfirmationMessageIfRequired(SubscriptionBookingRecord bookingRecord)
	{
		HashMap<Integer, String> confirmationMessages = new HashMap<>();
		if (requestBean.enableAccountOnDetailsStep())
		{
			final int currentLanguageId = requestBean.getCurrentLanguageId();
			final int defaultLanguageId = requestBean.getDefaultLanguageId();
			for (Map.Entry<SubscriptionBookingItem, IBookingTicket> entry : bookingRecord.getBookingItemMap()
					.entrySet())
			{
				final Integer productId = entry.getKey()
						.getProductId();
				SubscriptionProductAppearance subscriptionProductAppearance =
						getSubscriptionProductAppearance(productId, currentLanguageId, defaultLanguageId);
				confirmationMessages.put(productId,
						HtmlUtil.htmlUnescape(subscriptionProductAppearance.getConfirmationMessage()));
			}
		}
		return confirmationMessages;
	}

	private SubscriptionProductAppearance getSubscriptionProductAppearance(final int productId, final int langId,
			final int defLangId)
	{
		SubscriptionProductAppearance productAppearance = Optional
				.ofNullable(subscriptionService.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(productId,
						langId))
				.orElseGet(() -> subscriptionService
						.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(productId, defLangId));
		return productAppearance;
	}
}