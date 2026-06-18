package com.kmp.aeroparker.application.processor;

import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SubscriptionContactMembershipProcessor implements IProcessor
{
	private final ContactService contactService;
	private final SubscriptionMembershipGenerator subscriptionMembershipGenerator;

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP;
	}

	public String process(int affiliateId, int siteId, int contactId)
	{
		SubscriptionContactMembership subscriptionContactMembership =
				contactService.fetchSubscriptionContactMembershipIdByContactId(contactId);
		String membershipId = subscriptionMembershipGenerator.generateMembershipId(subscriptionContactMembership, affiliateId,
				siteId);

		if (contactService.saveSubscriptionContactMembership(contactId, membershipId))
		{
			log.debug("Successfully generated & saved membership id for contact: {}", contactId);
		}
		else
		{
			log.debug("Couldn't saved membership id for contact: {}", contactId);
		}
		return membershipId;
	}
}
