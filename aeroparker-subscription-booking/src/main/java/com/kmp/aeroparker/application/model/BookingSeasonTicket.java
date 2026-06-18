package com.kmp.aeroparker.application.model;

import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingSeasonTicket;

public class BookingSeasonTicket extends SubscriptionBookingSeasonTicket implements IBookingTicket
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public SubscriptionPeriodType getType()
	{
		return SubscriptionPeriodType.FIXED;
	}
}