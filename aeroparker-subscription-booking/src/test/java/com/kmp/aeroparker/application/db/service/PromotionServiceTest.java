package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.PromotionDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PromotionsPromoCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPromoBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest
{
	@Mock
	private PromotionDao dao;
	@Mock
	private SubscriptionConfigBean requestBean;
	@InjectMocks
	private PromotionService service;

	@Test
	void testSaveSessionPromotion_ValidData()
	{
		SubscriptionSessionPromotion sessionPromotion = EnhancedRandom.random(SubscriptionSessionPromotion.class);
		sessionPromotion.setGuid("guid-123");

		service.saveSessionPromotion(sessionPromotion);

		verify(dao).saveSessionPromotion(sessionPromotion);
	}

	@Test
	void testSaveSessionPromotion_NullSessionPromotion()
	{
		service.saveSessionPromotion(null);

		verify(dao, never()).saveSessionPromotion(any());
	}

	@Test
	void testSaveSessionPromotion_NullGuid()
	{
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid(null);

		service.saveSessionPromotion(sessionPromotion);

		verify(dao, never()).saveSessionPromotion(any());
	}

	@Test
	void testSaveSessionPromotion_EmptyGuid()
	{
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid("");

		service.saveSessionPromotion(sessionPromotion);

		verify(dao, never()).saveSessionPromotion(any());
	}

	@Test
	void testFetchSessionPromotionByGuid_ValidGuid()
	{
		String guid = "guid-456";
		SubscriptionSessionPromotion expected = EnhancedRandom.random(SubscriptionSessionPromotion.class);

		when(dao.fetchSessionPromotionByGuid(guid)).thenReturn(expected);

		SubscriptionSessionPromotion result = service.fetchSessionPromotionByGuid(guid);

		assertThat(result).isEqualTo(expected);
		verify(dao).fetchSessionPromotionByGuid(guid);
	}

	@Test
	void testFetchSessionPromotionByGuid_NullGuid()
	{
		SubscriptionSessionPromotion result = service.fetchSessionPromotionByGuid(null);

		assertThat(result).isNull();
		verify(dao, never()).fetchSessionPromotionByGuid(anyString());
	}

	@Test
	void testFetchSessionPromotionByGuid_EmptyGuid()
	{
		SubscriptionSessionPromotion result = service.fetchSessionPromotionByGuid("");

		assertThat(result).isNull();
		verify(dao, never()).fetchSessionPromotionByGuid(anyString());
	}

	@Test
	void testFetchSessionPromotionByGuid_NotFound()
	{
		String guid = "nonexistent-guid";

		when(dao.fetchSessionPromotionByGuid(guid)).thenReturn(null);

		SubscriptionSessionPromotion result = service.fetchSessionPromotionByGuid(guid);

		assertThat(result).isNull();
		verify(dao).fetchSessionPromotionByGuid(guid);
	}

	@Test
	void testSavePromoBooking_ValidData()
	{
		SubscriptionPromoBooking promoBooking = EnhancedRandom.random(SubscriptionPromoBooking.class);
		promoBooking.setSubBookingId(123);

		service.savePromoBooking(promoBooking);

		verify(dao).savePromoBooking(promoBooking);
	}

	@Test
	void testSavePromoBooking_NullPromoBooking()
	{
		service.savePromoBooking(null);

		verify(dao, never()).savePromoBooking(any());
	}

	@Test
	void testSavePromoBooking_NullSubBookingId()
	{
		SubscriptionPromoBooking promoBooking = new SubscriptionPromoBooking();
		promoBooking.setSubBookingId(null);

		service.savePromoBooking(promoBooking);

		verify(dao, never()).savePromoBooking(any());
	}

	@Test
	void testSavePromoBooking_ZeroSubBookingId()
	{
		SubscriptionPromoBooking promoBooking = new SubscriptionPromoBooking();
		promoBooking.setSubBookingId(0);

		service.savePromoBooking(promoBooking);

		verify(dao).savePromoBooking(promoBooking);
	}

	@Test
	void testFetchPromoByPromocodeAndSiteId_ValidCode()
	{
		String code = "SAVE10";
		PromotionsPromoCodes expected = EnhancedRandom.random(PromotionsPromoCodes.class);
		when(requestBean.getSiteId()).thenReturn(1);
		when(dao.fetchPromoByPromocodeAndSiteId(code, 1)).thenReturn(expected);

		PromotionsPromoCodes result = service.fetchPromoByPromocodeAndSiteId(code, requestBean.getSiteId());

		assertThat(result).isEqualTo(expected);
		verify(dao).fetchPromoByPromocodeAndSiteId(code, 1);
	}

	@Test
	void testFetchPromoByPromocodeAndSiteId_NullCode()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		PromotionsPromoCodes result = service.fetchPromoByPromocodeAndSiteId(null, requestBean.getSiteId());

		assertThat(result).isNull();
		verify(dao, never()).fetchPromoByPromocodeAndSiteId(anyString(), anyInt());
	}

	@Test
	void testfetchPromoByPromocodeAndSiteId_EmptyCode()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		PromotionsPromoCodes result = service.fetchPromoByPromocodeAndSiteId("", requestBean.getSiteId());

		assertThat(result).isNull();
		verify(dao, never()).fetchPromoByPromocodeAndSiteId(anyString(), anyInt());
	}

	@Test
	void testfetchPromoByPromocodeAndSiteId_NotFound()
	{
		String code = "NONEXISTENT";
		when(requestBean.getSiteId()).thenReturn(1);
		when(dao.fetchPromoByPromocodeAndSiteId(code, 1)).thenReturn(null);

		PromotionsPromoCodes result = service.fetchPromoByPromocodeAndSiteId(code, requestBean.getSiteId());

		assertThat(result).isNull();
		verify(dao).fetchPromoByPromocodeAndSiteId(code, 1);
	}

	@Test
	void testfetchPromoByPromocodeAndSiteId_CaseSensitive()
	{
		String code = "save10";
		PromotionsPromoCodes expected = EnhancedRandom.random(PromotionsPromoCodes.class);
		when(requestBean.getSiteId()).thenReturn(1);
		when(dao.fetchPromoByPromocodeAndSiteId(code, 1)).thenReturn(expected);

		PromotionsPromoCodes result = service.fetchPromoByPromocodeAndSiteId(code, requestBean.getSiteId());

		assertThat(result).isEqualTo(expected);
		verify(dao).fetchPromoByPromocodeAndSiteId(code, 1);
	}

	@Test
	void testSaveSessionPromotion_WithAllFields()
	{
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid("guid-full");
		sessionPromotion.setPromoCode("PROMO");
		sessionPromotion.setPromoId(100);
		sessionPromotion.setPromoCodeId(200);
		sessionPromotion.setValid(true);

		service.saveSessionPromotion(sessionPromotion);

		verify(dao).saveSessionPromotion(sessionPromotion);
	}

	@Test
	void testSavePromoBooking_WithAllFields()
	{
		SubscriptionPromoBooking promoBooking = new SubscriptionPromoBooking();
		promoBooking.setSubBookingId(456);
		promoBooking.setCode("SAVE20");
		promoBooking.setPromotionId(50);
		promoBooking.setPromoCodeId(150);

		service.savePromoBooking(promoBooking);

		verify(dao).savePromoBooking(promoBooking);
	}

	@Test
	void testIncrementPromoCodeUses_Success()
	{
		int promoCodeId = 200;
		when(dao.incrementPromoCodeUses(promoCodeId)).thenReturn(true);

		boolean result = service.incrementPromoCodeUses(promoCodeId);

		assertThat(result).isTrue();
		verify(dao).incrementPromoCodeUses(promoCodeId);
	}

	@Test
	void testIncrementPromoCodeUses_Failure()
	{
		int promoCodeId = 99999;
		when(dao.incrementPromoCodeUses(promoCodeId)).thenReturn(false);

		boolean result = service.incrementPromoCodeUses(promoCodeId);

		assertThat(result).isFalse();
		verify(dao).incrementPromoCodeUses(promoCodeId);
	}
}
