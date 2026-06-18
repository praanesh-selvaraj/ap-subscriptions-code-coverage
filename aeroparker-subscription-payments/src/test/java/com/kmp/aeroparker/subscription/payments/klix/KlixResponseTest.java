package com.kmp.aeroparker.subscription.payments.klix;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KlixResponseTest
{
	@Test
	public void testKlixResponse()
	{
		MatcherAssert.assertThat(KlixResponse.class,
				CoreMatchers.allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}
}
