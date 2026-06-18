package com.kmp.aeroparker.application.db.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.QuotaDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.QuotasSubscription;

@ExtendWith(MockitoExtension.class)
public class QuotaServiceTest
{
	@Mock
	private QuotaDao dao;
	@InjectMocks
	private QuotaService service;

	@Test
	public void testFetchActiveQuotasSubscriptionBySiteId()
	{
		when(dao.fetchActiveQuotasSubscriptionBySiteId(anyInt())).thenReturn(Arrays.asList(new QuotasSubscription()));

		assertEquals(1, service.fetchActiveQuotasSubscriptionBySiteId(1)
				.size());

		verify(dao).fetchActiveQuotasSubscriptionBySiteId(anyInt());
	}
	
	@Test
	public void testFetchActiveQuotasSubscriptionBySiteId_ZeroSiteId()
	{
		assertEquals(0, service.fetchActiveQuotasSubscriptionBySiteId(0)
				.size());

		verifyNoInteractions(dao);
	}

	@Test
	public void testFetchQuotasSubscriptionProductProductIdsByQuotaId()
	{
		when(dao.fetchQuotasSubscriptionProductProductIdsByQuotaId(anyInt())).thenReturn(Arrays.asList(1));

		assertEquals(1, service.fetchQuotasSubscriptionProductProductIdsByQuotaId(1)
				.size());

		verify(dao).fetchQuotasSubscriptionProductProductIdsByQuotaId(anyInt());
	}

	@Test
	public void testFetchQuotasSubscriptionProductProductIdsByQuotaId_ZeroQuotaId()
	{
		assertEquals(0, service.fetchQuotasSubscriptionProductProductIdsByQuotaId(0)
				.size());

		verifyNoInteractions(dao);
	}

	@Test
	public void testFetchSubscriptionProductIdBySiteId()
	{
		when(dao.fetchSubscriptionProductIdBySiteId(anyInt())).thenReturn(Arrays.asList(1));

		assertEquals(1, service.fetchSubscriptionProductIdBySiteId(1)
				.size());

		verify(dao).fetchSubscriptionProductIdBySiteId(anyInt());
	}

	@Test
	public void testFetchSubscriptionProductIdBySiteId_ZeroSiteId()
	{
		assertEquals(0, service.fetchSubscriptionProductIdBySiteId(0)
				.size());

		verifyNoInteractions(dao);
	}

	@Test
	public void testfetchQuotaOccupancy()
	{
		when(dao.fetchQuotaOccupancy(any(), any(), any())).thenReturn(1);

		assertEquals(1,
				service.fetchQuotaOccupancy(Date.valueOf("2025-05-1"), Date.valueOf("2025-05-2"), Arrays.asList(1))
						.intValue());

		verify(dao).fetchQuotaOccupancy(any(), any(), any());
	}

	@Test
	public void testfetchQuotaOccupancy_NullStartDate()
	{
		assertEquals(0, service.fetchQuotaOccupancy(null, Date.valueOf("2025-05-2"), Arrays.asList(1))
				.intValue());

		verifyNoInteractions(dao);
	}

	@Test
	public void testfetchQuotaOccupancy_NullEndDate()
	{
		assertEquals(0, service.fetchQuotaOccupancy(Date.valueOf("2025-05-1"), null, Arrays.asList(1))
				.intValue());

		verifyNoInteractions(dao);
	}

	@Test
	public void testfetchQuotaOccupancy_EmptyProductIds()
	{
		assertEquals(0,
				service.fetchQuotaOccupancy(Date.valueOf("2025-05-1"), Date.valueOf("2025-05-2"), new ArrayList<>())
						.intValue());

		verifyNoInteractions(dao);
	}
}
