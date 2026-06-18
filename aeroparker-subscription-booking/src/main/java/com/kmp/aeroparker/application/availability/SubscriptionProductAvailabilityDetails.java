package com.kmp.aeroparker.application.availability;

import java.time.LocalDate;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionProductAvailabilityDetails
{
	private LocalDate startDate;
	private SubscriptionProduct product;
	private SubscriptionProductTerms productTerms;
	private SubscriptionProductAppearance productAppearance;
	private PriceDetails priceDetails;
	private boolean isSeasonTicket;
	private boolean isRecurringTicket;
}