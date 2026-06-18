package com.kmp.aeroparker.application.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.builder.ConfigBuilder;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.external.api.SubscriptionBookingRequestHandler;
import com.kmp.aeroparker.application.model.AnalyticsLocations;
import com.kmp.aeroparker.application.model.DetailsConfig;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.external.api.datatypes.AvailabilityWindow;
import com.kmp.aeroparker.application.model.external.api.datatypes.Error;
import com.kmp.aeroparker.application.model.external.api.datatypes.Price;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionProduct;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionQuote;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionQuotes;
import com.kmp.aeroparker.application.model.external.api.response.RenewSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionAvailabilityResponse;
import com.kmp.aeroparker.application.payment.handler.PaymentHandler;
import com.kmp.aeroparker.application.processor.SubscriptionAnalyticsProcessor;
import com.kmp.aeroparker.application.validator.SubscriptionValidator;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

@ExtendWith(MockitoExtension.class)
class SubscriptionRenewalBookingControllerTest
{
	@InjectMocks
	private SubscriptionRenewalBookingController controller;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private SubscriptionBookingRequestHandler requestHandler;
	@Mock
	private SubscriptionControllerService controllerService;
	@Mock
	private PaymentHandler paymentHandler;
	@Mock
	private AffiliateService affiliateService;
	@Mock
	private BookingService bookingService;
	@Mock
	private ContactService contactService;
	@Mock
	private PaymentService paymentService;
	@Mock
	private ConfigBuilder configBuilder;
	@Mock
	private HttpSession session;
	@Mock
	private Model model;
	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	private AffiliateConfig affiliateConfig;
	@Mock
	private SubscriptionAvailabilityResponse availabilityResponse;
	@Mock
	private SubscriptionQuotes subscriptionQuotes;
	@Mock
	private SubscriptionQuote quote;
	@Mock
	private AvailabilityWindow window;
	@Mock
	private Affiliates affiliate;
	@Mock
	private Sites site;
	@Mock
	private SubscriptionProduct product;
	@Mock
	private PartialPayments partialPayment;
	@Mock
	private HttpServletRequest req;
	@Mock
	private SubscriptionValidator validator;
	@Mock
	private Error error;
	@Mock
	private SubscriptionAnalyticsProcessor analyticsProcessor;

