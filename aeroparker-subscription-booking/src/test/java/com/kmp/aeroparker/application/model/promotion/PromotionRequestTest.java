package com.kmp.aeroparker.application.model.promotion;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PromotionRequestTest
{
	@Test
	void testSubscriptionProductIdGetterSetter()
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(123);
		assertThat(request.getSubscriptionProductId()).isEqualTo(123);
	}

	@Test
	void testPromoCodeGetterSetter()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("SAVE10");
		assertThat(request.getPromoCode()).isEqualTo("SAVE10");
	}

	@Test
	void testEmailGetterSetter()
	{
		PromotionRequest request = new PromotionRequest();
		request.setEmail("test@example.com");
		assertThat(request.getEmail()).isEqualTo("test@example.com");
	}

	@Test
	void testAllFieldsTogetherWithValidData()
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(456);
		request.setPromoCode("PROMO20");
		request.setEmail("user@test.com");

		assertThat(request.getSubscriptionProductId()).isEqualTo(456);
		assertThat(request.getPromoCode()).isEqualTo("PROMO20");
		assertThat(request.getEmail()).isEqualTo("user@test.com");
	}

	@Test
	void testNullValues()
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(null);
		request.setPromoCode(null);
		request.setEmail(null);

		assertThat(request.getSubscriptionProductId()).isNull();
		assertThat(request.getPromoCode()).isNull();
		assertThat(request.getEmail()).isNull();
	}

	@Test
	void testZeroSubscriptionProductId()
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(0);
		assertThat(request.getSubscriptionProductId()).isEqualTo(0);
	}

	@Test
	void testNegativeSubscriptionProductId()
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(-1);
		assertThat(request.getSubscriptionProductId()).isEqualTo(-1);
	}

	@Test
	void testEmptyPromoCode()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("");
		assertThat(request.getPromoCode()).isEmpty();
	}

	@Test
	void testWhitespacePromoCode()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("   ");
		assertThat(request.getPromoCode()).isEqualTo("   ");
	}

	@Test
	void testEmptyEmail()
	{
		PromotionRequest request = new PromotionRequest();
		request.setEmail("");
		assertThat(request.getEmail()).isEmpty();
	}

	@Test
	void testInvalidEmailFormat()
	{
		PromotionRequest request = new PromotionRequest();
		request.setEmail("invalid-email");
		assertThat(request.getEmail()).isEqualTo("invalid-email");
	}

	@Test
	void testLongPromoCode()
	{
		PromotionRequest request = new PromotionRequest();
		String longCode = "VERYLONGPROMOCODETHATEXCEEDSREASONABLELENGTH";
		request.setPromoCode(longCode);
		assertThat(request.getPromoCode()).isEqualTo(longCode);
	}

	@Test
	void testSpecialCharactersInPromoCode()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("SAVE-10%");
		assertThat(request.getPromoCode()).isEqualTo("SAVE-10%");
	}

	@Test
	void testLargeSubscriptionProductId()
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(Integer.MAX_VALUE);
		assertThat(request.getSubscriptionProductId()).isEqualTo(Integer.MAX_VALUE);
	}
}
