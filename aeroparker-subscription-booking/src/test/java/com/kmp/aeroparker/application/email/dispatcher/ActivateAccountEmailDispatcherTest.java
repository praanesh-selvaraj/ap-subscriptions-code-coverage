package com.kmp.aeroparker.application.email.dispatcher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.contact.activation.ContactActivationProcessor;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.EmailService;
import com.kmp.aeroparker.application.model.EmailDispatcherParameters;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Emails;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

@ExtendWith(MockitoExtension.class)
class ActivateAccountEmailDispatcherTest
{
	@Mock
	private EmailService emailService;
	@Mock
	private ContactService contactService;
	@Mock
	private PlaceHolderReplacer placeHolderReplacer;
	@Mock
	private ContactActivationProcessor contactActivationProcessor;
	@Mock
	private GlobalProperties globalProperties;
	@InjectMocks
	private ActivateAccountEmailDispatcher dispatcher;

	@Test
	void testSendMail()
	{
		Contacts contacts = mock(Contacts.class);
		when(contacts.getEmailAddress()).thenReturn("Mario@gmail.com");
		when(contacts.getFirstName()).thenReturn("Mario");
		when(contacts.getLastName()).thenReturn("Mario");
		when(contactService.fetchById(anyInt())).thenReturn(contacts);

		Emails emails = mock(Emails.class);
		when(emails.getSenderAddress()).thenReturn("Mario");
		when(emails.getSenderName()).thenReturn("Mario");
		when(emails.getSubject()).thenReturn("Mario");
		when(emails.getBodyHtml()).thenReturn("Mario");
		when(emails.getSenderAddress()).thenReturn("Mario");

		when(contactActivationProcessor.processContactActivation(anyInt(), anyString())).thenReturn("123");
		when(emailService.fetchEmails(anyInt(), anyInt(), anyInt())).thenReturn(emails);

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);

		Sites site = mock(Sites.class);
		when(site.getId()).thenReturn(1);
		Affiliates affiliates = mock(Affiliates.class);
		when(affiliates.getCode()).thenReturn("SNN");

		when(placeHolderReplacer.replacePlaceHoldersActivationEmail(anyString(), any(), anyString()))
				.thenReturn("body");

		when(emailDispatcherParameters.getContactId()).thenReturn(1);
		when(emailDispatcherParameters.getTimeZone()).thenReturn("1");
		when(emailDispatcherParameters.getServletSchema()).thenReturn("1");
		when(emailDispatcherParameters.getServletName()).thenReturn("1");
		when(emailDispatcherParameters.getSite()).thenReturn(site);
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getAffiliate()).thenReturn(affiliates);

		dispatcher.sendEmail(emailDispatcherParameters);

		verify(emailService).addToQueue(any());
	}

	@Test
	void testSendEmail_Contact_Null()
	{
		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);

		when(emailDispatcherParameters.getContactId()).thenReturn(1);
		when(emailDispatcherParameters.getTimeZone()).thenReturn("1");
		when(emailDispatcherParameters.getServletSchema()).thenReturn("1");
		when(emailDispatcherParameters.getServletName()).thenReturn("1");
		when(emailDispatcherParameters.getSite()).thenReturn(mock(Sites.class));
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getAffiliate()).thenReturn(mock(Affiliates.class));

		dispatcher.sendEmail(emailDispatcherParameters);

		verify(contactService).fetchById(anyInt());
		verifyNoMoreInteractions(contactActivationProcessor);
	}

	@Test
	void testSendEmail_Activation_Code_Null()
	{
		Contacts contacts = mock(Contacts.class);
		when(contactService.fetchById(anyInt())).thenReturn(contacts);

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);

		when(emailDispatcherParameters.getContactId()).thenReturn(1);
		when(emailDispatcherParameters.getTimeZone()).thenReturn("1");
		when(emailDispatcherParameters.getServletSchema()).thenReturn("1");
		when(emailDispatcherParameters.getServletName()).thenReturn("1");
		when(emailDispatcherParameters.getSite()).thenReturn(mock(Sites.class));
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getAffiliate()).thenReturn(mock(Affiliates.class));

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(contactActivationProcessor).processContactActivation(anyInt(), anyString());
		verifyNoMoreInteractions(emailService);
	}

	@Test
	void testSendEmail_Affiliate_Custom_Emai_Null()
	{
		Contacts contacts = mock(Contacts.class);
		when(contactService.fetchById(anyInt())).thenReturn(contacts);

		when(contactActivationProcessor.processContactActivation(anyInt(), anyString())).thenReturn("123");

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);

		when(emailDispatcherParameters.getContactId()).thenReturn(1);
		when(emailDispatcherParameters.getTimeZone()).thenReturn("1");
		when(emailDispatcherParameters.getServletSchema()).thenReturn("1");
		when(emailDispatcherParameters.getServletName()).thenReturn("1");
		when(emailDispatcherParameters.getSite()).thenReturn(mock(Sites.class));
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(emailDispatcherParameters.isEmailFromAffiliate()).thenReturn(true);

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(emailService).fetchAffiliateCustomEmail(anyInt(), anyInt());
	}

	@Test
	void testSendEmail_Email_From_Site()
	{
		Contacts contacts = mock(Contacts.class);
		when(contactService.fetchById(anyInt())).thenReturn(contacts);

		when(contactActivationProcessor.processContactActivation(anyInt(), anyString())).thenReturn("123");

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);

		when(emailDispatcherParameters.getContactId()).thenReturn(1);
		when(emailDispatcherParameters.getTimeZone()).thenReturn("1");
		when(emailDispatcherParameters.getServletSchema()).thenReturn("1");
		when(emailDispatcherParameters.getServletName()).thenReturn("1");
		when(emailDispatcherParameters.getSite()).thenReturn(mock(Sites.class));
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(emailDispatcherParameters.isEmailFromAffiliate()).thenReturn(false);
		when(emailDispatcherParameters.getDefaultLanguageId()).thenReturn(1);

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(emailService).fetchEmails(anyInt(), anyInt(), anyInt());
	}
}
