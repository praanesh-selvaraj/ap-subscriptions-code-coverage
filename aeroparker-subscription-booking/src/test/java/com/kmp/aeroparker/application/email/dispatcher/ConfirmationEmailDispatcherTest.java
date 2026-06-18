
package com.kmp.aeroparker.application.email.dispatcher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.EmailService;
import com.kmp.aeroparker.application.model.EmailDispatcherParameters;
import com.kmp.aeroparker.application.model.enums.SubscriptionEmailType;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmail;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmailAppearance;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

@ExtendWith(MockitoExtension.class)
class ConfirmationEmailDispatcherTest
{

	@Mock
	private Localise localise;

	@Mock
	private EmailService emailService;

	@Mock
	private ContactService contactService;

	@Mock
	private EmailAppearanceValidator validator;

	@Mock
	private PlaceHolderReplacer placeHolderReplacer;

	@InjectMocks
	private ConfirmationEmailDispatcher dispatcher;

	@Mock
	private Sites site;

	@Test
	void testSendEmail()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBookingCustomerDetails bookingCustomerDetails = mock(SubscriptionBookingCustomerDetails.class);
		when(bookingRecord.getCustomerDetails()).thenReturn(bookingCustomerDetails);
		when(bookingCustomerDetails.getEmailAddress()).thenReturn("email@address.com");
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(booking.getContactId()).thenReturn(23123);
		when(bookingRecord.getBooking()).thenReturn(booking);
		SubscriptionEmail email = mock(SubscriptionEmail.class);
		when(email.getId()).thenReturn(1);
		when(emailService.fetchSubscriptionEmailBySiteAndType(anyInt(), anyString())).thenReturn(email);
		SubscriptionEmailAppearance appearance = mock(SubscriptionEmailAppearance.class);
		when(emailService.fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt())).thenReturn(appearance);
		when(email.getId()).thenReturn(1);
		when(validator.validateAppearance(any())).thenReturn(true);
		Contacts contact = mock(Contacts.class);
		when(contactService.fetchById(anyInt())).thenReturn(contact);
		when(contact.getFirstName()).thenReturn("fname");
		when(contact.getLastName()).thenReturn("lname");
		when(placeHolderReplacer.replacePlaceHolders(any())).thenReturn("body");
		when(emailService.addToQueue(any(), anyString(), any(), anyString(), anyString())).thenReturn(true);

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);
		Sites site = mock(Sites.class);

		when(site.getId()).thenReturn(1);
		when(emailDispatcherParameters.getEmailType()).thenReturn(SubscriptionEmailType.CONFIRMATION);
		when(emailDispatcherParameters.getSite()).thenReturn(site);
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getDefaultLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getBookingRecord()).thenReturn(bookingRecord);

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(emailService).fetchSubscriptionEmailBySiteAndType(anyInt(), anyString());
		verify(emailService).fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt());
		verify(emailService).addToQueue(any(), anyString(), any(), anyString(), anyString());
		verify(contactService).fetchById(anyInt());
		verify(validator).validateAppearance(any());
		verify(placeHolderReplacer).replacePlaceHolders(any());
		verifyNoMoreInteractions(emailService, contactService, validator);
	}

	@Test
	void testSendEmail_Use_Default_Language()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBookingCustomerDetails bookingCustomerDetails = mock(SubscriptionBookingCustomerDetails.class);
		when(bookingRecord.getCustomerDetails()).thenReturn(bookingCustomerDetails);
		when(bookingCustomerDetails.getEmailAddress()).thenReturn("email@address.com");
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(booking.getContactId()).thenReturn(23123);
		when(bookingRecord.getBooking()).thenReturn(booking);
		SubscriptionEmail email = mock(SubscriptionEmail.class);
		when(email.getId()).thenReturn(1);
		when(emailService.fetchSubscriptionEmailBySiteAndType(anyInt(), anyString())).thenReturn(email);
		SubscriptionEmailAppearance appearance = mock(SubscriptionEmailAppearance.class);
		when(emailService.fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt())).thenReturn(null)
				.thenReturn(appearance);
		when(email.getId()).thenReturn(1);
		when(validator.validateAppearance(any())).thenReturn(true);
		Contacts contact = mock(Contacts.class);
		when(contactService.fetchById(anyInt())).thenReturn(contact);
		when(contact.getFirstName()).thenReturn("fname");
		when(contact.getLastName()).thenReturn("lname");
		when(placeHolderReplacer.replacePlaceHolders(any())).thenReturn("body");
		when(emailService.addToQueue(any(), anyString(), any(), anyString(), anyString())).thenReturn(true);

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);
		Sites site = mock(Sites.class);

		when(site.getId()).thenReturn(1);
		when(emailDispatcherParameters.getEmailType()).thenReturn(SubscriptionEmailType.CONFIRMATION);
		when(emailDispatcherParameters.getSite()).thenReturn(site);
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getDefaultLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getBookingRecord()).thenReturn(bookingRecord);

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(emailService).fetchSubscriptionEmailBySiteAndType(anyInt(), anyString());
		verify(emailService, times(2)).fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt());
		verify(emailService).addToQueue(any(), anyString(), any(), anyString(), anyString());
		verify(contactService).fetchById(anyInt());
		verify(validator).validateAppearance(any());
		verify(placeHolderReplacer).replacePlaceHolders(any());
		verifyNoMoreInteractions(emailService, contactService, validator);
	}

	@Test
	void testSendEmail_Body_Empty()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBookingCustomerDetails bookingCustomerDetails = mock(SubscriptionBookingCustomerDetails.class);
		when(bookingRecord.getCustomerDetails()).thenReturn(bookingCustomerDetails);
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(booking.getContactId()).thenReturn(23123);
		when(bookingRecord.getBooking()).thenReturn(booking);
		SubscriptionEmail email = mock(SubscriptionEmail.class);
		when(email.getId()).thenReturn(1);
		when(emailService.fetchSubscriptionEmailBySiteAndType(anyInt(), anyString())).thenReturn(email);
		SubscriptionEmailAppearance appearance = mock(SubscriptionEmailAppearance.class);
		when(emailService.fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt())).thenReturn(appearance);
		when(email.getId()).thenReturn(1);
		when(validator.validateAppearance(any())).thenReturn(true);
		Contacts contact = mock(Contacts.class);
		when(contactService.fetchById(anyInt())).thenReturn(contact);
		when(contact.getFirstName()).thenReturn("fname");
		when(contact.getLastName()).thenReturn("lname");
		when(placeHolderReplacer.replacePlaceHolders(any())).thenReturn("");

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);
		Sites site = mock(Sites.class);

		when(site.getId()).thenReturn(1);
		when(emailDispatcherParameters.getEmailType()).thenReturn(SubscriptionEmailType.CONFIRMATION);
		when(emailDispatcherParameters.getSite()).thenReturn(site);
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getDefaultLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getBookingRecord()).thenReturn(bookingRecord);

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(emailService).fetchSubscriptionEmailBySiteAndType(anyInt(), anyString());
		verify(emailService).fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt());
		verify(emailService, times(0)).addToQueue(any(), anyString(), any(), anyString(), anyString());
		verify(contactService).fetchById(anyInt());
		verify(validator).validateAppearance(any());
		verify(placeHolderReplacer).replacePlaceHolders(any());
		verifyNoMoreInteractions(emailService, contactService, validator);
	}

	@Test
	void testSendEmail_Not_Added_To_Queue()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBookingCustomerDetails bookingCustomerDetails = mock(SubscriptionBookingCustomerDetails.class);
		when(bookingRecord.getCustomerDetails()).thenReturn(bookingCustomerDetails);
		when(bookingCustomerDetails.getEmailAddress()).thenReturn("email@address.com");
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(booking.getContactId()).thenReturn(23123);
		when(bookingRecord.getBooking()).thenReturn(booking);
		SubscriptionEmail email = mock(SubscriptionEmail.class);
		when(email.getId()).thenReturn(1);
		when(emailService.fetchSubscriptionEmailBySiteAndType(anyInt(), anyString())).thenReturn(email);
		SubscriptionEmailAppearance appearance = mock(SubscriptionEmailAppearance.class);
		when(emailService.fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt())).thenReturn(appearance);
		when(email.getId()).thenReturn(1);
		when(validator.validateAppearance(any())).thenReturn(true);
		Contacts contact = mock(Contacts.class);
		when(contactService.fetchById(anyInt())).thenReturn(contact);
		when(contact.getFirstName()).thenReturn("fname");
		when(contact.getLastName()).thenReturn("lname");
		when(placeHolderReplacer.replacePlaceHolders(any())).thenReturn("body");
		when(emailService.addToQueue(any(), anyString(), any(), anyString(), anyString())).thenReturn(false);

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);
		Sites site = mock(Sites.class);

		when(site.getId()).thenReturn(1);
		when(emailDispatcherParameters.getEmailType()).thenReturn(SubscriptionEmailType.CONFIRMATION);
		when(emailDispatcherParameters.getSite()).thenReturn(site);
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getDefaultLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getBookingRecord()).thenReturn(bookingRecord);

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(emailService).fetchSubscriptionEmailBySiteAndType(anyInt(), anyString());
		verify(emailService).fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt());
		verify(emailService).addToQueue(any(), anyString(), any(), anyString(), anyString());
		verify(contactService).fetchById(anyInt());
		verify(validator).validateAppearance(any());
		verify(placeHolderReplacer).replacePlaceHolders(any());
		verifyNoMoreInteractions(emailService, contactService, validator);
	}

	@Test
	void testSendEmail_Contact_Null()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBookingCustomerDetails bookingCustomerDetails = mock(SubscriptionBookingCustomerDetails.class);
		when(bookingRecord.getCustomerDetails()).thenReturn(bookingCustomerDetails);
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(booking.getContactId()).thenReturn(23123);
		when(bookingRecord.getBooking()).thenReturn(booking);
		SubscriptionEmail email = mock(SubscriptionEmail.class);
		when(email.getId()).thenReturn(1);
		when(emailService.fetchSubscriptionEmailBySiteAndType(anyInt(), anyString())).thenReturn(email);
		SubscriptionEmailAppearance appearance = mock(SubscriptionEmailAppearance.class);
		when(emailService.fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt())).thenReturn(appearance);
		when(email.getId()).thenReturn(1);
		when(validator.validateAppearance(any())).thenReturn(true);
		when(contactService.fetchById(anyInt())).thenReturn(null);

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);
		Sites site = mock(Sites.class);

		when(site.getId()).thenReturn(1);
		when(emailDispatcherParameters.getEmailType()).thenReturn(SubscriptionEmailType.CONFIRMATION);
		when(emailDispatcherParameters.getSite()).thenReturn(site);
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getDefaultLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getBookingRecord()).thenReturn(bookingRecord);

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(emailService).fetchSubscriptionEmailBySiteAndType(anyInt(), anyString());
		verify(emailService).fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt());
		verify(emailService, times(0)).addToQueue(any(), anyString(), any(), anyString(), anyString());
		verify(contactService).fetchById(anyInt());
		verify(validator).validateAppearance(any());
		verify(placeHolderReplacer, times(0)).replacePlaceHolders(any());
		verifyNoMoreInteractions(emailService, contactService, validator, placeHolderReplacer);
	}

	@Test
	void testSendEmail_Validation_failed()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionEmail email = mock(SubscriptionEmail.class);
		when(email.getId()).thenReturn(1);
		when(emailService.fetchSubscriptionEmailBySiteAndType(anyInt(), anyString())).thenReturn(email);
		SubscriptionEmailAppearance appearance = mock(SubscriptionEmailAppearance.class);
		when(emailService.fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt())).thenReturn(appearance);
		when(email.getId()).thenReturn(1);
		when(validator.validateAppearance(any())).thenReturn(false);

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);
		Sites site = mock(Sites.class);

		when(site.getId()).thenReturn(1);
		when(emailDispatcherParameters.getEmailType()).thenReturn(SubscriptionEmailType.CONFIRMATION);
		when(emailDispatcherParameters.getSite()).thenReturn(site);
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getDefaultLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getBookingRecord()).thenReturn(bookingRecord);

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(emailService).fetchSubscriptionEmailBySiteAndType(anyInt(), anyString());
		verify(emailService).fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt());
		verify(emailService, times(0)).addToQueue(any(), anyString(), any(), anyString(), anyString());
		verify(contactService, times(0)).fetchById(anyInt());
		verify(validator).validateAppearance(any());
		verify(placeHolderReplacer, times(0)).replacePlaceHolders(any());
		verifyNoMoreInteractions(emailService, contactService, validator, placeHolderReplacer);
	}

	@Test
	void testSendEmail_Email_Null()
	{
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		when(emailService.fetchSubscriptionEmailBySiteAndType(anyInt(), anyString())).thenReturn(null);

		EmailDispatcherParameters emailDispatcherParameters = mock(EmailDispatcherParameters.class);
		Sites site = mock(Sites.class);

		when(site.getId()).thenReturn(1);
		when(emailDispatcherParameters.getEmailType()).thenReturn(SubscriptionEmailType.CONFIRMATION);
		when(emailDispatcherParameters.getSite()).thenReturn(site);
		when(emailDispatcherParameters.getCurrentLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getDefaultLanguageId()).thenReturn(1);
		when(emailDispatcherParameters.getBookingRecord()).thenReturn(bookingRecord);

		dispatcher.sendEmail(emailDispatcherParameters);
		verify(emailService).fetchSubscriptionEmailBySiteAndType(anyInt(), anyString());
		verify(emailService, times(0)).fetchSubscriptionEmailAppearanceByEmailIdAndType(anyInt(), anyInt());
		verify(emailService, times(0)).addToQueue(any(), anyString(), any(), anyString(), anyString());
		verify(contactService, times(0)).fetchById(anyInt());
		verify(validator, times(0)).validateAppearance(any());
		verify(placeHolderReplacer, times(0)).replacePlaceHolders(any());
		verifyNoMoreInteractions(emailService, contactService, validator, placeHolderReplacer);
	}
}