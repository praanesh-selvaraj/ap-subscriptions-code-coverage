package com.kmp.aeroparker.subscription.payments.interfaces;

import java.math.BigDecimal;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

public interface RefundProcessor
{
	public boolean process(int affId, Payments payment, BigDecimal amount, String timeZone);
	
	default boolean processPartial(int affId, Payments payment, BigDecimal amount, String timeZone)
	{
		return false;
	}

	PaymentGatewayType getType();
}