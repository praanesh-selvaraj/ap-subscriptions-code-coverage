package com.kmp.aeroparker.application.model.external.api.datatypes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PromotionRequestTest
{
	@Test
	void testPromoCodeGetterSetter()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("SAVE10");
		assertThat(request.getPromoCode()).isEqualTo("SAVE10");
	}

	@Test
	void testNameGetterSetter()
	{
		PromotionRequest request = new PromotionRequest();
		request.setName("10% Discount");
		assertThat(request.getName()).isEqualTo("10% Discount");
	}

	@Test
	void testAllFieldsTogetherWithValidData()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("PROMO20");
		request.setName("20% Off Promotion");

		assertThat(request.getPromoCode()).isEqualTo("PROMO20");
		assertThat(request.getName()).isEqualTo("20% Off Promotion");
	}

	@Test
	void testNullValues()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode(null);
		request.setName(null);

		assertThat(request.getPromoCode()).isNull();
		assertThat(request.getName()).isNull();
	}

	@Test
	void testEmptyPromoCode()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("");
		assertThat(request.getPromoCode()).isEmpty();
	}

	@Test
	void testEmptyName()
	{
		PromotionRequest request = new PromotionRequest();
		request.setName("");
		assertThat(request.getName()).isEmpty();
	}

	@Test
	void testWhitespacePromoCode()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("   ");
		assertThat(request.getPromoCode()).isEqualTo("   ");
	}

	@Test
	void testWhitespaceName()
	{
		PromotionRequest request = new PromotionRequest();
		request.setName("   ");
		assertThat(request.getName()).isEqualTo("   ");
	}

	@Test
	void testSpecialCharactersInPromoCode()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("SAVE-10%");
		assertThat(request.getPromoCode()).isEqualTo("SAVE-10%");
	}

	@Test
	void testSpecialCharactersInName()
	{
		PromotionRequest request = new PromotionRequest();
		request.setName("Special 10% Off!");
		assertThat(request.getName()).isEqualTo("Special 10% Off!");
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
	void testCaseSensitivity()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("save10");
		assertThat(request.getPromoCode()).isEqualTo("save10");
		assertThat(request.getPromoCode()).isNotEqualTo("SAVE10");
	}

	@Test
	void testDefaultConstructor()
	{
		PromotionRequest request = new PromotionRequest();
		assertThat(request).isNotNull();
		assertThat(request.getPromoCode()).isNull();
		assertThat(request.getName()).isNull();
	}

	@Test
	void testNumericPromoCode()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("12345");
		assertThat(request.getPromoCode()).isEqualTo("12345");
	}

	@Test
	void testAlphanumericPromoCode()
	{
		PromotionRequest request = new PromotionRequest();
		request.setPromoCode("ABC123XYZ");
		assertThat(request.getPromoCode()).isEqualTo("ABC123XYZ");
	}
}
