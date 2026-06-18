package com.kmp.aeroparker.application.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.SubscriptionConfigBean;

class RequestBeanConfigTest
{
	private final RequestBeanConfig config = new RequestBeanConfig();

	@Test
	void test()
	{
		assertThat(config.subscriptionConfigBean()).isNotNull()
				.isInstanceOf(SubscriptionConfigBean.class);
	}
}