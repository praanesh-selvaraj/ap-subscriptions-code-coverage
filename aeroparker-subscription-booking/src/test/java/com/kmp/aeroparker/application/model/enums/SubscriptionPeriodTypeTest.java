package com.kmp.aeroparker.application.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;

class SubscriptionPeriodTypeTest
{

	@Test
	public void testSize()
	{
		assertThat(SubscriptionPeriodType.values().length).isEqualTo(2);
	}

	@Test
	public void testGetType()
	{
		assertThat(SubscriptionPeriodType.FIXED.getType()).isEqualTo("Season Ticket");
		assertThat(SubscriptionPeriodType.RECURRING.getType()).isEqualTo("Recurring Ticket");
	}

	@Test
	public void testGetName()
	{
		assertThat(SubscriptionPeriodType.FIXED.getName()).isEqualTo("Fixed");
		assertThat(SubscriptionPeriodType.RECURRING.getName()).isEqualTo("Monthly auto-renew");
	}

	@Test
	public void testValueOf()
	{
		assertThat(SubscriptionPeriodType.FIXED).isEqualTo(SubscriptionPeriodType.valueOf("FIXED"));
		assertThat(SubscriptionPeriodType.RECURRING).isEqualTo(SubscriptionPeriodType.valueOf("RECURRING"));
	}

	@Test
	public void testIsFixedTicket()
	{
		assertThat(SubscriptionPeriodType.isFixedTicket("FIXED")).isTrue();
		assertThat(SubscriptionPeriodType.isFixedTicket("RECURRING")).isFalse();
	}
}