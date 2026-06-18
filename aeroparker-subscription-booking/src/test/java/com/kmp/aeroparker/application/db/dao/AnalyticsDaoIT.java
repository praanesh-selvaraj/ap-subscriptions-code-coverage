package com.kmp.aeroparker.application.db.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionTracking;

import io.github.benas.randombeans.api.EnhancedRandom;

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AnalyticsDao.class })
class AnalyticsDaoIT
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getAnalyticsInstance();
	@Autowired
	private AnalyticsDao dao;

	@Test
	public void testFetchByAffiliateId()
	{
		assertNotNull(dao.fetchByAffiliateId(211));
	}

	@Test
	public void testFetchByAffiliateId_NonExisting()
	{
		assertNull(dao.fetchByAffiliateId(18));
	}

	@Test
	private void testSaveSubscriptionTracking()
	{
		SubscriptionTracking subscriptionTracking = EnhancedRandom.random(SubscriptionTracking.class, "id");
		subscriptionTracking.setBookingReference("SNWSC1001123");
		subscriptionTracking.setBookingTotal(BigDecimal.TEN);
		subscriptionTracking.setProductId(40812);
		subscriptionTracking.setProductName("Test Product");
		assertTrue(dao.saveSubscriptionTracking(subscriptionTracking));
	}
}
