package com.kmp.aeroparker.application.db.dao;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.CarparksDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Carparks;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCarPark;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class CarParkDao
{
	private final DSLContext dsl;

	public Carparks fetchCarParkById(final int carParkId)
	{
		return new CarparksDao(dsl.configuration()).fetchOneById(carParkId);
	}
	
	public Integer fetchCarParkIdByNameAndSiteId(final String name, final int siteId)
	{
		return dsl.select(Tables.AB_CARPARKS.ID)
				.from(Tables.AB_CARPARKS)
				.where(Tables.AB_CARPARKS.NAME.eq(name)
						.and(Tables.AB_CARPARKS.SITEID.eq(siteId)))
				.fetchOneInto(Integer.class);
	}
	
	public List<Carparks> fetchAllBookingCarParks(String reference)
	{
		return dsl.select(Tables.AB_CARPARKS.fields())
				.from(Tables.AB_CARPARKS)
				.join(Tables.SUBSCRIPTION_BOOKING_CAR_PARK)
				.on(Tables.SUBSCRIPTION_BOOKING_CAR_PARK.CAR_PARK_ID
						.eq(Tables.AB_CARPARKS.ID))
				.join(Tables.SUBSCRIPTION_BOOKING)
				.on(Tables.SUBSCRIPTION_BOOKING_CAR_PARK.SUBSCRIPTION_BOOKING_ID
						.eq(Tables.SUBSCRIPTION_BOOKING.ID))
				.where(Tables.SUBSCRIPTION_BOOKING.REFERENCE.eq(reference))
				.fetchInto(Carparks.class);
	}
	
	public List<Carparks> fetchAllCarParksLinkedToBookingProduct(int productId)
	{
		return dsl.select(Tables.AB_CARPARKS.fields())
				.from(Tables.AB_CARPARKS)
				.join(Tables.SUBSCRIPTION_PRODUCT_CAR_PARK)
				.on(Tables.SUBSCRIPTION_PRODUCT_CAR_PARK.CAR_PARK_ID.eq(Tables.AB_CARPARKS.ID))
				.where(Tables.SUBSCRIPTION_PRODUCT_CAR_PARK.SUBSCRIPTION_PRODUCT_ID.eq(productId))
				.fetchInto(Carparks.class);
	}
	
	public boolean saveSubscriptionBookingCarPark(SubscriptionBookingCarPark subscriptionBookingCarPark)
	{
		return subscriptionBookingCarPark.save(Tables.SUBSCRIPTION_BOOKING_CAR_PARK, dsl);
	}
}