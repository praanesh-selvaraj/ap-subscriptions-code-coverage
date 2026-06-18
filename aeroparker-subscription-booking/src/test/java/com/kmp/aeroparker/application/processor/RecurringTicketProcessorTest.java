package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteSubscriptionRecurringPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

@ExtendWith(MockitoExtension.class)
class RecurringTicketProcessorTest
{
	@Mock
	private BookingService service;
	@Mock
	private PaymentService paymentService;
	@Mock
	private SiteService siteService;
	@InjectMocks
	private RecurringTicketProcessor processor;

	@Test
	void testProcess()
	{
		SubscriptionBookingItem subscriptionBookingItem = mock(SubscriptionBookingItem.class);
		when(subscriptionBookingItem.getId()).thenReturn(1);
		when(subscriptionBookingItem.getSubBookingId()).thenReturn(12121);
		BookingRecurringTicket bookingRecurringTicket = mock(BookingRecurringTicket.class);
		Sites site = mock(Sites.class);
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getTimezone()).thenReturn("Europe/London");
		when(service.saveBookingRecurringTicket(any())).thenReturn(true);
		Payments payment = mock(Payments.class);
		when(payment.getId()).thenReturn(1);
		when(bookingRecurringTicket.getStartDate()).thenReturn(new Date(123456));
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(service.fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt()))
				.thenReturn(mock(SiteSubscriptionRecurringPayment.class));
		assertThat(processor.process(1, 1, 1, "bookingReference", subscriptionBookingItem, bookingRecurringTicket))
				.isFalse();
		verify(paymentService).fetchPaymentByReference(anyString());
		verify(service).saveBookingRecurringTicket(any());
		verify(paymentService).insertSubscriptionScheduledRecurringPayment(any());
		verify(service).fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt());
		verify(service, times(0)).insertSiteSubscriptionRecurringPayment(any());
		verifyNoMoreInteractions(paymentService, service);
	}

	@Test
	void testProcess_insert_SiteSubscriptionRecurringPayment()
	{
		SubscriptionBookingItem subscriptionBookingItem = mock(SubscriptionBookingItem.class);
		when(subscriptionBookingItem.getId()).thenReturn(1);
		when(subscriptionBookingItem.getSubBookingId()).thenReturn(12121);
		BookingRecurringTicket bookingRecurringTicket = mock(BookingRecurringTicket.class);
		when(service.saveBookingRecurringTicket(any())).thenReturn(true);
		Sites site = mock(Sites.class);
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getTimezone()).thenReturn("Europe/London");
		Payments payment = mock(Payments.class);
		when(payment.getId()).thenReturn(1);
		when(bookingRecurringTicket.getStartDate()).thenReturn(new Date(123456));
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(service.fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt())).thenReturn(null);
		assertThat(processor.process(1, 1, 1, "bookingReference", subscriptionBookingItem, bookingRecurringTicket))
				.isFalse();
		verify(paymentService).fetchPaymentByReference(anyString());
		verify(service).saveBookingRecurringTicket(any());
		verify(paymentService).insertSubscriptionScheduledRecurringPayment(any());
		verify(service).fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt());
		verify(service).insertSiteSubscriptionRecurringPayment(any());
		verifyNoMoreInteractions(paymentService, service);
	}

	@Test
	void testProcess_insert_SiteSubscriptionStartNow()
	{
		SubscriptionBookingItem subscriptionBookingItem = mock(SubscriptionBookingItem.class);
		when(subscriptionBookingItem.getId()).thenReturn(1);
		when(subscriptionBookingItem.getSubBookingId()).thenReturn(12121);
		BookingRecurringTicket bookingRecurringTicket = mock(BookingRecurringTicket.class);
		when(service.saveBookingRecurringTicket(any())).thenReturn(true);
		Sites site = mock(Sites.class);
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getTimezone()).thenReturn("Europe/London");
		Payments payment = mock(Payments.class);
		when(payment.getId()).thenReturn(1);
		when(bookingRecurringTicket.getStartDate()).thenReturn(DateUtil.nowDate(""));
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(service.fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt())).thenReturn(null);
		assertThat(processor.process(1, 1, 1, "bookingReference", subscriptionBookingItem, bookingRecurringTicket))
				.isFalse();
		verify(paymentService).fetchPaymentByReference(anyString());
		verify(service).saveBookingRecurringTicket(any());
		verify(paymentService).insertSubscriptionScheduledRecurringPayment(any());
		verify(service).fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt());
		verify(service).insertSiteSubscriptionRecurringPayment(any());
		verifyNoMoreInteractions(paymentService, service);
	}

	@Test
	void testProcess_Payment_Null()
	{
		SubscriptionBookingItem subscriptionBookingItem = mock(SubscriptionBookingItem.class);
		when(subscriptionBookingItem.getId()).thenReturn(1);
		BookingRecurringTicket bookingRecurringTicket = mock(BookingRecurringTicket.class);
		when(service.saveBookingRecurringTicket(any())).thenReturn(true);
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(null);
		assertThat(processor.process(1, 1, 1, "bookingReference", subscriptionBookingItem, bookingRecurringTicket))
				.isFalse();
		verify(paymentService).fetchPaymentByReference(anyString());
		verify(service).saveBookingRecurringTicket(any());
		verify(paymentService, times(0)).insertSubscriptionScheduledRecurringPayment(any());
		verify(service, times(0)).fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt());
		verify(service, times(0)).insertSiteSubscriptionRecurringPayment(any());
		verifyNoMoreInteractions(paymentService, service);
	}

	@Test
	void testProcess_BookingReference_Empty()
	{
		SubscriptionBookingItem subscriptionBookingItem = mock(SubscriptionBookingItem.class);
		when(subscriptionBookingItem.getId()).thenReturn(1);
		BookingRecurringTicket bookingRecurringTicket = mock(BookingRecurringTicket.class);
		when(service.saveBookingRecurringTicket(any())).thenReturn(true);
		assertThat(processor.process(1, 1, 2, "", subscriptionBookingItem, bookingRecurringTicket)).isFalse();
		verify(paymentService, times(0)).fetchPaymentByReference(anyString());
		verify(service).saveBookingRecurringTicket(any());
		verify(paymentService, times(0)).insertSubscriptionScheduledRecurringPayment(any());
		verify(service, times(0)).fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt());
		verify(service, times(0)).insertSiteSubscriptionRecurringPayment(any());
		verifyNoMoreInteractions(paymentService, service);
	}

	@Test
	void testProcess_BookingNotSaved()
	{
		SubscriptionBookingItem subscriptionBookingItem = mock(SubscriptionBookingItem.class);
		when(subscriptionBookingItem.getId()).thenReturn(1);
		BookingRecurringTicket bookingRecurringTicket = mock(BookingRecurringTicket.class);
		when(service.saveBookingRecurringTicket(any())).thenReturn(false);
		assertThat(processor.process(1, 1, 2, "", subscriptionBookingItem, bookingRecurringTicket)).isFalse();
		verify(paymentService, times(0)).fetchPaymentByReference(anyString());
		verify(service).saveBookingRecurringTicket(any());
		verify(paymentService, times(0)).insertSubscriptionScheduledRecurringPayment(any());
		verify(service, times(0)).fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt());
		verify(service, times(0)).insertSiteSubscriptionRecurringPayment(any());
		verifyNoMoreInteractions(paymentService, service);
	}
}