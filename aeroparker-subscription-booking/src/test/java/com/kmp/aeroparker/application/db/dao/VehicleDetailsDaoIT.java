package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { VehicleDetailsDao.class })
class VehicleDetailsDaoIT
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@Autowired
	private VehicleDetailsDao dao;

	@Test
	void testSaveSubscriptionBookingVehicleDetails()
	{
		SubscriptionBookingVehicleDetails vehicleDetails = new SubscriptionBookingVehicleDetails();
		vehicleDetails.setSubBookingId(63);
		vehicleDetails.setCarRegistration("Testing123");
		dao.saveSubscriptionBookingVehicleDetails(vehicleDetails);
		assertThat(dao.saveSubscriptionBookingVehicleDetails(vehicleDetails)).isTrue();
	}

	@Test
	void testFetchSubscriptionBookingVehicleDetailsByBookingId()
	{
		assertThat(dao.fetchSubscriptionBookingVehicleDetailsByBookingId(211)).isNotNull()
				.hasFieldOrPropertyWithValue("carRegistration", "A-B11-CD");
	}
}