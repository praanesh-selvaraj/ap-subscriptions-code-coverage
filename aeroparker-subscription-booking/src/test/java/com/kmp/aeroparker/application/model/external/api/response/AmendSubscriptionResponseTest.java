package com.kmp.aeroparker.application.model.external.api.response;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

public class AmendSubscriptionResponseTest
{
	@Test
	public void testBean()
	{
		MatcherAssert.assertThat(AmendSubscriptionResponse.class,
				CoreMatchers.allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}
}
