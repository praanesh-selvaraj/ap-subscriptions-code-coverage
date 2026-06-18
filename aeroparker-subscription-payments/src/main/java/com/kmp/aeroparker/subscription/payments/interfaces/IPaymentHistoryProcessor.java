package com.kmp.aeroparker.subscription.payments.interfaces;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionPaymentHistory;

public interface IPaymentHistoryProcessor
{
	PaymentGatewayType getType();

	SubscriptionPaymentHistory loadHistory(int affiliateId, int carParkId, String bookingReference);
}
