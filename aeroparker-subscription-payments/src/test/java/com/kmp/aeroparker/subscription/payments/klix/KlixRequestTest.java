package com.kmp.aeroparker.subscription.payments.klix;

import static com.google.code.beanmatchers.BeanMatchers.isABeanWithValidGettersAndSetters;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KlixRequestTest
{
	@Test
	public void testKlixRequest()
	{
		assertThat(
				new KlixRequest(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
						null, null, null, null, null, null, 0, null, false, false, null),
				isABeanWithValidGettersAndSetters());
	}
}
