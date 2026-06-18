package com.kmp.aeroparker.subscription.payments.refund.processor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.stripe.StripePaymentIntentProcessor;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Refunds;
import com.stripe.model.Refund;

@ExtendWith(MockitoExtension.class)
class StripeRefundProcessorTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private StripePaymentIntentProcessor stripePaymentIntentProcessor;
	@Mock
	private Payments payment;
	@InjectMocks
	private StripeRefundProcessor processor;

	@Test
	public void testProcess()
	{
		when(payment.getId()).thenReturn(1);
		when(payment.getReference()).thenReturn("ref");
		when(payment.getTransactionId()).thenReturn("transactionId");
		Refund refund = mock(Refund.class);
		when(refund.getStatus()).thenReturn("succeeded");
		when(stripePaymentIntentProcessor.stripeRefundPayment(anyInt(), any(), anyString())).thenReturn(refund);
		
		when(paymentService.insertRefund(any())).thenReturn(true);

		assertTrue(processor.process(1, payment, BigDecimal.TEN, "Europe/London"));

		verify(paymentService).insertRefund(any(Refunds.class));
		verify(paymentService).setAmountRefunded(eq(payment), any(BigDecimal.class));
	}

	@Test
	public void testProcess_AmountZero()
	{
		assertTrue(processor.process(1, payment, BigDecimal.ZERO, "Europe/London"));

		verifyNoInteractions(stripePaymentIntentProcessor);
		verifyNoInteractions(paymentService);
	}

	@Test
	public void testProcess_RefundFailed()
	{
		Payments payment = mock(Payments.class);
		when(payment.getTransactionId()).thenReturn("transactionId");
		when(stripePaymentIntentProcessor.stripeRefundPayment(anyInt(), any(), anyString())).thenReturn(null);

		assertFalse(processor.process(1, payment, BigDecimal.TEN, "Europe/London"));

		verify(paymentService, never()).insertRefund(any());
		verify(paymentService, never()).setAmountRefunded(any(), any());
	}

	@Test
	public void testProcess_RefundFailedNullResponse()
	{
		when(payment.getReference()).thenReturn("ref");
		when(payment.getTransactionId()).thenReturn("transactionId");
		Refund refund = mock(Refund.class);
		when(refund.getStatus()).thenReturn("failed");
		when(stripePaymentIntentProcessor.stripeRefundPayment(anyInt(), any(), anyString())).thenReturn(refund);

		assertFalse(processor.process(1, payment, BigDecimal.TEN, "Europe/London"));

		verify(paymentService, never()).insertRefund(any());
		verify(paymentService, never()).setAmountRefunded(any(), any());
	}

	@Test
	public void testProcess_SavingRefundFailed()
	{
		when(payment.getId()).thenReturn(1);
		when(payment.getReference()).thenReturn("ref");
		when(payment.getTransactionId()).thenReturn("transactionId");
		Refund refund = mock(Refund.class);
		when(refund.getStatus()).thenReturn("succeeded");
		when(stripePaymentIntentProcessor.stripeRefundPayment(anyInt(), any(), anyString())).thenReturn(refund);

		when(paymentService.insertRefund(any())).thenReturn(false);

		assertTrue(processor.process(1, payment, BigDecimal.TEN, "Europe/London"));

		verify(paymentService).insertRefund(any());
		verify(paymentService, never()).setAmountRefunded(any(), any());
	}

	@Test
	public void testProcessPartial()
	{
		List<PartialPayments> partialPayments = new ArrayList<>();
		PartialPayments partialPayment = mock(PartialPayments.class);
		partialPayments.add(partialPayment);
		when(payment.getReference()).thenReturn("bookingReference");
		when(payment.getId()).thenReturn(1);
		when(partialPayment.getAmount()).thenReturn(BigDecimal.TEN);
		when(partialPayment.getAmountRefunded()).thenReturn(BigDecimal.ZERO.toString());
		when(partialPayment.getTransactionId()).thenReturn("transactionId");
		Refund refund = mock(Refund.class);
		when(refund.getStatus()).thenReturn("succeeded");
		when(stripePaymentIntentProcessor.stripeRefundPayment(anyInt(), any(), anyString())).thenReturn(refund);
		when(paymentService.fetchAllPartialPaymentsByReferenceAndType(anyString(), anyInt()))
				.thenReturn(partialPayments);
		when(paymentService.insertRefund(any())).thenReturn(true);

		assertTrue(processor.processPartial(1, payment, BigDecimal.TEN, "Europe/London"));

		verify(paymentService).insertRefund(any());
		verify(paymentService).setAmountRefunded(any(), any());
		verify(paymentService).savePartialPaymentAmount(any());
	}

	@Test
	public void testProcessPartial_RefundFailed()
	{
		List<PartialPayments> partialPayments = new ArrayList<>();
		PartialPayments partialPayment = mock(PartialPayments.class);
		partialPayments.add(partialPayment);
		when(payment.getReference()).thenReturn("bookingReference");
		when(partialPayment.getAmount()).thenReturn(BigDecimal.TEN);
		when(partialPayment.getAmountRefunded()).thenReturn(BigDecimal.ZERO.toString());
		when(partialPayment.getTransactionId()).thenReturn("transactionId");
		Refund refund = mock(Refund.class);
		when(refund.getStatus()).thenReturn("failed");
		when(stripePaymentIntentProcessor.stripeRefundPayment(anyInt(), any(), anyString())).thenReturn(refund);
		when(paymentService.fetchAllPartialPaymentsByReferenceAndType(anyString(), anyInt()))
				.thenReturn(partialPayments);

		assertFalse(processor.processPartial(1, payment, BigDecimal.TEN, "Europe/London"));

		verify(paymentService, never()).insertRefund(any());
		verify(paymentService, never()).setAmountRefunded(any(), any());
		verify(paymentService, never()).savePartialPaymentAmount(any());
	}

	@Test
	public void testProcessPartial_Refund_FailedNullResponse()
	{
		List<PartialPayments> partialPayments = new ArrayList<>();
		PartialPayments partialPayment = mock(PartialPayments.class);
		partialPayments.add(partialPayment);
		when(payment.getReference()).thenReturn("bookingReference");
		when(partialPayment.getAmount()).thenReturn(BigDecimal.TEN);
		when(partialPayment.getAmountRefunded()).thenReturn(BigDecimal.ZERO.toString());
		when(partialPayment.getTransactionId()).thenReturn("transactionId");
		when(stripePaymentIntentProcessor.stripeRefundPayment(anyInt(), any(), anyString())).thenReturn(null);
		when(paymentService.fetchAllPartialPaymentsByReferenceAndType(anyString(), anyInt()))
				.thenReturn(partialPayments);

		assertFalse(processor.processPartial(1, payment, BigDecimal.TEN, "Europe/London"));

		verify(paymentService, never()).insertRefund(any());
		verify(paymentService, never()).savePartialPaymentAmount(any());
	}

	@Test
	public void testProcessPartialZeroAmount()
	{
		Payments payment = mock(Payments.class);

		assertTrue(processor.processPartial(1, payment, BigDecimal.ZERO, "Europe/London"));

		verifyNoInteractions(stripePaymentIntentProcessor);
		verifyNoInteractions(paymentService);
	}
}
