package com.kmp.aeroparker.application.presentation;

import java.util.ArrayList;
import java.util.List;

import com.kmp.aeroparker.application.availability.PriceDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SubscriptionProductDisplayItem
{
	private SubscriptionProduct product;
	private SubscriptionProductTerms productTerms;
	private SubscriptionProductAppearance productAppearance;
	private PriceDetails priceDetails;
	private List<String> bulletPoints = new ArrayList<>();
	private int productId;
	private boolean selected;
	private String unescapedMoreInfo;
	private boolean isSeasonTicket;
	private boolean isRecurringTicket;
	// create methods inside this class to do EASY stuff, like get display name
}