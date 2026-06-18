package com.kmp.aeroparker.application.presentation;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonTicketDisplayItem extends SubscriptionProductDisplayItem
{
	private LocalDate endDate;
	private String endDateLocalised;

	@Override
	public boolean isSeasonTicket()
	{
		return true;
	}

	@Override
	public boolean isRecurringTicket()
	{
		return false;
	}
}