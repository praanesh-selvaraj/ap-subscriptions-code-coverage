package com.kmp.aeroparker.subscription.payments.processor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.klix.KlixResponse;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

@ExtendWith(MockitoExtension.class)
class KlixPaymentProcessorTest
{
	@InjectMocks
	private KlixPaymentProcessor processor;
	@Mock
	private PaymentProcessorParameters paymentProcessorParameters;
	@Mock
	private PaymentService paymentService;
	@Mock
	private KlixResponse response;
	@Mock
	private Payments payments;

	@Test
	public void testProcess()
	{
		when(response.getId()).thenReturn("1");
		when(response.getBookingReference()).thenReturn("ref");
		when(response.getAmount()).thenReturn((double) 1);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentProcessorParameters.getConfirmationGuid()).thenReturn("5b1e1ad9-9588-484f-856f-7a972f315c14");
		when(paymentProcessorParameters.getKlixResponse()).thenReturn(response);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(true);

		assertNotNull(processor.process(paymentProcessorParameters));

		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).savePayment(any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
	}

	@Test
	public void testProcess_Payments_Not_Saved()
	{
		when(response.getId()).thenReturn("1");
		when(response.getBookingReference()).thenReturn("ref");
		when(response.getAmount()).thenReturn((double) 1);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentProcessorParameters.getKlixResponse()).thenReturn(response);
		when(paymentService.savePayment(any())).thenReturn(false);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);

		assertNotNull(processor.process(paymentProcessorParameters));

		verify(paymentService, times(0)).saveCustomValues(anyInt(), any());
		verify(paymentService).savePayment(any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
	}

	@Test
	public void testProcess_CustomValues_Not_Saved()
	{
		when(response.getId()).thenReturn("1");
		when(response.getBookingReference()).thenReturn("ref");
		when(response.getAmount()).thenReturn((double) 1);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentProcessorParameters.getConfirmationGuid()).thenReturn("5b1e1ad9-9588-484f-856f-7a972f315c14");
		when(paymentProcessorParameters.getKlixResponse()).thenReturn(response);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(false);

		assertNotNull(processor.process(paymentProcessorParameters));

		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).savePayment(any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
	}

	@Test
	public void testProcess_CustomValues_ResponseId_Null()
	{
		when(paymentProcessorParameters.getKlixResponse()).thenReturn(response);

		assertNull(processor.process(paymentProcessorParameters));

		verify(paymentService, times(0)).saveCustomValues(anyInt(), any());
		verify(paymentService, times(0)).savePayment(any());
		verify(paymentService, times(0)).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
	}

	@Test
	public void testProcess_Payments_Null_Params()
	{
		assertNull(processor.process(null));
	}

	@Test
	public void testProcess_Payments_Null_Response()
	{
		when(paymentProcessorParameters.getKlixResponse()).thenReturn(null);

		assertNull(processor.process(paymentProcessorParameters));
	}

	@Test
	public void testGetType()
	{
		assertEquals(PaymentGatewayType.KLIX, processor.getType());
	}
}
