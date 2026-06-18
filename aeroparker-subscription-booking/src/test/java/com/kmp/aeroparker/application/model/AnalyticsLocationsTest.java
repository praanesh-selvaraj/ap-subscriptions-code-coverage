package com.kmp.aeroparker.application.model;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

class AnalyticsLocationsTest
{
	@Test
	public void testAnalyticsLocations_ValidId()
	{
		assertEquals(AnalyticsLocations.SUBSCRIPTION_CONFIRMATION, AnalyticsLocations.fromId(39));
		assertEquals(AnalyticsLocations.SUBSCRIPTION_PAYMENT_DETAIL, AnalyticsLocations.fromId(40));
	}

	@Test
	public void testAnalyticsLocations_ZeroId()
	{
		assertEquals(AnalyticsLocations.UNKNOWN, AnalyticsLocations.fromId(0));
	}

	@Test
	public void testAnalyticsLocations_InvalidId()
	{
		assertEquals(AnalyticsLocations.UNKNOWN, AnalyticsLocations.fromId(-5));
	}
}
