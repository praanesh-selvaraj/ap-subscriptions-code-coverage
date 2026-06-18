package com.kmp.aeroparker.application.db.dao;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PromotionsPromoCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.records.SubscriptionSessionPromotionRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionPromoBookingDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPromoBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class PromotionDao
{
	private final DSLContext dsl;

	public void saveSessionPromotion(final SubscriptionSessionPromotion sessionPromotion)
	{
		SubscriptionSessionPromotionRecord record =
				dsl.newRecord(Tables.SUBSCRIPTION_SESSION_PROMOTION, sessionPromotion);

		dsl.insertInto(Tables.SUBSCRIPTION_SESSION_PROMOTION)
				.set(record)
				.onDuplicateKeyUpdate()
				.set(record)
				.execute();
	}

	public SubscriptionSessionPromotion fetchSessionPromotionByGuid(final String guid)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_SESSION_PROMOTION)
				.where(Tables.SUBSCRIPTION_SESSION_PROMOTION.GUID.eq(guid))
				.fetchOneInto(SubscriptionSessionPromotion.class);
	}

	public void savePromoBooking(final SubscriptionPromoBooking promoBooking)
	{
		SubscriptionPromoBookingDao dao = new SubscriptionPromoBookingDao(dsl.configuration());
		dao.insert(promoBooking);
	}

	public PromotionsPromoCodes fetchPromoByPromocodeAndSiteId(final String code, final int siteId)
	{
		return dsl.selectFrom(Tables.PROMOTIONS_PROMO_CODES)
				.where(Tables.PROMOTIONS_PROMO_CODES.CODE.eq(code))
				.and(Tables.PROMOTIONS_PROMO_CODES.SITEID.eq(siteId))
				.fetchOneInto(PromotionsPromoCodes.class);
	}

	public boolean incrementPromoCodeUses(final int promoCodeId)
	{
		return dsl.update(Tables.PROMOTIONS_PROMO_CODES)
				.set(Tables.PROMOTIONS_PROMO_CODES.USES, Tables.PROMOTIONS_PROMO_CODES.USES.add(1))
				.where(Tables.PROMOTIONS_PROMO_CODES.ID.eq(promoCodeId))
				.execute() > 0;
	}
}
