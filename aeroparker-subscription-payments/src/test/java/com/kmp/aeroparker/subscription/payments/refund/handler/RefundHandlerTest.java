package com.kmp.aeroparker.subscription.payments.refund.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.RefundProcessorFactory;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.refund.processor.BraintreeRefundProcessor;
import com.kmp.aeroparker.subscription.payments.refund.processor.StripeRefundProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

@ExtendWith(MockitoExtension.class)
class RefundHandlerTest
{
	@Mock
	private PaymentService service;
	@Mock
	private RefundProcessorFactory factory;
	@InjectMocks
	private RefundHandler handler;
	@Mock
	private SubscriptionBookingData bookingData;

	@Test
	void testProcessRefund()
	{
		when(bookingData.getBookingReference()).thenReturn("reference");
		when(bookingData.getAmount()).thenReturn(BigDecimal.TEN);
		Payments payment = mock(Payments.class);
		when(payment.getAmount()).thenReturn("10.00");
		when(payment.getAmountRefunded()).thenReturn("0.00");
		when(payment.getType()).thenReturn(1);
		when(service.fetchPaymentByReference(anyString())).thenReturn(payment);
		BraintreeRefundProcessor braintreeRefundProcessor = mock(BraintreeRefundProcessor.class);
		when(factory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(braintreeRefundProcessor);
		when(braintreeRefundProcessor.process(anyInt(), any(), any(), any())).thenReturn(true);
		assertThat(handler.processRefund(bookingData)).isTrue();
		verify(service).fetchPaymentByReference(anyString());
		verify(factory).getInstance(any());
	}

	@Test
	void testProcessRefund_IsPartialPayments()
	{
		when(bookingData.getBookingReference()).thenReturn("reference");
		when(bookingData.getAmount()).thenReturn(BigDecimal.TEN);
		when(bookingData.isPartialPaymentsEnabled()).thenReturn(true);
		Payments payment = mock(Payments.class);
		when(payment.getAmount()).thenReturn("10.00");
		when(payment.getAmountRefunded()).thenReturn("0.00");
		when(payment.getType()).thenReturn(28);
		when(service.fetchPaymentByReference(anyString())).thenReturn(payment);
		StripeRefundProcessor stripeRefundProcessor = mock(StripeRefundProcessor.class);
		when(factory.getInstance(eq(PaymentGatewayType.STRIPE))).thenReturn(stripeRefundProcessor);
		when(stripeRefundProcessor.processPartial(anyInt(), any(), any(), any())).thenReturn(true);
		assertThat(handler.processRefund(bookingData)).isTrue();
		verify(service).fetchPaymentByReference(anyString());
		verify(factory).getInstance(any());
		verify(stripeRefundProcessor).processPartial(anyInt(), any(), any(), any());
	}

	@Test
	void testProcessRefund_Refund_Failed()
	{
		when(bookingData.getBookingReference()).thenReturn("reference");
		when(bookingData.getAmount()).thenReturn(BigDecimal.TEN);
		Payments payment = mock(Payments.class);
		when(payment.getAmount()).thenReturn("10.00");
		when(payment.getAmountRefunded()).thenReturn("0.00");
		when(payment.getType()).thenReturn(1);
		when(service.fetchPaymentByReference(anyString())).thenReturn(payment);
		BraintreeRefundProcessor braintreeRefundProcessor = mock(BraintreeRefundProcessor.class);
		when(factory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(braintreeRefundProcessor);
		when(braintreeRefundProcessor.process(anyInt(), any(), any(), any())).thenReturn(false);
		assertThat(handler.processRefund(bookingData)).isFalse();
		verify(service).fetchPaymentByReference(anyString());
		verify(factory).getInstance(any());
	}

	@Test
	void testProcessRefund_Incorrect_Amount_To_Refund()
	{
		when(bookingData.getBookingReference()).thenReturn("reference");
		when(bookingData.getAmount()).thenReturn(BigDecimal.TEN);
		Payments payment = mock(Payments.class);
		when(payment.getAmount()).thenReturn("5.00");
		when(payment.getAmountRefunded()).thenReturn("10.00");
		when(service.fetchPaymentByReference(anyString())).thenReturn(payment);
		assertThat(handler.processRefund(bookingData)).isFalse();
		verifyNoInteractions(factory);
	}

	@Test
	void testProcessRefund_Payment_Null()
	{
		when(bookingData.getBookingReference()).thenReturn("reference");
		when(bookingData.getAmount()).thenReturn(BigDecimal.TEN);
		when(service.fetchPaymentByReference(anyString())).thenReturn(null);
		assertThat(handler.processRefund(bookingData)).isFalse();
		verify(service).fetchPaymentByReference(anyString());
		verifyNoInteractions(factory);
	}

	@Test
	void testProcessRefund_Reference_Null()
	{
		assertThat(handler.processRefund(bookingData)).isFalse();
		verifyNoInteractions(factory, service);
	}
}