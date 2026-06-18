package com.kmp.aeroparker.application.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;

@ExtendWith(MockitoExtension.class)
class ScopesMapManagerConfirmationTest
{
	@Mock
	private SubscriptionBookingItem subscriptionItem;
	@Mock
	private IBookingTicket interfaceTicket;
	@Mock
	private SubscriptionBooking subBooking;
	@InjectMocks
	private ScopesMapManagerConfirmation confirmationScopes;

	@SuppressWarnings("unchecked")
	@Test
	public void testGetScopes()
	{
		Map<SubscriptionBookingItem, IBookingTicket> subItemsMap = new HashMap<>();
		subItemsMap.put(subscriptionItem, interfaceTicket);
		when(subBooking.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(subBooking.getReference()).thenReturn("Subscription Ref");
		when(subscriptionItem.getProductId()).thenReturn(3);
		when(subscriptionItem.getProductDisplayName()).thenReturn("Subscription Gold");
		Map<String, Object> actualList = ScopesMapManagerConfirmation.getScopes(subBooking, subItemsMap);
		assertEquals(4, actualList.size());
		assertFalse(((List<Map<String, Object>>) actualList.get("subscriptionProducts")).isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetScopes_NoBookingItems()
	{
		Map<SubscriptionBookingItem, IBookingTicket> subItemsMap = new HashMap<>();
		when(subBooking.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(subBooking.getReference()).thenReturn("Subscription Ref");
		Map<String, Object> actualList = ScopesMapManagerConfirmation.getScopes(subBooking, subItemsMap);
		assertEquals(4, actualList.size());
		assertTrue(((List<Map<String, Object>>) actualList.get("subscriptionProducts")).isEmpty());
	}
}
