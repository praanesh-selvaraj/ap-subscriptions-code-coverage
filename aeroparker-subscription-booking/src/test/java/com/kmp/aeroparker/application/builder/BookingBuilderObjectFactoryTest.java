package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

import io.github.benas.randombeans.api.EnhancedRandom;

class BookingBuilderObjectFactoryTest
{
	BookingBuilderObjectFactory factory = new BookingBuilderObjectFactory();

	@Test
	void testBuildBookingItemList()
	{
		assertThat(factory.buildBookingItemList(new Timestamp(System.currentTimeMillis()), EnhancedRandom.random(SubscriptionPurchaseRequest.class)))
				.isNotNull()
				.isInstanceOf(SubscriptionBookingItem.class);
	}

	@Test
	void testBuildCustomerDetails()
	{
		assertThat(factory.buildCustomerDetails(EnhancedRandom.random(SubscriptionBookingData.class))).isNotNull()
				.isInstanceOf(SubscriptionBookingCustomerDetails.class);
	}

	@Test
	void testBuildBooking()
	{
		assertThat(factory.buildBooking(1, Timestamp.valueOf(LocalDateTime.now()), 155, EnhancedRandom.random(Basket.class), "Reference")).isNotNull()
				.isInstanceOf(SubscriptionBooking.class);
	}
	
	@Test
	void testBuildVehicleDetails()
	{
		assertThat(factory.buildVehicleDetails(EnhancedRandom.random(SubscriptionBookingData.class))).isNotNull().isInstanceOf(SubscriptionBookingVehicleDetails.class);
	}
	
	@Test
	void testBuildReceiptDetails()
	{
		assertThat(factory.buildReceiptDetails(EnhancedRandom.random(SubscriptionBookingData.class))).isNotNull().isInstanceOf(SubscriptionBookingReceiptDetails.class);
	}

	@Test
	void testBuildLanguage()
	{
		assertThat(factory.buildLanguage(EnhancedRandom.random(SubscriptionBookingData.class))).isNotNull().isInstanceOf(SubscriptionBookingLanguage.class);
	}

	@Test
	void testBuildCustomValues()
	{
		assertThat(factory.buildCustomValues(EnhancedRandom.random(SubscriptionBookingData.class))).isNotNull().isInstanceOf(List.class);
	}
}