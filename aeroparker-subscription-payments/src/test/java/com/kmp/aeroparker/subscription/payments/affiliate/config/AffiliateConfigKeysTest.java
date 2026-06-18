package com.kmp.aeroparker.subscription.payments.affiliate.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AffiliateConfigKeysTest
{
	@Test
	void testLength()
	{
		assertThat(AffiliateConfigKeys.values().length).isEqualTo(83);
	}

	@Test
	void testCustomValueOf()
	{
		assertThat(AffiliateConfigKeys.customValueOf("ALLOW_CANCEL_WITHOUT_REFUND")).isEqualTo(AffiliateConfigKeys.ALLOW_CANCEL_WITHOUT_REFUND);
	}

	@Test
	void testCustomValueOf_Null()
	{
		assertThat(AffiliateConfigKeys.customValueOf("UNKNOWN")).isNull();
	}
}