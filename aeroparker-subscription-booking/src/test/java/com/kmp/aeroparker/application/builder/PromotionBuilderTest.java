package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.external.api.datatypes.Promotion;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionPromotionResponse;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PromotionsPromoCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPromoBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;

class PromotionBuilderTest
{
	private PromotionBuilder builder;

	@BeforeEach
	void setUp()
	{
		builder = new PromotionBuilder();
	}

	@Test
	void testBuildSessionPromotion_ValidPromotion()
	{
		String guid = "guid-123";
		String promoCode = "SAVE10";
		BigDecimal discount = new BigDecimal("10.00");
		BigDecimal discountedPrice = new BigDecimal("90.00");

		Promotion promotion = new Promotion();
		promotion.setValid(true);
		promotion.setPromoCode(promoCode);
		promotion.setDiscount(discount);
		promotion.setDiscountedPrice(discountedPrice);

		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setPromotion(promotion);

		PromotionsPromoCodes promoCodeEntity = new PromotionsPromoCodes();
		promoCodeEntity.setId(100);
		promoCodeEntity.setPromotionId(50);

		SubscriptionSessionPromotion result = builder.buildSessionPromotion(guid, response, promoCodeEntity);

		assertThat(result).isNotNull();
		assertThat(result.getGuid()).isEqualTo(guid);
		assertThat(result.getPromoCode()).isEqualTo(promoCode);
		assertThat(result.getPromoId()).isEqualTo(50);
		assertThat(result.getPromoCodeId()).isEqualTo(100);
		assertThat(result.getDiscountAmount()).isEqualByComparingTo(discount);
		assertThat(result.getDiscountedPrice()).isEqualByComparingTo(discountedPrice);
		assertThat(result.getValid()).isTrue();
	}

	@Test
	void testBuildSessionPromotion_InvalidPromotion()
	{
		String guid = "guid-456";
		String promoCode = "INVALID";

		Promotion promotion = new Promotion();
		promotion.setValid(false);
		promotion.setPromoCode(promoCode);

		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setPromotion(promotion);

		PromotionsPromoCodes promoCodeEntity = new PromotionsPromoCodes();
		promoCodeEntity.setId(200);
		promoCodeEntity.setPromotionId(60);

		SubscriptionSessionPromotion result = builder.buildSessionPromotion(guid, response, promoCodeEntity);

		assertThat(result).isNotNull();
		assertThat(result.getGuid()).isEqualTo(guid);
		assertThat(result.getPromoCode()).isEqualTo(promoCode);
		assertThat(result.getPromoId()).isEqualTo(60);
		assertThat(result.getPromoCodeId()).isEqualTo(200);
		assertThat(result.getDiscountAmount()).isNull();
		assertThat(result.getDiscountedPrice()).isNull();
		assertThat(result.getValid()).isNull();
	}

	@Test
	void testBuildSessionPromotion_NullPromoCodeEntity()
	{
		String guid = "guid-789";
		String promoCode = "TEST";
		BigDecimal discount = new BigDecimal("5.00");
		BigDecimal discountedPrice = new BigDecimal("95.00");

		Promotion promotion = new Promotion();
		promotion.setValid(true);
		promotion.setPromoCode(promoCode);
		promotion.setDiscount(discount);
		promotion.setDiscountedPrice(discountedPrice);

		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setPromotion(promotion);

		SubscriptionSessionPromotion result = builder.buildSessionPromotion(guid, response, null);

		assertThat(result).isNotNull();
		assertThat(result.getGuid()).isEqualTo(guid);
		assertThat(result.getPromoCode()).isEqualTo(promoCode);
		assertThat(result.getPromoId()).isNull();
		assertThat(result.getPromoCodeId()).isNull();
		assertThat(result.getDiscountAmount()).isEqualByComparingTo(discount);
		assertThat(result.getDiscountedPrice()).isEqualByComparingTo(discountedPrice);
		assertThat(result.getValid()).isTrue();
	}

