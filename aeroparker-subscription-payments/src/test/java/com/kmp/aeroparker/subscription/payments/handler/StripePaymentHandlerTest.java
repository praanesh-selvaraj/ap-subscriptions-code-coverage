package com.kmp.aeroparker.subscription.payments.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.credentials.StripeCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.PaymentProcessorFactory;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.processor.StripePaymentProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.stripe.StripePaymentIntentProcessor;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeError;

@ExtendWith(MockitoExtension.class)
class StripePaymentHandlerTest
{
	@Mock
	private PaymentProcessorFactory paymentProcessorFactory;
	@Mock
	private PaymentService paymentService;
	@Mock
	private StripePaymentIntentProcessor stripePaymentIntentProcessor;
	@Mock
	private PaymentGatewayParameters paymentHandlerParams;
	@Mock
	private Affiliates affiliate;
	@Mock
	private StripeCredentials credentials;
	@Mock
	private PaymentGatewayTypes paymentGatewayType;
	@Mock
	private PaymentIntent paymentIntent;
	@Mock
	private Languages language;
	@Mock
	private SubscriptionBookingData bookingData;
	@Mock
	private StripePaymentProcessor paymentProcessor;
	@Mock
	private Payments parentPayment;
	@Mock
	private StripeError stripeError;
	@Mock
	private Charge charge;
	@InjectMocks
	private StripePaymentHandler stripePaymentHandler;

	@Test
	public void testSetUpTransaction_Success()
	{
		int affiliateId = 123;
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(affiliateId);
		when(affiliate.getName()).thenReturn("affName");
		when(paymentHandlerParams.getAmount()).thenReturn(new BigDecimal("100.00"));
		when(paymentHandlerParams.getCurrency()).thenReturn("USD");
		when(paymentHandlerParams.getLanguage()).thenReturn(language);
		when(language.getLanguageCode()).thenReturn("en");
		when(paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.STRIPE)).thenReturn(credentials);
		when(paymentService.fetchPaymentGatewayTypesById(PaymentGatewayType.STRIPE.getId())).thenReturn(paymentGatewayType);
		when(paymentGatewayType.getPaymentJsp()).thenReturn("StripePayment.jsp");
		when(stripePaymentIntentProcessor.generatePaymentIntent(affiliateId, "10000", "USD")).thenReturn(paymentIntent);
		when(paymentIntent.getId()).thenReturn("pi_123456");
		when(paymentIntent.getClientSecret()).thenReturn("client_secret_123");

		Map<String, Object> result = stripePaymentHandler.setUpTransaction(paymentHandlerParams);

