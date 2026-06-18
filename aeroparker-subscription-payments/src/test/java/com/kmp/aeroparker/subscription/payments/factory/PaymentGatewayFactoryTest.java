package com.kmp.aeroparker.subscription.payments.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.handler.BraintreePaymentHandler;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayFactoryTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private PaymentHandlerFactory paymentHandlerFactory;
	@InjectMocks
	private PaymentGatewayFactory handler;

	@Mock
	private Affiliates affiliate;
	@Mock
	private BraintreePaymentHandler braintreePaymentHandler;
	@Mock
	private IPaymentHandler paymentHandler;
	@Mock
	private SubscriptionBookingData bookingData;

	@Test
	void testPaymentGatewaySetUp()
	{
		Map<String, Object> paymentGatewayParams = new HashMap<>();
		paymentGatewayParams.put("clientToken", "clientToken");
		PaymentGatewayParameters paymentHandlerParams = mock(PaymentGatewayParameters.class);
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(braintreePaymentHandler);
		when(braintreePaymentHandler.setUpTransaction(any())).thenReturn(paymentGatewayParams);
		assertThat(handler.setUpTransaction(paymentHandlerParams)).isNotEmpty()
				.containsEntry("clientToken", "clientToken");
		verify(braintreePaymentHandler, times(1)).setUpTransaction(any());
		verify(paymentService, times(1)).fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt());
		verifyNoMoreInteractions(braintreePaymentHandler, paymentService);
	}

	@Test
	void testPaymentGatewaySetUp_PaymentHandler_Null()
	{
		Map<String, Object> paymentGatewayParams = new HashMap<>();
		paymentGatewayParams.put("clientToken", "clientToken");
		PaymentGatewayParameters paymentHandlerParams = mock(PaymentGatewayParameters.class);
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(1);
		when(paymentHandlerFactory.getInstance(any())).thenReturn(null);
		verify(braintreePaymentHandler, times(0)).setUpTransaction(any());
		assertThat(handler.setUpTransaction(paymentHandlerParams)).isEmpty();
		verify(paymentService, times(1)).fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt());
		verifyNoMoreInteractions(braintreePaymentHandler, paymentService);
	}

	@Test
	void testPaymentGatewaySetUp_PaymentGateway_Type_Null()
	{
		Map<String, Object> paymentGatewayParams = new HashMap<>();
		paymentGatewayParams.put("clientToken", "clientToken");
		PaymentGatewayParameters paymentHandlerParams = mock(PaymentGatewayParameters.class);
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(0);
		assertThat(handler.setUpTransaction(paymentHandlerParams)).isEmpty();
		verify(braintreePaymentHandler, times(0)).setUpTransaction(any());
		verify(paymentService, times(1)).fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt());
		verifyNoMoreInteractions(braintreePaymentHandler, paymentService);
	}

	@Test
	void testQueryTransaction()
	{
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getPaymentGatewayType()).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(braintreePaymentHandler);
		handler.processPayment(bookingData);
		verify(paymentHandlerFactory).getInstance(any());
		verify(braintreePaymentHandler).processPayment(any());
		verifyNoMoreInteractions(braintreePaymentHandler, paymentService);
	}

	@Test
	void testQueryTransaction_SubscriptionBookingData_PaymentGatewayType_Zero()
	{
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getPaymentGatewayType()).thenReturn(0);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(braintreePaymentHandler);
		handler.processPayment(bookingData);
		verify(paymentHandlerFactory).getInstance(any());
		verify(paymentService).fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt());
		verify(braintreePaymentHandler).processPayment(any());
		verifyNoMoreInteractions(braintreePaymentHandler, paymentService);
	}

	@Test
	void testQueryTransaction_PaymentHandler_Null()
	{
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getPaymentGatewayType()).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(null);
		handler.processPayment(bookingData);
		verify(paymentHandlerFactory).getInstance(any());
		verify(braintreePaymentHandler, times(0)).processPayment(any());
		verifyNoMoreInteractions(braintreePaymentHandler, paymentService);
	}

	@Test
	void testQueryTransaction_PaymentGatewayType_Zero()
	{
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getPaymentGatewayType()).thenReturn(0);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(0);
		handler.processPayment(bookingData);
		verify(paymentHandlerFactory, times(0)).getInstance(any());
		verify(paymentService).fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt());
		verify(braintreePaymentHandler, times(0)).processPayment(any());
		verifyNoMoreInteractions(braintreePaymentHandler, paymentService);
	}

	@Test
	public void testProcessRedirectPayment()
	{
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getPaymentGatewayType()).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(braintreePaymentHandler);
		when(braintreePaymentHandler.processRedirectPayment(any())).thenReturn(mock(PaymentHandlerBean.class));

		assertNotNull(handler.processRedirectPayment(bookingData));
	}

	@Test
	public void testProcessRedirectPayment_PaymentGatewayType_Zero_Use_Fetch()
	{
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(braintreePaymentHandler);
		when(braintreePaymentHandler.processRedirectPayment(any())).thenReturn(mock(PaymentHandlerBean.class));

		assertNotNull(handler.processRedirectPayment(bookingData));
	}

	@Test
	public void testProcessRedirectPayment_PaymentGatewayType_Zero()
	{
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(0);

		assertNull(handler.processRedirectPayment(bookingData));
	}

	@Test
	public void testProcessRedirectPayment_Null_Handler()
	{
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(null);

		assertNull(handler.processRedirectPayment(bookingData));
	}

	@Test
	public void testProcessSubCmd()
	{
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(braintreePaymentHandler);
		when(braintreePaymentHandler.processSubCmd(anyInt(), anyString(), anyString(), anyString(), anyBoolean(),
				anyString())).thenReturn(bookingData);

		assertNotNull(handler.processSubCmd(1, "ref", "cmd", "data", false, "guid"));
	}

	@Test
	public void testProcessSubCmd_PaymentGatewayType_Zero()
	{
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(0);

		assertNull(handler.processSubCmd(1, "ref", "cmd", "data", false, "guid"));
	}

	@Test
	public void testProcessSubCmd_Null_PaymentHandler()
	{
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt())).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(null);

		assertNull(handler.processSubCmd(1, "ref", "cmd", "data", false, "guid"));
	}

	@Test
	public void testProcessAfterBooking()
	{
		when(bookingData.getAffiliateId()).thenReturn(123);
		when(bookingData.getPaymentGatewayType()).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(paymentHandler);
		when(paymentHandler.processAfterBooking(bookingData)).thenReturn(true);

		assertTrue(handler.processAfterBooking(bookingData));
	}

	@Test
	public void testProcessAfterBooking_InvalidPaymentGatewayType()
	{
		when(bookingData.getAffiliateId()).thenReturn(123);
		when(bookingData.getPaymentGatewayType()).thenReturn(0);

		assertFalse(handler.processAfterBooking(bookingData));
	}

	@Test
	public void testProcessAfterBooking_PaymentHandler_Null()
	{
		when(bookingData.getAffiliateId()).thenReturn(123);
		when(bookingData.getPaymentGatewayType()).thenReturn(1);
		when(paymentHandlerFactory.getInstance(eq(PaymentGatewayType.BRAINTREE))).thenReturn(null);

		assertFalse(handler.processAfterBooking(bookingData));
	}
}