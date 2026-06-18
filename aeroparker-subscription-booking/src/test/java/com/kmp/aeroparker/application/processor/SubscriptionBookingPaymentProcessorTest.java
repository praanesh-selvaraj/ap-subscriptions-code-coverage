package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

@ExtendWith(MockitoExtension.class)
class SubscriptionBookingPaymentProcessorTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private BookingService bookingService;
	@InjectMocks
	private SubscriptionBookingPaymentProcessor processor;

	@Test
	void testProcess()
	{
		Payments payment = mock(Payments.class);
		when(payment.getId()).thenReturn(10);
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(bookingService.insertSubscriptionBookingPayment(any())).thenReturn(true);
		processor.process(1111, "bookingReference");
		verify(paymentService).fetchPaymentByReference(anyString());
		verify(bookingService).insertSubscriptionBookingPayment(any());
		verifyNoMoreInteractions(paymentService, bookingService);
	}

	@Test
	void testProcess_insert_False()
	{
		Payments payment = mock(Payments.class);
		when(payment.getId()).thenReturn(10);
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(bookingService.insertSubscriptionBookingPayment(any())).thenReturn(false);
		processor.process(1111, "bookingReference");
		verify(paymentService).fetchPaymentByReference(anyString());
		verify(bookingService).insertSubscriptionBookingPayment(any());
		verifyNoMoreInteractions(paymentService, bookingService);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(ProcessorType.BOOKING_PAYMENT);
	}
}