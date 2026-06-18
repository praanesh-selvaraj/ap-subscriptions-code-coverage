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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.builder.SubscriptionGuidBuilder;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuid;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuidBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPurchaseData;

@ExtendWith(MockitoExtension.class)
class SubscriptionGuidProcessorTest
{
	@Mock
	private SubscriptionGuidBuilder builder;
	@Mock
	private BookingService service;
	@InjectMocks
	private SubscriptionGuidProcessor processor;
	@Mock
	private SubscriptionGuidBooking subscriptionGuidBooking;
	@Mock
	private SubscriptionGuid subscriptionGuid;

	@Test
	void testProcess()
	{
		when(builder.build(any(String.class))).thenReturn(subscriptionGuid);
		when(subscriptionGuid.getId()).thenReturn(1);
		when(service.saveSubscriptionGuid(any())).thenReturn(true);
		when(builder.buildGuidBooking(any(Integer.class), any(Integer.class), anyInt())).thenReturn(subscriptionGuidBooking);
		when(service.saveSubscriptionGuidBooking(any())).thenReturn(true);
		SubscriptionPurchaseData purchaseData = mock(SubscriptionPurchaseData.class);
		when(purchaseData.getId()).thenReturn(1);
		when(service.fetchPurchaseData(anyInt(), anyString())).thenReturn(purchaseData);
		processor.process(1, 1, "guid", "customer_guid");
		verify(service).saveSubscriptionGuid(any());
		verify(service).saveSubscriptionGuidBooking(any());
		verify(service).fetchPurchaseData(anyInt(), anyString());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testProcess_SubscriptionGuidBooking_Not_Saved()
	{
		when(builder.build(any(String.class))).thenReturn(subscriptionGuid);
		when(subscriptionGuid.getId()).thenReturn(1);
		when(service.saveSubscriptionGuid(any())).thenReturn(true);
		when(builder.buildGuidBooking(any(Integer.class), any(Integer.class), anyInt())).thenReturn(subscriptionGuidBooking);
		when(service.saveSubscriptionGuidBooking(any())).thenReturn(false);
		SubscriptionPurchaseData purchaseData = mock(SubscriptionPurchaseData.class);
		when(purchaseData.getId()).thenReturn(1);
		when(service.fetchPurchaseData(anyInt(), anyString())).thenReturn(purchaseData);
		processor.process(1, 1, "guid", "customer_guid");
		verify(service).saveSubscriptionGuid(any());
		verify(service).saveSubscriptionGuidBooking(any());
		verify(service).fetchPurchaseData(anyInt(), anyString());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testProcess_Purchase_Data_Null()
	{
		when(service.fetchPurchaseData(anyInt(), anyString())).thenReturn(null);
		when(service.saveSubscriptionGuid(any())).thenReturn(true);
		processor.process(1, 1, "guid", "customer_guid");
		verify(service).saveSubscriptionGuid(any());
		verify(service, times(0)).saveSubscriptionGuidBooking(any());
		verify(service).fetchPurchaseData(anyInt(), anyString());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testProcess_Not_Saved()
	{
		when(service.saveSubscriptionGuid(any())).thenReturn(false);
		processor.process(1, 1, "guid", "customer_guid");
		verify(service).saveSubscriptionGuid(any());
		verify(service, times(0)).saveSubscriptionGuidBooking(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(ProcessorType.GUID_BOOKING);
	}
}