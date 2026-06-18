package com.kmp.aeroparker.application.db.dao;

import java.sql.Date;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.QuotasSubscription;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class QuotaDao
{
	private final DSLContext dsl;
	
	public List<QuotasSubscription> fetchActiveQuotasSubscriptionBySiteId(final int affiliateId)
	{
		return dsl.select(Tables.QUOTAS_SUBSCRIPTION.fields())
				.from(Tables.QUOTAS_SUBSCRIPTION)
				.join(Tables.AB_AFFILIATES)
				.on(Tables.QUOTAS_SUBSCRIPTION.SITE_ID.eq(Tables.AB_AFFILIATES.SITEID))
				.where(Tables.QUOTAS_SUBSCRIPTION.MAXIMUM_OCCUPANCY_ENABLED.isTrue())
				.and(Tables.AB_AFFILIATES.ID.eq(affiliateId))
				.fetchInto(QuotasSubscription.class);
	}

	public List<Integer> fetchQuotasSubscriptionProductProductIdsByQuotaId(final int quotaId)
	{
		return dsl.select(Tables.QUOTAS_SUBSCRIPTION_PRODUCT.SUBSCRIPTION_PRODUCT_ID)
				.from(Tables.QUOTAS_SUBSCRIPTION_PRODUCT)
				.where(Tables.QUOTAS_SUBSCRIPTION_PRODUCT.SUBSCRIPTION_QUOTA_ID.eq(quotaId))
				.fetchInto(Integer.class);
	}

	public List<Integer> fetchSubscriptionProductIdBySiteId(final int siteId)
	{
		return dsl.select(Tables.SUBSCRIPTION_PRODUCT.ID)
				.from(Tables.SUBSCRIPTION_PRODUCT)
				.where(Tables.SUBSCRIPTION_PRODUCT.SITE_ID.eq(siteId))
				.fetchInto(Integer.class);
	}

	public Integer fetchQuotaOccupancy(Date startDate, Date endDateTime,
			List<Integer> quotaProductIds)
	 {
			return dsl.selectCount()
					.from(Tables.SUBSCRIPTION_BOOKING)
					.join(Tables.SUBSCRIPTION_BOOKING_ITEM)
					.on(Tables.SUBSCRIPTION_BOOKING_ITEM.SUB_BOOKING_ID.eq(Tables.SUBSCRIPTION_BOOKING.ID))
					.join(Tables.SUBSCRIPTION_BOOKING_TICKET)
					.on(Tables.SUBSCRIPTION_BOOKING_TICKET.BOOKING_ID.eq(Tables.SUBSCRIPTION_BOOKING.ID))
					.where(Tables.SUBSCRIPTION_BOOKING.CANCELLED.isFalse())
					.and(Tables.SUBSCRIPTION_BOOKING_TICKET.START_DATE.le(endDateTime))
					.and(Tables.SUBSCRIPTION_BOOKING_TICKET.END_DATE.ge(startDate))
					.and(Tables.SUBSCRIPTION_BOOKING_ITEM.PRODUCT_ID.in(quotaProductIds))
					.fetchOneInto(Integer.class);
	 }
}
