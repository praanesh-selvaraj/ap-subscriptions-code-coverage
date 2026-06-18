package com.kmp.aeroparker.subscription.payments.credentials;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class KlixCredentials implements Credentials
{
	private String brandId;
	private String secretKey;
	private String apiUrl;
	private boolean saveCardEnabled;

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.KLIX;
	}
}
