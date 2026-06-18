package com.kmp.aeroparker.subscription.payments.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardPaymentResponse;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class WirecardPaymentProcessorTest
{
	@Mock
	private PaymentService paymentService;
	@InjectMocks
	private WirecardPaymentProcessor processor;

	@Mock
	private PaymentProcessorParameters paymentProcessorParameters;

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(PaymentGatewayType.WIRECARD);
	}

	@Test
	void testProcess()
	{
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentProcessorParameters.getConfirmationGuid()).thenReturn("5b1e1ad9-9588-484f-856f-7a972f315c14");
		when(paymentProcessorParameters.getWirecardPaymentResponse()).thenReturn(EnhancedRandom.random(WirecardPaymentResponse.class));
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(true);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(EnhancedRandom.random(Payments.class));
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).savePayment(any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_Payments_Not_Saved()
	{
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentProcessorParameters.getWirecardPaymentResponse()).thenReturn(EnhancedRandom.random(WirecardPaymentResponse.class));
		when(paymentService.savePayment(any())).thenReturn(false);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(EnhancedRandom.random(Payments.class));
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService, times(0)).saveCustomValues(anyInt(), any());
		verify(paymentService).savePayment(any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_PaymentsCustomValues_Not_Saved()
	{
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentProcessorParameters.getConfirmationGuid()).thenReturn("5b1e1ad9-9588-484f-856f-7a972f315c14");
		when(paymentProcessorParameters.getWirecardPaymentResponse()).thenReturn(EnhancedRandom.random(WirecardPaymentResponse.class));
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(false);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(EnhancedRandom.random(Payments.class));
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).savePayment(any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_WirecardPaymentResponse_Null()
	{
		assertThat(processor.process(paymentProcessorParameters)).isNull();
		verifyNoInteractions(paymentService);
	}

	@Test
	void testProcess_PaymentProcessorParameters_Null()
	{
		assertThat(processor.process(null)).isNull();
		verifyNoInteractions(paymentService);
	}
}