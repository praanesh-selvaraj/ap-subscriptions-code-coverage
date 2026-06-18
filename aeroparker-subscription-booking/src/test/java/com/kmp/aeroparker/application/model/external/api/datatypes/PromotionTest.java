package com.kmp.aeroparker.application.model.external.api.datatypes;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class PromotionTest
{
	@Test
	void testPromotionIdGetterSetter()
	{
		Promotion promotion = new Promotion();
		promotion.setPromotionId(123);
		assertThat(promotion.getPromotionId()).isEqualTo(123);
	}

	@Test
	void testPromotionTypeGetterSetter()
	{
		Promotion promotion = new Promotion();
		promotion.setPromotionType("DISCOUNT");
		assertThat(promotion.getPromotionType()).isEqualTo("DISCOUNT");
	}

	@Test
	void testPromoCodeGetterSetter()
	{
		Promotion promotion = new Promotion();
		promotion.setPromoCode("SAVE10");
		assertThat(promotion.getPromoCode()).isEqualTo("SAVE10");
	}

	@Test
	void testNameGetterSetter()
	{
		Promotion promotion = new Promotion();
		promotion.setName("10% Discount");
		assertThat(promotion.getName()).isEqualTo("10% Discount");
	}

	@Test
	void testValidGetterSetter()
	{
		Promotion promotion = new Promotion();
		promotion.setValid(true);
		assertThat(promotion.getValid()).isTrue();
	}

	@Test
	void testValidGetterSetter_False()
	{
		Promotion promotion = new Promotion();
		promotion.setValid(false);
		assertThat(promotion.getValid()).isFalse();
	}

	@Test
	void testDiscountGetterSetter()
	{
		Promotion promotion = new Promotion();
		BigDecimal discount = new BigDecimal("10.00");
		promotion.setDiscount(discount);
		assertThat(promotion.getDiscount()).isEqualByComparingTo(discount);
	}

	@Test
	void testDiscountedPriceGetterSetter()
	{
		Promotion promotion = new Promotion();
		BigDecimal discountedPrice = new BigDecimal("90.00");
		promotion.setDiscountedPrice(discountedPrice);
		assertThat(promotion.getDiscountedPrice()).isEqualByComparingTo(discountedPrice);
	}

	@Test
	void testAllFieldsTogetherWithValidData()
	{
		Promotion promotion = new Promotion();
		promotion.setPromotionId(456);
		promotion.setPromotionType("PERCENTAGE");
		promotion.setPromoCode("PROMO20");
		promotion.setName("20% Off");
		promotion.setValid(true);
		promotion.setDiscount(new BigDecimal("20.00"));
		promotion.setDiscountedPrice(new BigDecimal("80.00"));

		assertThat(promotion.getPromotionId()).isEqualTo(456);
		assertThat(promotion.getPromotionType()).isEqualTo("PERCENTAGE");
		assertThat(promotion.getPromoCode()).isEqualTo("PROMO20");
		assertThat(promotion.getName()).isEqualTo("20% Off");
		assertThat(promotion.getValid()).isTrue();
		assertThat(promotion.getDiscount()).isEqualByComparingTo(new BigDecimal("20.00"));
		assertThat(promotion.getDiscountedPrice()).isEqualByComparingTo(new BigDecimal("80.00"));
	}

	@Test
	void testNullValues()
	{
		Promotion promotion = new Promotion();
		promotion.setPromotionType(null);
		promotion.setPromoCode(null);
		promotion.setName(null);
		promotion.setValid(null);
		promotion.setDiscount(null);
		promotion.setDiscountedPrice(null);

		assertThat(promotion.getPromotionType()).isNull();
		assertThat(promotion.getPromoCode()).isNull();
		assertThat(promotion.getName()).isNull();
		assertThat(promotion.getValid()).isNull();
		assertThat(promotion.getDiscount()).isNull();
		assertThat(promotion.getDiscountedPrice()).isNull();
	}

	@Test
	void testZeroPromotionId()
	{
		Promotion promotion = new Promotion();
		promotion.setPromotionId(0);
		assertThat(promotion.getPromotionId()).isEqualTo(0);
	}

	@Test
	void testNegativePromotionId()
	{
		Promotion promotion = new Promotion();
		promotion.setPromotionId(-1);
		assertThat(promotion.getPromotionId()).isEqualTo(-1);
	}

	@Test
	void testEmptyStrings()
	{
		Promotion promotion = new Promotion();
		promotion.setPromotionType("");
		promotion.setPromoCode("");
		promotion.setName("");

		assertThat(promotion.getPromotionType()).isEmpty();
		assertThat(promotion.getPromoCode()).isEmpty();
		assertThat(promotion.getName()).isEmpty();
	}

	@Test
	void testZeroDiscount()
	{
		Promotion promotion = new Promotion();
		promotion.setDiscount(BigDecimal.ZERO);
		assertThat(promotion.getDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void testZeroDiscountedPrice()
	{
		Promotion promotion = new Promotion();
		promotion.setDiscountedPrice(BigDecimal.ZERO);
		assertThat(promotion.getDiscountedPrice()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void testNegativeDiscount()
	{
		Promotion promotion = new Promotion();
		BigDecimal negativeDiscount = new BigDecimal("-10.00");
		promotion.setDiscount(negativeDiscount);
		assertThat(promotion.getDiscount()).isEqualByComparingTo(negativeDiscount);
	}

	@Test
	void testLargeDiscount()
	{
		Promotion promotion = new Promotion();
		BigDecimal largeDiscount = new BigDecimal("99999.99");
		promotion.setDiscount(largeDiscount);
		assertThat(promotion.getDiscount()).isEqualByComparingTo(largeDiscount);
	}

	@Test
	void testLargeDiscountedPrice()
	{
		Promotion promotion = new Promotion();
		BigDecimal largePrice = new BigDecimal("999999.99");
		promotion.setDiscountedPrice(largePrice);
		assertThat(promotion.getDiscountedPrice()).isEqualByComparingTo(largePrice);
	}

	@Test
	void testPrecisionDiscount()
	{
		Promotion promotion = new Promotion();
		BigDecimal precisionDiscount = new BigDecimal("10.555");
		promotion.setDiscount(precisionDiscount);
		assertThat(promotion.getDiscount()).isEqualByComparingTo(precisionDiscount);
	}

	@Test
	void testWhitespaceStrings()
	{
		Promotion promotion = new Promotion();
		promotion.setPromotionType("   ");
		promotion.setPromoCode("   ");
		promotion.setName("   ");

		assertThat(promotion.getPromotionType()).isEqualTo("   ");
		assertThat(promotion.getPromoCode()).isEqualTo("   ");
		assertThat(promotion.getName()).isEqualTo("   ");
	}

	@Test
	void testSpecialCharactersInStrings()
	{
		Promotion promotion = new Promotion();
		promotion.setPromotionType("DISCOUNT-TYPE");
		promotion.setPromoCode("SAVE!@#$%");
		promotion.setName("Special 10% Off!");

		assertThat(promotion.getPromotionType()).isEqualTo("DISCOUNT-TYPE");
		assertThat(promotion.getPromoCode()).isEqualTo("SAVE!@#$%");
		assertThat(promotion.getName()).isEqualTo("Special 10% Off!");
	}
}
