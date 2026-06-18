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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class BookingItemProcessorTest
{
	@Mock
	private BookingService service;
	@Mock
	private RecurringTicketProcessor recurringTicketProcessor;
	@Mock
	private SeasonTicketProcessor seasonTicketProcessor;
	@InjectMocks
	private BookingItemProcessor processor;

	@Test
	void testProcess_BookingSeasonTicket()
	{
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = EnhancedRandom.random(SubscriptionBookingItem.class);
		BookingSeasonTicket bookingSeasonTicket = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(bookingItem, bookingSeasonTicket);
		when(service.saveSubscriptionBookingItem(any())).thenReturn(true);
		when(seasonTicketProcessor.process(anyInt(), any(BookingSeasonTicket.class))).thenReturn(true);
		assertThat(processor.process(1, 1, 1, 1, "bookingReference", bookingItemMap)).isTrue();
		verify(service).saveSubscriptionBookingItem(any());
		verify(seasonTicketProcessor).process(anyInt(), any(BookingSeasonTicket.class));
		verifyNoMoreInteractions(service, seasonTicketProcessor);
	}

	@Test
	void testProcess_BookingRecurringTicket()
	{
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = EnhancedRandom.random(SubscriptionBookingItem.class);
		BookingRecurringTicket bookingRecurringTicket = mock(BookingRecurringTicket.class);
		when(bookingRecurringTicket.getType()).thenReturn(SubscriptionPeriodType.RECURRING);
		bookingItemMap.put(bookingItem, bookingRecurringTicket);
		when(service.saveSubscriptionBookingItem(any())).thenReturn(true);
		when(recurringTicketProcessor.process(anyInt(), anyInt(), anyInt(), anyString(), any(), any(BookingRecurringTicket.class))).thenReturn(true);
		assertThat(processor.process(1, 2, 1, 1, "bookingReference", bookingItemMap)).isTrue();
		verify(service).saveSubscriptionBookingItem(any());
		verify(recurringTicketProcessor).process(anyInt(), anyInt(), anyInt(), anyString(), any(), any(BookingRecurringTicket.class));
		verifyNoMoreInteractions(service, recurringTicketProcessor);
	}

	@Test
	void testProcess_Booking_Ticket_Null()
	{
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = EnhancedRandom.random(SubscriptionBookingItem.class);
		bookingItemMap.put(bookingItem, null);
		when(service.saveSubscriptionBookingItem(any())).thenReturn(true);
		assertThat(processor.process(1, 1, 1, 1, "bookingReference", bookingItemMap)).isFalse();
		verify(service).saveSubscriptionBookingItem(any());
		verify(recurringTicketProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyString(), any(), any(BookingRecurringTicket.class));
		verifyNoMoreInteractions(service, recurringTicketProcessor);
	}

	@Test
	void testProcess_Save_Booking_Item_False()
	{
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = EnhancedRandom.random(SubscriptionBookingItem.class);
		bookingItemMap.put(bookingItem, null);
		when(service.saveSubscriptionBookingItem(any())).thenReturn(false);
		assertThat(processor.process(1, 1, 1, 1, "bookingReference", bookingItemMap)).isFalse();
		verify(service).saveSubscriptionBookingItem(any());
		verify(recurringTicketProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyString(), any(), any(BookingRecurringTicket.class));
		verifyNoMoreInteractions(service, recurringTicketProcessor);
	}

	@Test
	void testProcess_Booking_Item_Null()
	{
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		bookingItemMap.put(null, null);
		assertThat(processor.process(1, 1, 1, 1, "bookingReference", bookingItemMap)).isFalse();
		verify(service, times(0)).saveSubscriptionBookingItem(any());
		verify(recurringTicketProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyString(), any(), any(BookingRecurringTicket.class));
		verifyNoMoreInteractions(service, recurringTicketProcessor);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(ProcessorType.ITEM_BOOKING);
	}
}