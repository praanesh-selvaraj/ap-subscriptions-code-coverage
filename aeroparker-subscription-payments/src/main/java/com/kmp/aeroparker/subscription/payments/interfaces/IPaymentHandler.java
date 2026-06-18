package com.kmp.aeroparker.subscription.payments.interfaces;

import java.util.Map;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

public interface IPaymentHandler
{
	PaymentGatewayType getType();

	Map<String, Object> setUpTransaction(PaymentGatewayParameters paymentHandlerParams);

	boolean processPayment(SubscriptionBookingData bookingData);

	default PaymentHandlerBean processRedirectPayment(SubscriptionBookingData bookingData)
	{
		return null;
	}

	default boolean processAfterBooking(SubscriptionBookingData bookingData)
	{
		return true;
	}

	default SubscriptionBookingData processSubCmd(int affiliateId, String reference, String cmd, String callbackData,
			boolean isCallback, String guid)
	{
		return null;
	}
}