package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.ConfirmationDetails;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

@ExtendWith(MockitoExtension.class)
class ConfirmationBuilderTest
{
	@Mock
	private ConfirmationProductDetailsBuilder productDetailsBuilder;
	@Mock
	private ConfirmationDetailsBuilder detailsBuilder;
	@InjectMocks
	private ConfirmationBuilder builder;

	@Mock
	private Affiliates affiliate;
	@Mock
	private Locations location;
	@Mock
	private SubscriptionBookingRecord bookingRecord;

	@Test
	void testBuild()
	{
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		HashMap<Integer, String> confirmationMessages = new HashMap<>();
		SubscriptionBookingItem bookingItem = mock(SubscriptionBookingItem.class);
		BookingSeasonTicket bookingSeasonTicket = mock(BookingSeasonTicket.class);
		bookingItemMap.put(bookingItem, bookingSeasonTicket);
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		ConfirmationDetails confirmationDetails = mock(ConfirmationDetails.class);
		when(detailsBuilder.build(any(), any(), any())).thenReturn(confirmationDetails);
		productDetailsBuilder.build(any(), any());
		SubscriptionBookingQuery bookingQuery = mock(SubscriptionBookingQuery.class);
		when(bookingQuery.getAffiliate()).thenReturn(affiliate);
		builder.build(bookingQuery, bookingRecord, confirmationMessages);
		verify(detailsBuilder).build(any(), any(), any());
		verify(productDetailsBuilder, times(2)).build(any(), any());
		verifyNoMoreInteractions(detailsBuilder, productDetailsBuilder);
	}

	@Test
	void testBuild_BookingItemMap_Empty()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		HashMap<Integer, String> confirmationMessages = new HashMap<>();
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		ConfirmationDetails confirmationDetails = mock(ConfirmationDetails.class);
		when(detailsBuilder.build(any(), any(), any())).thenReturn(confirmationDetails);
		SubscriptionBookingQuery bookingQuery = mock(SubscriptionBookingQuery.class);
		when(bookingQuery.getAffiliate()).thenReturn(affiliate);
		builder.build(bookingQuery, bookingRecord, confirmationMessages);
		verify(detailsBuilder).build(any(), any(), any());
		verify(productDetailsBuilder, times(0)).build(any(), any());
		verifyNoMoreInteractions(detailsBuilder, productDetailsBuilder);
	}

	@Test
	void testBuild_SubscriptionBookingRecord_Null()
	{
		SubscriptionBookingQuery bookingQuery = mock(SubscriptionBookingQuery.class);
		HashMap<Integer, String> confirmationMessages = new HashMap<>();
		when(bookingQuery.getAffiliate()).thenReturn(affiliate);
		builder.build(bookingQuery, null, confirmationMessages);
		verify(detailsBuilder, times(0)).build(any(), any(), any());
		verify(productDetailsBuilder, times(0)).build(any(), any());
		verifyNoMoreInteractions(detailsBuilder, productDetailsBuilder);
	}

	@Test
	void testGetType()
	{
		assertThat(builder.getType()).isEqualTo(BuilderType.CONFIRMATION);
	}
}