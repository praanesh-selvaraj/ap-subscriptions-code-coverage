package com.kmp.aeroparker.application.db.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionBookingVehicleDetailsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class VehicleDetailsDao
{
	private final DSLContext dsl;

	public boolean saveSubscriptionBookingVehicleDetails(final SubscriptionBookingVehicleDetails vehicleDetails)
	{
		return vehicleDetails.save(Tables.SUBSCRIPTION_BOOKING_VEHICLE_DETAILS, dsl);
	}

	public SubscriptionBookingVehicleDetails fetchSubscriptionBookingVehicleDetailsByBookingId(final int subBookingId)
	{
		return new SubscriptionBookingVehicleDetailsDao(dsl.configuration()).fetchBySubBookingId(subBookingId)
				.stream()
				.findFirst()
				.orElse(null);
	}
}