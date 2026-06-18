package com.kmp.aeroparker.application.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;

@ExtendWith(MockitoExtension.class)
class SubscriptionMembershipGeneratorTest
{
	// Class Under Test
	@InjectMocks
	private SubscriptionMembershipGenerator subscriptionMembershipGenerator;
	
	// Injected Mocks
	@Mock
	private SiteService siteService;
	@Mock
	private AffiliateService affiliateService;

	// Other Mocks
	@Mock
	private AffiliateSubscriptionSettings affliAffiliateSubscriptionSettings;
	@Mock
	private Sites site;
	@Mock
	private SubscriptionContactMembership subscriptionContactMembership;
	
	@Test
	void testGenerateMembershipId()
	{
		int affiliateId = 1;
		int siteId = 1;
		String membershipIdFormat = "MEM{I}ABZ";
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(affliAffiliateSubscriptionSettings.getMembershipIdFormat()).thenReturn(membershipIdFormat);
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(affliAffiliateSubscriptionSettings);
		when(siteService.getNextMembershipSequence(siteId)).thenReturn(1);
		
		assertEquals("MEM1ABZ", subscriptionMembershipGenerator.generateMembershipId(null, affiliateId, siteId));
	}

	@Test
	void testGenerateMembershipId_IncrementMembershipId()
	{
		int affiliateId = 1;
		int siteId = 1;
		String membershipIdFormat = "MEM{I}ABZ";
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getIncrementMembershipIdPerBooking()).thenReturn(true);
		when(affliAffiliateSubscriptionSettings.getMembershipIdFormat()).thenReturn(membershipIdFormat);
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(affliAffiliateSubscriptionSettings);
		when(siteService.getNextMembershipSequence(siteId)).thenReturn(1);

		assertEquals("MEM1ABZ", subscriptionMembershipGenerator.generateMembershipId(subscriptionContactMembership, affiliateId, siteId));
	}

	@Test
	void testGenerateMembershipId_ExistingMember_IncrementDisabled()
	{
		int affiliateId = 1;
		int siteId = 1;
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getIncrementMembershipIdPerBooking()).thenReturn(false);
		when(subscriptionContactMembership.getMembershipId()).thenReturn("MEM1ABZ");

		assertEquals("MEM1ABZ", subscriptionMembershipGenerator.generateMembershipId(subscriptionContactMembership, affiliateId, siteId));
	}

	@Test
	void testGenerateMembershipId_JustSequence()
	{
		int affiliateId = 1;
		int siteId = 1;
		String membershipIdFormat = "{I}";
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(affliAffiliateSubscriptionSettings.getMembershipIdFormat()).thenReturn(membershipIdFormat);
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(affliAffiliateSubscriptionSettings);
		when(siteService.getNextMembershipSequence(siteId)).thenReturn(1);

		
		assertEquals("1", subscriptionMembershipGenerator.generateMembershipId(null, affiliateId, siteId));
	}

	@Test
	void testGenerateMembershipId_SubscriptionSettings_Null()
	{
		int affiliateId = 1;
		int siteId = 1;
		when(siteService.getNextMembershipSequence(siteId)).thenReturn(1);
		
		assertEquals("1", subscriptionMembershipGenerator.generateMembershipId(null, affiliateId, siteId));
	}

	@Test
	void testGenerateMembershipId_MembershipIdFormat_Null()
	{
		int affiliateId = 1;
		int siteId = 1;
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(affliAffiliateSubscriptionSettings);
		when(affliAffiliateSubscriptionSettings.getMembershipIdFormat()).thenReturn(null);
		when(siteService.getNextMembershipSequence(siteId)).thenReturn(1);
		
		assertEquals("1", subscriptionMembershipGenerator.generateMembershipId(null, affiliateId, siteId));
	}
}