	@Test
	void testBuildSessionPromotion_ZeroDiscount()
	{
		String guid = "guid-000";
		String promoCode = "ZERO";

		Promotion promotion = new Promotion();
		promotion.setValid(true);
		promotion.setPromoCode(promoCode);
		promotion.setDiscount(BigDecimal.ZERO);
		promotion.setDiscountedPrice(new BigDecimal("100.00"));

		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setPromotion(promotion);

		SubscriptionSessionPromotion result = builder.buildSessionPromotion(guid, response, null);

		assertThat(result).isNotNull();
		assertThat(result.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void testBuildPromoBooking_ValidData()
	{
		Integer subBookingId = 123;
		String promoCode = "SAVE10";
		BigDecimal discount = new BigDecimal("10.00");
		Integer promotionId = 50;
		Integer promoCodeId = 100;

		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setPromoCode(promoCode);
		sessionPromotion.setDiscountAmount(discount);
		sessionPromotion.setPromoId(promotionId);
		sessionPromotion.setPromoCodeId(promoCodeId);

		SubscriptionPromoBooking result = builder.buildPromoBooking(subBookingId, sessionPromotion);

		assertThat(result).isNotNull();
		assertThat(result.getSubBookingId()).isEqualTo(subBookingId);
		assertThat(result.getCode()).isEqualTo(promoCode);
		assertThat(result.getDiscount()).isEqualByComparingTo(discount);
		assertThat(result.getPromotionId()).isEqualTo(promotionId);
		assertThat(result.getPromoCodeId()).isEqualTo(promoCodeId);
	}

	@Test
	void testBuildPromoBooking_NullValues()
	{
		Integer subBookingId = 456;

		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setPromoCode(null);
		sessionPromotion.setDiscountAmount(null);
		sessionPromotion.setPromoId(null);
		sessionPromotion.setPromoCodeId(null);

		SubscriptionPromoBooking result = builder.buildPromoBooking(subBookingId, sessionPromotion);

		assertThat(result).isNotNull();
		assertThat(result.getSubBookingId()).isEqualTo(subBookingId);
		assertThat(result.getCode()).isNull();
		assertThat(result.getDiscount()).isNull();
		assertThat(result.getPromotionId()).isNull();
		assertThat(result.getPromoCodeId()).isNull();
	}

	@Test
	void testBuildPromoBooking_ZeroSubBookingId()
	{
		Integer subBookingId = 0;
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setPromoCode("TEST");

		SubscriptionPromoBooking result = builder.buildPromoBooking(subBookingId, sessionPromotion);

		assertThat(result).isNotNull();
		assertThat(result.getSubBookingId()).isEqualTo(0);
	}

	@Test
	void testBuildInvalidSessionPromotion_ValidData()
	{
		String promoCode = "INVALID123";
		String guid = "guid-invalid";

		SubscriptionSessionPromotion result = builder.buildInvalidSessionPromotion(promoCode, guid);

		assertThat(result).isNotNull();
		assertThat(result.getPromoCode()).isEqualTo(promoCode);
		assertThat(result.getGuid()).isEqualTo(guid);
		assertThat(result.getValid()).isNotNull();
		assertThat(result.getDiscountAmount()).isNull();
		assertThat(result.getDiscountedPrice()).isNull();
		assertThat(result.getPromoId()).isNull();
		assertThat(result.getPromoCodeId()).isNull();
	}

	@Test
	void testBuildInvalidSessionPromotion_EmptyPromoCode()
	{
		String promoCode = "";
		String guid = "guid-empty";

		SubscriptionSessionPromotion result = builder.buildInvalidSessionPromotion(promoCode, guid);

		assertThat(result).isNotNull();
		assertThat(result.getPromoCode()).isEmpty();
		assertThat(result.getGuid()).isEqualTo(guid);
	}

	@Test
	void testBuildInvalidSessionPromotion_NullValues()
	{
		SubscriptionSessionPromotion result = builder.buildInvalidSessionPromotion(null, null);

		assertThat(result).isNotNull();
		assertThat(result.getPromoCode()).isNull();
		assertThat(result.getGuid()).isNull();
	}

	@Test
	void testBuildPromoBooking_LargeDiscount()
	{
		Integer subBookingId = 999;
		BigDecimal largeDiscount = new BigDecimal("9999.99");

		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setPromoCode("LARGE");
		sessionPromotion.setDiscountAmount(largeDiscount);

		SubscriptionPromoBooking result = builder.buildPromoBooking(subBookingId, sessionPromotion);

		assertThat(result).isNotNull();
		assertThat(result.getDiscount()).isEqualByComparingTo(largeDiscount);
	}

	@Test
	void testBuildSessionPromotion_LargeDiscountedPrice()
	{
		String guid = "guid-large";
		BigDecimal largePrice = new BigDecimal("99999.99");

		Promotion promotion = new Promotion();
		promotion.setValid(true);
		promotion.setPromoCode("BIGPRICE");
		promotion.setDiscount(new BigDecimal("1.00"));
		promotion.setDiscountedPrice(largePrice);

		SubscriptionPromotionResponse response = new SubscriptionPromotionResponse();
		response.setPromotion(promotion);

		SubscriptionSessionPromotion result = builder.buildSessionPromotion(guid, response, null);

		assertThat(result).isNotNull();
		assertThat(result.getDiscountedPrice()).isEqualByComparingTo(largePrice);
	}
}
