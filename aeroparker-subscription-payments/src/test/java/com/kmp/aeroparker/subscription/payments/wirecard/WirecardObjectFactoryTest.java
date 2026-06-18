package com.kmp.aeroparker.subscription.payments.wirecard;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.payments.enums.CustomValue;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsWirecardResponse;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class WirecardObjectFactoryTest
{
	@InjectMocks
	private WirecardObjectFactory factory;

	@Test
	void testBuildWirecardRequestParameters()
	{
		assertThat(factory.buildWirecardRequestParameters("merchant_id", EnhancedRandom.random(SubscriptionBookingData.class))).isNotNull()
				.isInstanceOf(WirecardRequestParameters.class);
	}

	@Test
	void testBuildPaymentsWirecardResponse()
	{
		assertThat(factory.buildPaymentsWirecardResponse(EnhancedRandom.random(WirecardPaymentResponse.class))).isNotNull()
				.isInstanceOf(PaymentsWirecardResponse.class);
	}

	@Test
	void testBuildRefundRequestParameters()
	{
		Map<String, String> paymentCustomValues = new HashMap<>();
		paymentCustomValues.put(CustomValue.EMAIL_ADDRESS.getField(), "email_adddress");
		paymentCustomValues.put(CustomValue.CURRENCY.getField(), "EUR");
		assertThat(factory.buildRefundRequestParameters("transactionId", BigDecimal.ONE, EnhancedRandom.random(WirecardCredentials.class),
				paymentCustomValues)).isNotNull()
						.isInstanceOf(WirecardRequestParameters.class);
	}
}