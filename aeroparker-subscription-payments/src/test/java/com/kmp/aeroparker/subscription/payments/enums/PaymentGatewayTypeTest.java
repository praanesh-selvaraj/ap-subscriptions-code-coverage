package com.kmp.aeroparker.subscription.payments.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

class PaymentGatewayTypeTest
{
	@Test
	void testSize()
	{
		assertThat(PaymentGatewayType.values().length).isEqualTo(4);
	}

	@Test
	void testGetId()
	{
		assertThat(PaymentGatewayType.BRAINTREE.getId()).isEqualTo(1);
		assertThat(PaymentGatewayType.WIRECARD.getId()).isEqualTo(7);
		assertThat(PaymentGatewayType.KLIX.getId()).isEqualTo(22);
		assertThat(PaymentGatewayType.STRIPE.getId()).isEqualTo(28);
	}

	@Test
	void testGetType()
	{
		assertThat(PaymentGatewayType.getType(1)).isEqualTo(PaymentGatewayType.BRAINTREE);
		assertThat(PaymentGatewayType.getType(7)).isEqualTo(PaymentGatewayType.WIRECARD);
		assertThat(PaymentGatewayType.getType(22)).isEqualTo(PaymentGatewayType.KLIX);
		assertThat(PaymentGatewayType.getType(28)).isEqualTo(PaymentGatewayType.STRIPE);
	}

	@Test
	void testGetType_Invalid_Type()
	{
		assertThat(PaymentGatewayType.getType(0)).isNull();
	}
}