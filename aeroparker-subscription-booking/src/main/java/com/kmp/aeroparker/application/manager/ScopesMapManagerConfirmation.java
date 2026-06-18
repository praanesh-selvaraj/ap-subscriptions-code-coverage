package com.kmp.aeroparker.application.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;

public class ScopesMapManagerConfirmation
{
	public static Map<String, Object> getScopes(SubscriptionBooking subBooking,
			Map<SubscriptionBookingItem, IBookingTicket> subProducts)
	{
		Map<String, Object> scopes = new HashMap<>();
		scopes.put("reference", subBooking.getReference());
		scopes.put("revenue", subBooking.getGrandTotal());
		scopes.put("tax", subBooking.getVatAmount());

		// Add map of products to scope ready for Mustache to compile
		List<Map<String, Object>> products = new ArrayList<>();
		for (Map.Entry<SubscriptionBookingItem, IBookingTicket> entry : subProducts.entrySet())
		{
			Map<String, Object> productMap = new HashMap<>();
			SubscriptionBookingItem item = entry.getKey();
			productMap.put("productId", item.getProductId());
			productMap.put("productName", item.getProductDisplayName());
			productMap.put("productPrice", item.getSubTotal());
			products.add(productMap);
		}
		scopes.put("subscriptionProducts", products);
		return scopes;
	}
}
