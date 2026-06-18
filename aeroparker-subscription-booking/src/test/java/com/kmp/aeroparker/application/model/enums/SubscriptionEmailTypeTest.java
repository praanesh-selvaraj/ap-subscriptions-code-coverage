package com.kmp.aeroparker.application.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.enums.SubscriptionEmailType;

public class SubscriptionEmailTypeTest
{

	@Test
	void testSize()
	{
		assertThat(SubscriptionEmailType.values().length).isEqualTo(3);
	}

	@Test
	void testValueOf()
	{
		assertThat(SubscriptionEmailType.valueOf("CONFIRMATION")).isEqualTo(SubscriptionEmailType.CONFIRMATION);
		assertThat(SubscriptionEmailType.valueOf("CHARGE")).isEqualTo(SubscriptionEmailType.CHARGE);
		assertThat(SubscriptionEmailType.valueOf("NOTICE_OF_TERMINATION")).isEqualTo(SubscriptionEmailType.NOTICE_OF_TERMINATION);
	}

	@Test
	void testGetStrEnum()
	{
		assertThat(SubscriptionEmailType.getStrEnum("confirmation")).isEqualTo(SubscriptionEmailType.CONFIRMATION.toString());
		assertThat(SubscriptionEmailType.getStrEnum("notice_of_termination")).isEqualTo(SubscriptionEmailType.NOTICE_OF_TERMINATION.toString());
	}

	@Test
	void testGetStrEnum_Empty_String()
	{
		assertThat(SubscriptionEmailType.getStrEnum("")).isEqualTo(SubscriptionEmailType.CONFIRMATION.toString());
	}
}