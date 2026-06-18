package com.kmp.aeroparker.application.processor;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionMembershipGenerator
{
	private final SiteService siteService;
	private final AffiliateService affiliateService;

	private static final String DEFAULT_FORMAT = "{I}";

	/**
	 * @param subscriptionContactMembership
	 * @param affiliateId
	 * @param siteId
	 * @returnt
	 */
	public String generateMembershipId(SubscriptionContactMembership subscriptionContactMembership, int affiliateId,
			int siteId)
	{
		String membershipId = DEFAULT_FORMAT;
		Sites site = siteService.fetchSiteById(siteId);

		if (subscriptionContactMembership != null && Boolean.FALSE.equals(site.getIncrementMembershipIdPerBooking()))
		{
			log.debug("Membership id already exists for contact: {} , no new membership id needed.",
					subscriptionContactMembership.getContactId());
			membershipId = subscriptionContactMembership.getMembershipId();
		}
		else
		{
			AffiliateSubscriptionSettings affiliateSubscriptionSettings =
					affiliateService.fetchSubscriptionSettingsByAffiliateId(affiliateId);
			if (affiliateSubscriptionSettings != null
					&& !StringUtil.isNullOrEmpty(affiliateSubscriptionSettings.getMembershipIdFormat()))
			{
				membershipId = affiliateSubscriptionSettings.getMembershipIdFormat().trim();
			}
			membershipId = StringUtil.replace(membershipId, DEFAULT_FORMAT,
					String.valueOf(siteService.getNextMembershipSequence(siteId)));
		}
		return membershipId;
	}
}
