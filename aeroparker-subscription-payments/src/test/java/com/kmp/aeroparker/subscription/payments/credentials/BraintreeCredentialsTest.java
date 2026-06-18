package com.kmp.aeroparker.subscription.payments.credentials;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

@ExtendWith(MockitoExtension.class)
class BraintreeCredentialsTest
{
	@InjectMocks
	private BraintreeCredentials braintreeCredentials;

	@Test
	void testGetType()
	{
		assertThat(braintreeCredentials.getType()).isEqualTo(PaymentGatewayType.BRAINTREE);
	}
}