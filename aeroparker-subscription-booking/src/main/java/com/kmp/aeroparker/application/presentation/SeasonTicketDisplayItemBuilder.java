package com.kmp.aeroparker.application.presentation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetails;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Component
public class SeasonTicketDisplayItemBuilder extends AbstractTicketBuilder
{
	private final Localise localise;
	private final LanguageFieldsList languageFieldsList;

	public SubscriptionProductDisplayItem build(final SubscriptionProductAvailabilityDetails availabilityDetails)
	{
		log.debug("Building subscription season ticket product");
		SeasonTicketDisplayItem displayItem = new SeasonTicketDisplayItem();
		LocalDate endDate = buildMinimumMaximumDate(availabilityDetails.getStartDate(), getValidTerm(availabilityDetails.getProductTerms()));
		displayItem.setEndDate(endDate);
		displayItem.setEndDateLocalised(localise.longDateTranslated(DateUtil.localDateToCalendar(endDate), languageFieldsList));
		return displayItem;
	}
}