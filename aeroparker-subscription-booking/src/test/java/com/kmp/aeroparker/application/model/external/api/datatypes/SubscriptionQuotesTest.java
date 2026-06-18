package com.kmp.aeroparker.application.model.external.api.datatypes;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class SubscriptionQuotesTest
{
	@Test
	public void testBean()
	{
		MatcherAssert.assertThat(SubscriptionQuotes.class, CoreMatchers.allOf(hasValidBeanConstructor()));
	}
}
