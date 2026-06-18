package com.kmp.aeroparker.application.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.AnalyticsService;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.PurchaseRequestList;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.facade.AffiliateAnalyticsFacade;
import com.kmp.aeroparker.application.model.AnalyticsLocations;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.application.utils.MustacheUtil;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateAnalyticsNew;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;

@ExtendWith(MockitoExtension.class)
class SubscriptionAnalyticsProcessorTest
{
	@Mock
	private SubscriptionBookingItem subscriptionItem;
	@Mock
	private IBookingTicket interfaceTicket;
	@Mock
	private SubscriptionBooking subBooking;
	@Mock
	private SubscriptionBookingRecord subRecord;
	@Mock
	private AffiliateAnalyticsNew affiliateAnalytics;
	@Mock
	private AnalyticsService analyticsService;
	@Mock
	private AffiliateAnalyticsFacade analyticsFacade;
	@Mock
	private Model model;
	@InjectMocks
	private SubscriptionAnalyticsProcessor analyticsProcessor;

	@Test
	public void testProcessSubscriptionAnalytics()
	{
		Map<SubscriptionBookingItem, IBookingTicket> subItemsMap = new HashMap<>();
		subItemsMap.put(subscriptionItem, interfaceTicket);
		List<AffiliateAnalyticsNew> analyticsList = new ArrayList<>();
		analyticsList.add(affiliateAnalytics);
		when(subRecord.getBooking()).thenReturn(subBooking);
		when(subRecord.getBookingItemMap()).thenReturn(subItemsMap);
		when(subBooking.getAffiliateId()).thenReturn(3);
		when(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(anyInt(), anyInt())).thenReturn(analyticsList);
		try (MockedStatic<MustacheUtil> mocked = mockStatic(MustacheUtil.class))
		{
			mocked.when(() -> MustacheUtil.compileMustacheString(any(), any()))
					.thenReturn("Mustache Test");
			analyticsProcessor.processSubscriptionAnalytics(model, subRecord, "EUR");
			mocked.verify(() -> MustacheUtil.compileMustacheString(any(), any()), times(4));
			verify(analyticsService, times(1)).saveSubscriptionTracking(any());
		}
	}

	@Test
	public void testProcessSubscriptionAnalytics_NullAnalytics()
	{
		Map<SubscriptionBookingItem, IBookingTicket> subItemsMap = new HashMap<>();
		subItemsMap.put(subscriptionItem, interfaceTicket);
		when(subRecord.getBooking()).thenReturn(subBooking);
		when(subRecord.getBookingItemMap()).thenReturn(subItemsMap);
		when(subBooking.getAffiliateId()).thenReturn(3);
		try (MockedStatic<MustacheUtil> mocked = mockStatic(MustacheUtil.class))
		{
			analyticsProcessor.processSubscriptionAnalytics(model, subRecord, "EUR");
			mocked.verify(() -> MustacheUtil.compileMustacheString(any(), any()), never());
		}
	}

	@Test
	public void testProcessSubscriptionAnalytics_EmptyAnalytics()
	{
		Map<SubscriptionBookingItem, IBookingTicket> subItemsMap = new HashMap<>();
		subItemsMap.put(subscriptionItem, interfaceTicket);
		when(subRecord.getBooking()).thenReturn(subBooking);
		when(subRecord.getBookingItemMap()).thenReturn(subItemsMap);
		when(subBooking.getAffiliateId()).thenReturn(3);
		when(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(anyInt(), anyInt()))
				.thenReturn(Collections.emptyList());
		try (MockedStatic<MustacheUtil> mocked = mockStatic(MustacheUtil.class))
		{
			analyticsProcessor.processSubscriptionAnalytics(model, subRecord, "EUR");
			mocked.verify(() -> MustacheUtil.compileMustacheString(any(), any()), never());
		}
	}

	@Test
	public void testProcessSubscriptionAnalytics_NoProducts()
	{
		List<AffiliateAnalyticsNew> analyticsList = new ArrayList<>();
		analyticsList.add(affiliateAnalytics);
		when(subRecord.getBooking()).thenReturn(subBooking);
		when(subBooking.getAffiliateId()).thenReturn(3);
		when(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(anyInt(), anyInt())).thenReturn(analyticsList);
		try (MockedStatic<MustacheUtil> mocked = mockStatic(MustacheUtil.class))
		{
			mocked.when(() -> MustacheUtil.compileMustacheString(any(), any()))
					.thenReturn("Mustache Test");
			analyticsProcessor.processSubscriptionAnalytics(model, subRecord, "EUR");
			mocked.verify(() -> MustacheUtil.compileMustacheString(any(), any()), times(4));
			verifyNoMoreInteractions(analyticsService);
		}
	}

