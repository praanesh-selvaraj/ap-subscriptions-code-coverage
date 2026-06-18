package com.kmp.aeroparker.application.db.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.ContactHashedPasswordDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.ContactsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactHashedPassword;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactsActivationCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class ContactDao
{
	private final DSLContext dsl;

	public boolean saveContact(final Contacts contact)
	{
		return contact.save(Tables.AB_CONTACTS, dsl);
	}

	public Contacts fetchById(final int contactId)
	{
		return new ContactsDao(dsl.configuration()).fetchOneById(contactId);
	}

	public boolean saveHashedPassword(final int contactId, final String hashedPassword)
	{
		return dsl
				.insertInto(Tables.CONTACT_HASHED_PASSWORD, Tables.CONTACT_HASHED_PASSWORD.CONTACT_ID,
						Tables.CONTACT_HASHED_PASSWORD.HASHED_PASSWORD)
				.values(contactId, hashedPassword)
				.onDuplicateKeyUpdate()
				.set(Tables.CONTACT_HASHED_PASSWORD.HASHED_PASSWORD, hashedPassword)
				.execute() > 0;
	}

	public boolean setContactActivated(final int contactId)
	{
		return dsl.update(Tables.AB_CONTACTS)
				.set(Tables.AB_CONTACTS.ACTIVATED, 1)
				.where(Tables.AB_CONTACTS.ID.eq(contactId))
				.execute() > 0;
	}

	public ContactHashedPassword fetchContactHashedPasswordByContactId(final int contactId)
	{
		return new ContactHashedPasswordDao(dsl.configuration()).fetchOneByContactId(contactId);
	}

	public Contacts fetchContactByEmailAndSiteId(String email, int siteId)
	{
		return dsl.selectFrom(Tables.AB_CONTACTS)
				.where(Tables.AB_CONTACTS.EMAILADDRESS.eq(email)
						.and(Tables.AB_CONTACTS.SITEID.eq(siteId)))
				.orderBy(Tables.AB_CONTACTS.ID.desc())
				.fetchInto(Contacts.class)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public ContactsActivationCodes fetchActivationCodeByContactId(int id)
	{
		return dsl.selectFrom(Tables.AB_CONTACTS_ACTIVATION_CODES)
				.where(Tables.AB_CONTACTS_ACTIVATION_CODES.CONTACTID.eq(id))
				.fetchOneInto(ContactsActivationCodes.class);
	}

	public boolean saveActivationCode(ContactsActivationCodes contactActivactionCode)
	{
		return contactActivactionCode.save(Tables.AB_CONTACTS_ACTIVATION_CODES, dsl);
	}

	public SubscriptionContactMembership fetchSubscriptionContactMembershipByContactId(int contactId) {
	    return dsl.selectFrom(Tables.SUBSCRIPTION_CONTACT_MEMBERSHIP)
	              .where(Tables.SUBSCRIPTION_CONTACT_MEMBERSHIP.CONTACT_ID.eq(contactId))
	              .orderBy(Tables.SUBSCRIPTION_CONTACT_MEMBERSHIP.ID.desc())
	              .limit(1)
	              .fetchOneInto(SubscriptionContactMembership.class);
	}

	public boolean saveSubscriptionContactMembership(SubscriptionContactMembership subscriptionContactMembership)
	{
		int result = dsl.insertInto(Tables.SUBSCRIPTION_CONTACT_MEMBERSHIP)
				.set(Tables.SUBSCRIPTION_CONTACT_MEMBERSHIP.CONTACT_ID, subscriptionContactMembership.getContactId())
				.set(Tables.SUBSCRIPTION_CONTACT_MEMBERSHIP.MEMBERSHIP_ID, subscriptionContactMembership.getMembershipId())
				.onDuplicateKeyUpdate()
				.set(Tables.SUBSCRIPTION_CONTACT_MEMBERSHIP.MEMBERSHIP_ID, subscriptionContactMembership.getMembershipId()) // No-op update or update timestamp, etc.
				.execute();

		return result > 0;
	}
}