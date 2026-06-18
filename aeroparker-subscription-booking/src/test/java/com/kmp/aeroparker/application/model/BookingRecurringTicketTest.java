package com.kmp.aeroparker.application.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;

class BookingRecurringTicketTest
{

	@Test
	void testGetType()
	{
		BookingRecurringTicket bookingRecurringTicket = new BookingRecurringTicket();
		assertThat(bookingRecurringTicket.getType()).isEqualTo(SubscriptionPeriodType.RECURRING);
	}
}