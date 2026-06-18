package com.kmp.aeroparker.subscription.payments.refund.processor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequestBuilder;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequestHandler;
import com.kmp.aeroparker.subscription.payments.klix.KlixResponse;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

@ExtendWith(MockitoExtension.class)
class KlixRefundProcessorTest
{
	@InjectMocks
	private KlixRefundProcessor processor;
	@Mock
	private Payments payments;
	@Mock
	private PaymentService paymentService;
	@Mock
	private KlixCredentials credentials;
	@Mock
	private KlixRequestBuilder klixRequestBuilder;
	@Mock
	private KlixRequestHandler requestHandler;
	@Mock
	private KlixResponse response;

	@Test
	public void testProcess()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(credentials);
		when(payments.getTransactionId()).thenReturn("TransactionId");
		when(payments.getReference()).thenReturn("ref");
		when(payments.getId()).thenReturn(1);
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.insertRefund(any())).thenReturn(true);
		when(klixRequestBuilder.generateJsonForRefund(any())).thenReturn(new JsonObject());
		when(requestHandler.sendRefundRequest(any(), anyString(), anyString(), anyInt(), any())).thenReturn(response);
		when(response.getStatus()).thenReturn("success");

		assertTrue(processor.process(1, payments, BigDecimal.TEN, "BST"));
	}

	@Test
	public void testProcess_Null_Response()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(credentials);
		when(payments.getTransactionId()).thenReturn("TransactionId");
		when(payments.getReference()).thenReturn("ref");
		when(klixRequestBuilder.generateJsonForRefund(any())).thenReturn(new JsonObject());

		assertFalse(processor.process(1, payments, BigDecimal.TEN, "BST"));
	}

	@Test
	public void testProcess_Pending_Refund()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(credentials);
		when(payments.getTransactionId()).thenReturn("TransactionId");
		when(payments.getReference()).thenReturn("ref");
		when(payments.getId()).thenReturn(1);
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.insertRefund(any())).thenReturn(true);
		when(klixRequestBuilder.generateJsonForRefund(any())).thenReturn(new JsonObject());
		when(requestHandler.sendRefundRequest(any(), anyString(), anyString(), anyInt(), any())).thenReturn(response);
		when(response.getStatus()).thenReturn("pending_refund");

		assertTrue(processor.process(1, payments, BigDecimal.TEN, "BST"));
	}

	@Test
	public void testProcess_Unknown_Status()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(credentials);
		when(payments.getTransactionId()).thenReturn("TransactionId");
		when(payments.getReference()).thenReturn("ref");
		when(klixRequestBuilder.generateJsonForRefund(any())).thenReturn(new JsonObject());
		when(requestHandler.sendRefundRequest(any(), anyString(), anyString(), anyInt(), any())).thenReturn(response);
		when(response.getStatus()).thenReturn("unknown");

		assertFalse(processor.process(1, payments, BigDecimal.TEN, "BST"));
	}

	@Test
	public void testProcess_Null_Payment()
	{
		assertFalse(processor.process(1, null, BigDecimal.TEN, "BST"));
	}

	@Test
	public void testProcess_Zero_Amount()
	{
		assertTrue(processor.process(1, payments, BigDecimal.ZERO, "BST"));
	}

	@Test
	public void testProcess_Negative_Amount()
	{
		assertFalse(processor.process(1, payments, BigDecimal.valueOf(-10), "BST"));
	}

	@Test
	public void testProcess_Null_Creds()
	{
		assertFalse(processor.process(1, payments, BigDecimal.TEN, "BST"));
	}

	@Test
	public void testGetType()
	{
		assertEquals(PaymentGatewayType.KLIX, processor.getType());
	}
}
