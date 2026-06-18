package com.kmp.aeroparker.application.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.CarParkService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Carparks;

@ExtendWith(MockitoExtension.class)
public class BookingCarParkProcessorTest
{
	@Mock
	private CarParkService carParkService;
	@InjectMocks
	private BookingCarParkProcessor processor;
	@Mock
	private Carparks carPark;
	
	private List<Carparks> bookingCarParks;
	
	@BeforeEach
	public void setUp()
	{
		bookingCarParks = new ArrayList<>();
		
		bookingCarParks.add(carPark);
	}
	
	@Test
	public void testProcess()
	{
		when(carParkService.fetchAllCarParksLinkedToBookingProduct(anyInt())).thenReturn(bookingCarParks);
		when(carParkService.saveSubscriptionBookingCarPark(any())).thenReturn(true);
		
		assertTrue(processor.process(10, 2));
	}
	
	@Test
	public void testProcess_NoProductCarParks()
	{
		when(carParkService.fetchAllCarParksLinkedToBookingProduct(anyInt())).thenReturn(Collections.emptyList());
		
		assertFalse(processor.process(10, 2));
		
		verify(carParkService, never()).saveSubscriptionBookingCarPark(any());
	}
	
	@Test
	public void testProcess_SubscriptionBookingCarParkNotSaved()
	{
		when(carParkService.fetchAllCarParksLinkedToBookingProduct(anyInt())).thenReturn(bookingCarParks);
		when(carParkService.saveSubscriptionBookingCarPark(any())).thenReturn(false);
		
		assertFalse(processor.process(10, 2));
	}

	@Test
	public void testGetType()
	{
		assertEquals(ProcessorType.CAR_PARK, processor.getType());
	}
}
