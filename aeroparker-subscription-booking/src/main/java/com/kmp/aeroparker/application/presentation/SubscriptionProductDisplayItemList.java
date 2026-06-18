package com.kmp.aeroparker.application.presentation;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionProductDisplayItemList extends ArrayList<SubscriptionProductDisplayItem>
{
	private static final long serialVersionUID = 1L;

	public void setSelectedProducts(final List<Integer> ids)
	{
		for (SubscriptionProductDisplayItem displayItem : this)
		{
			if (ids.contains(displayItem.getProductId()))
			{
				displayItem.setSelected(true);
			}
		}
	}
}