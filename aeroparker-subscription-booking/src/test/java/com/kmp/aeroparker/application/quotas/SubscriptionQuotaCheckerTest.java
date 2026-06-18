package com.kmp.aeroparker.application.quotas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.QuotaService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.QuotasSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;

@ExtendWith(MockitoExtension.class)
public class SubscriptionQuotaCheckerTest
{
	@Mock
	private QuotaService quotaService;
	@Mock
	private SubscriptionService subscriptionService;
	@InjectMocks
	private SubscriptionQuotaChecker checker;

	@Mock
	private QuotasSubscription quota;
	@Mock
	private SubscriptionProductTerms productTerms;

	private List<QuotasSubscription> quotas = new ArrayList<>();

	@BeforeEach
	public void init()
	{
		quotas.add(quota);
	}

	@Test
	public void testCheckProductOccupancy()
	{
		when(quotaService.fetchActiveQuotasSubscriptionBySiteId(anyInt())).thenReturn(quotas);
		when(subscriptionService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		when(productTerms.getMinimumTerm()).thenReturn("ONE_MONTH");
		when(quota.getAllProductsEnabled()).thenReturn(false);
		when(quota.getMaximumOccupancyValue()).thenReturn(2);
		when(quotaService.fetchQuotasSubscriptionProductProductIdsByQuotaId(anyInt())).thenReturn(Arrays.asList(1));
		when(quotaService.fetchQuotaOccupancy(any(), any(), any())).thenReturn(1);

		assertTrue(checker.checkProductOccupancy(1, LocalDate.now(), 1));

		verify(quotaService).fetchActiveQuotasSubscriptionBySiteId(anyInt());
		verify(subscriptionService).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(quotaService).fetchQuotasSubscriptionProductProductIdsByQuotaId(anyInt());
		verify(quotaService).fetchQuotaOccupancy(any(), any(), any());
	}

	@Test
	public void testCheckProductOccupancy_OccupancyReached()
	{
		when(quotaService.fetchActiveQuotasSubscriptionBySiteId(anyInt())).thenReturn(quotas);
		when(subscriptionService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		when(productTerms.getMinimumTerm()).thenReturn("ONE_MONTH");
		when(quota.getAllProductsEnabled()).thenReturn(false);
		when(quota.getMaximumOccupancyValue()).thenReturn(1);
		when(quotaService.fetchQuotasSubscriptionProductProductIdsByQuotaId(anyInt())).thenReturn(Arrays.asList(1));
		when(quotaService.fetchQuotaOccupancy(any(), any(), any())).thenReturn(2);

		assertFalse(checker.checkProductOccupancy(1, LocalDate.now(), 1));

		verify(quotaService).fetchActiveQuotasSubscriptionBySiteId(anyInt());
		verify(subscriptionService).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(quotaService).fetchQuotasSubscriptionProductProductIdsByQuotaId(anyInt());
		verify(quotaService).fetchQuotaOccupancy(any(), any(), any());
	}

	@Test
	public void testCheckProductOccupancy_AllProductsEnabled()
	{
		when(quotaService.fetchActiveQuotasSubscriptionBySiteId(anyInt())).thenReturn(quotas);
		when(subscriptionService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		when(productTerms.getMinimumTerm()).thenReturn("ONE_MONTH");
		when(quota.getAllProductsEnabled()).thenReturn(true);
		when(quota.getMaximumOccupancyValue()).thenReturn(2);
		when(quotaService.fetchSubscriptionProductIdBySiteId(anyInt())).thenReturn(Arrays.asList(1));
		when(quotaService.fetchQuotaOccupancy(any(), any(), any())).thenReturn(1);

		assertTrue(checker.checkProductOccupancy(1, LocalDate.now(), 1));

		verify(quotaService).fetchActiveQuotasSubscriptionBySiteId(anyInt());
		verify(subscriptionService).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(quotaService).fetchSubscriptionProductIdBySiteId(anyInt());
		verify(quotaService).fetchQuotaOccupancy(any(), any(), any());
	}

	@Test
	public void testCheckProductOccupancy_EmptyQuotas()
	{
		when(subscriptionService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);

		assertTrue(checker.checkProductOccupancy(1, LocalDate.now(), 1));

		verify(quotaService).fetchActiveQuotasSubscriptionBySiteId(anyInt());
		verify(subscriptionService).fetchSubscriptionProductTermsBySubProductId(anyInt());
	}

	@Test
	public void testCheckProductOccupancy_NullProductTerms()
	{
		when(quotaService.fetchActiveQuotasSubscriptionBySiteId(anyInt())).thenReturn(quotas);
		when(subscriptionService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(null);

		assertFalse(checker.checkProductOccupancy(1, LocalDate.now(), 1));

		verify(quotaService).fetchActiveQuotasSubscriptionBySiteId(anyInt());
		verify(subscriptionService).fetchSubscriptionProductTermsBySubProductId(anyInt());
	}

	@Test
	public void testCheckProductOccupancy_EmptyProductIds()
	{
		when(quotaService.fetchActiveQuotasSubscriptionBySiteId(anyInt())).thenReturn(quotas);
		when(subscriptionService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		when(quota.getAllProductsEnabled()).thenReturn(false);

		assertTrue(checker.checkProductOccupancy(1, LocalDate.now(), 1));

		verify(quotaService).fetchActiveQuotasSubscriptionBySiteId(anyInt());
		verify(subscriptionService).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(quotaService).fetchQuotasSubscriptionProductProductIdsByQuotaId(anyInt());
		verify(quotaService, times(0)).fetchQuotaOccupancy(any(), any(), any());
	}
}
