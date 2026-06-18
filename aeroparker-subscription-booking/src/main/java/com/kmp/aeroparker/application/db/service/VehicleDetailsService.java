package com.kmp.aeroparker.application.db.service;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.VehicleDetailsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class VehicleDetailsService
{
	private final VehicleDetailsDao dao;

	public boolean saveSubscriptionBookingVehicleDetails(final SubscriptionBookingVehicleDetails vehicleDetails)
	{
		boolean saved = false;
		if (vehicleDetails != null)
		{
			saved = dao.saveSubscriptionBookingVehicleDetails(vehicleDetails);
		}
		else
		{
			log.debug("Vehicle Details are null, Vehicle Details will not be saved");
		}
		return saved;
	}

	public SubscriptionBookingVehicleDetails fetchSubscriptionBookingVehicleDetailsByBookingId(final int subBookingId)
	{
		SubscriptionBookingVehicleDetails subscriptionBookingVehicleDetails = null;
		if (subBookingId > 0)
		{
			subscriptionBookingVehicleDetails = dao.fetchSubscriptionBookingVehicleDetailsByBookingId(subBookingId);
			log.debug("subscription booking vehicle fetched successfully");
		}
		else
		{
			log.debug("Subscription booking ID not valid, subscription booking vehicle will not be fetched");
		}
		return subscriptionBookingVehicleDetails;
	}
}