package com.kmp.aeroparker.subscription.payments.credentials;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class WirecardCredentials implements Credentials
{
	private boolean payPalEnabled;
	private boolean sofortEnabled;
	private String paymentJsLocation;
	// A unique identifier assigned for every Merchant Account.
	private String merchantId;
	// Your password for back-end operations.
	private String secret;
	private String creditCardRestUrl;
	private String restUser;
	private String restPassword;
	private String payPalRestUrl;
	private String payPalRestPassword;
	private String payPalRestUser;
	private String payPalMerchantId;
	private String sofortRestUrl;
	private String sofortRestPassword;
	private String sofortRestUser;
	private String sofortMerchantId;

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.WIRECARD;
	}
}