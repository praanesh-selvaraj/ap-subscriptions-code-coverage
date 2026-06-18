package com.kmp.aeroparker.application.builder;

import com.kmp.aeroparker.application.model.external.api.datatypes.Promotion;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PromotionsPromoCodes;
import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.model.external.api.response.SubscriptionPromotionResponse;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPromoBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;

@Component
public class PromotionBuilder
{
	public SubscriptionSessionPromotion buildSessionPromotion(final String guid,
		final SubscriptionPromotionResponse promotionResponse, PromotionsPromoCodes promotionPromoCode)
	{
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		Promotion promotion = promotionResponse.getPromotion();
		sessionPromotion.setGuid(guid);
		if (promotionPromoCode != null)
		{
			sessionPromotion.setPromoId(promotionPromoCode.getPromotionId());
			sessionPromotion.setPromoCodeId(promotionPromoCode.getId());
		}
		sessionPromotion.setPromoCode(promotion.getPromoCode());

		if (promotion.getValid())
		{
			sessionPromotion.setDiscountAmount(promotion.getDiscount());
			sessionPromotion.setDiscountedPrice(promotion.getDiscountedPrice());
			sessionPromotion.setValid(true);
		}
		return sessionPromotion;
	}

	public SubscriptionPromoBooking buildPromoBooking(final Integer subBookingId,
			final SubscriptionSessionPromotion sessionPromotion)
	{
		SubscriptionPromoBooking promoBooking = new SubscriptionPromoBooking();
		promoBooking.setSubBookingId(subBookingId);
		promoBooking.setCode(sessionPromotion.getPromoCode());
		promoBooking.setDiscount(sessionPromotion.getDiscountAmount());
		promoBooking.setPromotionId(sessionPromotion.getPromoId());
		promoBooking.setPromoCodeId(sessionPromotion.getPromoCodeId());
		return promoBooking;
	}

	public SubscriptionSessionPromotion buildInvalidSessionPromotion(String promoCode, String guid)
	{
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid(guid);
		sessionPromotion.setPromoCode(promoCode);
		sessionPromotion.setValid(false);
		return  sessionPromotion;
	}
}
