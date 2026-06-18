package com.kmp.aeroparker.subscription.payments.credentials;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;

@ExtendWith(MockitoExtension.class)
class KlixCredentialsTest
{
	@InjectMocks
	private KlixCredentials credentials;

	@Test
	public void testGetType()
	{
		assertEquals(PaymentGatewayType.KLIX, credentials.getType());
	}
}
