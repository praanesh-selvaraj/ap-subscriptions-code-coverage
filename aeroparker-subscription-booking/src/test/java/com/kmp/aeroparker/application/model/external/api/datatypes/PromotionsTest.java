package com.kmp.aeroparker.application.model.external.api.datatypes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.benas.randombeans.api.EnhancedRandom;

class PromotionsTest
{
	@Test
	void testPromotionGetterSetter()
	{
		Promotions promotions = new Promotions();
		List<PromotionRequest> promotionList = new ArrayList<>();
		PromotionRequest request = EnhancedRandom.random(PromotionRequest.class);
		promotionList.add(request);

		promotions.setPromotion(promotionList);

		assertThat(promotions.getPromotion()).isNotNull();
		assertThat(promotions.getPromotion()).hasSize(1);
		assertThat(promotions.getPromotion()).contains(request);
	}

	@Test
	void testPromotionGetterSetter_MultiplePromotions()
	{
		Promotions promotions = new Promotions();
		List<PromotionRequest> promotionList = new ArrayList<>();

		PromotionRequest request1 = EnhancedRandom.random(PromotionRequest.class);
		PromotionRequest request2 = EnhancedRandom.random(PromotionRequest.class);
		PromotionRequest request3 = EnhancedRandom.random(PromotionRequest.class);

		promotionList.add(request1);
		promotionList.add(request2);
		promotionList.add(request3);

		promotions.setPromotion(promotionList);

		assertThat(promotions.getPromotion()).isNotNull();
		assertThat(promotions.getPromotion()).hasSize(3);
		assertThat(promotions.getPromotion()).contains(request1, request2, request3);
	}

	@Test
	void testPromotionGetterSetter_EmptyList()
	{
		Promotions promotions = new Promotions();
		List<PromotionRequest> emptyList = new ArrayList<>();

		promotions.setPromotion(emptyList);

		assertThat(promotions.getPromotion()).isNotNull();
		assertThat(promotions.getPromotion()).isEmpty();
	}

	@Test
	void testPromotionGetterSetter_NullList()
	{
		Promotions promotions = new Promotions();
		promotions.setPromotion(null);

		assertThat(promotions.getPromotion()).isNull();
	}

	@Test
	void testPromotionGetterSetter_SingletonList()
	{
		Promotions promotions = new Promotions();
		PromotionRequest request = EnhancedRandom.random(PromotionRequest.class);
		List<PromotionRequest> singletonList = Collections.singletonList(request);

		promotions.setPromotion(singletonList);

		assertThat(promotions.getPromotion()).hasSize(1);
		assertThat(promotions.getPromotion().get(0)).isEqualTo(request);
	}

	@Test
	void testPromotionGetterSetter_LargeList()
	{
		Promotions promotions = new Promotions();
		List<PromotionRequest> largeList = new ArrayList<>();

		for (int i = 0; i < 100; i++)
		{
			largeList.add(EnhancedRandom.random(PromotionRequest.class));
		}

		promotions.setPromotion(largeList);

		assertThat(promotions.getPromotion()).hasSize(100);
	}

	@Test
	void testPromotionGetterSetter_ModifyList()
	{
		Promotions promotions = new Promotions();
		List<PromotionRequest> promotionList = new ArrayList<>();
		PromotionRequest request1 = EnhancedRandom.random(PromotionRequest.class);
		promotionList.add(request1);

		promotions.setPromotion(promotionList);

		// Modify the list after setting
		PromotionRequest request2 = EnhancedRandom.random(PromotionRequest.class);
		promotionList.add(request2);

		// The promotions object should reflect the change since it holds the same reference
		assertThat(promotions.getPromotion()).hasSize(2);
	}

	@Test
	void testPromotionGetterSetter_ReplaceList()
	{
		Promotions promotions = new Promotions();

		// Set initial list
		List<PromotionRequest> list1 = new ArrayList<>();
		list1.add(EnhancedRandom.random(PromotionRequest.class));
		promotions.setPromotion(list1);

		assertThat(promotions.getPromotion()).hasSize(1);

		// Replace with new list
		List<PromotionRequest> list2 = new ArrayList<>();
		list2.add(EnhancedRandom.random(PromotionRequest.class));
		list2.add(EnhancedRandom.random(PromotionRequest.class));
		promotions.setPromotion(list2);

		assertThat(promotions.getPromotion()).hasSize(2);
	}

	@Test
	void testPromotionGetterSetter_NullElements()
	{
		Promotions promotions = new Promotions();
		List<PromotionRequest> listWithNulls = new ArrayList<>();
		listWithNulls.add(null);
		listWithNulls.add(EnhancedRandom.random(PromotionRequest.class));
		listWithNulls.add(null);

		promotions.setPromotion(listWithNulls);

		assertThat(promotions.getPromotion()).hasSize(3);
		assertThat(promotions.getPromotion().get(0)).isNull();
		assertThat(promotions.getPromotion().get(1)).isNotNull();
		assertThat(promotions.getPromotion().get(2)).isNull();
	}

	@Test
	void testDefaultConstructor()
	{
		Promotions promotions = new Promotions();
		assertThat(promotions).isNotNull();
		assertThat(promotions.getPromotion()).isNull();
	}
}
