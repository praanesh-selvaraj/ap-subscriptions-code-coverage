package com.kmp.aeroparker.application.db.dao;

import java.sql.SQLException;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateAnalyticsNew;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionTracking;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class AnalyticsDao
{
	private DSLContext dsl;

	public List<AffiliateAnalyticsNew> fetchByAffiliateId(int affiliateId)
	{
		return dsl.selectFrom(Tables.AFFILIATE_ANALYTICS_NEW)
				.where(Tables.AFFILIATE_ANALYTICS_NEW.AFFILIATEID.eq(affiliateId))
				.fetchInto(AffiliateAnalyticsNew.class);
	}

	public boolean saveSubscriptionTracking(SubscriptionTracking subscriptionTracking)
	{
		return dsl.insertInto(Tables.SUBSCRIPTION_TRACKING)
				.set(dsl.newRecord(Tables.SUBSCRIPTION_TRACKING, subscriptionTracking))
				.execute() > 0;
	}
}
