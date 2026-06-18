package com.kmp.aeroparker.application.quotas;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.kmp.aeroparker.application.db.service.QuotaService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.QuotasSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class SubscriptionQuotaChecker
{
	private final QuotaService quotaService;
	private final SubscriptionService subscriptionService;

	public boolean checkProductOccupancy(final int productId, final LocalDate startDate, final int affiliateId)
	{
		boolean isProductAvailable = true;

		List<QuotasSubscription> quotas = quotaService.fetchActiveQuotasSubscriptionBySiteId(affiliateId);
		SubscriptionProductTerms productTerms =
				subscriptionService.fetchSubscriptionProductTermsBySubProductId(productId);

		// Not expected behaviour but if there is no product terms we can't calculate the end date
		if (productTerms == null)
		{
			return false;
		}

		for (QuotasSubscription quota : quotas)
		{
			Integer quotaId = quota.getId();
			List<Integer> productIds =
					quota.getAllProductsEnabled() ? quotaService.fetchSubscriptionProductIdBySiteId(quota.getSiteId())
							: quotaService.fetchQuotasSubscriptionProductProductIdsByQuotaId(quotaId);
			if (CollectionUtils.isEmpty(productIds) || !productIds.contains(productId))
			{
				log.debug("No products set for quota: {}, skipping check.", quotaId);
				continue;
			}

			Integer activeBookings = quotaService.fetchQuotaOccupancy(DateUtil.localDateToDate(startDate),
					getEndDate(startDate, productTerms.getMinimumTerm()), productIds);

			if (activeBookings >= quota.getMaximumOccupancyValue())
			{
				log.debug("Product id: {} reached maximum active bookings for quota id: {} and will be removed from available products.", 
						productId, quotaId);
				isProductAvailable = false;
				break;
			}
		}

		return isProductAvailable;
	}

	private Date getEndDate(final LocalDate startDate, final String strMinimumTerm)
	{
		int minimumTerm = SubscriptionMinimumTerm.valueOf(strMinimumTerm)
				.getIntValue();
		LocalDate subscriptionEndDate = startDate.plusMonths(minimumTerm);
		return DateUtil.localDateToDate(subscriptionEndDate);
	}
}
