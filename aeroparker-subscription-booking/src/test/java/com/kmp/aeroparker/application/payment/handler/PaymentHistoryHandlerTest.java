package com.kmp.aeroparker.application.payment.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.CarParkService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;
import com.kmp.aeroparker.subscription.payments.factory.PaymentHistoryProcessorFactory;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionPaymentHistory;
import com.kmp.aeroparker.subscription.payments.processor.history.BraintreePaymentHistoryProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryHandlerTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private CarParkService carParkService;
	@Mock
	private PaymentHistoryProcessorFactory processorFactory;
	@Mock
	private Payments mockPayment;
	@Mock
	private SubscriptionAllBookingData mockBookingData;
	
	@InjectMocks
	private PaymentHistoryHandler handler;
	
	@Test
	void testProcessHistory()
	{
		SubscriptionAllBookingData mockBookingData = mock(SubscriptionAllBookingData.class);
		Payments mockPayment = mock(Payments.class);
		BraintreePaymentHistoryProcessor mockProcessor = mock(BraintreePaymentHistoryProcessor.class);
		
		when(mockBookingData.getSubscriptionReference()).thenReturn("Reference");
		when(mockBookingData.getCarparkName()).thenReturn("Name");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(mockPayment);
		when(mockPayment.getType()).thenReturn(1);
		when(carParkService.fetchCarParkIdByNameAndSiteId(anyString(), anyInt())).thenReturn(1);
		when(processorFactory.getInstance(any())).thenReturn(mockProcessor);
		when(mockProcessor.loadHistory(anyInt(), anyInt(), anyString())).thenReturn(mock(SubscriptionPaymentHistory.class));
	
		assertThat(handler.processHistory(mockBookingData, 1, 1)).isNotNull();
	}
	
	@Test
	void testProcessHistory_NullPayment()
	{
		when(mockBookingData.getSubscriptionReference()).thenReturn("Reference");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(null);
	
		assertNull(handler.processHistory(mockBookingData, 1, 1));
		
		verifyNoInteractions(processorFactory, carParkService);
	}
	
	@Test
	void testProcessHistory_TypeNotSupportedForPaymentHistory()
	{
		when(mockBookingData.getSubscriptionReference()).thenReturn("Reference");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(mockPayment);
		when(mockPayment.getType()).thenReturn(28);
		when(processorFactory.getInstance(any())).thenReturn(null);
		
		assertNull(handler.processHistory(mockBookingData, 1, 1));
		
		verify(processorFactory).getInstance(any());
		verifyNoInteractions(carParkService);
	}
	
	@Test
	void testProcessHistory_TypeNotSupportedForSubscriptionsApp()
	{
		when(mockBookingData.getSubscriptionReference()).thenReturn("Reference");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(mockPayment);
		when(mockPayment.getType()).thenReturn(100);
	
		assertNull(handler.processHistory(mockBookingData, 1, 1));
		
		verifyNoInteractions(processorFactory, carParkService);
	}
}
