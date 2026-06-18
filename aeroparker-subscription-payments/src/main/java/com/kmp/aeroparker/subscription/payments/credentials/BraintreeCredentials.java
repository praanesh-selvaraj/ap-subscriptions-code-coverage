package com.kmp.aeroparker.subscription.payments.credentials;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class BraintreeCredentials implements Credentials
{
	private String merchantId;
	private String publicKey;
	private String privateKey;
	private String planId;
	private boolean applePayEnabled;
	private boolean payPalEnabled;
	private boolean cvvCheckEnabled;
	private boolean threeds2Enabled;

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.BRAINTREE;
	}
}