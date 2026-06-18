package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SubscriptionDao.class)
class SubscriptionDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@Autowired
	private SubscriptionDao dao;

	@Test
	void testFetchSubscriptionProductBySiteId()
	{
		assertThat(dao.fetchSubscriptionProductBySiteId(17)).hasSize(5);
	}

	@Test
	void testFetchAffiliateSubscriptionProductsIds()
	{
		assertThat(dao.fetchAffiliateSubscriptionProductsIds(103)).hasSize(2);
	}

	@Test
	void testFetchSubscriptionProductAppearanceBySubProductIdAndLangId()
	{
		assertThat(dao.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(9, 1)).isNotNull()
				.hasFieldOrPropertyWithValue("displayName", "Sub1")
				.hasFieldOrPropertyWithValue("colour", "#ff0000");
	}

	@Test
	void testFetchSubscriptionProductTermsBySubProductId()
	{
		assertThat(dao.fetchSubscriptionProductTermsBySubProductId(3)).isNotNull()
				.hasFieldOrPropertyWithValue("periodType", "FIXED")
				.hasFieldOrPropertyWithValue("minimumTerm", "ONE_MONTH");
	}

	@Test
	void testFetchSubscriptionProductById()
	{
		assertThat(dao.fetchSubscriptionProductById(13)).isNotNull()
				.hasFieldOrPropertyWithValue("name", "Sub2");
	}

	@Test
	void testFetchSubscriptionProductByIds()
	{
		assertThat(dao.fetchSubscriptionProductByIds(new Integer[] { 13, 3, 9 })).hasSize(3)
				.extracting("name")
				.contains("test1 NEW", "Sub2", "Sub1");
	}
	
	@Test
	void testfetchPaymentStepFieldsSubscriptionProductByProductId()
	{
		assertTrue(dao.fetchPaymentStepFieldsSubscriptionProductByProductId(98).size() == 2);
	}
	
	@Test
	void testFetchSubscriptionDetailsByReference()
	{
		assertNotNull(dao.fetchSubscriptionBookingDetailsByRef("SNWSC100150"));
	}
}