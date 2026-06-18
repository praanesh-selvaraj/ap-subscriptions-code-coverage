package com.kmp.aeroparker.application.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.kmp.aeroparker.application.builder.ConfirmationBuilder;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.ConfirmationDetails;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.application.processor.SubscriptionAnalyticsProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class ConfirmationControllerTest
{

	@Mock
	private BookingService service;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private ConfirmationBuilder builder;
	@Mock
	private SubscriptionService subscriptionService;
	@InjectMocks
	private ConfirmationController controller;
	@Mock
	private Sites site;
	@Mock
	private Languages currentLaguages;
	@Mock
	private Affiliates affiliates;
	@Mock
	private SubscriptionAnalyticsProcessor analyticsProcessor;
	@Mock
	private Model model;

	@Test
	void testConfirmation()
	{
		when(service.fetchSubscriptionBooking(anyString(), anyInt())).thenReturn(mock(SubscriptionBookingRecord.class));
		when(builder.build(any(), any(), any())).thenReturn(mock(ConfirmationDetails.class));
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getCurrentLanguage()).thenReturn(currentLaguages);
		controller.confirmation(model, "guid");
		verify(builder).build(any(), any(), any());
		verify(service).fetchSubscriptionBooking(anyString(), anyInt());
		verify(service).fetchEncryptedReferenceByBookingId(anyInt());
		verifyNoMoreInteractions(service, builder);
	}

	@Test
	void testConfirmation_EnableAccountOnDetailsStep()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		when(service.fetchSubscriptionBooking(anyString(), anyInt())).thenReturn(bookingRecord);
		when(builder.build(any(), any(), any())).thenReturn(mock(ConfirmationDetails.class));
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.enableAccountOnDetailsStep()).thenReturn(true);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getCurrentLanguage()).thenReturn(currentLaguages);
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = EnhancedRandom.random(SubscriptionBookingItem.class);
		BookingSeasonTicket bookingSeasonTicket = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(bookingItem, bookingSeasonTicket);
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		SubscriptionProductAppearance appearance = mock(SubscriptionProductAppearance.class);
		when(appearance.getConfirmationMessage()).thenReturn("confirmation message");
		when(subscriptionService.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt()))
				.thenReturn(appearance);
		controller.confirmation(model, "guid");
		verify(builder).build(any(), any(), any());
		verify(service).fetchSubscriptionBooking(anyString(), anyInt());
		verify(subscriptionService).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(service).fetchEncryptedReferenceByBookingId(anyInt());
		verifyNoMoreInteractions(service, builder, subscriptionService);
	}

	@Test
	void testConfirmation_EnableAccountOnDetailsStep_Default_Language()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		when(service.fetchSubscriptionBooking(anyString(), anyInt())).thenReturn(bookingRecord);
		when(builder.build(any(), any(), any())).thenReturn(mock(ConfirmationDetails.class));
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.enableAccountOnDetailsStep()).thenReturn(true);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getCurrentLanguage()).thenReturn(currentLaguages);
		when(requestBean.getDefaultLanguageId()).thenReturn(10);
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = EnhancedRandom.random(SubscriptionBookingItem.class);
		BookingSeasonTicket bookingSeasonTicket = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(bookingItem, bookingSeasonTicket);
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		SubscriptionProductAppearance appearance = mock(SubscriptionProductAppearance.class);
		when(appearance.getConfirmationMessage()).thenReturn("confirmation message");
		when(subscriptionService.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt()))
				.thenReturn(null)
				.thenReturn(appearance);
		controller.confirmation(model, "guid");
		verify(builder).build(any(), any(), any());
		verify(service).fetchSubscriptionBooking(anyString(), anyInt());
		verify(subscriptionService, times(2)).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(),
				anyInt());
		verify(service).fetchEncryptedReferenceByBookingId(anyInt());
		verifyNoMoreInteractions(service, builder, subscriptionService);
	}

	@Test
	void testConfirmation_SubscriptionBookingRecord_Null()
	{
		when(service.fetchSubscriptionBooking(anyString(), anyInt())).thenReturn(null);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getCurrentLanguage()).thenReturn(currentLaguages);
		controller.confirmation(model, "guid");
		verify(service).fetchSubscriptionBooking(anyString(), anyInt());
		verify(subscriptionService, times(0)).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(),
				anyInt());
		verifyNoMoreInteractions(service);
		verifyNoInteractions(builder, subscriptionService);
	}

	@Test
	void testRenewalConfirmation()
	{
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		
		when(service.fetchBookingByEncryptedReference(anyString())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(service.fetchSubscriptionBooking(anyInt(), anyInt())).thenReturn(mock(SubscriptionBookingRecord.class));
		when(builder.build(any(), any(), any())).thenReturn(mock(ConfirmationDetails.class));
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getCurrentLanguage()).thenReturn(currentLaguages);
		
		controller.renewalConfirmation(model, "encRef");
		
		verify(builder).build(any(), any(), any());
		verify(service).fetchBookingByEncryptedReference(anyString());
		verify(service).fetchSubscriptionBooking(anyInt(), anyInt());
		verifyNoMoreInteractions(service, builder);
	}

	@Test
	void testRenewalConfirmation_NullBookingRecord()
	{
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		
		when(service.fetchBookingByEncryptedReference(anyString())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getCurrentLanguage()).thenReturn(currentLaguages);
		
		controller.renewalConfirmation(model, "encRef");
		
		verify(service).fetchBookingByEncryptedReference(anyString());
		verify(service).fetchSubscriptionBooking(anyInt(), anyInt());
		verifyNoInteractions(builder, subscriptionService);
	}
}