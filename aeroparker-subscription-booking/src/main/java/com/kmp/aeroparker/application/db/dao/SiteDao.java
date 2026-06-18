package com.kmp.aeroparker.application.db.dao;

import java.sql.SQLException;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.LocationsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.PaymentStepFieldsSubscriptionsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Currencies;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionMembershipSequence;
import com.kmp.aeroparker.subscription.payments.tables.daos.SitesDao;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class SiteDao
{
	private final DSLContext dsl;

	public Sites fetchSiteById(final int siteId)
	{
		return new SitesDao(dsl.configuration()).fetchOneById(siteId);
	}

	public List<PaymentStepFieldsSubscriptions> fetchPaymentStepFieldsSubscriptionBySiteId(final int siteId)
	{
		return new PaymentStepFieldsSubscriptionsDao(dsl.configuration()).fetchBySiteId(siteId);
	}

	public Currencies fetchSiteCurrencyBySiteId(final int siteId)
	{
		return dsl.select(Tables.CURRENCIES.fields())
				.from(Tables.CURRENCIES)
				.join(Tables.LOCATIONS)
				.on(Tables.CURRENCIES.ID.eq(Tables.LOCATIONS.CURRENCY))
				.join(com.kmp.aeroparker.subscription.payments.tables.Sites.AB_SITES)
				.on(Tables.LOCATIONS.ID.eq(com.kmp.aeroparker.subscription.payments.tables.Sites.AB_SITES.LOCATIONID))
				.where(com.kmp.aeroparker.subscription.payments.tables.Sites.AB_SITES.ID.eq(siteId))
				.fetchOneInto(Currencies.class);
	}

	public Locations fetchLocationById(final int locationId)
	{
		return new LocationsDao(dsl.configuration()).fetchOneById(locationId);
	}

	public boolean updateSubscriptionMembershipSequence(
			final SubscriptionMembershipSequence subscriptionMembershipSequence)
	{
		return subscriptionMembershipSequence.save(Tables.SUBSCRIPTION_MEMBERSHIP_SEQUENCE, dsl);
	}

	public SubscriptionMembershipSequence fetchSubscriptionMembershipSequenceBySiteId(final int siteId)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_MEMBERSHIP_SEQUENCE)
				.where(Tables.SUBSCRIPTION_MEMBERSHIP_SEQUENCE.SITE_ID.eq(siteId))
				.orderBy(Tables.SUBSCRIPTION_MEMBERSHIP_SEQUENCE.ID.desc())
				.limit(1)
				.fetchOneInto(SubscriptionMembershipSequence.class);
	}
}