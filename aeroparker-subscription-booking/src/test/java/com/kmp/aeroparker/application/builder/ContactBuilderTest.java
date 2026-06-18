package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class ContactBuilderTest
{
	private final ContactBuilder builder = new ContactBuilder();

	@Test
	void testBuild()
	{
		SubscriptionBookingData bookingData = EnhancedRandom.random(SubscriptionBookingData.class);
		bookingData.setEmailOptIn(true);
		bookingData.setSmsOptIn(true);
		Contacts contacts = new Contacts();
		builder.build(contacts, bookingData);
		assertThat(contacts).hasFieldOrPropertyWithValue("siteId", bookingData.getSiteId())
				.hasFieldOrPropertyWithValue("county", bookingData.getCounty())
				.hasFieldOrPropertyWithValue("emailAddress", bookingData.getEmail());
	}

	@Test
	void testBuild_OptIns_False()
	{
		SubscriptionBookingData bookingData = EnhancedRandom.random(SubscriptionBookingData.class);
		bookingData.setEmailOptIn(false);
		bookingData.setSmsOptIn(false);
		Contacts contacts = new Contacts();
		builder.build(contacts, bookingData);
		assertThat(contacts).hasFieldOrPropertyWithValue("siteId", bookingData.getSiteId())
				.hasFieldOrPropertyWithValue("county", bookingData.getCounty())
				.hasFieldOrPropertyWithValue("emailAddress", bookingData.getEmail());
	}
}