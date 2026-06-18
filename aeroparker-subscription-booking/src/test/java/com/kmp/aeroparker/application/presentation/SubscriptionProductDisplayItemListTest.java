package com.kmp.aeroparker.application.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionProductDisplayItemListTest
{
	private final SubscriptionProductDisplayItemList displayItemList = new SubscriptionProductDisplayItemList();

	@Test
	void testSetSelectedProducts()
	{
		SubscriptionProductDisplayItem displayItem = mock(SubscriptionProductDisplayItem.class);
		when(displayItem.getProductId()).thenReturn(1);
		displayItemList.add(displayItem);
		List<Integer> ids = Arrays.asList(1, 2, 3);
		SubscriptionProductDisplayItem displayItem1 = mock(SubscriptionProductDisplayItem.class);
		when(displayItem1.getProductId()).thenReturn(8);
		displayItemList.add(displayItem1);
		displayItemList.setSelectedProducts(ids);
		assertThat(displayItemList).filteredOn("selected", true);
	}
}