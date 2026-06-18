package com.kmp.aeroparker.application.availability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionProductFinderTest
{
	@Mock
	private SubscriptionService service;
	@InjectMocks
	private SubscriptionProductFinder finder;
	@Mock
	private Sites site;
	@Mock
	private Affiliates affiliates;
	@Mock
	private Languages curreLanguages;
	@Mock
	private SubscriptionBookingQuery bookingDetails;

	@Test
	void testGetAllAvailableProducts()
	{
		List<SubscriptionProduct> subscriptionProductList = new ArrayList<>();
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class, "id");
		subscriptionProduct.setId(1);
		subscriptionProductList.add(subscriptionProduct);
		when(service.fetchSubscriptionProductBySiteId(anyInt())).thenReturn(subscriptionProductList);
		when(service.fetchAffiliateSubscriptionProductsIds(anyInt())).thenReturn(Arrays.asList(1));
		SubscriptionProductAppearance subscriptionProductAppearance = mock(SubscriptionProductAppearance.class);
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt())).thenReturn(subscriptionProductAppearance);
		SubscriptionProductTerms subscriptionProductTerms = mock(SubscriptionProductTerms.class);
		when(subscriptionProductTerms.getPrice()).thenReturn(BigDecimal.TEN);
		when(subscriptionProductTerms.getPeriodType()).thenReturn("FIXED");
		when(service.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(subscriptionProductTerms);
		assertThat(finder.getAllAvailableProducts(1, 1, 1, 1, DateUtil.nowLocalDate("Europe/London"))).isNotNull()
				.hasOnlyElementsOfTypes(SubscriptionProductAvailabilityDetails.class);
		verify(service).fetchSubscriptionProductBySiteId(anyInt());
		verify(service).fetchAffiliateSubscriptionProductsIds(anyInt());
		verify(service).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testGetAllAvailableProducts_Recurring()
	{
		List<SubscriptionProduct> subscriptionProductList = new ArrayList<>();
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class, "id");
		subscriptionProduct.setId(1);
		subscriptionProductList.add(subscriptionProduct);
		when(service.fetchSubscriptionProductBySiteId(anyInt())).thenReturn(subscriptionProductList);
		when(service.fetchAffiliateSubscriptionProductsIds(anyInt())).thenReturn(Arrays.asList(1));
		SubscriptionProductAppearance subscriptionProductAppearance = mock(SubscriptionProductAppearance.class);
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt())).thenReturn(subscriptionProductAppearance);
		SubscriptionProductTerms subscriptionProductTerms = mock(SubscriptionProductTerms.class);
		when(subscriptionProductTerms.getPrice()).thenReturn(BigDecimal.TEN);
		when(subscriptionProductTerms.getPeriodType()).thenReturn("RECURRING");
		when(service.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(subscriptionProductTerms);
		assertThat(finder.getAllAvailableProducts(1, 1, 1, 1, DateUtil.nowLocalDate("Europe/London"))).isNotNull()
				.hasOnlyElementsOfTypes(SubscriptionProductAvailabilityDetails.class);
		verify(service).fetchSubscriptionProductBySiteId(anyInt());
		verify(service).fetchAffiliateSubscriptionProductsIds(anyInt());
		verify(service).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testGetAllAvailableProducts_No_Products()
	{
		when(service.fetchSubscriptionProductBySiteId(anyInt())).thenReturn(Collections.emptyList());
		assertThat(finder.getAllAvailableProducts(1, 1, 1, 1, DateUtil.nowLocalDate("Europe/London"))).isNotNull()
				.hasOnlyElementsOfTypes(SubscriptionProductAvailabilityDetails.class);
		verify(service).fetchSubscriptionProductBySiteId(anyInt());
		verify(service, times(0)).fetchAffiliateSubscriptionProductsIds(anyInt());
		verify(service, times(0)).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(service, times(0)).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBuild_Product_Not_Contained_In_AffilaiteId_List()
	{
		List<SubscriptionProduct> subscriptionProductList = new ArrayList<>();
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class, "id");
		subscriptionProduct.setId(1);
		subscriptionProductList.add(subscriptionProduct);
		when(service.fetchSubscriptionProductBySiteId(anyInt())).thenReturn(subscriptionProductList);
		when(service.fetchAffiliateSubscriptionProductsIds(anyInt())).thenReturn(Arrays.asList(3));
		assertThat(finder.getAllAvailableProducts(1, 1, 1, 1, DateUtil.nowLocalDate("Europe/London"))).isNotNull()
				.hasOnlyElementsOfTypes(SubscriptionProductAvailabilityDetails.class);
		verify(service).fetchSubscriptionProductBySiteId(anyInt());
		verify(service).fetchAffiliateSubscriptionProductsIds(anyInt());
		verify(service, times(0)).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(service, times(0)).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBuild_SubscriptionProductAppearance_Null()
	{
		List<SubscriptionProduct> subscriptionProductList = new ArrayList<>();
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class, "id");
		subscriptionProduct.setId(1);
		subscriptionProductList.add(subscriptionProduct);
		when(service.fetchSubscriptionProductBySiteId(anyInt())).thenReturn(subscriptionProductList);
		when(service.fetchAffiliateSubscriptionProductsIds(anyInt())).thenReturn(Arrays.asList(1));
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt())).thenReturn(null);
		assertThat(finder.getAllAvailableProducts(1, 1, 1, 1, DateUtil.nowLocalDate("Europe/London"))).isNotNull()
				.hasOnlyElementsOfTypes(SubscriptionProductAvailabilityDetails.class);
		verify(service).fetchSubscriptionProductBySiteId(anyInt());
		verify(service).fetchAffiliateSubscriptionProductsIds(anyInt());
		verify(service).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(service, times(0)).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBuild_SubscriptionProductAppearance_Null_Different_Languages()
	{
		List<SubscriptionProduct> subscriptionProductList = new ArrayList<>();
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class, "id");
		subscriptionProduct.setId(1);
		subscriptionProductList.add(subscriptionProduct);
		when(service.fetchSubscriptionProductBySiteId(anyInt())).thenReturn(subscriptionProductList);
		when(service.fetchAffiliateSubscriptionProductsIds(anyInt())).thenReturn(Arrays.asList(1));
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt())).thenReturn(null)
				.thenReturn(mock(SubscriptionProductAppearance.class));
		assertThat(finder.getAllAvailableProducts(1, 1, 3, 1, DateUtil.nowLocalDate("Europe/London"))).isNotNull()
				.hasOnlyElementsOfTypes(SubscriptionProductAvailabilityDetails.class);
		verify(service).fetchSubscriptionProductBySiteId(anyInt());
		verify(service).fetchAffiliateSubscriptionProductsIds(anyInt());
		verify(service, times(2)).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBuild_SubscriptionProductTerms_Null()
	{
		List<SubscriptionProduct> subscriptionProductList = new ArrayList<>();
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class, "id");
		subscriptionProduct.setId(1);
		subscriptionProductList.add(subscriptionProduct);
		when(service.fetchSubscriptionProductBySiteId(anyInt())).thenReturn(subscriptionProductList);
		when(service.fetchAffiliateSubscriptionProductsIds(anyInt())).thenReturn(Arrays.asList(1));
		SubscriptionProductAppearance subscriptionProductAppearance = mock(SubscriptionProductAppearance.class);
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt())).thenReturn(subscriptionProductAppearance);
		when(service.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(null);
		assertThat(finder.getAllAvailableProducts(1, 1, 1, 1, DateUtil.nowLocalDate("Europe/London"))).isNotNull()
				.hasOnlyElementsOfTypes(SubscriptionProductAvailabilityDetails.class);
		verify(service).fetchSubscriptionProductBySiteId(anyInt());
		verify(service).fetchAffiliateSubscriptionProductsIds(anyInt());
		verify(service).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verifyNoMoreInteractions(service);
	}
}