	@Test
	void testSetupBookingRenewal()
	{
		List<SubscriptionQuote> quotesList = new ArrayList<>();

		quotesList.add(quote);

		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt()))
				.thenReturn(PaymentGatewayType.STRIPE.getId());
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(requestHandler.getRenewalAvailability(any(), anyString())).thenReturn(availabilityResponse);
		when(availabilityResponse.getSubscriptionQuotes()).thenReturn(subscriptionQuotes);
		when(subscriptionQuotes.getQuotes()).thenReturn(quotesList);
		when(availabilityResponse.getAvailabilityWindow()).thenReturn(window);
		when(window.getArrivalDateTime()).thenReturn("2025-02-18T00:00:00");
		when(window.getReturnDateTime()).thenReturn("2025-02-18T00:00:00");
		when(quote.getSubscriptionProduct()).thenReturn(mock(SubscriptionProduct.class));
		when(validator.validateRenewalPeriod(anyString(), anyString())).thenReturn(true);
		when(quote.getPrice()).thenReturn(mock(Price.class));

		assertEquals("subscription-display-take-payment", controller.setupBookingRenewal(session, model,
				redirectAttributes, "email", "ref", LocalDate.MAX.toString(), LocalDate.MAX.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, times(3)).setAttribute(anyString(), any());
		verify(model, times(5)).addAttribute(anyString(), any());
		verify(model).addAttribute(eq("startDate"), eq(LocalDateTime.MAX.truncatedTo(ChronoUnit.SECONDS)));
	}

	@Test
	void testSetupBookingRenewal_OriginalEndDateInPast()
	{
		List<SubscriptionQuote> quotesList = new ArrayList<>();

		quotesList.add(quote);

		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt()))
				.thenReturn(PaymentGatewayType.STRIPE.getId());
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(requestHandler.getRenewalAvailability(any(), anyString())).thenReturn(availabilityResponse);
		when(availabilityResponse.getSubscriptionQuotes()).thenReturn(subscriptionQuotes);
		when(subscriptionQuotes.getQuotes()).thenReturn(quotesList);
		when(availabilityResponse.getAvailabilityWindow()).thenReturn(window);
		when(window.getArrivalDateTime()).thenReturn("2025-02-18T00:00:00");
		when(window.getReturnDateTime()).thenReturn("2025-02-18T00:00:00");
		when(validator.validateRenewalPeriod(anyString(), anyString())).thenReturn(true);
		when(quote.getSubscriptionProduct()).thenReturn(mock(SubscriptionProduct.class));
		when(quote.getPrice()).thenReturn(mock(Price.class));

		assertEquals("subscription-display-take-payment", controller.setupBookingRenewal(session, model,
				redirectAttributes, "email", "ref", LocalDate.MIN.toString(), LocalDate.MIN.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, times(3)).setAttribute(anyString(), any());
		verify(model, times(5)).addAttribute(anyString(), any());
		verify(model).addAttribute(eq("startDate"), not(eq(LocalDateTime.MIN.truncatedTo(ChronoUnit.SECONDS))));
		verifyNoInteractions(redirectAttributes);
	}

	@Test
	void testSetupBookingRenewal_EmptyQuotesList()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt()))
				.thenReturn(PaymentGatewayType.STRIPE.getId());
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(requestHandler.getRenewalAvailability(any(), anyString())).thenReturn(availabilityResponse);
		when(availabilityResponse.getSubscriptionQuotes()).thenReturn(subscriptionQuotes);
		when(subscriptionQuotes.getQuotes()).thenReturn(new ArrayList<>());
		when(validator.validateRenewalPeriod(anyString(), anyString())).thenReturn(true);

		assertEquals("redirect:view-booking", controller.setupBookingRenewal(session, model, redirectAttributes,
				"email", "ref", LocalDate.MIN.toString(), LocalDate.MIN.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"), eq("Renewal is not available for this booking"));
		verifyNoInteractions(model);
	}

	@Test
	void testSetupBookingRenewal_NullQuotesList()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt()))
				.thenReturn(PaymentGatewayType.STRIPE.getId());
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(validator.validateRenewalPeriod(anyString(), anyString())).thenReturn(true);
		when(requestHandler.getRenewalAvailability(any(), anyString())).thenReturn(availabilityResponse);
		when(availabilityResponse.getSubscriptionQuotes()).thenReturn(subscriptionQuotes);

		assertEquals("redirect:view-booking", controller.setupBookingRenewal(session, model, redirectAttributes,
				"email", "ref", LocalDate.MIN.toString(), LocalDate.MIN.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"), eq("Renewal is not available for this booking"));
		verifyNoInteractions(model);
	}

	@Test
	void testSetupBookingRenewal_NullQuotes()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt()))
				.thenReturn(PaymentGatewayType.STRIPE.getId());
		when(validator.validateRenewalPeriod(anyString(), anyString())).thenReturn(true);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(requestHandler.getRenewalAvailability(any(), anyString())).thenReturn(availabilityResponse);

		assertEquals("redirect:view-booking", controller.setupBookingRenewal(session, model, redirectAttributes,
				"email", "ref", LocalDate.MIN.toString(), LocalDate.MIN.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"), eq("Renewal is not available for this booking"));
		verifyNoInteractions(model);
	}

	@Test
	void testSetupBookingRenewal_NullAvailabilityResponse()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt()))
				.thenReturn(PaymentGatewayType.STRIPE.getId());
		when(validator.validateRenewalPeriod(anyString(), anyString())).thenReturn(true);

		assertEquals("redirect:view-booking", controller.setupBookingRenewal(session, model, redirectAttributes,
				"email", "ref", LocalDate.MIN.toString(), LocalDate.MIN.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"), eq("Renewal is not available for this booking"));
		verifyNoInteractions(model);
	}

	@Test
	void testSetupBookingRenewal_PaymentGatewayNotSupported()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt()))
				.thenReturn(PaymentGatewayType.WIRECARD.getId());

		assertEquals("redirect:view-booking", controller.setupBookingRenewal(session, model, redirectAttributes,
				"email", "ref", LocalDate.MIN.toString(), LocalDate.MIN.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"), eq("Subscription booking renewal is not allowed"));
		verifyNoInteractions(model);
	}

	@Test
	void testSetupBookingRenewal_FeatureFlagDisabled()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(false);

		assertEquals("redirect:view-booking", controller.setupBookingRenewal(session, model, redirectAttributes,
				"email", "ref", LocalDate.MIN.toString(), LocalDate.MIN.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"), eq("Subscription booking renewal is not allowed"));
		verifyNoInteractions(model);
	}
	
	@Test
	void testSetupBookingRenewal_OutsideRenewalPeriod()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt()))
				.thenReturn(PaymentGatewayType.STRIPE.getId());
		when(validator.validateRenewalPeriod(anyString(), anyString())).thenReturn(false);

		assertEquals("redirect:view-booking", controller.setupBookingRenewal(session, model, redirectAttributes,
				"email", "ref", LocalDate.MIN.toString(), LocalDate.MIN.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"),
				eq("Subscription end date is outside of the renewal period"));
		verifyNoInteractions(model);
	}
	
	@Test
	void testSetupBookingRenewal_MissingRenewalScope()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(controllerService.isSubscriptionRenewalEnabled(anyInt())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(anyInt(), anyInt()))
				.thenReturn(PaymentGatewayType.STRIPE.getId());
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(requestHandler.getRenewalAvailability(any(), anyString())).thenReturn(availabilityResponse);
		when(availabilityResponse.getError()).thenReturn(error);
		when(validator.validateRenewalPeriod(anyString(), anyString())).thenReturn(true);
		when(error.getCode()).thenReturn("323");

		assertEquals("redirect:view-booking", controller.setupBookingRenewal(session, model, redirectAttributes,
				"email", "ref", LocalDate.MIN.toString(), LocalDate.MIN.toString()));

		verify(session, times(3)).removeAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"), eq("We are unable to renew your booking due to a technical issue. Please try again later."));
		verifyNoInteractions(model);
	}

	@Test
	void testDisplayTakeBookingRenewal()
	{
		List<SubscriptionQuote> quotesList = new ArrayList<>();
		Price price = mock(Price.class);
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		booking.setId(1);
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);

		quotesList.add(quote);

		when(session.getAttribute(eq("availabilityResponse"))).thenReturn(availabilityResponse);
		when(availabilityResponse.getSubscriptionQuotes()).thenReturn(subscriptionQuotes);
		when(subscriptionQuotes.getQuotes()).thenReturn(quotesList);
		when(quote.getPrice()).thenReturn(price);
		when(price.getValue()).thenReturn(BigDecimal.TEN);
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getSite()).thenReturn(mock(Sites.class));
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(requestBean.getSiteId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(requestBean.getCurrency()).thenReturn("EUR");
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(mock(DetailsConfig.class));
		when(paymentHandler.setUpTransaction(any())).thenReturn(new HashMap<>());
		when(quote.getSubscriptionProduct()).thenReturn(product);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(any(), anyInt())).thenReturn(booking);
		when(bookingService.fetchSubscriptionBooking(anyInt(), anyInt())).thenReturn(bookingRecord);

		assertEquals("subscription-renewal-payment",
				controller.displayTakeBookingRenewal(session, model, redirectAttributes, "email", "ref"));

		verify(session).getAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(model, times(4)).addAttribute(anyString(), any());
		verify(analyticsProcessor).processSubscriptionAnalytics(model, bookingRecord, requestBean.getCurrency(), 
				AnalyticsLocations.SUBSCRIPTION_PAYMENT_DETAIL.getId());
		verifyNoInteractions(redirectAttributes);
	}

	@Test
	void testDisplayTakeBookingRenewal_NullAvailabilityResponse()
	{
		assertEquals("redirect:view-booking",
				controller.displayTakeBookingRenewal(session, model, redirectAttributes, "email", "ref"));

		verify(session).getAttribute(anyString());
		verify(session, never()).setAttribute(anyString(), any());
		verify(redirectAttributes, times(3)).addAttribute(anyString(), any());
		verifyNoInteractions(model);
	}

	@Test
	void testTakeRenewalPayment()
	{
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);

		when(req.getRequestURL()).thenReturn(new StringBuffer());
		when(session.getAttribute("reference")).thenReturn("Reference");
		when(session.getAttribute("email")).thenReturn("test@example.com");
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(booking.getId()).thenReturn(1);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(booking);
		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBookingCustomerDetails customerDetails = mock(SubscriptionBookingCustomerDetails.class);
		when(customerDetails.getPhoneNumber()).thenReturn("123456789");
		when(bookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		SubscriptionBookingVehicleDetails vehicleDetails = mock(SubscriptionBookingVehicleDetails.class);
		when(vehicleDetails.getCarRegistration()).thenReturn("ABC 123");
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(vehicleDetails);
		when(bookingService.fetchSubscriptionBooking(anyInt(), anyInt())).thenReturn(bookingRecord);
		Contacts contact = mock(Contacts.class);
		when(contact.getId()).thenReturn(123);
		when(contactService.fetchContactByEmailAndSiteId(anyString(), anyInt())).thenReturn(contact);
		SubscriptionContactMembership subscriptionContactMembership = mock(SubscriptionContactMembership.class);
		when(subscriptionContactMembership.getMembershipId()).thenReturn("MEM123");
		when(contactService.fetchSubscriptionContactMembershipIdByContactId(anyInt())).thenReturn(subscriptionContactMembership);
		when(affiliate.getSiteid()).thenReturn(79);
		when(affiliateService.fetchAffiliateById(anyInt())).thenReturn(affiliate);
		when(paymentHandler.processRedirectPayment(any())).thenReturn(mock(PaymentHandlerBean.class));

		assertEquals("stripe-redirect", controller.takeRenewalPayment(req, session, bookingData));

		verify(req).setAttribute(anyString(), any());
	}

	@Test
	void testSendRenewal()
	{
		when(session.getAttribute(eq("availabilityResponse"))).thenReturn(availabilityResponse);
		when(session.getAttribute(eq("email"))).thenReturn("email");
		when(session.getAttribute(eq("reference"))).thenReturn("reference");
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(availabilityResponse.getAvailabilityWindow()).thenReturn(window);
		when(requestHandler.sendRenewalRequest(any(), any(), any(), any(), any(), any()))
				.thenReturn(mock(RenewSubscriptionResponse.class));

		assertEquals("redirect:renewal-confirmation",
				controller.sendRenewal(session, redirectAttributes, "token", BigDecimal.ONE, "GBP"));

		verify(session, times(3)).removeAttribute(anyString());
		verify(redirectAttributes).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("encryptedReference"), any());
		verify(redirectAttributes, never()).addAttribute(eq("errorMessage"), any());
	}

	@Test
	void testSendRenewal_ErrorInResponse()
	{
		RenewSubscriptionResponse response = mock(RenewSubscriptionResponse.class);
		Error error = mock(Error.class);

		when(session.getAttribute(eq("availabilityResponse"))).thenReturn(availabilityResponse);
		when(session.getAttribute(eq("email"))).thenReturn("email");
		when(session.getAttribute(eq("reference"))).thenReturn("reference");
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(availabilityResponse.getAvailabilityWindow()).thenReturn(window);
		when(requestHandler.sendRenewalRequest(any(), any(), any(), any(), any(), any())).thenReturn(response);
		when(response.getError()).thenReturn(error);
		when(error.getMessage()).thenReturn("error");

		assertEquals("redirect:view-booking",
				controller.sendRenewal(session, redirectAttributes, "token", BigDecimal.ONE, "GBP"));

		verify(session, times(3)).removeAttribute(anyString());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes, never()).addAttribute(eq("encryptedReference"), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"), eq("error"));
	}

	@Test
	void testSendRenewal_NullResponse()
	{
		when(session.getAttribute(eq("availabilityResponse"))).thenReturn(availabilityResponse);
		when(session.getAttribute(eq("email"))).thenReturn("email");
		when(session.getAttribute(eq("reference"))).thenReturn("reference");
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(availabilityResponse.getAvailabilityWindow()).thenReturn(window);

		assertEquals("redirect:view-booking",
				controller.sendRenewal(session, redirectAttributes, "token", BigDecimal.ONE, "GBP"));

		verify(session, times(3)).removeAttribute(anyString());
		verify(redirectAttributes, times(4)).addAttribute(anyString(), any());
		verify(redirectAttributes, never()).addAttribute(eq("encryptedReference"), any());
		verify(redirectAttributes).addAttribute(eq("errorMessage"), eq("Failed to update booking"));
	}

	@Test
	void testSendRenewal_PaymentAlreadyTaken()
	{
		when(session.getAttribute(eq("availabilityResponse"))).thenReturn(availabilityResponse);
		when(session.getAttribute(eq("email"))).thenReturn("email");
		when(session.getAttribute(eq("reference"))).thenReturn("reference");
		when(paymentService.fetchPartialPaymentsByTransactionIdReferenceAndType(
				anyString(), anyString(), anyInt())).thenReturn(partialPayment);

		assertEquals("redirect:renewal-confirmation",
				controller.sendRenewal(session, redirectAttributes, "token", BigDecimal.ONE, "GBP"));

		verify(session, times(3)).removeAttribute(anyString());
		verify(redirectAttributes).addAttribute(anyString(), any());
		verify(redirectAttributes).addAttribute(eq("encryptedReference"), any());
		verify(redirectAttributes, never()).addAttribute(eq("errorMessage"), any());
	}
}
