package com.kmp.aeroparker.subscription.payments.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ClientConfigTest
{
	@Test
	void testHttpClient()
	{
		assertThat(new ClientConfig()).isNotNull();
	}
}