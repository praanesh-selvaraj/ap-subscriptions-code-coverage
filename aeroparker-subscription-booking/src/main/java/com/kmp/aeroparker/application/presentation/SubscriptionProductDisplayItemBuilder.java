package com.kmp.aeroparker.application.presentation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetailsList;
import com.kmp.aeroparker.application.availability.SubscriptionProductFinder;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.model.interfaces.IBuilder;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SubscriptionProductDisplayItemBuilder implements IBuilder
{
	private final SubscriptionProductDisplayItemFactory displayItemFactory;
	private final SubscriptionProductFinder productFinder;

	public SubscriptionProductDisplayItemList build(final SubscriptionBookingQuery bookingDetails)
	{
		log.info("Building available products");
		Sites site = bookingDetails.getSite();
		Affiliates affiliates = bookingDetails.getAffiliate();
		Languages curreLanguage = bookingDetails.getLanguage();
		Languages defaultLanguage = bookingDetails.getDefaultLanguage();
		int affId = affiliates.getId();
		LocalDate startDate = DateUtil.strToLocalDate(bookingDetails.getStartDate(), bookingDetails.getLocation()
				.getDateFormat());
		SubscriptionProductAvailabilityDetailsList availabilityDetailsList =
				productFinder.getAllAvailableProducts(affId, site.getId(), curreLanguage.getId(), defaultLanguage.getId(), startDate);
		log.debug("Size of available products: {}", availabilityDetailsList.size());

		SubscriptionProductDisplayItemList productDisplayItemList = new SubscriptionProductDisplayItemList();
		if (!availabilityDetailsList.isEmpty())
		{
			productDisplayItemList = displayItemFactory.build(availabilityDetailsList);
		}
		return productDisplayItemList;
	}

	@Override
	public BuilderType getType()
	{
		return BuilderType.PRODUCT_DISPLAY_ITEM;
	}
}