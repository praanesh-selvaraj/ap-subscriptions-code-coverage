package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.CarParkDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Carparks;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCarPark;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class CarParkServiceTest
{
	@Mock
	private CarParkDao dao;

	@InjectMocks
	private CarParkService service;

	@Test
	void testFetchCarParkById()
	{
		when(dao.fetchCarParkById(anyInt())).thenReturn(EnhancedRandom.random(Carparks.class));
		assertThat(service.fetchCarParkById(1)).isNotNull()
				.isInstanceOf(Carparks.class);
	}

	@Test
	void testFetchCarParkById_Empty_Id()
	{
		assertThat(service.fetchCarParkById(0)).isNull();
		verifyNoInteractions(dao);
	}
	
	@Test
	void testFetchCarParkIdByNameAndSiteId()
	{
		when(dao.fetchCarParkIdByNameAndSiteId(anyString(), anyInt())).thenReturn(1);
		assertThat(service.fetchCarParkIdByNameAndSiteId("Name", 1)).isNotNull();
	}
	
	@Test
	void testFetchCarParkIdByNameAndSiteId_EmptyName()
	{
		assertEquals(service.fetchCarParkIdByNameAndSiteId("", 1).intValue(), 0);
	}
	
	@Test
	void testFetchCarParkIdByNameAndSiteId_EmptyId()
	{
		assertEquals(service.fetchCarParkIdByNameAndSiteId("Name", 0).intValue(), 0);
	}

	@Test
	public void testFetchAllBookingCarParks()
	{
		List<Carparks> bookingCarParks = new ArrayList<>();
		
		bookingCarParks.add(mock(Carparks.class));
		
		when(dao.fetchAllBookingCarParks(anyString())).thenReturn(bookingCarParks);
		
		assertEquals(1, service.fetchAllBookingCarParks("test").size());
	}
	
	@Test
	public void testFetchAllBookingCarParks_NullReference()
	{
		assertTrue(service.fetchAllBookingCarParks(null).isEmpty());
		
		verifyNoInteractions(dao);
	}
	
	@Test
	public void testFetchAllCarParksLinkedToBookingProduct()
	{
		List<Carparks> subscriptionCarParks = new ArrayList<>();
		
		subscriptionCarParks.add(mock(Carparks.class));
		
		when(dao.fetchAllCarParksLinkedToBookingProduct(anyInt())).thenReturn(subscriptionCarParks);
		
		assertEquals(1, service.fetchAllCarParksLinkedToBookingProduct(1).size());
	}
	
	@Test
	public void testFetchAllCarParksLinkedToBookingProduct_InvalidProductId()
	{
		assertTrue(service.fetchAllCarParksLinkedToBookingProduct(0).isEmpty());
	}

	@Test
	public void testSaveSubscriptionBookingCarPark()
	{
		when(dao.saveSubscriptionBookingCarPark(any())).thenReturn(true);
		
		assertTrue(service.saveSubscriptionBookingCarPark(mock(SubscriptionBookingCarPark.class)));
	}
	
	@Test
	public void testSaveSubscriptionBookingCarPark_NullSubscriptionBookingCarPark()
	{
		assertFalse(service.saveSubscriptionBookingCarPark(null));
		
		verifyNoInteractions(dao);
	}
}
