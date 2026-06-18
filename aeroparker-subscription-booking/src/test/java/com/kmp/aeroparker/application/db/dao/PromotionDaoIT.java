package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PromotionsPromoCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPromoBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PromotionDao.class)
class PromotionDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getPromotionInstance();

	@Autowired
	private PromotionDao dao;

	@Test
	void testSaveSessionPromotion_ValidData()
	{
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid("new-guid-999");
		sessionPromotion.setPromoCode("NEWSAVE");
		sessionPromotion.setPromoId(100);
		sessionPromotion.setPromoCodeId(200);
		sessionPromotion.setDiscountAmount(new BigDecimal("15.00"));
		sessionPromotion.setDiscountedPrice(new BigDecimal("85.00"));
		sessionPromotion.setValid(true);

		dao.saveSessionPromotion(sessionPromotion);

		SubscriptionSessionPromotion result = dao.fetchSessionPromotionByGuid("new-guid-999");
		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("guid", "new-guid-999")
				.hasFieldOrPropertyWithValue("promoCode", "NEWSAVE")
				.hasFieldOrPropertyWithValue("promoId", 100)
				.hasFieldOrPropertyWithValue("promoCodeId", 200)
				.hasFieldOrPropertyWithValue("valid", true);
		assertThat(result.getDiscountAmount()).isEqualByComparingTo(new BigDecimal("15.00"));
	}

	@Test
	void testSaveSessionPromotion_UpdateExisting()
	{
		// First insert
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid("update-guid-888");
		sessionPromotion.setPromoCode("SAVE10");
		sessionPromotion.setPromoId(100);
		sessionPromotion.setPromoCodeId(200);
		sessionPromotion.setDiscountAmount(new BigDecimal("10.00"));
		sessionPromotion.setDiscountedPrice(new BigDecimal("90.00"));
		sessionPromotion.setValid(true);

		dao.saveSessionPromotion(sessionPromotion);

		// Update with new values
		sessionPromotion.setPromoCode("SAVE20");
		sessionPromotion.setPromoId(101);
		sessionPromotion.setPromoCodeId(201);
		sessionPromotion.setDiscountAmount(new BigDecimal("20.00"));
		sessionPromotion.setDiscountedPrice(new BigDecimal("80.00"));

		dao.saveSessionPromotion(sessionPromotion);

		SubscriptionSessionPromotion result = dao.fetchSessionPromotionByGuid("update-guid-888");
		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("guid", "update-guid-888")
				.hasFieldOrPropertyWithValue("promoCode", "SAVE20")
				.hasFieldOrPropertyWithValue("promoId", 101)
				.hasFieldOrPropertyWithValue("promoCodeId", 201);
		assertThat(result.getDiscountAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
	}

	@Test
	void testFetchSessionPromotionByGuid_ExistingData()
	{
		SubscriptionSessionPromotion result = dao.fetchSessionPromotionByGuid("test-guid-123");

		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("guid", "test-guid-123")
				.hasFieldOrPropertyWithValue("promoCode", "SAVE10")
				.hasFieldOrPropertyWithValue("promoId", 100)
				.hasFieldOrPropertyWithValue("promoCodeId", 200)
				.hasFieldOrPropertyWithValue("valid", true);
		assertThat(result.getDiscountAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
	}

	@Test
	void testFetchSessionPromotionByGuid_NotFound()
	{
		SubscriptionSessionPromotion result = dao.fetchSessionPromotionByGuid("nonexistent-guid");

		assertThat(result).isNull();
	}

	@Test
	void testFetchSessionPromotionByGuid_InvalidPromotion()
	{
		SubscriptionSessionPromotion result = dao.fetchSessionPromotionByGuid("test-guid-invalid");

		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("guid", "test-guid-invalid")
				.hasFieldOrPropertyWithValue("promoCode", "INVALID")
				.hasFieldOrPropertyWithValue("valid", false);
		assertThat(result.getPromoId()).isNull();
		assertThat(result.getDiscountAmount()).isNull();
	}

	@Test
	void testSavePromoBooking_ValidData()
	{
		SubscriptionPromoBooking promoBooking = new SubscriptionPromoBooking();
		promoBooking.setSubBookingId(2000);
		promoBooking.setDiscount(new BigDecimal("15.00"));
		promoBooking.setCode("SAVE15");
		promoBooking.setPromotionId(100);
		promoBooking.setPromoCodeId(200);

		dao.savePromoBooking(promoBooking);

		// Verify by checking it was saved (implementation detail - we can't easily fetch it back)
		// The test passes if no exception is thrown
	}

	@Test
	void testFetchPromoByPromocode_AndSiteId_ExistingCode()
	{
		PromotionsPromoCodes result = dao.fetchPromoByPromocodeAndSiteId("SAVE10", 1);

		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("code", "SAVE10")
				.hasFieldOrPropertyWithValue("promotionId", 100)
				.hasFieldOrPropertyWithValue("id", 200)
				.hasFieldOrPropertyWithValue("uses", 5)
				.hasFieldOrPropertyWithValue("maximumUses", 100)
				.hasFieldOrPropertyWithValue("enabled", true);
	}

	@Test
	void testFetchPromoByPromocode_AndSiteId_PercentageDiscount()
	{
		PromotionsPromoCodes result = dao.fetchPromoByPromocodeAndSiteId("PERCENT15", 1);

		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("code", "PERCENT15")
				.hasFieldOrPropertyWithValue("promotionId", 102)
				.hasFieldOrPropertyWithValue("id", 202)
				.hasFieldOrPropertyWithValue("enabled", true);
	}

	@Test
	void testFetchPromoByPromocode_AndSiteId_ExpiredCode()
	{
		PromotionsPromoCodes result = dao.fetchPromoByPromocodeAndSiteId("EXPIRED", 1);

		assertThat(result).isNotNull()
				.hasFieldOrPropertyWithValue("code", "EXPIRED")
				.hasFieldOrPropertyWithValue("promotionId", 103)
				.hasFieldOrPropertyWithValue("enabled", false);
	}

	@Test
	void testFetchPromoByPromocode_AndSiteId_NotFound()
	{
		PromotionsPromoCodes result = dao.fetchPromoByPromocodeAndSiteId("NONEXISTENT",1 );

		assertThat(result).isNull();
	}

	@Test
	void testIncrementPromoCodeUses_Success()
	{
		// Get initial uses count for SAVE10 (promo code ID 200)
		PromotionsPromoCodes promoBefore = dao.fetchPromoByPromocodeAndSiteId("SAVE10", 1);
		assertThat(promoBefore).isNotNull();
		int initialUses = promoBefore.getUses();

		// Increment uses
		boolean success = dao.incrementPromoCodeUses(200);
		assertThat(success).isTrue();

		// Verify uses was incremented
		PromotionsPromoCodes promoAfter = dao.fetchPromoByPromocodeAndSiteId("SAVE10", 1);
		assertThat(promoAfter).isNotNull();
		assertThat(promoAfter.getUses()).isEqualTo(initialUses + 1);
	}

	@Test
	void testIncrementPromoCodeUses_NonExistentId()
	{
		boolean success = dao.incrementPromoCodeUses(99999);
		assertThat(success).isFalse();
	}
}
