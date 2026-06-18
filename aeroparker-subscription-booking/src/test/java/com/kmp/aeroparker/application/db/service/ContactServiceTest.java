package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.ContactDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactHashedPassword;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactsActivationCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest
{
	@Mock
	private ContactDao dao;
	@InjectMocks
	private ContactService service;
	@Mock
	private SubscriptionContactMembership subscriptionContactMembership;

	@Test
	void testSaveContact()
	{
		when(dao.saveContact(any())).thenReturn(true);
		assertThat(service.saveContact(mock(Contacts.class))).isTrue();
		verify(dao).saveContact(any());
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testSaveContact_Contact_Null()
	{
		assertThat(service.saveContact(null)).isFalse();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchById()
	{
		when(dao.fetchById(anyInt())).thenReturn(mock(Contacts.class));
		assertThat(service.fetchById(1)).isNotNull();
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFetchById_Id_Invalid()
	{
		assertThat(service.fetchById(0)).isNull();
		verifyNoInteractions(dao);
	}

	@Test
	void testSavehashedPassword_True()
	{
		when(dao.saveHashedPassword(anyInt(), anyString())).thenReturn(true);
		assertThat(service.saveHashedPassword(1,
				"$2a$10$SH5huHIEgaB.AtBwIZ/Yyuuu/4VBUR1C4QvDyCJ1YFOxJaIgkh94ydoesn'tmatch")).isTrue();
	}

	@Test
	void testSavehashedPassword_False()
	{
		when(dao.saveHashedPassword(anyInt(), anyString())).thenReturn(false);
		assertThat(service.saveHashedPassword(1,
				"$2a$10$SH5huHIEgaB.AtBwIZ/Yyuuu/4VBUR1C4QvDyCJ1YFOxJaIgkh94ydoesn'tmatch")).isFalse();
	}

	@Test
	void testSavehashedPassword_ContactId_Is_Zero()
	{
		assertThat(service.saveHashedPassword(0,
				"$2a$10$SH5huHIEgaB.AtBwIZ/Yyuuu/4VBUR1C4QvDyCJ1YFOxJaIgkh94ydoesn'tmatch")).isFalse();
	}

	@Test
	void testSavehashedPassword_When_HashedPassword_IsEmpty()
	{
		assertThat(service.saveHashedPassword(1, ""));
	}

	@Test
	void testSavehashedPassword_DataAccessException()
	{
		assertThat(service.saveHashedPassword(1,
				"$2a$10$SH5huHIEgaB.AtBwIZ/Yyuuu/4VBUR1C4QvDyCJ1YFOxJaIgkh94ydoesn'tmatch")).isFalse();
	}

	@Test
	void testSavehashedPassword_SQLException()
	{
		assertThat(service.saveHashedPassword(1,
				"$2a$10$SH5huHIEgaB.AtBwIZ/Yyuuu/4VBUR1C4QvDyCJ1YFOxJaIgkh94ydoesn'tmatch")).isFalse();
	}

	@Test
	void testSetContactActivated() throws DataAccessException, SQLException
	{
		int contactId = 5;
		assertThat(service.setContactActivated(contactId)).isTrue();
	}

	@Test
	void testSetContactActivated_ContactId_zero() throws DataAccessException, SQLException
	{
		int contactId = 0;
		assertThat(service.setContactActivated(contactId)).isFalse();
	}

	@Test
	void testFetchContactHashedPasswordByContactId()
	{
		when(dao.fetchContactHashedPasswordByContactId(anyInt())).thenReturn(mock(ContactHashedPassword.class));
		int contactId = 3;
		assertThat(service.fetchContactHashedPasswordByContactId(contactId)).isNotNull();
		assertThat(service.fetchContactHashedPasswordByContactId(0)).isNull();
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFetchContactByEmailAndSiteId()
	{
		when(dao.fetchContactByEmailAndSiteId(anyString(), anyInt())).thenReturn(mock(Contacts.class));
		assertThat(service.fetchContactByEmailAndSiteId("test@aeroparker.com", 1)).isNotNull();
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFetchContactByEmailAndSiteId_Email_Empty()
	{
		assertThat(service.fetchContactByEmailAndSiteId("", 1)).isNull();
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFetchContactByEmailAndSiteId_SiteID_Zero()
	{
		assertThat(service.fetchContactByEmailAndSiteId("test@aeroparker.com", 0)).isNull();
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testGetActivationCode()
	{
		when(dao.fetchActivationCodeByContactId(anyInt())).thenReturn(mock(ContactsActivationCodes.class));
		assertThat(service.getActivationCode(1)).isNotNull();
	}

	void testGetActivationCode_Contact_Null()
	{
		when(dao.fetchActivationCodeByContactId(anyInt())).thenReturn(null);
		assertThat(service.getActivationCode(1)).isNotNull();
	}

	void testInsertActivationCode()
	{
		when(dao.saveActivationCode(any())).thenReturn(true);
		assert (service.insertActivationCode(mock(ContactsActivationCodes.class)));
	}

	@Test
	void testFetchSubscriptionContactMembershipIdByContactId()
	{
		when(dao.fetchSubscriptionContactMembershipByContactId(1)).thenReturn(subscriptionContactMembership);
		
		assertNotNull(service.fetchSubscriptionContactMembershipIdByContactId(1));
	}

	@Test
	void testFetchSubscriptionContactMembershipIdByContactId_ContactId_Invalid()
	{		
		assertNull(service.fetchSubscriptionContactMembershipIdByContactId(0));
	}

	@Test
	void testSaveSubscriptionContactMembership()
	{
		when(dao.saveSubscriptionContactMembership(any(SubscriptionContactMembership.class))).thenReturn(true);
		assertTrue(service.saveSubscriptionContactMembership(1, "mem-id"));
	}

	@Test
	void testSaveSubscriptionContactMembership_ContactId_Invalid()
	{
		assertFalse(service.saveSubscriptionContactMembership(0, "mem-id"));
	}

	@Test
	void testSaveSubscriptionContactMembership_MembershipId_Invalid()
	{
		assertFalse(service.saveSubscriptionContactMembership(1, ""));
	}

	@Test
	void testSaveSubscriptionContactMembership_MembershipId_Null()
	{
		assertFalse(service.saveSubscriptionContactMembership(0, null));
	}
}