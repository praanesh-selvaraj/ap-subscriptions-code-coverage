package com.kmp.aeroparker.application.model;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionBookingQuery
{
	private String startDate;
	private Affiliates affiliate;
	private Sites site;
	private Languages language;
	private int selectedProductId;
	private Locations location;
	private String timeZone;
	private boolean isValid;
	private Languages defaultLanguage;
}