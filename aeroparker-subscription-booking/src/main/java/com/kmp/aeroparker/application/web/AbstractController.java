package com.kmp.aeroparker.application.web;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.aeroparker.i18n.LanguageFieldsList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public abstract class AbstractController
{
	@Autowired
	protected LanguageFieldsList languageFieldsList;
	
	private static final List<String> DAYS_OF_WEEK_FULL =
			Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
	private static final List<String> DAYS_OF_WEEK_SHORT = Arrays.asList("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa");
	private static final List<String> MONTHS_FULL = Arrays.asList("January", "February", "March", "April", "May",
			"June", "July", "August", "September", "October", "November", "December");
	private static final List<String> MONTHS_SHORT =
			Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
	
	protected SubscriptionBookingQuery createBookingQuery(final SubscriptionConfigBean requestBean, final String startDate)
	{
		log.info("Creating booking query");
		SubscriptionBookingQuery bookingQuery = new SubscriptionBookingQuery();
		Affiliates affiliate = requestBean.getAffiliate();
		Sites site = requestBean.getSite();
		Languages currentLanguage = requestBean.getCurrentLanguage();
		bookingQuery.setAffiliate(affiliate);
		bookingQuery.setSite(site);
		bookingQuery.setLanguage(currentLanguage);
		bookingQuery.setTimeZone(requestBean.getTimeZone());
		bookingQuery.setLocation(requestBean.getLocation());
		bookingQuery.setStartDate(startDate);
		bookingQuery.setDefaultLanguage(requestBean.getDefaultLanguage());
		log.info("Booking query created: start date{}, timezone: {}, ", bookingQuery.getStartDate(), bookingQuery.getTimeZone());
		return bookingQuery;
	}
	
	protected void setTranslationsForDatePicker(final Model model)
	{
		model.addAttribute("days", getTranslatedList(DAYS_OF_WEEK_FULL));
		model.addAttribute("daysShort", getTranslatedList(DAYS_OF_WEEK_SHORT));
		model.addAttribute("daysMin", getTranslatedList(DAYS_OF_WEEK_SHORT));
		model.addAttribute("months", getTranslatedList(MONTHS_FULL));
		model.addAttribute("monthsShort", getTranslatedList(MONTHS_SHORT));
	}
	
	private List<String> getTranslatedList(List<String> list)
	{
		return list.stream()
				.map(languageFieldsList::getTranslation)
				.collect(Collectors.toList());
	}
}