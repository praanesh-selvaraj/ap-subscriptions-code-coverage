package com.kmp.aeroparker.subscription.payments.credentials;

import static com.google.code.beanmatchers.BeanMatchers.isABeanWithValidGettersAndSettersExcluding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

public class StripeCredentialsTest
{
	@Test
	public void testBean()
	{
		assertThat(new StripeCredentials(), isABeanWithValidGettersAndSettersExcluding("type"));
	}
	
	@Test
	public void testGetType()
	{
		assertEquals(PaymentGatewayType.STRIPE, new StripeCredentials().getType());
	}
}
