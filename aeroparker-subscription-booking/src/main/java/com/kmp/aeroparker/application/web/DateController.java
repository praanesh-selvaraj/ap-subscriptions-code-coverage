package com.kmp.aeroparker.application.web;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class DateController extends AbstractController
{
	private final AffiliateService affiliateService;
	private final Localise localise;
	private final SiteService siteService;
	private final SubscriptionConfigBean requestBean;
	private final SubscriptionControllerService service;

	@GetMapping(value = "/dates")
	public String init(final Model model,
			@RequestParam(value = "noProduct", defaultValue = "false") boolean noProduct)
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = requestBean.getAffiliateSubscriptionSettings();
		model.addAttribute("datePickerStartDate", service.setUpDate(affiliateSubscriptionSettings, requestBean.getTimeZone()));
		model.addAttribute("currentStage", 1);
		AffiliateSubscription affiliateSubscription = requestBean.getAffiliateSubscription();
		model.addAttribute("header", affiliateSubscription == null ? "" : affiliateSubscription.getTitle());
		model.addAttribute("bookingDetails", new SubscriptionBookingQuery());
		model.addAttribute("datePickerfixedStartDay", affiliateSubscriptionSettings == null ? 0 : affiliateSubscriptionSettings.getFixedStartDay());
		model.addAttribute("noProduct", noProduct);
		boolean skipProductSelection = service.shouldSkipProductSelectionOnDatesPage(affiliateSubscriptionSettings,
				requestBean.getAffiliateId(), requestBean.getSiteId(), requestBean.getCurrentLanguageId(),
				requestBean.getDefaultLanguageId());
		model.addAttribute("skipProductSelection", skipProductSelection);

		setDatePickerFormat(model);
		setTranslationsForDatePicker(model);
		if (affiliateSubscriptionSettings != null)
		{
			setCalendarStartDay(affiliateSubscriptionSettings.getAffiliateId(), model);
		}
		log.debug("Displaying dates (step one)");
		return "subscription-date";
	}

	private void setDatePickerFormat(final Model model)
	{
		model.addAttribute("dateInputFormat", localise.getDateFormatForDatePicker(false, true));
		model.addAttribute("dateDisplayFormat", localise.getDateFormatForDatePicker(true, true));
	}

	/**
	 * This is called when the search button is clicked on step 1 or we change
	 * the date on step 2, this will redirect to /products
	 * 
	 * @param redirectAttributes
	 * @param startDate
	 * @return
	 */
	@PostMapping(value = "/dates")
	public String redirectToSelectProduct(final RedirectAttributes redirectAttributes, @RequestParam(value = "startDate") final String startDate)
	{
		SubscriptionBookingQuery bookingQuery = createBookingQuery(requestBean, startDate);
		if (service.validateBookingTimes(bookingQuery))
		{
			log.debug("Booking query is valid, redirecting to select products");
			redirectAttributes.addFlashAttribute("bookingQuery", bookingQuery);
			redirectAttributes.addAttribute("startDate", bookingQuery.getStartDate());
			log.info("Redirecting to products (Step two)");
			return "redirect:products";
		}
		else
		{
			log.info("Booking query is invalid, stay on same page (Step one)");
			return "redirect:dates";
		}
	}

	private void setCalendarStartDay(final int affiliateId, final Model model)
	{
		Affiliates affiliate = affiliateService.fetchAffiliateById(affiliateId);
		if (affiliate == null || affiliate.getId() <= 0)
		{
			return;
		}

		Sites site = siteService.fetchSiteById(affiliate.getSiteid());
		if (site == null || site.getId() <= 0)
		{
			return;
		}

		Locations location = siteService.fetchLocationById(site.getLocationId());
		if (location != null && location.getCalendarStartDay() != null)
		{
			model.addAttribute("calendarStartDay", location.getCalendarStartDay());
		}
	}
}