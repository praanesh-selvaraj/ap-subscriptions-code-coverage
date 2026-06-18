package com.kmp.aeroparker.application.db.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.kmp.aeroparker.application.db.dao.QuotaDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.QuotasSubscription;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Service
@Slf4j
public class QuotaService
{
	private final QuotaDao dao;

	public List<QuotasSubscription> fetchActiveQuotasSubscriptionBySiteId(final int siteId)
	{
		List<QuotasSubscription> quotasSubscriptions = new ArrayList<>();

		if (siteId > 0)
		{
			quotasSubscriptions = dao.fetchActiveQuotasSubscriptionBySiteId(siteId);
		}
		else
		{
			log.debug("Site id was zero. Unable to fetch quotas subscriptions.");
		}

		return quotasSubscriptions;
	}

	public List<Integer> fetchQuotasSubscriptionProductProductIdsByQuotaId(final int quotaId)
	{
		List<Integer> productIds = new ArrayList<>();

		if (quotaId > 0)
		{
			productIds = dao.fetchQuotasSubscriptionProductProductIdsByQuotaId(quotaId);
		}
		else
		{
			log.debug("Quota id was zero. Unable to fetch quotas subscription product ids.");
		}

		return productIds;
	}

	public List<Integer> fetchSubscriptionProductIdBySiteId(final int siteId)
	{
		List<Integer> productIds = new ArrayList<>();

		if (siteId > 0)
		{
			productIds = dao.fetchSubscriptionProductIdBySiteId(siteId);
		}
		else
		{
			log.debug("Site id was zero. Unable to fetch subscription product ids.");
		}

		return productIds;
	}

	public Integer fetchQuotaOccupancy(Date startDate, Date endDate, List<Integer> quotaProductIds)
	{
		Integer activeBookings = 0;

		if (startDate == null)
		{
			log.debug("Start date is null. Unable to fetch count for active subscription bookings.");
		}
		else if (endDate == null)
		{
			log.debug("End date is null. Unable to fetch count for active subscription bookings.");
		}
		else if (CollectionUtils.isEmpty(quotaProductIds))
		{
			log.debug("Product ids are empty. Unable to fetch count for active subscription bookings.");
		}
		else
		{
			activeBookings = dao.fetchQuotaOccupancy(startDate, endDate, quotaProductIds);
		}

		return activeBookings;
	}
}
