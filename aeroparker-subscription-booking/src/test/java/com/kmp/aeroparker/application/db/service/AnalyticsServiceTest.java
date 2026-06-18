package com.kmp.aeroparker.application.db.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.AnalyticsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateAnalyticsNew;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionTracking;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest
{
	@Mock
	private AnalyticsDao dao;
	@Mock
	private SubscriptionTracking subTracking;
	@InjectMocks
	private AnalyticsService service;

	private final List<AffiliateAnalyticsNew> analytics = new ArrayList<>();
	private final AffiliateAnalyticsNew analytic = new AffiliateAnalyticsNew();
	private final AffiliateAnalyticsNew analytic2 = new AffiliateAnalyticsNew();

	@Before
	public void setUp() throws Exception
	{
		when(dao.fetchByAffiliateId(anyInt())).thenReturn(analytics);
		analytic.setLocation(1);
		analytic2.setLocation(2);

	}

	@Test
	public void testGetAnalyticsByAffiliateId_InvalidId()
	{
		assertTrue(service.getAnalyticsByAffiliateId(0)
				.isEmpty());
	}

	@Test
	public void testGetAnalyticsByAffiliateId_SeparatedSteps() throws SQLException
	{
		when(dao.fetchByAffiliateId(anyInt())).thenReturn(analytics);
		analytic.setLocation(1);
		analytic2.setLocation(2);
		analytic.setSteps("1,2,3,4,5");
		analytic2.setSteps("1,2,5");
		analytics.add(analytic);
		analytics.add(analytic2);
		assertEquals("1,2,3,4,5", service.getAnalyticsByAffiliateId(17)
				.get(0)
				.getSteps());
		assertEquals("1,2,5", service.getAnalyticsByAffiliateId(17)
				.get(1)
				.getSteps());
	}

	@Test
	public void testSaveSubscriptionTracking()
	{
		when(dao.saveSubscriptionTracking(any())).thenReturn(true);
		assertTrue(service.saveSubscriptionTracking(subTracking));
	}

	@Test
	public void testSaveSubscriptionTracking_NullTracking()
	{
		assertFalse(service.saveSubscriptionTracking(null));
	}
}