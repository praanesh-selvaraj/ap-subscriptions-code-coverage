package com.kmp.aeroparker.application.db.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.AnalyticsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateAnalyticsNew;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionTracking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class AnalyticsService
{
	private AnalyticsDao dao;

	public List<AffiliateAnalyticsNew> getAnalyticsByAffiliateId(int affiliateId)
	{
		List<AffiliateAnalyticsNew> analytics = new ArrayList<>();
		if (affiliateId > 0)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Retrieving analytics for " + affiliateId);
			}
			analytics = dao.fetchByAffiliateId(affiliateId);
		}
		else if (log.isDebugEnabled())
		{
			log.warn("AffiliateId was invalid. No affiliate analytics will be retrieved");
		}
		return analytics;
	}

	public boolean saveSubscriptionTracking(SubscriptionTracking subscriptionTracking)
	{
		boolean saved = false;
		if (subscriptionTracking != null)
		{
			saved = dao.saveSubscriptionTracking(subscriptionTracking);
		}
		else if (log.isDebugEnabled())
		{
			log.debug("Unable to save as SubscriptionTracking object is null");
		}
		return saved;
	}
}
