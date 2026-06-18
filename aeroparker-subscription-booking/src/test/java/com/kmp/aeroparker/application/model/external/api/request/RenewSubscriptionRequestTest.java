package com.kmp.aeroparker.application.model.external.api.request;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class RenewSubscriptionRequestTest
{
	@Test
	public void testBean()
	{
		MatcherAssert.assertThat(RenewSubscriptionRequest.class,
				CoreMatchers.allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}
}
