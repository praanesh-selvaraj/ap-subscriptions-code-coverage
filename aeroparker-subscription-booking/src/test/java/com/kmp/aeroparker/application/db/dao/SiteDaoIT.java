package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionMembershipSequence;
@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SiteDao.class)
class SiteDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getSiteInstance();
	@Autowired
	private SiteDao dao;

	@Test
	void testFetchSiteById()
	{
		assertThat(dao.fetchSiteById(2)).isNotNull()
				.hasFieldOrPropertyWithValue("title", "AeroParker")
				.hasFieldOrPropertyWithValue("timezone", "Etc/GMT");
	}

	@Test
	void testFetchPaymentStepFieldsIncludedBySiteId()
	{
		assertThat(dao.fetchPaymentStepFieldsSubscriptionBySiteId(2)).isNotEmpty()
				.extracting("fieldId")
				.contains(2, 3, 8, 9, 12, 17, 18, 19, 24, 25);
	}

	@Test
	void testFetchSiteCurrencyBySiteId()
	{
		assertThat(dao.fetchSiteCurrencyBySiteId(2)).isNotNull()
				.hasFieldOrPropertyWithValue("code", "GBP")
				.hasFieldOrPropertyWithValue("htmlSymbol", "&pound;")
				.hasFieldOrPropertyWithValue("symbol", "&pound;");
	}

	@Test
	void testFetchLocationById()
	{
		assertThat(dao.fetchLocationById(14)).isNotNull()
				.hasFieldOrPropertyWithValue("name", "United Kingdom")
				.hasFieldOrPropertyWithValue("currencySymbol", "&pound;");
	}

	@Test
	void testUpdateSubscriptionMembershipSequence()
	{
		SubscriptionMembershipSequence subscriptionMembershipSequence = new SubscriptionMembershipSequence();
		subscriptionMembershipSequence.setSiteId(2);
		subscriptionMembershipSequence.setValue(123);
		
		assertTrue(dao.updateSubscriptionMembershipSequence(subscriptionMembershipSequence));
	}

	@Test
	void testFetchSubscriptionMembershipSequence()
	{
		assertThat(dao.fetchSubscriptionMembershipSequenceBySiteId(2)).isNotNull()
		.hasFieldOrPropertyWithValue("value", 13);
	}
}