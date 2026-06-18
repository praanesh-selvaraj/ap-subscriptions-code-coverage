package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.EmailDao;
import com.kmp.aeroparker.application.model.enums.SubscriptionEmailType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateCustomEmail;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.EmailQueue;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Emails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmail;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmailAppearance;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest
{
	@Mock
	private EmailDao dao;
	@InjectMocks
	private EmailService service;
	@Mock
	private Sites site;

	@Test
	void testFetchSubscriptionEmailBySiteAndType()
	{
		when(dao.fetchSubscriptionEmailBySiteAndType(anyInt(), anyString())).thenReturn(mock(SubscriptionEmail.class));
		assertThat(service.fetchSubscriptionEmailBySiteAndType(1, SubscriptionEmailType.CONFIRMATION.toString()))
				.isNotNull()
				.isInstanceOf(SubscriptionEmail.class);
	}

	@Test
	void testFetchSubscriptionEmailBySiteAndType_Invalid_Params()
	{
		assertThat(service.fetchSubscriptionEmailBySiteAndType(1, "")).isNull();
		assertThat(service.fetchSubscriptionEmailBySiteAndType(0, SubscriptionEmailType.CONFIRMATION.toString()))
				.isNull();
		assertThat(service.fetchSubscriptionEmailBySiteAndType(0, "")).isNull();
	}

	@Test
	void testFetchSubscriptionEmailAppearanceByEmailIdAndType()
	{
		when(dao.fetchSubscriptionEmailAppearanceByEmailIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(mock(SubscriptionEmailAppearance.class));
		assertThat(service.fetchSubscriptionEmailAppearanceByEmailIdAndType(1, 1)).isNotNull()
				.isInstanceOf(SubscriptionEmailAppearance.class);
	}

	@Test
	void testFetchSubscriptionEmailAppearanceByEmailIdAndType_Invalid_Params()
	{
		assertThat(service.fetchSubscriptionEmailAppearanceByEmailIdAndType(1, 0)).isNull();
		assertThat(service.fetchSubscriptionEmailAppearanceByEmailIdAndType(0, 0)).isNull();
		assertThat(service.fetchSubscriptionEmailAppearanceByEmailIdAndType(0, 1)).isNull();
	}

	@Test
	void testAddToQueue()
	{
		assertThat(service.addToQueue(mock(EmailQueue.class))).isTrue();
		verify(dao).addToQueue(any());
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testAddToQueue_EmailQueue_Null()
	{
		assertThat(service.addToQueue(null)).isFalse();
		verifyNoInteractions(dao);
	}

	@Test
	public void testAddToQueue_Create_EmailQueue()
	{
		assertThat(service.addToQueue(site, "customerName", EnhancedRandom.random(SubscriptionEmailAppearance.class),
				"emailAddress", "body")).isTrue();
		verify(dao).addToQueue(any());
		verifyNoMoreInteractions(dao);
	}

	@Test
	public void testCreateEmailQueue()
	{
		assertThat(service.createEmailQueue(site, "customerName",
				EnhancedRandom.random(SubscriptionEmailAppearance.class), "emailAddress", "body")).isNotNull()
						.isInstanceOf(EmailQueue.class);
	}

	@Test
	void testFetchAffiliateCustomEmail()
	{
		when(dao.fetchActivateAccountEmail(anyInt(), anyInt())).thenReturn(mock(AffiliateCustomEmail.class));
		service.fetchAffiliateCustomEmail(1, 1);
		verify(dao).fetchActivateAccountEmail(anyInt(), anyInt());
	}

	@Test
	void testFetchAffiliateCustomEmail_Affiliate_Id_Null()
	{
		assertNull(service.fetchAffiliateCustomEmail(0, 1));
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFetchAffiliateCustomEmail_Lang_Id_Null()
	{
		assertNull(service.fetchAffiliateCustomEmail(1, 0));
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFetchEmails()
	{
		Emails emails = mock(Emails.class);
		when(emails.getLanguageId()).thenReturn(1);
		List<Emails> emailList = new ArrayList<>();
		emailList.add(emails);
		when(dao.fetchEmails(anyInt())).thenReturn(emailList);
		assertNotNull(service.fetchEmails(1, 1, 1));
	}

	@Test
	void testFetchEmails_Default_Language_Id()
	{
		Emails emails = mock(Emails.class);
		when(emails.getLanguageId()).thenReturn(1);
		List<Emails> emailList = new ArrayList<>();
		emailList.add(emails);
		when(dao.fetchEmails(anyInt())).thenReturn(emailList);
		assertNotNull(service.fetchEmails(1, 4, 1));
	}

	@Test
	void testFetchEmails_Site_Id_Invalid()
	{
		assertNull(service.fetchEmails(0, 1, 1));
	}
}