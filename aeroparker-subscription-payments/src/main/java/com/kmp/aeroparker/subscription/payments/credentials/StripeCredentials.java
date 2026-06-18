package com.kmp.aeroparker.subscription.payments.credentials;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class StripeCredentials implements Credentials
{
	private String publishableKey;
	private String secretKey;
	private String stripeJsUrl;
	private boolean paymentLinkEnabled;
	private boolean cardholderNameEnabled;
	private boolean saveCardEnabled;
	private boolean showCountryEnabled;
	private boolean sendMetadata;
	private String webhookEndpointSecret;
	
	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.STRIPE;
	}
}
