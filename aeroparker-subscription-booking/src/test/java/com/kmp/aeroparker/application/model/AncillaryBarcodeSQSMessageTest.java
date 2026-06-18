package com.kmp.aeroparker.application.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class AncillaryBarcodeSQSMessageTest
{
	@Test
	public void testConstructor()
	{
		assertThat(AncillaryBarcodeSQSMessage.class, hasValidBeanConstructor());
	}

	@Test
	public void testGettersAndSetters()
	{
		assertThat(AncillaryBarcodeSQSMessage.class, hasValidGettersAndSetters());
	}
}
