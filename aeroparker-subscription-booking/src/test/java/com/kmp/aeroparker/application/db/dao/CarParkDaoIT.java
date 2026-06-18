package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCarPark;
@Testcontainers
@JooqTest
@ContextConfiguration(classes = CarParkDao.class)
@ExtendWith(SpringExtension.class)
class CarParkDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@Autowired
	private CarParkDao dao;

	@Test
	void testFetchCarParkById()
	{
		assertThat(dao.fetchCarParkById(1)).isNotNull()
				.hasFieldOrPropertyWithValue("name", "Long Stay");
	}
	
	void testFetchCarParkIdByNameAndSiteId()
	{
		assertEquals(dao.fetchCarParkIdByNameAndSiteId("Long Stay", 17).intValue(), 1);
	}
	
	@Test
	void testFetchAllBookingCarParks()
	{
		assertEquals(1, dao.fetchAllBookingCarParks("SNWSC100150").size());
	}
	
	@Test
	void testFetchAllCarParksLinkedToBookingProduct()
	{
		assertEquals(1, dao.fetchAllCarParksLinkedToBookingProduct(1).size());
	}
	
	@Test
	void testSaveSubscriptionBookingCarPark()
	{
		SubscriptionBookingCarPark subscriptionBookingCarPark = new SubscriptionBookingCarPark();
		subscriptionBookingCarPark.setCarParkId(10);
		subscriptionBookingCarPark.setSubscriptionBookingId(2);
		
		assertTrue(dao.saveSubscriptionBookingCarPark(subscriptionBookingCarPark));
	}
}