package com.kmp.aeroparker.subscription.payments.enums;

import java.util.Arrays;

public enum PaymentGatewayType
{
	BRAINTREE(1), WIRECARD(7), KLIX(22), STRIPE(28);

	int id;

	private PaymentGatewayType(final int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public static PaymentGatewayType getType(final int paymentGatewayType)
	{
		return Arrays.asList(PaymentGatewayType.values())
				.stream()
				.filter(pgt -> pgt.getId() == paymentGatewayType)
				.findFirst()
				.orElse(null);
	}
}