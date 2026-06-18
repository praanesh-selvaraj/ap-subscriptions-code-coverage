package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.VehicleDetailsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class VehicleDetailsServiceTest
{
	@Mock
	private VehicleDetailsDao dao;
	@InjectMocks
	private VehicleDetailsService service;
	
	@Test
	void testSaveSubscriptionBookingVehicleDetails()
	{
		when(dao.saveSubscriptionBookingVehicleDetails(any())).thenReturn(true);
		assertThat(service.saveSubscriptionBookingVehicleDetails(mock(SubscriptionBookingVehicleDetails.class))).isTrue();
		verify(dao).saveSubscriptionBookingVehicleDetails(any());
		verifyNoMoreInteractions(dao);
	}
	
	@Test
	void testSaveSubscriptionBookingVehicleDetails_null()
	{
		assertThat(service.saveSubscriptionBookingVehicleDetails(null));
	}

	@Test
	void testFetchSubscriptionBookingVehicleDetailsByBookingId()
	{
		when(dao.fetchSubscriptionBookingVehicleDetailsByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.random(SubscriptionBookingVehicleDetails.class));
		assertThat(service.fetchSubscriptionBookingVehicleDetailsByBookingId(1)).isNotNull()
				.isInstanceOf(SubscriptionBookingVehicleDetails.class);
		verify(dao).fetchSubscriptionBookingVehicleDetailsByBookingId(anyInt());
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFetchSubscriptionBookingVehicleDetailsByBookingId_Id_Zero()
	{
		assertThat(service.fetchSubscriptionBookingVehicleDetailsByBookingId(0)).isNull();
		verifyNoInteractions(dao);
	}
}