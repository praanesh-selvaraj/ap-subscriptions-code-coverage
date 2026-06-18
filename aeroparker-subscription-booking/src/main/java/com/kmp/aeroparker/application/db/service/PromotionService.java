package com.kmp.aeroparker.application.db.service;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PromotionsPromoCodes;
import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.PromotionDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPromoBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PromotionService
{
	private final PromotionDao dao;

	public void saveSessionPromotion(final SubscriptionSessionPromotion sessionPromotion)
	{
		if (sessionPromotion == null || StringUtil.isNullOrEmpty(sessionPromotion.getGuid()))
		{
			log.info("Session promotion or GUID is null/empty, session promotion will not be saved");
		}
		else
		{
			dao.saveSessionPromotion(sessionPromotion);
		}
	}

	public SubscriptionSessionPromotion fetchSessionPromotionByGuid(final String guid)
	{
		SubscriptionSessionPromotion sessionPromotion = null;
		if (StringUtil.isNullOrEmpty(guid))
		{
			log.info("GUID is null/empty, session promotion will not be fetched");
		}
		else
		{
			sessionPromotion = dao.fetchSessionPromotionByGuid(guid);
			log.debug("Session promotion fetched: {}", sessionPromotion);
		}
		return sessionPromotion;
	}

	public void savePromoBooking(final SubscriptionPromoBooking promoBooking)
	{
		if (promoBooking == null || promoBooking.getSubBookingId() == null)
		{
			log.info("Promo booking or sub_booking_id is null, promo booking will not be saved");
		}
		else
		{
			dao.savePromoBooking(promoBooking);
			log.debug("Promo booking saved: {}", promoBooking);
		}
	}

	public PromotionsPromoCodes fetchPromoByPromocodeAndSiteId(final String code, int siteId)
	{
		PromotionsPromoCodes promotion = null;
		if (StringUtil.isNullOrEmpty(code))
		{
			log.info("code is null/empty, no promotion will not be fetched");
		}
		else
		{
			promotion = dao.fetchPromoByPromocodeAndSiteId(code, siteId);
		}
		return promotion;
	}

	public boolean incrementPromoCodeUses(final int promoCodeId)
	{
		log.debug("Incrementing uses for promo code ID: {}", promoCodeId);
		boolean success = dao.incrementPromoCodeUses(promoCodeId);
		if (success)
		{
			log.info("Successfully incremented uses for promo code ID: {}", promoCodeId);
		}
		else
		{
			log.warn("Failed to increment uses for promo code ID: {}", promoCodeId);
		}
		return success;
	}
}
