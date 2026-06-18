package com.kmp.aeroparker.application.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.email.dispatcher.ConfirmationEmailDispatcher;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSProducer;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

@ExtendWith(MockitoExtension.class)
public class FreemarkerSubmitterTest
{
	@Mock
	private Model model;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private BookingService bookingService;
	@Mock
	private ContactService contactService;
	@Mock
	private SubscriptionBooking booking;
	@Mock
	private SubscriptionBookingRecord bookingRecord;
	@Mock
	private Contacts contact;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private AffiliateConfig affiliateConfig;
	@Mock
	private Affiliates affiliates;
	@Mock
	private Sites sites;
	@Mock
	private ConfirmationEmailDispatcher confEmailDispatcher;
	@Mock
	private SubscriptionControllerService controllerService;
	@Mock
	private CustomConnectionProvider customConnectionProvider;
	@Mock
	private FreemarkerSQSProducer freemarkerSQSProducer;

	@InjectMocks
	private FreemarkerSubmitter helper;

	@Test
	void testResendEmailConfirmation()
	{
		when(bookingService.fetchBookingByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(bookingService.fetchSubscriptionBooking(anyInt(), anyInt())).thenReturn(bookingRecord);
		when(booking.getContactId()).thenReturn(1);
		when(contactService.fetchById(anyInt())).thenReturn(contact);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);
		when(requestBean.getSite()).thenReturn(sites);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);

		assertEquals("{\"sent\":true}",
				helper.resendConfirmationEmail(httpServletRequest, model, "reference", requestBean));
	}

	@Test
	void testResendEmailConfirmation_NullBookingRecord()
	{
		when(bookingService.fetchBookingByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(bookingService.fetchSubscriptionBooking(anyInt(), anyInt())).thenReturn(null);

		assertEquals("{\"sent\":false}",
				helper.resendConfirmationEmail(httpServletRequest, model, "reference", requestBean));
	}

	@Test
	void testResendEmailConfirmation_NullContact()
	{
		when(bookingService.fetchBookingByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(bookingService.fetchSubscriptionBooking(anyInt(), anyInt())).thenReturn(bookingRecord);
		when(booking.getContactId()).thenReturn(1);
		when(contactService.fetchById(anyInt())).thenReturn(null);

		assertEquals("{\"sent\":false}",
				helper.resendConfirmationEmail(httpServletRequest, model, "reference", requestBean));
	}

	@Test
	void testResendEmailConfirmation_DisabledConfirmationEmail()
	{
		when(bookingService.fetchBookingByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(bookingService.fetchSubscriptionBooking(anyInt(), anyInt())).thenReturn(bookingRecord);
		when(booking.getContactId()).thenReturn(1);
		when(contactService.fetchById(anyInt())).thenReturn(contact);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);
		when(requestBean.getSite()).thenReturn(sites);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(false);

		assertEquals("{\"sent\":false}",
				helper.resendConfirmationEmail(httpServletRequest, model, "reference", requestBean));
	}

	@Test
	void testResendEmailConfirmation_Freemarker_Enabled()
	{
		SubscriptionBookingCustomerDetails customerDetails = mock(SubscriptionBookingCustomerDetails.class);

		when(bookingService.fetchBookingByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(bookingService.fetchSubscriptionBooking(anyInt(), anyInt())).thenReturn(bookingRecord);
		when(booking.getContactId()).thenReturn(1);
		when(contactService.fetchById(anyInt())).thenReturn(contact);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);
		when(requestBean.getSite()).thenReturn(sites);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getFreemarkerQueueUrl()).thenReturn("freemarkerurl");
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(controllerService.isFreemarkerEnabled(anyInt())).thenReturn(true);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getContactId()).thenReturn(1);
		when(booking.getReference()).thenReturn("ref");
		when(bookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		when(customerDetails.getEmailAddress()).thenReturn("abc@email.com");

		assertEquals("{\"sent\":true}",
				helper.resendConfirmationEmail(httpServletRequest, model, "reference", requestBean));
	}
}
