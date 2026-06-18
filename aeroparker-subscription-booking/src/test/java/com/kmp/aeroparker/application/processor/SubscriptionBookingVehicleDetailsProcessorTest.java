package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.VehicleDetailsService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionBookingVehicleDetailsProcessorTest
{
	@Mock
	VehicleDetailsService service;
	@InjectMocks
	private SubscriptionBookingVehicleDetailsProcessor processor;

	@Test
	void testProcess()
	{
		SubscriptionBookingVehicleDetails vehicleDetails =
				EnhancedRandom.random(SubscriptionBookingVehicleDetails.class);
		when(service.saveSubscriptionBookingVehicleDetails(vehicleDetails)).thenReturn(true);
		assertThat(processor.process(1, vehicleDetails)).isTrue();
		verify(service).saveSubscriptionBookingVehicleDetails(vehicleDetails);
		verifyNoMoreInteractions(service);
	}

	@Test
	void testProcess_Null()
	{
		NullPointerException thrown = assertThrows(NullPointerException.class, () -> processor.process(1, null));
		assertThat(thrown.getMessage()).isNotNull();
		verifyNoMoreInteractions(service);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(ProcessorType.VEHICLE_DETAILS);
	}
}
