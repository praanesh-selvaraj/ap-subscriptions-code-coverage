package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.SiteDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Currencies;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionMembershipSequence;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SiteServiceTest
{
	@Mock
	private SiteDao dao;
	@InjectMocks
	private SiteService service;

	@Test
	void testFetchSiteById()
	{
		when(dao.fetchSiteById(anyInt())).thenReturn(mock(Sites.class));
		assertThat(service.fetchSiteById(1)).isNotNull()
				.isInstanceOf(Sites.class);
	}

	@Test
	void testFetchSiteById_Incorrect_Id()
	{
		assertThat(service.fetchSiteById(0)).isNull();
	}

	@Test
	void testFetchPaymentStepFieldsSubscriptionBySiteId()
	{
		when(dao.fetchPaymentStepFieldsSubscriptionBySiteId(anyInt())).thenReturn(EnhancedRandom.randomListOf(1, PaymentStepFieldsSubscriptions.class));
		assertThat(service.fetchPaymentStepFieldsSubscriptionBySiteId(1)).isNotEmpty()
				.hasOnlyElementsOfTypes(PaymentStepFieldsSubscriptions.class);
	}

	@Test
	void testfetchPaymentStepFieldsIncludedBySiteId_Incorrect_Id()
	{
		assertThat(service.fetchPaymentStepFieldsSubscriptionBySiteId(0)).isEmpty();
	}

	@Test
	void testFetchSiteCurrencyBySiteId()
	{
		when(dao.fetchSiteCurrencyBySiteId(anyInt())).thenReturn(mock(Currencies.class));
		assertThat(service.fetchSiteCurrencyBySiteId(1)).isNotNull()
				.isInstanceOf(Currencies.class);
	}

	@Test
	void testFetchSiteCurrencyBySiteId_Incorrect_Id()
	{
		assertThat(service.fetchSiteCurrencyBySiteId(0)).isNull();
	}

	@Test
	void testFetchLocationById()
	{
		when(dao.fetchLocationById(anyInt())).thenReturn(mock(Locations.class));
		assertThat(service.fetchLocationById(1)).isNotNull()
				.isInstanceOf(Locations.class);
	}

	@Test
	void testFetchLocationById_Incorrect_Id()
	{
		assertThat(service.fetchLocationById(0)).isNull();
	}

	@Test
	void testUpdateSubscriptionMembershipSequence()
	{
		SubscriptionMembershipSequence subscriptionMembershipSequence = new SubscriptionMembershipSequence();
		when(dao.updateSubscriptionMembershipSequence(subscriptionMembershipSequence)).thenReturn(true);
		
		assertTrue(service.updateSubscriptionMembershipSequence(subscriptionMembershipSequence));
	}

	@Test
	void testUpdateSubscriptionMembershipSequence_SubscriptionMembershipSequence_Null()
	{		
		assertFalse(service.updateSubscriptionMembershipSequence(null));
	}

	@Test
	void testFetchSubscriptionMembershipSequenceBySiteId()
	{
		SubscriptionMembershipSequence subscriptionMembershipSequence = new SubscriptionMembershipSequence();
		when(dao.fetchSubscriptionMembershipSequenceBySiteId(1)).thenReturn(subscriptionMembershipSequence);
		
		assertEquals(subscriptionMembershipSequence, service.fetchSubscriptionMembershipSequenceBySiteId(1));
	}

	@Test
	void testFetchSubscriptionMembershipSequenceBySiteId_SiteId_Invalid()
	{	
		assertNull(service.fetchSubscriptionMembershipSequenceBySiteId(0));
	}

	@Test
	void TestGetNextMembershipSequence()
	{
		SubscriptionMembershipSequence subscriptionMembershipSequence = new SubscriptionMembershipSequence();
		subscriptionMembershipSequence.setSiteId(1);
		subscriptionMembershipSequence.setValue(2);
		when(dao.fetchSubscriptionMembershipSequenceBySiteId(1)).thenReturn(subscriptionMembershipSequence);
		when(dao.updateSubscriptionMembershipSequence(subscriptionMembershipSequence)).thenReturn(true);
		
		assertEquals(3, service.getNextMembershipSequence(1));
	}

	@Test
	void TestGetNextMembershipSequence_SiteId_Invalid()
	{
		assertEquals(0, service.getNextMembershipSequence(0));
	}

	@Test
	void TestGetNextMembershipSequence_NoMembershipSequence()
	{
		when(dao.fetchSubscriptionMembershipSequenceBySiteId(1)).thenReturn(null);
		when(dao.updateSubscriptionMembershipSequence(any(SubscriptionMembershipSequence.class))).thenReturn(true);

		assertEquals(1, service.getNextMembershipSequence(1));
	}

	@Test
	void TestGetNextMembershipSequence_UpdateFailed()
	{
		SubscriptionMembershipSequence subscriptionMembershipSequence = new SubscriptionMembershipSequence();
		subscriptionMembershipSequence.setSiteId(1);
		subscriptionMembershipSequence.setValue(2);
		when(dao.fetchSubscriptionMembershipSequenceBySiteId(1)).thenReturn(subscriptionMembershipSequence);
		when(dao.updateSubscriptionMembershipSequence(subscriptionMembershipSequence)).thenReturn(false);
		
		assertEquals(0, service.getNextMembershipSequence(1));
	}
}