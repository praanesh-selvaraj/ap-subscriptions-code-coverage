package com.kmp.aeroparker.application.db.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.CarParkDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Carparks;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCarPark;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CarParkService
{
	private final CarParkDao dao;

	public Carparks fetchCarParkById(final int carParkId)
	{
		Carparks carpark = null;
		if (carParkId < 1)
		{
			log.info("ID is not greater than 0, car park will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch Car park by id: {}", carParkId);
			carpark = dao.fetchCarParkById(carParkId);
			log.debug("Car park fetched: {}", carpark);
		}
		return carpark;
	}
	
	public Integer fetchCarParkIdByNameAndSiteId(final String name, final int siteId)
	{
		if (!StringUtil.isEmpty(name) && siteId > 0)
		{
			log.debug("Fetching car park id, for carpark: {} with site id: {}", name, siteId);
			return dao.fetchCarParkIdByNameAndSiteId(name, siteId);
		}
		else
		{
			log.info("Unable to fetch car park id, as name or site id was invalid");
		}
		return 0;
	}
	
	public List<Carparks> fetchAllBookingCarParks(String reference)
	{
		List<Carparks> bookingCarParks = new ArrayList<>();
		
		if (!StringUtil.hasText(reference))
		{
			log.debug("Failed to fetch subscription booking car parks.");
		}
		else
		{
			bookingCarParks = dao.fetchAllBookingCarParks(reference);
		}
		
		return bookingCarParks;
	}
	
	public List<Carparks> fetchAllCarParksLinkedToBookingProduct(int productId)
	{
		List<Carparks> subscriptionCarParks = new ArrayList<>();
		
		if (productId < 1)
		{
			log.debug("Failed to fetch subscription product car parks because product id was 0 or less than 0.");
		}
		else
		{
			subscriptionCarParks = dao.fetchAllCarParksLinkedToBookingProduct(productId);
		}
		
		return subscriptionCarParks;
	}
	
	public boolean saveSubscriptionBookingCarPark(SubscriptionBookingCarPark subscriptionBookingCarPark)
	{
		boolean success = false;
		if (subscriptionBookingCarPark == null)
		{
			log.debug("Failed to save subscription booking car park because given object is null.");
		}
		else
		{
			success = dao.saveSubscriptionBookingCarPark(subscriptionBookingCarPark);
		}
		
		return success;
	}
}