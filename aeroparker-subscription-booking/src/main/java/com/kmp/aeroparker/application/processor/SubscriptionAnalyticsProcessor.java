package com.kmp.aeroparker.application.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.AnalyticsService;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.facade.AffiliateAnalyticsFacade;
import com.kmp.aeroparker.application.manager.ScopesMapManagerConfirmation;
import com.kmp.aeroparker.application.model.AnalyticsLocations;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.application.utils.MustacheUtil;
import com.kmp.aeroparker.i18n.stringutil.StringUtil;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateAnalyticsNew;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionTracking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Component
public class SubscriptionAnalyticsProcessor
{
	private AnalyticsService analyticsService;
	private AffiliateAnalyticsFacade analyticsFacade;

	private static final int UPPER_HEAD_LOCATION = 1;
	private static final int LOWER_HEAD_LOCATION = 2;
	private static final int BODY_LOCATION = 3;
	private static final int FOOTER_LOCATION = 4;

	public void processSubscriptionAnalytics(Model model, Basket basket, String currency, int step, int affiliateId)
	{ 
		Map<SubscriptionBookingItem, IBookingTicket> subProducts = new HashMap<>();
		SubscriptionBooking subBooking = new SubscriptionBooking();
		subBooking.setAffiliateId(affiliateId);
		subBooking.setGrandTotal(basket.getGrandTotal());
		for (SubscriptionPurchaseRequest request : basket.getPurchaseRequestList())
		{
			SubscriptionBookingItem subItem = new SubscriptionBookingItem();
			if (request.getProductId() > 0 && !StringUtil.isNullOrEmpty(request.getProduct().getName()) 
					&& request.getGrandTotal() != null)
			{
				subItem.setProductId(request.getProductId());
				subItem.setProductDisplayName(request.getProduct().getName());
				subItem.setSubTotal(request.getGrandTotal());
				IBookingTicket ticket = request.isSeasonTicket() ? new BookingSeasonTicket() : new BookingRecurringTicket();
				
				subProducts.put(subItem, ticket);
			}
		}
		setNewAnalyticsForStep(model, subBooking, subProducts, step);
	}
	
	public void processSubscriptionAnalytics(Model model, SubscriptionBookingRecord subRecord, String currency)
	{
		processSubscriptionAnalytics(model, subRecord, currency, AnalyticsLocations.SUBSCRIPTION_CONFIRMATION.getId());
	}
	
	public void processSubscriptionAnalytics(Model model, SubscriptionBookingRecord subRecord, String currency, int step)
	{
		log.info("Setting analytics for subscription booking {}", subRecord.getBooking()
				.getReference());
		SubscriptionBooking subBooking = subRecord.getBooking();
		Map<SubscriptionBookingItem, IBookingTicket> subProducts = subRecord.getBookingItemMap();
		setNewAnalyticsForStep(model, subBooking, subProducts, step);
		saveSubscriptionTracking(subBooking, subProducts);
	}

	private void setNewAnalyticsForStep(Model model, SubscriptionBooking subBooking,
			Map<SubscriptionBookingItem, IBookingTicket> subProducts, int step)
	{
		log.info("Setting analytics for subscription step: {}", step);
		int affiliateId = subBooking.getAffiliateId();
		List<AffiliateAnalyticsNew> affiliateNewAnalyticsList =
				analyticsFacade.getEnabledAnalyticsAtStepByAffiliateId(affiliateId, step);
		if (affiliateNewAnalyticsList != null && !affiliateNewAnalyticsList.isEmpty())
		{
			Map<String, Object> scopes = ScopesMapManagerConfirmation.getScopes(subBooking, subProducts);
			List<String> upperHeadAnalytics = new ArrayList<>();
			List<String> lowerHeadAnalytics = new ArrayList<>();
			List<String> bodyAnalytics = new ArrayList<>();
			List<String> footerAnalytics = new ArrayList<>();
			affiliateNewAnalyticsList.forEach(temp ->
			{
				if (temp.getLocation() == UPPER_HEAD_LOCATION)
				{
					upperHeadAnalytics.add(temp.getContent());
				}
				else if (temp.getLocation() == LOWER_HEAD_LOCATION)
				{
					lowerHeadAnalytics.add(temp.getContent());
				}
				else if (temp.getLocation() == BODY_LOCATION)
				{
					bodyAnalytics.add(temp.getContent());
				}
				else if (temp.getLocation() == FOOTER_LOCATION)
				{
					footerAnalytics.add(temp.getContent());
				}
			});
			String compiledUpperHeadAnalytics = MustacheUtil.compileMustacheString(upperHeadAnalytics, scopes);
			String compiledLowerHeadAnalytics = MustacheUtil.compileMustacheString(lowerHeadAnalytics, scopes);
			String compiledBodyAnalytics = MustacheUtil.compileMustacheString(bodyAnalytics, scopes);
			String compiledFooterAnalytics = MustacheUtil.compileMustacheString(footerAnalytics, scopes);

			model.addAttribute("upperHeadAnalytics", compiledUpperHeadAnalytics);
			model.addAttribute("lowerHeadAnalytics", compiledLowerHeadAnalytics);
			model.addAttribute("bodyAnalytics", compiledBodyAnalytics);
			model.addAttribute("footerAnalytics", compiledFooterAnalytics);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("No analytics setup for affiliate " + affiliateId + " at step " + step);
			}
		}
	}

	private void saveSubscriptionTracking(SubscriptionBooking subBooking,
			Map<SubscriptionBookingItem, IBookingTicket> subProducts)
	{
		log.info("Saving subscription tracking records for sub booking: {}", subBooking.getReference());
		for (Map.Entry<SubscriptionBookingItem, IBookingTicket> entry : subProducts.entrySet())
		{
			SubscriptionTracking subTracking = new SubscriptionTracking();
			SubscriptionBookingItem item = entry.getKey();
			subTracking.setSubscriptionId(subBooking.getId());
			subTracking.setBookingTotal(subBooking.getGrandTotal());
			subTracking.setBookingReference(subBooking.getReference());
			subTracking.setProductId(item.getProductId());
			subTracking.setProductName(item.getProductDisplayName());
			analyticsService.saveSubscriptionTracking(subTracking);
		}
	}
}