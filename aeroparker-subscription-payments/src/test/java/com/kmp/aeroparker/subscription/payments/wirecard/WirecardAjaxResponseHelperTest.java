package com.kmp.aeroparker.subscription.payments.wirecard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;

@ExtendWith(MockitoExtension.class)
class WirecardAjaxResponseHelperTest
{
	@InjectMocks
	private WirecardAjaxResponseHelper requestHandler;

	@Test
	void testGenerateJsonRequestDataForDisplayPaymentForm()
	{
		WirecardCredentials credentials = mock(WirecardCredentials.class);
		when(credentials.getMerchantId()).thenReturn("merchantId");
		when(credentials.getSecret()).thenReturn("secret");
		assertThat(requestHandler.generateJsonRequestDataForDisplayPaymentForm("10.00", "EUR", credentials)).isNotNull()
				.hasFieldOrPropertyWithValue("merchant_account_id", "merchantId")
				.hasFieldOrPropertyWithValue("amount", "10.00");
	}
}