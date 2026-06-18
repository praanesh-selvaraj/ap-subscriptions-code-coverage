package com.kmp.aeroparker.subscription.payments.ajax;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardAjaxResponse;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardAjaxResponseHelper;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class WirecardSeamlessRequestGeneratorTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private WirecardAjaxResponseHelper wirecardRequestHandler;
	@InjectMocks
	private WirecardSeamlessRequestGenerator wirecardSeamlessRequestGenerator;

	@Test
	void testProcessRequest()
	{
		when(wirecardRequestHandler.generateJsonRequestDataForDisplayPaymentForm(anyString(), anyString(), any()))
				.thenReturn(EnhancedRandom.random(WirecardAjaxResponse.class));
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(WirecardCredentials.class));
		assertThat(wirecardSeamlessRequestGenerator.processRequest("init", 1, "EUR", "10.00")).isNotNull();
	}

	@Test
	void testProcessRequest_Cmd_Not_Init()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(WirecardCredentials.class));
		assertThat(wirecardSeamlessRequestGenerator.processRequest("Unknown", 1, "EUR", "10.00")).isNotNull();
	}

	@Test
	void testProcessRequest_Affiliate_Zero()
	{
		assertThat(wirecardSeamlessRequestGenerator.processRequest("init", 0, "EUR", "10.00")).isNotNull();
	}

	@Test
	void testProcessRequest_Credentials_Null()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(null);
		assertThat(wirecardSeamlessRequestGenerator.processRequest("init", 1, "EUR", "10.00")).isNotNull();
	}
}