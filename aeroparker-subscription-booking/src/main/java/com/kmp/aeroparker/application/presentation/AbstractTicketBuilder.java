package com.kmp.aeroparker.application.presentation;

import java.time.LocalDate;

import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractTicketBuilder
{
	protected int getValidTerm(final SubscriptionProductTerms productTerms)
	{
		int validTerm = 0;
		if (productTerms != null)
		{
			validTerm = SubscriptionMinimumTerm.valueOf(productTerms.getMinimumTerm())
					.getIntValue();
		}
		else
		{
			// this should never happen
			log.info("PRODUCT TERMS INVALID");
		}
		return validTerm;
	}

	protected LocalDate buildMinimumMaximumDate(final LocalDate startDate, final int validTerm)
	{
		return startDate.plusMonths(validTerm);
	}
}