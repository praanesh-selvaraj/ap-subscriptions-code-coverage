package com.kmp.aeroparker.subscription.payments.enums;

public enum PaymentMethod
{
	PAYPAL(2), CARD(1), SOFORT(6);

	private int id;

	private PaymentMethod(final int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public static PaymentMethod getPaymentMethod(final String paymentMethodStr)
	{
		PaymentMethod method = null;
		switch (paymentMethodStr)
		{
			case "creditcard":
			{
				method = PaymentMethod.CARD;
				break;
			}
			case "paypal":
			{
				method = PaymentMethod.PAYPAL;
				break;
			}
			case "sofortbanking":
			{
				method = PaymentMethod.SOFORT;
				break;
			}
		}
		return method;
	}
}