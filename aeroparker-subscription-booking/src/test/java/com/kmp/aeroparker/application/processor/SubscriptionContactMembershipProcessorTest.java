package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;

@ExtendWith(MockitoExtension.class)
class SubscriptionContactMembershipProcessorTest
{
	// Class Under Test
	@InjectMocks
	private SubscriptionContactMembershipProcessor processor;

	// Injected Mocks
	@Mock
	private ContactService contactService;
	@Mock
	private SubscriptionMembershipGenerator subscriptionMembershipGenerator;

	// Other Mocks
	@Mock
	private SubscriptionContactMembership subscriptionContactMembership;

	private final static int AFFILIATE_ID = 1;
	private final static int SITE_ID = 1;
	private final static int CONTACT_ID = 1;
	private final static String MEMBERSHIP_ID = "MEM123";

	@Test
	void testProcess_savesSuccessfully()
	{
		when(contactService.fetchSubscriptionContactMembershipIdByContactId(CONTACT_ID))
				.thenReturn(subscriptionContactMembership);
		when(subscriptionMembershipGenerator.generateMembershipId(subscriptionContactMembership, AFFILIATE_ID, SITE_ID))
				.thenReturn(MEMBERSHIP_ID);
		when(contactService.saveSubscriptionContactMembership(CONTACT_ID, MEMBERSHIP_ID)).thenReturn(true);

		String result = processor.process(AFFILIATE_ID, SITE_ID, CONTACT_ID);

		assertThat(result).isEqualTo(MEMBERSHIP_ID);
		verify(contactService).fetchSubscriptionContactMembershipIdByContactId(CONTACT_ID);
		verify(subscriptionMembershipGenerator).generateMembershipId(subscriptionContactMembership, AFFILIATE_ID,
				SITE_ID);
		verify(contactService).saveSubscriptionContactMembership(CONTACT_ID, MEMBERSHIP_ID);
	}

	@Test
	void testProcess_saveFails()
	{
		when(contactService.fetchSubscriptionContactMembershipIdByContactId(CONTACT_ID))
				.thenReturn(subscriptionContactMembership);
		when(subscriptionMembershipGenerator.generateMembershipId(subscriptionContactMembership, AFFILIATE_ID, SITE_ID))
				.thenReturn(MEMBERSHIP_ID);
		when(contactService.saveSubscriptionContactMembership(CONTACT_ID, MEMBERSHIP_ID)).thenReturn(false);

		String result = processor.process(AFFILIATE_ID, SITE_ID, CONTACT_ID);

		assertThat(result).isEqualTo(MEMBERSHIP_ID);
		verify(contactService).fetchSubscriptionContactMembershipIdByContactId(CONTACT_ID);
		verify(subscriptionMembershipGenerator).generateMembershipId(subscriptionContactMembership, AFFILIATE_ID,
				SITE_ID);
		verify(contactService).saveSubscriptionContactMembership(CONTACT_ID, MEMBERSHIP_ID);
	}
}