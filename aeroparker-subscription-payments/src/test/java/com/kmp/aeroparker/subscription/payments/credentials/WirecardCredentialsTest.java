package com.kmp.aeroparker.subscription.payments.credentials;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

@ExtendWith(MockitoExtension.class)
class WirecardCredentialsTest
{
	@InjectMocks
	private WirecardCredentials credentials;

	@Test
	void test()
	{
		assertThat(credentials.getType()).isEqualTo(PaymentGatewayType.WIRECARD);
	}
}