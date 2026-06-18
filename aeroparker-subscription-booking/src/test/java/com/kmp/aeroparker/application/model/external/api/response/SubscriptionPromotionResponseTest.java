package com.kmp.aeroparker.application.model.external.api.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.external.api.datatypes.Promotion;

import io.github.benas.randombeans.api.EnhancedRandom;

class SubscriptionPromotionResponseTest
{
	@Test
	void testPromotionGetterSetter()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		Promotion promotion = EnhancedRandom.random(Promotion.class);
		response.setPromotion(promotion);
		assertThat(response.getPromotion()).isEqualTo(promotion);
	}

	@Test
	void testTimestampGetterSetter()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		String timestamp = "2025-11-19T10:30:00Z";
		response.setTimestamp(timestamp);
		assertThat(response.getTimestamp()).isEqualTo(timestamp);
	}

	@Test
	void testJsonStringGetterSetter()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		String jsonString = "{\"promotion\": \"test\"}";
		response.setJsonString(jsonString);
		assertThat(response.getJsonString()).isEqualTo(jsonString);
	}

	@Test
	void testAllFieldsTogetherWithValidData()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		Promotion promotion = EnhancedRandom.random(Promotion.class);
		String timestamp = "2025-11-19T10:30:00Z";
		String jsonString = "{\"promotion\": \"SAVE10\"}";

		response.setPromotion(promotion);
		response.setTimestamp(timestamp);
		response.setJsonString(jsonString);

		assertThat(response.getPromotion()).isEqualTo(promotion);
		assertThat(response.getTimestamp()).isEqualTo(timestamp);
		assertThat(response.getJsonString()).isEqualTo(jsonString);
	}

	@Test
	void testNullPromotion()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setPromotion(null);
		assertThat(response.getPromotion()).isNull();
	}

	@Test
	void testNullTimestamp()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setTimestamp(null);
		assertThat(response.getTimestamp()).isNull();
	}

	@Test
	void testNullJsonString()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setJsonString(null);
		assertThat(response.getJsonString()).isNull();
	}

	@Test
	void testEmptyTimestamp()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setTimestamp("");
		assertThat(response.getTimestamp()).isEmpty();
	}

	@Test
	void testEmptyJsonString()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setJsonString("");
		assertThat(response.getJsonString()).isEmpty();
	}

	@Test
	void testWhitespaceTimestamp()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setTimestamp("   ");
		assertThat(response.getTimestamp()).isEqualTo("   ");
	}

	@Test
	void testComplexJsonString()
	{
		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		String complexJson = "{\"promotion\":{\"code\":\"SAVE10\",\"discount\":10.0,\"valid\":true}}";
		response.setJsonString(complexJson);
		assertThat(response.getJsonString()).isEqualTo(complexJson);
	}
}
