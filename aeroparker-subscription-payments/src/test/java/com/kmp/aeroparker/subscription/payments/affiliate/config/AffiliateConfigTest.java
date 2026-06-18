package com.kmp.aeroparker.subscription.payments.affiliate.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;

@ExtendWith(MockitoExtension.class)
class AffiliateConfigTest
{
	@InjectMocks
	private AffiliateConfig config;

	@BeforeEach
	void init()
	{
		config.put(AffiliateConfigKeys.valueOf("STEP1_SHOW_FLIGHT_LOOKUP"), "1");
		config.put(AffiliateConfigKeys.valueOf("DEFAULT_VIEW"), "Enable Grid");
		config.put(AffiliateConfigKeys.valueOf("ENABLE_EMAIL_CONFIRMATIONS"), "true");
		config.put(AffiliateConfigKeys.valueOf("ROUND_TO_WHOLE_CURRENCY_UNIT"), "false");
	}

	@Test
	void testGetConfigValue_String()
	{
		assertThat(config.getConfigValue_String(AffiliateConfigKeys.DEFAULT_VIEW, "")).isEqualTo("Enable Grid");
	}

	@Test
	void testGetConfigValue_String_Null()
	{
		assertThat(config.getConfigValue_String(AffiliateConfigKeys.ALLOW_CANCEL_WITHOUT_REFUND, "")).isEqualTo("");
	}

	@Test
	void testGetConfigValue_Boolean()
	{
		assertThat(config.getConfigValue_Boolean(AffiliateConfigKeys.STEP1_SHOW_FLIGHT_LOOKUP)).isTrue();
	}

	@Test
	void testGetConfigValue_Boolean_True()
	{
		assertThat(config.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_EMAIL_CONFIRMATIONS)).isTrue();
	}

	@Test
	void testGetConfigValue_Boolean_False()
	{
		assertThat(config.getConfigValue_Boolean(AffiliateConfigKeys.ROUND_TO_WHOLE_CURRENCY_UNIT)).isFalse();
	}
}