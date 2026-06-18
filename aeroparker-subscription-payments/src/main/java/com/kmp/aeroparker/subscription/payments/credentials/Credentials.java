package com.kmp.aeroparker.subscription.payments.credentials;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

public interface Credentials
{
	PaymentGatewayType getType();
}