package com.kmp.aeroparker.application.builder;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuid;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuidBooking;

@Component
public class SubscriptionGuidBuilder
{
	public SubscriptionGuidBooking buildGuidBooking(final int bookingId, final int guidId, final int purchaseDataId)
	{
		SubscriptionGuidBooking subscriptionGuidBooking = new SubscriptionGuidBooking();
		subscriptionGuidBooking.setSubscriptionBookingId(bookingId);
		subscriptionGuidBooking.setSubscriptionGuidId(guidId);
		subscriptionGuidBooking.setSubscriptionPurchaseDataId(purchaseDataId);
		return subscriptionGuidBooking;
	}

	public SubscriptionGuid build(final String guid)
	{
		SubscriptionGuid subscriptionGuid = new SubscriptionGuid();
		subscriptionGuid.setGuid(guid);
		return subscriptionGuid;
	}
}