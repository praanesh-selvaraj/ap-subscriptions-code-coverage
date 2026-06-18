package com.kmp.aeroparker.application.payment.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.processor.SubscriptionBookingProcessor;
import com.kmp.aeroparker.subscription.payments.factory.PaymentGatewayFactory;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.refund.handler.RefundHandler;

@ExtendWith(MockitoExtension.class)
class PaymentHandlerTest
{
	@Mock
	private RefundHandler refundHandler;
	@Mock
	private PaymentGatewayFactory paymentGatewayFactory;
	@Mock
	private SubscriptionBookingProcessor bookingProcessor;
	@Mock
	private Basket basket;
	@InjectMocks
	private PaymentHandler handler;

	@Test
	void testProcessPayment()
	{
		when(paymentGatewayFactory.processPayment(any())).thenReturn(true);
		when(bookingProcessor.process(any(), any())).thenReturn(true);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		assertThat(handler.processPayment(mock(SubscriptionBookingData.class), basket)).isTrue();
		verify(bookingProcessor).process(any(), any());
		verify(paymentGatewayFactory).processPayment(any());
		verify(refundHandler, times(0)).processRefund(any());
		verify(paymentGatewayFactory).processAfterBooking(any());
		verifyNoMoreInteractions(paymentGatewayFactory, bookingProcessor);
	}

	@Test
	void testProcessPayment_Payment_Failed_Refund_Successfull()
	{
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(paymentGatewayFactory.processPayment(any())).thenReturn(false);
		when(refundHandler.processRefund(any())).thenReturn(true);
		assertThat(handler.processPayment(mock(SubscriptionBookingData.class), basket)).isFalse();
		verify(bookingProcessor, times(0)).process(any(), any());
		verify(paymentGatewayFactory).processPayment(any());
		verify(refundHandler).processRefund(any());
		verifyNoMoreInteractions(paymentGatewayFactory, bookingProcessor);
	}

	@Test
	void testProcessPayment_Payment_Failed_Refund_Failed()
	{
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(paymentGatewayFactory.processPayment(any())).thenReturn(false);
		when(refundHandler.processRefund(any())).thenReturn(false);
		assertThat(handler.processPayment(mock(SubscriptionBookingData.class), basket)).isFalse();
		verify(bookingProcessor, times(0)).process(any(), any());
		verify(paymentGatewayFactory).processPayment(any());
		verify(refundHandler).processRefund(any());
		verifyNoMoreInteractions(paymentGatewayFactory, bookingProcessor);
	}

	@Test
	void testProcessPayment_Booking_Failed()
	{
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(paymentGatewayFactory.processPayment(any())).thenReturn(true);
		when(bookingProcessor.process(any(), any())).thenReturn(false);
		when(refundHandler.processRefund(any())).thenReturn(true);
		assertThat(handler.processPayment(mock(SubscriptionBookingData.class), basket)).isFalse();
		verify(bookingProcessor).process(any(), any());
		verify(paymentGatewayFactory).processPayment(any());
		verify(refundHandler).processRefund(any());
		verifyNoMoreInteractions(paymentGatewayFactory, bookingProcessor);
	}

	@Test
	void testSetUpTransaction()
	{
		Map<String, Object> resut = new HashMap<>();
		resut.put("amount", 10);
		when(paymentGatewayFactory.setUpTransaction(any())).thenReturn(resut);
		assertThat(handler.setUpTransaction(mock(PaymentGatewayParameters.class))).isNotEmpty();
		verify(paymentGatewayFactory).setUpTransaction(any());
		verifyNoMoreInteractions(paymentGatewayFactory);
	}

	@Test
	public void testProcessRedirectPayment()
	{
		when(paymentGatewayFactory.processRedirectPayment(any())).thenReturn(mock(PaymentHandlerBean.class));

		assertNotNull(handler.processRedirectPayment(mock(SubscriptionBookingData.class)));
	}

	@Test
	public void testProcessSubCmd()
	{
		when(paymentGatewayFactory.processSubCmd(anyInt(), anyString(), anyString(), anyString(), anyBoolean(),
				anyString())).thenReturn(mock(SubscriptionBookingData.class));

		assertNotNull(handler.processSubCmd(1, "ref", "cmd", "data", false, "guid"));
	}

	@Test
	public void testProcessPayment_NoPaymentRequired()
	{
		when(basket.getGrandTotal()).thenReturn(BigDecimal.ZERO);
		when(bookingProcessor.process(any(), any())).thenReturn(true);
		assertThat(handler.processPayment(mock(SubscriptionBookingData.class), basket)).isTrue();
		verify(bookingProcessor).process(any(), any());
		verifyNoMoreInteractions(bookingProcessor);
		verifyNoMoreInteractions(paymentGatewayFactory);
		verifyNoMoreInteractions(refundHandler);
		verifyNoMoreInteractions(paymentGatewayFactory);
		verifyNoMoreInteractions(bookingProcessor);
	}
}