package com.kmp.aeroparker.application.db.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.SiteDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Currencies;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionMembershipSequence;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SiteService
{
	private final SiteDao dao;

	public Sites fetchSiteById(final int siteId)
	{
		Sites site = null;
		if (siteId < 1)
		{
			log.info("site ID is not greater than 0, site will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch site using ID {}", siteId);
			site = dao.fetchSiteById(siteId);
			log.debug("Successfully fetched site {}", site);
		}
		return site;
	}

	public List<PaymentStepFieldsSubscriptions> fetchPaymentStepFieldsSubscriptionBySiteId(final int siteId)
	{
		List<PaymentStepFieldsSubscriptions> paymentStepFieldsIncludeds = new ArrayList<>();
		if (siteId < 1)
		{
			log.info("site ID is not greater than 0, payment step fields will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch Payment step fields subscriptions using site ID: {}", siteId);
			paymentStepFieldsIncludeds = dao.fetchPaymentStepFieldsSubscriptionBySiteId(siteId);
			log.debug("Successfully fetched Payment step fields subscriptions:  {}", paymentStepFieldsIncludeds);
		}
		return paymentStepFieldsIncludeds;
	}

	public Currencies fetchSiteCurrencyBySiteId(final int siteId)
	{
		Currencies currency = null;
		if (siteId < 1)
		{
			log.info("site ID is not greater than 0, Site currency will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch currency using site ID: {}", siteId);
			currency = dao.fetchSiteCurrencyBySiteId(siteId);
			log.debug("Successfully fetched currency:  {}", currency);
		}
		return currency;
	}

	public Locations fetchLocationById(final int locationId)
	{
		Locations location = null;
		if (locationId < 1)
		{
			log.info("Location ID is not greater than 0, location will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch location using location ID: {}", locationId);
			location = dao.fetchLocationById(locationId);
			log.debug("Successfully fetched location:  {}", location);
		}
		return location;
	}

	public boolean updateSubscriptionMembershipSequence(
			final SubscriptionMembershipSequence subscriptionMembershipSequence)
	{
		boolean success = false;
		if (subscriptionMembershipSequence != null)
		{
			success = dao.updateSubscriptionMembershipSequence(subscriptionMembershipSequence);
		}
		else
		{
			log.debug("Subscription membership sequence was null. Could not update subscription membership sequence");
		}
		return success;
	}

	public SubscriptionMembershipSequence fetchSubscriptionMembershipSequenceBySiteId(final int siteId)
	{
		SubscriptionMembershipSequence membershipSequence = null;
		if (siteId > 0)
		{
			membershipSequence = dao.fetchSubscriptionMembershipSequenceBySiteId(siteId);
		}
		else
		{
			log.debug("Site id is zero. Could not fetch subscription membership sequence");
		}
		return membershipSequence;
	}

	public int getNextMembershipSequence(final int siteId)
	{
		log.debug("Attempting to get next membership sequence");
		int membershipSequence = 0;
		if (siteId > 0)
		{
			SubscriptionMembershipSequence subscriptionMembershipSequence =
					fetchSubscriptionMembershipSequenceBySiteId(siteId);
			if (subscriptionMembershipSequence == null)
			{
				subscriptionMembershipSequence = new SubscriptionMembershipSequence();
				subscriptionMembershipSequence.setSiteId(siteId);
				subscriptionMembershipSequence.setValue(0);
			}
			subscriptionMembershipSequence.setValue(subscriptionMembershipSequence.getValue() + 1);
			if (updateSubscriptionMembershipSequence(subscriptionMembershipSequence))
			{
				membershipSequence = subscriptionMembershipSequence.getValue();
			}
		}
		else
		{
			log.debug("Site id was zero. Could not fetch next sequence for subscription membership id.");
		}
		return membershipSequence;
	}
}