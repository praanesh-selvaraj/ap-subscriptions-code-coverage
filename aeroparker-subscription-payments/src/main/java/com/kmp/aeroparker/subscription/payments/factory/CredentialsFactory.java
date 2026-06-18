package com.kmp.aeroparker.subscription.payments.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.credentials.Credentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CredentialsFactory
{
	private final List<Credentials> credentials;

	public Class<? extends Credentials> getInstance(final PaymentGatewayType type)
	{
		Credentials cre = credentials.stream()
				.filter(credential -> credential.getType() == type)
				.findFirst()
				.orElse(null);

		return cre == null ? null : cre.getClass();
	}
}