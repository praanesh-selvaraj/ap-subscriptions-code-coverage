package com.kmp.aeroparker.application.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetails;
import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetailsList;
import com.kmp.aeroparker.application.availability.SubscriptionProductFinder;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionProductDisplayItemBuilderTest
{
	@Mock
	private SubscriptionProductDisplayItemFactory displayItemFactory;
	@Mock
	private SubscriptionProductFinder productFinder;
	@InjectMocks
	private SubscriptionProductDisplayItemBuilder builder;
	@Mock
	private SubscriptionBookingQuery bookingDetails;
	@Mock
	private Sites site;
	@Mock
	private Affiliates affiliates;
	@Mock
	private Languages curreLanguages;

	@Test
	void testBuild()
	{
		when(bookingDetails.getAffiliate()).thenReturn(affiliates);
		when(bookingDetails.getSite()).thenReturn(site);
		when(bookingDetails.getLanguage()).thenReturn(curreLanguages);
		when(bookingDetails.getDefaultLanguage()).thenReturn(curreLanguages);
		when(affiliates.getId()).thenReturn(1);
		when(bookingDetails.getStartDate()).thenReturn("27/08/2019");
		Locations locations = mock(Locations.class);
		when(locations.getDateFormat()).thenReturn("dd/MM/yyyy");
		when(bookingDetails.getLocation()).thenReturn(locations);
		SubscriptionProductAvailabilityDetails availabilityDetails = EnhancedRandom.random(SubscriptionProductAvailabilityDetails.class);
		SubscriptionProductAvailabilityDetailsList availabilityDetailsList = new SubscriptionProductAvailabilityDetailsList();
		availabilityDetailsList.add(availabilityDetails);
		when(productFinder.getAllAvailableProducts(anyInt(), anyInt(), anyInt(), anyInt(), any())).thenReturn(availabilityDetailsList);
		SubscriptionProductDisplayItemList displayItemList = new SubscriptionProductDisplayItemList();
		SubscriptionProductDisplayItem displayItem = EnhancedRandom.random(SeasonTicketDisplayItem.class);
		displayItemList.add(displayItem);
		when(displayItemFactory.build(any())).thenReturn(displayItemList);
		assertThat(builder.build(bookingDetails)).isNotEmpty()
				.hasSize(1);
	}

	@Test
	void testBuild_No_Product_Available()
	{
		when(bookingDetails.getAffiliate()).thenReturn(affiliates);
		when(bookingDetails.getSite()).thenReturn(site);
		when(bookingDetails.getLanguage()).thenReturn(curreLanguages);
		when(bookingDetails.getDefaultLanguage()).thenReturn(curreLanguages);
		when(affiliates.getId()).thenReturn(1);
		when(bookingDetails.getStartDate()).thenReturn("27/08/2019");
		Locations locations = mock(Locations.class);
		when(locations.getDateFormat()).thenReturn("dd/MM/yyyy");
		when(bookingDetails.getLocation()).thenReturn(locations);
		SubscriptionProductAvailabilityDetailsList availabilityDetailsList = new SubscriptionProductAvailabilityDetailsList();
		when(productFinder.getAllAvailableProducts(anyInt(), anyInt(), anyInt(), anyInt(), any())).thenReturn(availabilityDetailsList);
		assertThat(builder.build(bookingDetails)).isEmpty();
		verifyNoInteractions(displayItemFactory);
	}

	@Test
	void testGetType()
	{
		assertThat(builder.getType()).isEqualTo(BuilderType.PRODUCT_DISPLAY_ITEM);
	}
}