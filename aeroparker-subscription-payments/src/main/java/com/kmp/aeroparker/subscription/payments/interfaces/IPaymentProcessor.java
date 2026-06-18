package com.kmp.aeroparker.subscription.payments.interfaces;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

public interface IPaymentProcessor
{
	PaymentGatewayType getType();

	Payments process(PaymentProcessorParameters paymentProcessorParameters);

	default Payments processPartial(PaymentProcessorParameters paymentProcessorParameters, Payments parentPayment)
	{
		return null;
	}
}