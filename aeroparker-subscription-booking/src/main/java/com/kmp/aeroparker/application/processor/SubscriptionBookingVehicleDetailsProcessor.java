package com.kmp.aeroparker.application.processor;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.VehicleDetailsService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@Component
public class SubscriptionBookingVehicleDetailsProcessor implements IProcessor
{
	private final VehicleDetailsService service;

	public boolean process(final int bookingId, @NonNull final SubscriptionBookingVehicleDetails vehicleDetails)
	{
		vehicleDetails.setSubBookingId(bookingId);
		return service.saveSubscriptionBookingVehicleDetails(vehicleDetails);
	}

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.VEHICLE_DETAILS;
	}
}