		assertNotNull(result);
		assertEquals("pi_123456", result.get("paymentIntentId"));
		assertEquals(credentials, result.get("credentials"));
		assertEquals("client_secret_123", result.get("clientSecret"));
		assertEquals("en", result.get("seamlessLocale"));
		assertEquals(PaymentGatewayType.STRIPE.getId(), result.get("paymentGatewayType"));
		assertEquals("USD", result.get("currency"));
		assertEquals(new BigDecimal("100.00"), result.get("amount"));
		assertEquals(affiliateId, result.get("affiliateId"));
		assertTrue((Boolean) result.get("isSeamlessPayment"));
		assertEquals("StripePayment", result.get("paymentHtml"));
	}

	@Test
	public void testSetUpTransaction_NoPaymentIntent()
	{
		int affiliateId = 123;
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(affiliateId);
		when(paymentHandlerParams.getAmount()).thenReturn(new BigDecimal("100.00"));
		when(paymentHandlerParams.getCurrency()).thenReturn("USD");
		when(paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.STRIPE)).thenReturn(credentials);
		when(paymentService.fetchPaymentGatewayTypesById(PaymentGatewayType.STRIPE.getId())).thenReturn(paymentGatewayType);
		when(stripePaymentIntentProcessor.generatePaymentIntent(affiliateId, "10000", "USD")).thenReturn(paymentIntent);
		when(paymentIntent.getId()).thenReturn(null); // No valid paymentIntent ID

		Map<String, Object> result = stripePaymentHandler.setUpTransaction(paymentHandlerParams);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSetUpTransaction_NoCredentials()
	{
		int affiliateId = 123;
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(affiliateId);
		when(paymentHandlerParams.getAmount()).thenReturn(new BigDecimal("100.00"));
		when(paymentHandlerParams.getCurrency()).thenReturn("USD");
		when(paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.STRIPE)).thenReturn(null); // No credentials

		Map<String, Object> result = stripePaymentHandler.setUpTransaction(paymentHandlerParams);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testProcessPayment_Success()
	{
		String reference = "reference123";
		int affiliateId = 123;
		when(bookingData.isPaymentSuccess()).thenReturn(true);
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getPaymentIntentId()).thenReturn("paymentIntent");
		when(stripePaymentIntentProcessor.getPaymentIntent(anyInt(), anyString())).thenReturn(paymentIntent);
		when(paymentService.fetchPaymentByReference(reference)).thenReturn(parentPayment);
		when(paymentService.fetchPartialPaymentsByTransactionIdReferenceAndType("paymentIntent", reference, PaymentGatewayType.STRIPE.getId())).thenReturn(null); // No partial payment exists

		when(paymentProcessorFactory.getInstance(PaymentGatewayType.STRIPE)).thenReturn(paymentProcessor);
		when(paymentProcessor.processPartial(any(), eq(parentPayment))).thenReturn(parentPayment);
		when(stripePaymentIntentProcessor.getCharge(anyString(), anyInt(), anyString())).thenReturn(charge);
		when(paymentIntent.getId()).thenReturn("paymentIntent");
		when(paymentIntent.getLatestCharge()).thenReturn("chargeId");

		assertTrue(stripePaymentHandler.processPayment(bookingData));

		verify(paymentService).fetchPaymentByReference(reference);
		verify(paymentService, times(2)).fetchPartialPaymentsByTransactionIdReferenceAndType(paymentIntent.getId(), reference, PaymentGatewayType.STRIPE.getId());
		verify(paymentService).savePaymentTransaction(any());
		verify(paymentProcessorFactory).getInstance(PaymentGatewayType.STRIPE);
		verify(bookingData).setPaymentId(anyInt());
	}

	@Test
	public void testProcessPayment_PaymentAlreadyProcessed()
	{
		String reference = "reference123";
		int affiliateId = 123;
		when(bookingData.isPaymentSuccess()).thenReturn(true);
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getPaymentIntentId()).thenReturn("paymentIntent");
		when(stripePaymentIntentProcessor.getPaymentIntent(anyInt(), anyString())).thenReturn(paymentIntent);
		when(paymentService.fetchPaymentByReference(reference)).thenReturn(parentPayment);
		when(paymentService.fetchPartialPaymentsByTransactionIdReferenceAndType(paymentIntent.getId(), reference, PaymentGatewayType.STRIPE.getId()))
		.thenReturn(mock(PartialPayments.class)); // Partial payment exists

		assertTrue(stripePaymentHandler.processPayment(bookingData));

		verify(paymentService).fetchPaymentByReference(reference);
		verify(paymentService).fetchPartialPaymentsByTransactionIdReferenceAndType(paymentIntent.getId(), reference, PaymentGatewayType.STRIPE.getId());
		verify(paymentProcessorFactory, never()).getInstance(PaymentGatewayType.STRIPE); // Ensure the processor is not invoked since payment is already processed
		verify(bookingData).setPaymentId(Optional.ofNullable(parentPayment.getId()).orElse(0)); // Assert the payment ID is set
	}

	@Test
	public void testProcessPayment_FailurePaymentNotSuccessful()
	{
		when(bookingData.isPaymentSuccess()).thenReturn(false);

		assertFalse(stripePaymentHandler.processPayment(bookingData));

		verify(paymentService, never()).fetchPaymentByReference(any());
		verify(paymentProcessorFactory, never()).getInstance(any());
	}

	@Test
	public void testProcessPayment_ProcessedPaymentNull()
	{
		String reference = "reference123";
		int affiliateId = 123;
		when(bookingData.isPaymentSuccess()).thenReturn(true);
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getPaymentIntentId()).thenReturn("paymentIntent");
		when(stripePaymentIntentProcessor.getPaymentIntent(anyInt(), anyString())).thenReturn(paymentIntent);
		when(paymentService.fetchPaymentByReference(reference)).thenReturn(parentPayment);
		when(paymentService.fetchPartialPaymentsByTransactionIdReferenceAndType(paymentIntent.getId(), reference, PaymentGatewayType.STRIPE.getId())).thenReturn(null); // No partial payment exists

		when(paymentProcessorFactory.getInstance(PaymentGatewayType.STRIPE)).thenReturn(paymentProcessor);
		when(paymentProcessor.processPartial(any(), eq(parentPayment))).thenReturn(parentPayment);
		when(parentPayment.getId()).thenReturn(null); // Simulate null payment ID

		assertTrue(stripePaymentHandler.processPayment(bookingData));

		verify(bookingData).setPaymentId(0);
	}

	@Test
	public void testProcessPayment_PaymentIntentNull()
	{
		String reference = "reference123";
		int affiliateId = 123;
		when(bookingData.isPaymentSuccess()).thenReturn(true);
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getPaymentIntentId()).thenReturn("paymentIntent");
		when(stripePaymentIntentProcessor.getPaymentIntent(anyInt(), anyString())).thenReturn(null);

		assertFalse(stripePaymentHandler.processPayment(bookingData));

		verifyNoInteractions(paymentProcessorFactory);
	}

	@Test
	public void testProcessRedirectPayment_Success()
	{
		int affiliateId = 1;
		String guid = "guid123";
		String reference = "reference123";
		String email = "email@example.com";
		String phone = "1234567890";
		String registration = "ABC123";
		String paymentIntentId = "pi_123456";
		String membershipId = "123";

		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getCustomerGuid()).thenReturn(guid);
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getRawServletAbsoluteUrl()).thenReturn("redirectUrl/path");
		when(bookingData.getCardholderName()).thenReturn("John Doe");
		when(bookingData.getEmail()).thenReturn(email);
		when(bookingData.getTelno()).thenReturn(phone);
		when(bookingData.getCarReg()).thenReturn(registration);
		when(bookingData.getPaymentIntentId()).thenReturn(paymentIntentId);
		when(bookingData.getStartDate()).thenReturn("01-01-2025");
		when(bookingData.getFname()).thenReturn("John");
		when(bookingData.getLname()).thenReturn("Doe");
		when(bookingData.getSiteId()).thenReturn(1);
		when(bookingData.getMembershipId()).thenReturn(membershipId);

		when(paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.STRIPE)).thenReturn(credentials);
		when(credentials.isCardholderNameEnabled()).thenReturn(true);
		when(credentials.isSendMetadata()).thenReturn(true);
		when(paymentService.fetchAffiliatesById(affiliateId)).thenReturn(affiliate);
		String affiliateCode = "affiliateCode";
		when(affiliate.getCode()).thenReturn(affiliateCode);

		PaymentHandlerBean result = stripePaymentHandler.processRedirectPayment(bookingData);

		assertNotNull(result);
		assertTrue(result.isRedirectRequired());
		assertTrue(result.isStripeRedirect());
		assertEquals("redirectUrl/processPaymentCmd?cmd=stripeConfirmation"
				+ "&affiliateId=1"
				+ "&redirectUrl=redirectUrl%2Fpath"
				+ "&customerGuid=guid123"
				+ "&bookingReference=reference123"
				+ "&startDate=01-01-2025"
				+ "&firstName=John"
				+ "&lastName=Doe"
				+ "&siteId=1", 
				result.getCheckoutUrl());

		verify(stripePaymentIntentProcessor).updatePaymentIntentWithMetadata(eq(reference), eq(affiliateCode),
				eq(email), eq(phone), eq(registration), eq(paymentIntentId), eq(affiliateId), eq(guid),
				eq(membershipId), eq(false));
	}

	@Test
	public void testProcessRedirectPayment_Success_Renewals()
	{
		int affiliateId = 1;
		String paymentIntentId = "token";

		when(bookingData.getCustomerGuid()).thenReturn("");
		when(bookingData.getRawServletAbsoluteUrl()).thenReturn("redirectUrl/path");
		when(bookingData.getPaymentIntentId()).thenReturn(paymentIntentId);
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.isRenewal()).thenReturn(true);
		when(bookingData.getCurrency()).thenReturn("GBP");
		when(bookingData.getAmount()).thenReturn(BigDecimal.TEN);
		when(paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.STRIPE)).thenReturn(credentials);
		when(credentials.isCardholderNameEnabled()).thenReturn(true);
		when(credentials.isSendMetadata()).thenReturn(true);
		when(paymentService.fetchAffiliatesById(affiliateId)).thenReturn(affiliate);
		String affiliateCode = "affiliateCode";
		when(affiliate.getCode()).thenReturn(affiliateCode);

		PaymentHandlerBean result = stripePaymentHandler.processRedirectPayment(bookingData);

		assertNotNull(result);
		assertTrue(result.isRedirectRequired());
		assertTrue(result.isStripeRedirect());
		assertEquals("redirectUrl/send-renewal?token=token"
				+ "&amount=10.00"
				+ "&currency=GBP",
				result.getCheckoutUrl());
	}

	@Test
	public void testProcessRedirectPayment_CardholderNameEmptyWhenEnabled()
	{
		int affiliateId = 1;
		String guid = "guid123";
		String reference = "reference123";
		String servletUrl = "http://example.com/payment";
		String cardholderName = ""; // Invalid empty cardholder name
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getCustomerGuid()).thenReturn(guid);
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getRawServletAbsoluteUrl()).thenReturn(servletUrl);
		when(bookingData.getCardholderName()).thenReturn(cardholderName);

		when(paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.STRIPE)).thenReturn(credentials);
		when(credentials.isCardholderNameEnabled()).thenReturn(true); // Cardholder name is required

		PaymentHandlerBean result = stripePaymentHandler.processRedirectPayment(bookingData);

		assertNull(result); // The method should return null when cardholder name is empty and enabled
		verifyNoInteractions(stripePaymentIntentProcessor);
	}

	@Test
	public void testProcessRedirectPayment_NoCardholderNameRequired()
	{
		int affiliateId = 1;
		String guid = "guid123";
		String reference = "reference123";
		String email = "email@example.com";
		String phone = "1234567890";
		String registration = "ABC123";
		String paymentIntentId = "pi_123456";
		String membershipId = "123";

		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getCustomerGuid()).thenReturn(guid);
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getRawServletAbsoluteUrl()).thenReturn("redirectUrl/path");
		when(bookingData.getCardholderName()).thenReturn("");
		when(bookingData.getEmail()).thenReturn(email);
		when(bookingData.getTelno()).thenReturn(phone);
		when(bookingData.getCarReg()).thenReturn(registration);
		when(bookingData.getPaymentIntentId()).thenReturn(paymentIntentId);
		when(bookingData.getStartDate()).thenReturn("01-01-2025");
		when(bookingData.getFname()).thenReturn("John");
		when(bookingData.getLname()).thenReturn("Doe");
		when(bookingData.getSiteId()).thenReturn(1);
		when(bookingData.getMembershipId()).thenReturn(membershipId);

		when(paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.STRIPE)).thenReturn(credentials);
		when(credentials.isCardholderNameEnabled()).thenReturn(false);
		when(credentials.isSendMetadata()).thenReturn(true);
		when(paymentService.fetchAffiliatesById(affiliateId)).thenReturn(affiliate);
		String affiliateCode = "affiliateCode";
		when(affiliate.getCode()).thenReturn(affiliateCode);

		PaymentHandlerBean result = stripePaymentHandler.processRedirectPayment(bookingData);

		assertNotNull(result);
		assertTrue(result.isRedirectRequired());
		assertTrue(result.isStripeRedirect());
		assertEquals("redirectUrl/processPaymentCmd?cmd=stripeConfirmation"
				+ "&affiliateId=1"
				+ "&redirectUrl=redirectUrl%2Fpath"
				+ "&customerGuid=guid123"
				+ "&bookingReference=reference123"
				+ "&startDate=01-01-2025"
				+ "&firstName=John"
				+ "&lastName=Doe"
				+ "&siteId=1", 
				result.getCheckoutUrl());

		verify(stripePaymentIntentProcessor).updatePaymentIntentWithMetadata(eq(reference), eq(affiliateCode),
				eq(email), eq(phone), eq(registration), eq(paymentIntentId), eq(affiliateId), eq(guid),
				eq(membershipId), eq(false));
	}
	
	@Test
	public void testProcessRedirectPayment_CardholderNameEmptyWhenEnabled_NonCardPayment()
	{
		int affiliateId = 1;
		String guid = "guid123";
		String reference = "reference123";
		String servletUrl = "http://example.com/payment";
		String cardholderName = ""; // Invalid empty cardholder name
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getCustomerGuid()).thenReturn(guid);
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getRawServletAbsoluteUrl()).thenReturn(servletUrl);
		when(bookingData.getCardholderName()).thenReturn(cardholderName);
		when(bookingData.isNonCardPayment()).thenReturn(true);

		when(paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.STRIPE)).thenReturn(credentials);
		when(credentials.isCardholderNameEnabled()).thenReturn(true); // Cardholder name is required

		PaymentHandlerBean result = stripePaymentHandler.processRedirectPayment(bookingData);

		assertNotNull(result); // The method should return null when cardholder name is empty and enabled
	}
	
	@Test
	public void testProcessSubCmd_StripeConfirmation_SucceededStatus()
	{
		int affiliateId = 1;
		String reference = "reference123";
		String cmd = "stripeConfirmation";
		String callbackData = "paymentIntentId";
		Map<String, String> testMetaData = new HashMap<>();
		testMetaData.put("Customer_email", "customer@example.com");

		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, callbackData)).thenReturn(paymentIntent);
		when(paymentIntent.getStatus()).thenReturn("succeeded");
		when(paymentIntent.getMetadata()).thenReturn(testMetaData);

		SubscriptionBookingData result = stripePaymentHandler.processSubCmd(affiliateId, reference, cmd, callbackData, false, "guid");

		assertTrue(result.isPaymentSuccess());
		assertEquals("customer@example.com", result.getEmail());
		verify(stripePaymentIntentProcessor).getPaymentIntent(affiliateId, callbackData);
	}

	@Test
	public void testProcessSubCmd_StripeConfirmation_SucceededStatus_EmptyMetaDataField_EmailNotSaved()
	{
		int affiliateId = 1;
		String reference = "reference123";
		String cmd = "stripeConfirmation";
		String callbackData = "paymentIntentId";
		Map<String, String> testMetaData = new HashMap<>();
		testMetaData.put("Customer_email", "");

		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, callbackData)).thenReturn(paymentIntent);
		when(paymentIntent.getStatus()).thenReturn("succeeded");
		when(paymentIntent.getMetadata()).thenReturn(testMetaData);

		SubscriptionBookingData result = stripePaymentHandler.processSubCmd(affiliateId, reference, cmd, callbackData, false, "guid");

		assertTrue(result.isPaymentSuccess());
		verify(stripePaymentIntentProcessor).getPaymentIntent(affiliateId, callbackData);
	}

	@Test
	public void testProcessSubCmd_StripeConfirmation_SucceededStatus_NullMetaData_EmailNotSaved()
	{
		int affiliateId = 1;
		String reference = "reference123";
		String cmd = "stripeConfirmation";
		String callbackData = "paymentIntentId";

		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, callbackData)).thenReturn(paymentIntent);
		when(paymentIntent.getStatus()).thenReturn("succeeded");
		when(paymentIntent.getMetadata()).thenReturn(null);

		SubscriptionBookingData result = stripePaymentHandler.processSubCmd(affiliateId, reference, cmd, callbackData, false, "guid");

		assertTrue(result.isPaymentSuccess());
		verify(stripePaymentIntentProcessor).getPaymentIntent(affiliateId, callbackData);
	}

	@Test
	public void testProcessSubCmd_StripeConfirmation_ProcessingStatus()
	{
		int affiliateId = 1;
		String reference = "reference123";
		String cmd = "stripeConfirmation";
		String callbackData = "paymentIntentId";
		Map<String, String> testMetaData = new HashMap<>();
		testMetaData.put("Customer_email", "customer@example.com");

		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, callbackData)).thenReturn(paymentIntent);
		when(paymentIntent.getStatus()).thenReturn("processing");
		when(paymentIntent.getMetadata()).thenReturn(testMetaData);

		SubscriptionBookingData result = stripePaymentHandler.processSubCmd(affiliateId, reference, cmd, callbackData, false, "guid");

		assertNotNull(result);
		assertTrue(result.isPaymentSuccess());
		assertEquals("customer@example.com", result.getEmail());
		verify(stripePaymentIntentProcessor).getPaymentIntent(affiliateId, callbackData);
	}

	@Test
	public void testProcessSubCmd_StripeConfirmation_PaymentIntentNull()
	{
		int affiliateId = 1;
		String reference = "reference123";
		String cmd = "stripeConfirmation";
		String callbackData = "paymentIntentId";

		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, callbackData)).thenReturn(null); // No payment intent

		SubscriptionBookingData result = stripePaymentHandler.processSubCmd(affiliateId, reference, cmd, callbackData, false, "guid");

		assertNotNull(result);
		assertFalse(result.isPaymentSuccess());
		assertTrue(result.isPaymentFail());
	}

	@Test
	public void testProcessSubCmd_StripeConfirmation_FailedPaymentStatus()
	{
		int affiliateId = 1;
		String cmd = "stripeConfirmation";
		String callbackData = "paymentIntentId";

		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, callbackData)).thenReturn(paymentIntent);
		when(paymentIntent.getStatus()).thenReturn("failed");
		when(paymentIntent.getLastPaymentError()).thenReturn(stripeError);
		when(stripeError.getMessage()).thenReturn("Payment failed");
		when(stripeError.getCode()).thenReturn("card_declined");
		when(stripeError.getDeclineCode()).thenReturn("insufficient_funds");

		SubscriptionBookingData result = stripePaymentHandler.processSubCmd(affiliateId, "ref", cmd, callbackData, false, "guid");

		assertNotNull(result);
		assertFalse(result.isPaymentSuccess());
		verify(stripePaymentIntentProcessor).getPaymentIntent(affiliateId, callbackData);
	}

	@Test
	public void testProcessSubCmd_CommandNotRecognized()
	{
		assertNull(stripePaymentHandler.processSubCmd(123, "ref", "invalidCmd", "", false, "guid"));
	}

	@Test
	public void testProcessAfterBooking()
	{
		String reference = "ref";
		int affiliateId = 123;
		String paymentIntentId = "pi_3Qn1BEAUpPOy7RTs3Qtj1pky";
		String membershipId = "123456";
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getPaymentIntentId()).thenReturn(paymentIntentId);
		when(bookingData.getMembershipId()).thenReturn(membershipId);
		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, paymentIntentId)).thenReturn(paymentIntent);
		when(stripePaymentIntentProcessor.updatePaymentIntentWithMembershipId(reference, affiliateId, paymentIntent, membershipId)).thenReturn(true);

		assertTrue(stripePaymentHandler.processAfterBooking(bookingData));
	}

	@Test
	public void testProcessAfterBooking_PaymentIntent_Null()
	{
		String reference = "ref";
		int affiliateId = 123;
		String paymentIntentId = "pi_3Qn1BEAUpPOy7RTs3Qtj1pky";
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getPaymentIntentId()).thenReturn(paymentIntentId);
		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, paymentIntentId)).thenReturn(null);

		assertFalse(stripePaymentHandler.processAfterBooking(bookingData));
	}

	@Test
	public void testProcessAfterBooking_MembershipId_Empty()
	{
		String reference = "ref";
		int affiliateId = 123;
		String paymentIntentId = "pi_3Qn1BEAUpPOy7RTs3Qtj1pky";
		String membershipId = "";
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getPaymentIntentId()).thenReturn(paymentIntentId);
		when(bookingData.getMembershipId()).thenReturn(membershipId);
		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, paymentIntentId)).thenReturn(paymentIntent);

		assertFalse(stripePaymentHandler.processAfterBooking(bookingData));
	}

	@Test
	public void testProcessAfterBooking_PaymentIntentNotUpdated()
	{
		String reference = "ref";
		int affiliateId = 123;
		String paymentIntentId = "pi_3Qn1BEAUpPOy7RTs3Qtj1pky";
		String membershipId = "123456";
		when(bookingData.getBookingReference()).thenReturn(reference);
		when(bookingData.getAffiliateId()).thenReturn(affiliateId);
		when(bookingData.getPaymentIntentId()).thenReturn(paymentIntentId);
		when(bookingData.getMembershipId()).thenReturn(membershipId);
		when(stripePaymentIntentProcessor.getPaymentIntent(affiliateId, paymentIntentId)).thenReturn(paymentIntent);
		when(stripePaymentIntentProcessor.updatePaymentIntentWithMembershipId(reference, affiliateId, paymentIntent, membershipId)).thenReturn(false);

		assertFalse(stripePaymentHandler.processAfterBooking(bookingData));
	}

	@Test
	public void testGetType()
	{
		assertEquals(PaymentGatewayType.STRIPE, stripePaymentHandler.getType());
	}
}
