package com.kmp.aeroparker.application.model.external.api.datatypes;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class CarParkTest
{
	@Test
	public void testBean()
	{
		MatcherAssert.assertThat(CarPark.class,
				CoreMatchers.allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
	}
}
