package com.kmp.aeroparker.application.facade;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.AnalyticsService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateAnalyticsNew;

@ExtendWith(MockitoExtension.class)
class AffiliateAnalyticsFacadeTest
{
	@Mock
	private AnalyticsService service;
	@InjectMocks
	private AffiliateAnalyticsFacade analyticsFacade;

	private final List<AffiliateAnalyticsNew> analytics = new ArrayList<>();
	private final AffiliateAnalyticsNew analytic = new AffiliateAnalyticsNew();
	private final AffiliateAnalyticsNew analytic2 = new AffiliateAnalyticsNew();

	@Test
	public void testGetEnabledAnalyticsByAffiliateId() throws SQLException
	{
		analytic.setLocation(1);
		analytic2.setLocation(2);
		analytic.setSteps("1,2,3,4,5");
		analytic2.setSteps("1,2,5,17");
		analytic.setEnabled(1);
		analytic2.setEnabled(1);
		analytics.add(analytic);
		analytics.add(analytic2);
		AffiliateAnalyticsNew analytic3 = new AffiliateAnalyticsNew();
		analytic3.setSteps("35");
		analytic3.setLocation(3);
		analytic3.setEnabled(1);
		analytics.add(analytic3);
		when(service.getAnalyticsByAffiliateId(anyInt())).thenReturn(analytics);
		assertFalse(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(5, 17)
				.isEmpty());
	}

	@Test
	public void testGetEnabledAnalyticsByAffiliateId_NoEnabledAnalytics() throws SQLException
	{
		analytic.setLocation(1);
		analytic2.setLocation(2);
		analytic.setSteps("1,2,3,4,5");
		analytic2.setSteps("1,2,5,17");
		analytic.setEnabled(0);
		analytic2.setEnabled(0);
		analytics.add(analytic);
		analytics.add(analytic2);
		when(service.getAnalyticsByAffiliateId(anyInt())).thenReturn(analytics);
		assertTrue(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(5, 17)
				.isEmpty());
	}

	@Test
	public void testGetEnabledAnalyticsByAffiliateId_NoEnabledSteps() throws SQLException
	{
		analytic.setLocation(1);
		analytic2.setLocation(2);
		analytic.setSteps("1,2,3,4,5");
		analytic2.setSteps("1,2,5,17");
		analytic.setEnabled(1);
		analytic2.setEnabled(1);
		analytics.add(analytic);
		analytics.add(analytic2);
		when(service.getAnalyticsByAffiliateId(anyInt())).thenReturn(analytics);
		assertTrue(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(5, 35)
				.isEmpty());
	}

	@Test
	public void testGetAnalyticsByAffiliateId_Steps() throws SQLException
	{
		analytic.setLocation(1);
		analytic2.setLocation(2);
		analytic.setSteps("12345");
		analytic2.setSteps("125");
		analytic.setEnabled(1);
		analytic2.setEnabled(1);
		analytics.add(analytic);
		analytics.add(analytic2);
		when(service.getAnalyticsByAffiliateId(anyInt())).thenReturn(analytics);
		assertEquals("1,2,3,4,5", analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(5, 5)
				.get(0)
				.getSteps());
		assertEquals("1,2,5", analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(5, 5)
				.get(1)
				.getSteps());
	}

	@Test
	public void testGetAnalyticsByAffiliateId_Ordering() throws SQLException
	{
		analytic.setLocation(1);
		analytic2.setLocation(2);
		analytic.setSteps("1,2,3,4,5");
		analytic2.setSteps("1,2,5");
		analytic.setEnabled(1);
		analytic2.setEnabled(1);
		analytics.add(analytic2);
		analytics.add(analytic);
		when(service.getAnalyticsByAffiliateId(anyInt())).thenReturn(analytics);
		assertEquals(analytic, analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(5, 5)
				.get(0));
	}

	@Test
	public void testGetAnalyticsByAffiliateId_SortingKnownDoubleDigitStep() throws SQLException
	{
		analytic.setLocation(1);
		analytic2.setLocation(2);
		analytic.setSteps("39");
		analytic2.setSteps("39");
		analytic.setEnabled(1);
		analytic2.setEnabled(1);
		analytics.add(analytic2);
		analytics.add(analytic);
		when(service.getAnalyticsByAffiliateId(anyInt())).thenReturn(analytics);
		assertEquals(analytic, analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(5, 39)
				.get(0));
	}

	@Test
	public void testGetEnabledAnalyticsByAffiliateId_EmptyAnalytics() throws SQLException
	{
		when(service.getAnalyticsByAffiliateId(anyInt())).thenReturn(analytics);
		assertTrue(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(5, 30)
				.isEmpty());
	}

	@Test
	public void testGetEnabledAnalyticsByAffiliateId_InvalidAffiliate() throws SQLException
	{
		assertTrue(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(0, 30)
				.isEmpty());
	}

	@Test
	public void testGetEnabledAnalyticsByAffiliateId_InvalidStep() throws SQLException
	{
		assertTrue(analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(5, 0)
				.isEmpty());
	}
}
