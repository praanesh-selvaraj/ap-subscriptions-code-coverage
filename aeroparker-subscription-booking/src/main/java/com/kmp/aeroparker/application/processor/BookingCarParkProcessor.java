package com.kmp.aeroparker.application.processor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.CarParkService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Carparks;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCarPark;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BookingCarParkProcessor implements IProcessor
{
	private final CarParkService carParkService;
	
	public boolean process(int bookingId, int productId)
	{
		List<Carparks> bookingCarParks = carParkService
					.fetchAllCarParksLinkedToBookingProduct(productId);
		boolean saved = !bookingCarParks.isEmpty();
		
		for (Carparks carPark : bookingCarParks)
		{
			saved &= carParkService
					.saveSubscriptionBookingCarPark(buildSubscriptionBookingCarPark(bookingId, carPark.getId()));
		}
		
		return saved;
	}
	
	private SubscriptionBookingCarPark buildSubscriptionBookingCarPark(int bookingId, int carParkId)
	{
		SubscriptionBookingCarPark bookingCarPark = new SubscriptionBookingCarPark();
		bookingCarPark.setSubscriptionBookingId(bookingId);
		bookingCarPark.setCarParkId(carParkId);
		
		return bookingCarPark;
	}
	
	@Override
	public ProcessorType getType()
	{
		return ProcessorType.CAR_PARK;
	}
}
