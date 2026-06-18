package com.kmp.aeroparker.application.presentation;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecurringTicketDisplayItem extends SubscriptionProductDisplayItem
{
	private LocalDate minimumTermDate;
	private String minimumTerm;

	@Override
	public boolean isSeasonTicket()
	{
		return false;
	}

	@Override
	public boolean isRecurringTicket()
	{
		return true;
	}
}