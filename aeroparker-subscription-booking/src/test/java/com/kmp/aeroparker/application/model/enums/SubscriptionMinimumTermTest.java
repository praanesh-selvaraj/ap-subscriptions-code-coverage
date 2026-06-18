package com.kmp.aeroparker.application.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;

class SubscriptionMinimumTermTest
{
	@Test
	void testSize()
	{
		assertThat(SubscriptionMinimumTerm.values()).hasSize(13);
	}

	@Test
	void testGetStrValue()
	{
		assertThat(SubscriptionMinimumTerm.RECURRING.getIntValue()).isEqualTo(0);
		assertThat(SubscriptionMinimumTerm.ONE_MONTH.getIntValue()).isEqualTo(1);
		assertThat(SubscriptionMinimumTerm.TWO_MONTHS.getIntValue()).isEqualTo(2);
		assertThat(SubscriptionMinimumTerm.THREE_MONTHS.getIntValue()).isEqualTo(3);
		assertThat(SubscriptionMinimumTerm.FOUR_MONTHS.getIntValue()).isEqualTo(4);
		assertThat(SubscriptionMinimumTerm.FIVE_MONTHS.getIntValue()).isEqualTo(5);
		assertThat(SubscriptionMinimumTerm.SIX_MONTHS.getIntValue()).isEqualTo(6);
		assertThat(SubscriptionMinimumTerm.SIX_MONTHS.getIntValue()).isEqualTo(6);
		assertThat(SubscriptionMinimumTerm.SEVEN_MONTHS.getIntValue()).isEqualTo(7);
		assertThat(SubscriptionMinimumTerm.EIGHT_MONTHS.getIntValue()).isEqualTo(8);
		assertThat(SubscriptionMinimumTerm.NINE_MONTHS.getIntValue()).isEqualTo(9);
		assertThat(SubscriptionMinimumTerm.TEN_MONTHS.getIntValue()).isEqualTo(10);
		assertThat(SubscriptionMinimumTerm.ELEVEN_MONTHS.getIntValue()).isEqualTo(11);
		assertThat(SubscriptionMinimumTerm.TWELVE_MONTHS.getIntValue()).isEqualTo(12);
	}

	@Test
	void testValueOf()
	{
		assertThat(SubscriptionMinimumTerm.RECURRING).isEqualTo(SubscriptionMinimumTerm.valueOf("RECURRING"));
		assertThat(SubscriptionMinimumTerm.ONE_MONTH).isEqualTo(SubscriptionMinimumTerm.valueOf("ONE_MONTH"));
		assertThat(SubscriptionMinimumTerm.TWO_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("TWO_MONTHS"));
		assertThat(SubscriptionMinimumTerm.THREE_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("THREE_MONTHS"));
		assertThat(SubscriptionMinimumTerm.FOUR_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("FOUR_MONTHS"));
		assertThat(SubscriptionMinimumTerm.FIVE_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("FIVE_MONTHS"));
		assertThat(SubscriptionMinimumTerm.SIX_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("SIX_MONTHS"));
		assertThat(SubscriptionMinimumTerm.SEVEN_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("SEVEN_MONTHS"));
		assertThat(SubscriptionMinimumTerm.EIGHT_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("EIGHT_MONTHS"));
		assertThat(SubscriptionMinimumTerm.NINE_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("NINE_MONTHS"));
		assertThat(SubscriptionMinimumTerm.NINE_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("NINE_MONTHS"));
		assertThat(SubscriptionMinimumTerm.ELEVEN_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("ELEVEN_MONTHS"));
		assertThat(SubscriptionMinimumTerm.TWELVE_MONTHS).isEqualTo(SubscriptionMinimumTerm.valueOf("TWELVE_MONTHS"));
	}
}