	@Test
	public void testProcessSubscriptionAnalytics_PaymentDetails()
	{
		List<AffiliateAnalyticsNew> analyticsList = new ArrayList<>();
		analyticsList.add(affiliateAnalytics);
		when(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(anyInt(), anyInt())).thenReturn(analyticsList);
		Basket basket = new Basket();
		PurchaseRequestList list = new PurchaseRequestList();
		SubscriptionPurchaseRequest request = mock(SubscriptionPurchaseRequest.class);
		when(request.getProductId()).thenReturn(10);
		SubscriptionProduct product = mock(SubscriptionProduct.class);
		when(product.getName()).thenReturn("Season Product");
		when(request.getProduct()).thenReturn(product);
		when(request.getGrandTotal()).thenReturn(BigDecimal.valueOf(20));
		when(request.isSeasonTicket()).thenReturn(true);
		list.add(request);
		basket.setPurchaseRequestList(list);
		try (MockedStatic<MustacheUtil> mocked = mockStatic(MustacheUtil.class))
		{
			mocked.when(() -> MustacheUtil.compileMustacheString(any(), any()))
					.thenReturn("Mustache Test");
			analyticsProcessor.processSubscriptionAnalytics(model, basket, "EUR",
					AnalyticsLocations.SUBSCRIPTION_PAYMENT_DETAIL.getId(), 1);
			mocked.verify(() -> MustacheUtil.compileMustacheString(any(), any()), times(4));
			verifyNoInteractions(analyticsService);
		}
	}

	@Test
	public void testProcessSubscriptionAnalytics_NullPaymentDetailsAnalytics()
	{
		Basket basket = new Basket();
		try (MockedStatic<MustacheUtil> mocked = mockStatic(MustacheUtil.class))
		{
			analyticsProcessor.processSubscriptionAnalytics(model, basket, "EUR",
					AnalyticsLocations.SUBSCRIPTION_PAYMENT_DETAIL.getId(), 1);
			mocked.verify(() -> MustacheUtil.compileMustacheString(any(), any()), never());
			verifyNoInteractions(analyticsService);
		}
	}

	@Test
	public void testProcessSubscriptionAnalytics_NoBasketPurchaseRequests()
	{
		List<AffiliateAnalyticsNew> analyticsList = new ArrayList<>();
		analyticsList.add(affiliateAnalytics);
		when(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(anyInt(), anyInt())).thenReturn(analyticsList);
		Basket basket = new Basket();
		PurchaseRequestList list = new PurchaseRequestList();
		basket.setPurchaseRequestList(list);
		try (MockedStatic<MustacheUtil> mocked = mockStatic(MustacheUtil.class))
		{
			mocked.when(() -> MustacheUtil.compileMustacheString(any(), any()))
					.thenReturn("Mustache Test");
			analyticsProcessor.processSubscriptionAnalytics(model, basket, "EUR",
					AnalyticsLocations.SUBSCRIPTION_PAYMENT_DETAIL.getId(), 1);
			mocked.verify(() -> MustacheUtil.compileMustacheString(any(), any()), times(4));
			verifyNoInteractions(analyticsService);
		}
	}
	
	@Test
	public void testProcessSubscriptionAnalytics_InvalidProducts()
	{
		List<AffiliateAnalyticsNew> analyticsList = new ArrayList<>();
		analyticsList.add(affiliateAnalytics);
		when(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(anyInt(), anyInt())).thenReturn(analyticsList);
		Basket basket = new Basket();
		
		SubscriptionPurchaseRequest requestInvalidId = mock(SubscriptionPurchaseRequest.class);
		when(requestInvalidId.getProductId()).thenReturn(-1);
		when(requestInvalidId.getGrandTotal()).thenReturn(BigDecimal.valueOf(15));
		
		SubscriptionPurchaseRequest requestInvalidName = mock(SubscriptionPurchaseRequest.class);
		when(requestInvalidName.getProductId()).thenReturn(12);
		SubscriptionProduct productInvlidName = mock(SubscriptionProduct.class);
		when(productInvlidName.getName()).thenReturn("");
		when(requestInvalidName.getProduct()).thenReturn(productInvlidName);
		when(requestInvalidName.getGrandTotal()).thenReturn(BigDecimal.valueOf(15));
		
		PurchaseRequestList list = new PurchaseRequestList();
		list.add(requestInvalidId);
		list.add(requestInvalidName);
		basket.setPurchaseRequestList(list);
		
		try (MockedStatic<MustacheUtil> mocked = mockStatic(MustacheUtil.class))
		{
			mocked.when(() -> MustacheUtil.compileMustacheString(any(), any()))
					.thenReturn("Mustache Test");
			analyticsProcessor.processSubscriptionAnalytics(model, basket, "EUR",
					AnalyticsLocations.SUBSCRIPTION_PAYMENT_DETAIL.getId(), 1);
			mocked.verify(() -> MustacheUtil.compileMustacheString(any(), any()), times(4));
			verifyNoInteractions(analyticsService);
		}
	}
}
