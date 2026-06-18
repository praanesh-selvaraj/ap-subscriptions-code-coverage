package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.SubscriptionDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptionProducts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest
{
	@Mock
	private SubscriptionDao dao;
	@InjectMocks
	private SubscriptionService service;

	@Test
	void testFetchSubscriptionProductBySiteId()
	{
		when(dao.fetchSubscriptionProductBySiteId(anyInt())).thenReturn(EnhancedRandom.randomListOf(2, SubscriptionProduct.class));
		assertThat(service.fetchSubscriptionProductBySiteId(2)).isNotEmpty()
				.hasOnlyElementsOfType(SubscriptionProduct.class);
	}

	@Test
	void testFetchSubscriptionProductBySiteId_Invalid_SiteId()
	{
		assertThat(service.fetchSubscriptionProductBySiteId(0)).isEmpty();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchAffiliateSubscriptionProductsIds()
	{
		when(dao.fetchAffiliateSubscriptionProductsIds(anyInt())).thenReturn(EnhancedRandom.randomListOf(1, Integer.class));
		assertThat(service.fetchAffiliateSubscriptionProductsIds(1)).isNotEmpty()
				.hasOnlyElementsOfType(Integer.class);
	}

	@Test
	void testFetchAffiliateSubscriptionProductsIds_Invalid_AffId()
	{
		assertThat(service.fetchAffiliateSubscriptionProductsIds(0)).isEmpty();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchSubscriptionProductAppearanceBySubProductIdAndLangId()
	{
		when(dao.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.random(SubscriptionProductAppearance.class));
		assertThat(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(1, 1)).isNotNull()
				.isInstanceOf(SubscriptionProductAppearance.class);
	}

	@Test
	void testFetchSubscriptionProductAppearanceBySubProductIdAndLangId_Invalid_SubProductIdAndLanguageId()
	{
		assertThat(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(0, 1)).isNull();
		assertThat(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(0, 0)).isNull();
		assertThat(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(1, 0)).isNull();
	}

	@Test
	void testFetchSubscriptionProductTermsBySubProductId()
	{
		when(dao.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(EnhancedRandom.random(SubscriptionProductTerms.class));
		assertThat(service.fetchSubscriptionProductTermsBySubProductId(1)).isNotNull()
				.isInstanceOf(SubscriptionProductTerms.class);
	}

	@Test
	void testFetchSubscriptionProductTermsBySubProductId_Invalid_SubProductId()
	{
		assertThat(service.fetchSubscriptionProductTermsBySubProductId(0)).isNull();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchSubscriptionProductById()
	{
		when(dao.fetchSubscriptionProductById(anyInt())).thenReturn(EnhancedRandom.random(SubscriptionProduct.class));
		assertThat(service.fetchSubscriptionProductById(2)).isNotNull()
				.isInstanceOf(SubscriptionProduct.class);
	}

	@Test
	void testFetchSubscriptionProductById_Invalid_Id()
	{
		assertThat(service.fetchSubscriptionProductById(0)).isNull();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchSubscriptionProductByIds()
	{
		when(dao.fetchSubscriptionProductByIds(any())).thenReturn(EnhancedRandom.randomListOf(1, SubscriptionProduct.class));
		assertThat(service.fetchSubscriptionProductByIds(new Integer[] { 1 })).isNotEmpty()
				.hasOnlyElementsOfType(SubscriptionProduct.class);
	}

	@Test
	void testFetchSubscriptionProductById_Empty_Id()
	{
		assertThat(service.fetchSubscriptionProductByIds(new Integer[] {})).isEmpty();
		verifyNoInteractions(dao);
	}
	
	@Test
	void testFetchPaymentStepFieldsSubscriptionProductByProductId()
	{
		List<PaymentStepFieldsSubscriptionProducts> paymentStepFieldsSubscriptionProducts = new ArrayList<>();
		PaymentStepFieldsSubscriptionProducts paymentStepFieldSubscriptionProduct = mock(PaymentStepFieldsSubscriptionProducts.class);
		paymentStepFieldsSubscriptionProducts.add(paymentStepFieldSubscriptionProduct);
		
		when(dao.fetchPaymentStepFieldsSubscriptionProductByProductId(anyInt())).thenReturn(paymentStepFieldsSubscriptionProducts);
		
		assertFalse(service.fetchPaymentStepFieldsSubscriptionProductByProductId(1).isEmpty());
	}
	
	@Test
	void testFetchPaymentStepFieldsSubscriptionProductByProductId_InvalidProductId()
	{
		assertTrue(service.fetchPaymentStepFieldsSubscriptionProductByProductId(0).isEmpty());
	}
	
	@Test
	void testFetchSubscriptionBookingDetailsByRef()
	{
		when(dao.fetchSubscriptionBookingDetailsByRef(anyString())).thenReturn(mock(SubscriptionBookingDetails.class));

		assertNotNull(service.fetchSubscriptionBookingDetailsByRef("testref"));
	}

	@Test
	void testFetchSubscriptionBookingDetailsByRef_Null_Email()
	{
		assertNull(service.fetchSubscriptionBookingDetailsByRef(null));
	}
}