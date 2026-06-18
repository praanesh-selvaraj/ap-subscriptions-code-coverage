package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactsActivationCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;

import io.github.benas.randombeans.api.EnhancedRandom;

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ContactDao.class })
class ContactDaoIT
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getContactInstance();
	@Autowired
	private ContactDao dao;

	@Test
	void testSaveContact()
	{
		Contacts contact = EnhancedRandom.random(Contacts.class, "id");
		contact.setSiteId(17);
		contact.setFirstName("Test");
		contact.setLanguageId(1);
		contact.setTitle("Mr");
		assertThat(dao.saveContact(contact)).isTrue();
		assertThat(dao.fetchById(51023)).isNotNull();
	}

	@Test
	void testFetchById()
	{
		assertThat(dao.fetchById(51023)).isNotNull()
				.hasFieldOrPropertyWithValue("firstName", "Noman");
	}

	@Test
	void testsaveHashedPassword() throws DataAccessException, SQLException
	{
		assertThat(dao.fetchById(51460)).isNotNull();
		dao.saveHashedPassword(51460, "afdsdasdf1321");
		assertThat(dao.fetchContactHashedPasswordByContactId(51460)).isNotNull();
	}

	@Test
	void testContactHahsedPassword() throws DataAccessException, SQLException
	{
		assertThat(dao.fetchContactHashedPasswordByContactId(51460)).isNotNull()
				.hasFieldOrPropertyWithValue("contactId", 51460);
	}

	@Test
	void testSetContactActivated() throws DataAccessException, SQLException
	{
		dao.setContactActivated(51460);
		assertThat(dao.fetchById(51460)).isNotNull()
				.hasFieldOrPropertyWithValue("firstName", "zarrar");
	}

	@Test
	void testFetchContactByEmailAndSiteId()
	{
		assertThat(dao.fetchContactByEmailAndSiteId("derrick.feehi@aeroparker.com", 27)).isNotNull()
				.hasFieldOrPropertyWithValue("firstName", "Derrick");
	}

	@Test
	void testFetchActivationCodeById()
	{
		assertThat(dao.fetchActivationCodeByContactId(17514)).isNotNull()
				.hasFieldOrPropertyWithValue("id", 121);
	}

	@Test
	void testInsertActivationCode()
	{
		assertThat(dao.fetchById(17514)).isNotNull();
		ContactsActivationCodes contactsActivationCodes = EnhancedRandom.random(ContactsActivationCodes.class);
		contactsActivationCodes.setActivationCode("afdsdasdf1321");
		contactsActivationCodes.setContactId(17515);
		assert (dao.saveActivationCode(contactsActivationCodes));
	}

	@Test
	void testFetchSubscriptionContactMembershipByContactId()
	{
		assertThat(dao.fetchSubscriptionContactMembershipByContactId(51460)).isNotNull();
	}
	
	@Test
	void saveSubscriptionContactMembership()
	{
		SubscriptionContactMembership subscriptionContactMembership = new SubscriptionContactMembership();
		subscriptionContactMembership.setContactId(51461);
		subscriptionContactMembership.setMembershipId("123");
		assertTrue(dao.saveSubscriptionContactMembership(subscriptionContactMembership));
	}
}