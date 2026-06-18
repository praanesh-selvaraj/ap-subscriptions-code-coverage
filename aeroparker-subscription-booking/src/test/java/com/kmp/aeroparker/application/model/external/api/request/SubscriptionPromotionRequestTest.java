package com.kmp.aeroparker.application.model.external.api.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.external.api.datatypes.Promotions;

import io.github.benas.randombeans.api.EnhancedRandom;

class SubscriptionPromotionRequestTest
{
	@Test
	void testSubscriptionProductIdGetterSetter()
	{
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		request.setSubscriptionProductId(123);
		assertThat(request.getSubscriptionProductId()).isEqualTo(123);
	}

	@Test
	void testKeyGetterSetter()
	{
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		request.setKey("testKey");
		assertThat(request.getKey()).isEqualTo("testKey");
	}

	@Test
	void testPromotionsGetterSetter()
	{
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		Promotions promotions = EnhancedRandom.random(Promotions.class);
		request.setPromotions(promotions);
		assertThat(request.getPromotions()).isEqualTo(promotions);
	}

	@Test
	void testAllFieldsTogetherWithValidData()
	{
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		Promotions promotions = EnhancedRandom.random(Promotions.class);

		request.setSubscriptionProductId(456);
		request.setKey("SAVE10");
		request.setPromotions(promotions);

		assertThat(request.getSubscriptionProductId()).isEqualTo(456);
		assertThat(request.getKey()).isEqualTo("SAVE10");
		assertThat(request.getPromotions()).isEqualTo(promotions);
	}

	@Test
	void testNullValues()
	{
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		request.setKey(null);
		request.setPromotions(null);

		assertThat(request.getKey()).isNull();
		assertThat(request.getPromotions()).isNull();
	}

	@Test
	void testZeroSubscriptionProductId()
	{
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		request.setSubscriptionProductId(0);
		assertThat(request.getSubscriptionProductId()).isEqualTo(0);
	}

	@Test
	void testNegativeSubscriptionProductId()
	{
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		request.setSubscriptionProductId(-1);
		assertThat(request.getSubscriptionProductId()).isEqualTo(-1);
	}

	@Test
	void testEmptyKey()
	{
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		request.setKey("");
		assertThat(request.getKey()).isEmpty();
	}

	@Test
	void testWhitespaceKey()
	{
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		request.setKey("   ");
		assertThat(request.getKey()).isEqualTo("   ");
	}
}
