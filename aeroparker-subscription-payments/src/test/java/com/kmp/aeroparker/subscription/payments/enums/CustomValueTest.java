package com.kmp.aeroparker.subscription.payments.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.enums.CustomValue;

@ExtendWith(MockitoExtension.class)
class CustomValueTest
{

	@Test
	void testSize()
	{
		assertThat(CustomValue.values().length).isEqualTo(27);
	}

	@Test
	void testGetField()
	{
		assertThat(CustomValue.ACCEPTANCE_CODE.getField()).isEqualTo("acceptanceCode");
		assertThat(CustomValue.BRAINTREE_SUB_ID.getField()).isEqualTo("braintreeSubId");
	}
}