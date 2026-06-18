package com.kmp.aeroparker.application.db.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kmp.aeroparker.application.db.dao.ContactDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactHashedPassword;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactsActivationCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class ContactService
{
	private final ContactDao dao;

	public boolean saveContact(final Contacts contact)
	{
		boolean saved = false;
		if (contact != null)
		{
			saved = dao.saveContact(contact);
		}
		else
		{
			log.debug("Contact is null, contact will not be saved");
		}
		return saved;
	}

	public Contacts fetchById(final int contactId)
	{
		Contacts contact = null;
		if (contactId > 0)
		{
			contact = dao.fetchById(contactId);
		}
		else
		{
			log.debug("Contact ID is invalid, contact will not be fetched");
		}
		return contact;
	}

	public boolean saveHashedPassword(final int contactId, final String hashedPassword)
	{
		boolean success = false;
		if (contactId < 1)
		{
			log.debug("Hashed password will not be saved the contact ID is not greater than 0");
		}
		else if (StringUtil.isEmpty(hashedPassword))
		{
			log.debug("Hashed password will not be saved the hashed password is null or empty");
		}
		else
		{
			success = dao.saveHashedPassword(contactId, hashedPassword);
		}
		return success;
	}

	public boolean setContactActivated(final int contactId)
	{
		boolean saved = false;
		if (contactId >= 1)
		{
			dao.setContactActivated(contactId);
			saved = true;
		}
		return saved;
	}

	public ContactHashedPassword fetchContactHashedPasswordByContactId(final int contactId)
	{
		ContactHashedPassword contactPassword = null;
		if (contactId >= 1)
		{
			contactPassword = dao.fetchContactHashedPasswordByContactId(contactId);
		}
		else
		{
			log.debug("Contact ID is invalid, password will not be fetched");
		}
		return contactPassword;
	}

	public Contacts fetchContactByEmailAndSiteId(String email, int siteId)
	{
		Contacts contact = null;
		if (!StringUtil.isEmpty(email) && siteId > 0)
		{
			contact = dao.fetchContactByEmailAndSiteId(email, siteId);
		}
		else
		{
			log.debug("Email or site Id is invalid, contact will not be fetched");
		}
		return contact;
	}

	public ContactsActivationCodes getActivationCode(int contactId)
	{
		ContactsActivationCodes contactsActivationCodes = null;
		if (contactId > 0)
		{
			contactsActivationCodes = dao.fetchActivationCodeByContactId(contactId);
		}
		else
		{
			log.debug("Contact Id is invalid, contact will not be fetched");
		}

		return contactsActivationCodes;
	}

	public boolean insertActivationCode(ContactsActivationCodes contactActivactionCode)
	{
		if (contactActivactionCode != null)
		{
			return dao.saveActivationCode(contactActivactionCode);

		}
		log.debug("Activation Code invalid, data not inserted");
		return false;
	}
	
	public SubscriptionContactMembership fetchSubscriptionContactMembershipIdByContactId(int contactId)
	{
		SubscriptionContactMembership subscriptionContactMembershipId = null;
		if (contactId > 0)
		{
			subscriptionContactMembershipId = dao.fetchSubscriptionContactMembershipByContactId(contactId);
		}
		else
		{
			log.debug("Contact Id is invalid, subscription contact membership will not be fetched");
		}
		return subscriptionContactMembershipId;
	}

	public boolean saveSubscriptionContactMembership(int contactId, String membershipId)
	{
		boolean saved = false;
		if (contactId > 0 && StringUtils.hasText(membershipId))
		{
			SubscriptionContactMembership subscriptionContactMembership = new SubscriptionContactMembership();
			subscriptionContactMembership.setContactId(contactId);
			subscriptionContactMembership.setMembershipId(membershipId);
			saved = dao.saveSubscriptionContactMembership(subscriptionContactMembership);
		}
		else
		{
			log.debug("Contact Id is invalid, contact will not be fetched");
		}
		return saved;
	}
}