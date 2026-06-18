package com.kmp.aeroparker.application.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SeasonTicketDisplayItemTest
{
	private SeasonTicketDisplayItem displayItem = new SeasonTicketDisplayItem();

	@Test
	void testIsSeasonTicket()
	{
		assertThat(displayItem.isSeasonTicket()).isTrue();
	}

	@Test
	void testIsRollingTicket()
	{
		assertThat(displayItem.isRecurringTicket()).isFalse();
	}
}