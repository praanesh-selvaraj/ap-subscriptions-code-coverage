package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.PurchaseRequestList;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.factory.TicketBuilderFactory;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionCustomValue;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

@ExtendWith(MockitoExtension.class)
class BookingBuilderTest
{
	@Mock
	private BookingBuilderObjectFactory objectFactory;
	@Mock
	private TicketBuilderFactory ticketFactory;
	@InjectMocks
	private BookingBuilder bookingBuilder;
	@Mock
	private SubscriptionCustomValue subscriptionCustomValue;
	@Mock
	private SubscriptionBookingCustomerDetails bookingCustomerDetails;
	@Mock
	private SubscriptionBookingLanguage subscriptionBookingLanguage;
	@Mock
	private SubscriptionBookingReceiptDetails bookingReceiptDetails;
	@Mock
	private SubscriptionBookingVehicleDetails bookingVehicleDetails;
	@Mock
	private RecurringTicketBookingBuilder recurringTicketBookingBuilder;
	@Mock
	private SeasonTicketBookingBuilder seasonTicketBookingBuilder;


	@Test
	void testBuild()
	{
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getBookingReference()).thenReturn("Reference");
		when(bookingData.getTimeZone()).thenReturn("Europe/London");
		Basket basket = mock(Basket.class);
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(objectFactory.buildBooking(anyInt(), any(), anyInt(), any(), anyString())).thenReturn(booking);
		when(objectFactory.buildCustomerDetails(any())).thenReturn(bookingCustomerDetails);
		when(objectFactory.buildVehicleDetails(any())).thenReturn(bookingVehicleDetails);
		when(objectFactory.buildReceiptDetails(any())).thenReturn(bookingReceiptDetails);
		when(objectFactory.buildLanguage(any())).thenReturn(subscriptionBookingLanguage);
		when(objectFactory.buildCustomValues(any())).thenReturn(Arrays.asList(subscriptionCustomValue));
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = mock(SubscriptionPurchaseRequest.class);
		when(purchaseRequest.getPeriodType()).thenReturn(SubscriptionPeriodType.FIXED);
		purchaseRequestList.add(purchaseRequest);
		when(ticketFactory.getInstance(eq(SubscriptionPeriodType.FIXED), eq(SeasonTicketBookingBuilder.class)))
				.thenReturn(seasonTicketBookingBuilder);
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		assertThat(bookingBuilder.build(1, bookingData, basket)).isNotNull()
				.isInstanceOf(SubscriptionBookingRecord.class);
		verify(objectFactory).buildBooking(anyInt(), any(), anyInt(), any(), anyString());
		verify(objectFactory).buildBookingItemList(any(), any());
		verify(objectFactory).buildCustomerDetails(any());
		verify(objectFactory).buildVehicleDetails(any());
		verify(objectFactory).buildReceiptDetails(any());
		verify(objectFactory).buildLanguage(any());
		verify(objectFactory).buildCustomValues(any());
		verify(ticketFactory).getInstance(any(), any());
		verify(seasonTicketBookingBuilder).build(any());
		verifyNoInteractions(recurringTicketBookingBuilder);
		verifyNoMoreInteractions(objectFactory, ticketFactory);
	}

	@Test
	void testBuild_Reccuring()
	{
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getBookingReference()).thenReturn("Reference");
		when(bookingData.getTimeZone()).thenReturn("Europe/London");
		Basket basket = mock(Basket.class);
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(objectFactory.buildBooking(anyInt(), any(), anyInt(), any(), anyString())).thenReturn(booking);
		when(objectFactory.buildCustomerDetails(any())).thenReturn(bookingCustomerDetails);
		when(objectFactory.buildVehicleDetails(any())).thenReturn(bookingVehicleDetails);
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		when(objectFactory.buildReceiptDetails(any())).thenReturn(bookingReceiptDetails);
		when(objectFactory.buildLanguage(any())).thenReturn(subscriptionBookingLanguage);
		when(objectFactory.buildCustomValues(any())).thenReturn(Arrays.asList(subscriptionCustomValue));
		SubscriptionPurchaseRequest purchaseRequest = mock(SubscriptionPurchaseRequest.class);
		when(purchaseRequest.getPeriodType()).thenReturn(null);
		purchaseRequestList.add(purchaseRequest);
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		purchaseRequestList.add(purchaseRequest);
		when(ticketFactory.getInstance(eq(SubscriptionPeriodType.RECURRING), eq(RecurringTicketBookingBuilder.class)))
				.thenReturn(recurringTicketBookingBuilder);
		assertThat(bookingBuilder.build(1, bookingData, basket)).isNotNull()
				.isInstanceOf(SubscriptionBookingRecord.class);
		verify(objectFactory).buildBooking(anyInt(), any(), anyInt(), any(), anyString());
		verify(objectFactory, times(2)).buildBookingItemList(any(), any());
		verify(objectFactory).buildCustomerDetails(any());
		verify(objectFactory).buildVehicleDetails(any());
		verify(objectFactory).buildReceiptDetails(any());
		verify(objectFactory).buildLanguage(any());
		verify(objectFactory).buildCustomValues(any());
		verify(ticketFactory, times(2)).getInstance(any(), any());
		verify(recurringTicketBookingBuilder, times(2)).build(any());
		verifyNoInteractions(seasonTicketBookingBuilder);
		verifyNoMoreInteractions(objectFactory, ticketFactory);
	}
}