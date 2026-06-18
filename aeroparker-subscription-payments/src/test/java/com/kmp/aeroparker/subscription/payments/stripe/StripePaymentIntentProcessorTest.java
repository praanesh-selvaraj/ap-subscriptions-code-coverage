package com.kmp.aeroparker.subscription.payments.stripe;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.credentials.StripeCredentials;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import com.stripe.param.RefundCreateParams;

@ExtendWith(MockitoExtension.class)
public class StripePaymentIntentProcessorTest
{
	private static final String STRIPE_API_SECRET_KEY = "sk_test_placeholder";
	@Mock
	private PaymentService service;
	@Mock
	private StripePaymentRequestSender requestSender;
	@Mock
	private PaymentIntentCreateParams paymentIntentCreateParams;
	@Mock
	private PaymentIntent paymentIntent;
	@Mock
	private PaymentMethod paymentMethod;
	@Mock
	private StripeCredentials credentials;
	@Mock
	private PaymentIntentUpdateParams paymentIntentUpdateParams;
	@Mock
	private StripeException stripeException;
	@InjectMocks
	private StripePaymentIntentProcessor stripePaymentIntentProcessor;

	@BeforeEach
	public void init()
	{
		StripeCredentials credentials = mock(StripeCredentials.class);
		when(credentials.getSecretKey()).thenReturn(STRIPE_API_SECRET_KEY);
		when(service.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(credentials);
	}

	@Test
	public void testGeneratePaymentIntent() throws StripeException
	{
		when(requestSender.paramBuilder(any(), any())).thenReturn(paymentIntentCreateParams);
		when(requestSender.create(any(), any())).thenReturn(paymentIntent);

		assertNotNull(stripePaymentIntentProcessor.generatePaymentIntent(1, "1000", "gbp"));
	}

	@Test
	public void testGeneratePaymentIntent_NullIntent() throws StripeException
	{
		when(requestSender.paramBuilder(any(), any())).thenReturn(paymentIntentCreateParams);
		when(requestSender.create(any(), any())).thenReturn(null);

		assertNull(stripePaymentIntentProcessor.generatePaymentIntent(1, "1000", "gbp"));
	}

	@Test
	public void testGeneratePaymentIntent_NullIntent_StripeException() throws StripeException
	{
		when(requestSender.paramBuilder(any(), any())).thenReturn(paymentIntentCreateParams);
		when(requestSender.create(any(), any())).thenThrow(mock(StripeException.class));

		assertNull(stripePaymentIntentProcessor.generatePaymentIntent(1, "1000", "gbp"));
	}

	@Test
	public void testUpdatePaymentIntent() throws StripeException
	{
		when(requestSender.retrieve(anyString(), any())).thenReturn(paymentIntent);
		when(paymentIntent.getAmount()).thenReturn(200000l);
		when(paymentIntent.getId()).thenReturn("Id");
		when(paymentIntent.getStatus()).thenReturn("pending");
		when(requestSender.update(any(), any(), any())).thenReturn(paymentIntent);

		assertTrue(stripePaymentIntentProcessor.updatePaymentIntent(paymentIntent.getId(), 1, "2000"));
		
		verify(requestSender).update(any(), any(), any());
	}

	@Test
	public void testUpdatePaymentIntent_FailedToUpdate() throws StripeException
	{
		when(requestSender.retrieve(anyString(), any())).thenReturn(paymentIntent);
		when(paymentIntent.getId()).thenReturn("Id");
		when(paymentIntent.getStatus()).thenReturn("pending");
		when(requestSender.update(any(), any(), any())).thenReturn(null);

		assertFalse(stripePaymentIntentProcessor.updatePaymentIntent(paymentIntent.getId(), 1, "2000"));
		
		verify(requestSender).update(any(), any(), any());
	}

	@Test
	public void testUpdatePaymentIntent_AlreadySucceeded() throws StripeException
	{
		when(requestSender.retrieve(anyString(), any())).thenReturn(paymentIntent);
		when(paymentIntent.getId()).thenReturn("Id");
		when(paymentIntent.getStatus()).thenReturn("succeeded");

		assertTrue(stripePaymentIntentProcessor.updatePaymentIntent(paymentIntent.getId(), 1, "2000"));
		
		verify(requestSender, never()).update(any(), any(), any());
	}
	
	@Test
	public void testStripeRefundPayment() throws StripeException
	{
		RefundCreateParams refundParams = mock(RefundCreateParams.class);
		Refund expectedRefund = mock(Refund.class);
		
		when(requestSender.paramBuilderRefunds(any(BigDecimal.class), anyString())).thenReturn(refundParams);
		when(requestSender.createRefund(eq(refundParams), any())).thenReturn(expectedRefund);
		
		assertEquals(expectedRefund, stripePaymentIntentProcessor.stripeRefundPayment(1, BigDecimal.TEN, "transaction"));
		
		verify(requestSender).paramBuilderRefunds(any(BigDecimal.class), anyString());
		verify(requestSender).createRefund(eq(refundParams), any());
	}
	
	@Test
	public void testStripeRefundPayment_NullRefund() throws StripeException
	{
		RefundCreateParams refundParams = mock(RefundCreateParams.class);
		
		when(requestSender.paramBuilderRefunds(any(BigDecimal.class), anyString())).thenReturn(refundParams);
		when(requestSender.createRefund(eq(refundParams), any())).thenReturn(null);
		
		assertNull(stripePaymentIntentProcessor.stripeRefundPayment(1, BigDecimal.TEN, "transaction"));
		
		verify(requestSender).paramBuilderRefunds(any(BigDecimal.class), anyString());
		verify(requestSender).createRefund(eq(refundParams), any());
	}
	
	@Test
	public void testStripeRefundPayment_NullRefund_StripeException() throws StripeException
	{
		RefundCreateParams refundParams = mock(RefundCreateParams.class);
		
		when(requestSender.paramBuilderRefunds(any(BigDecimal.class), anyString())).thenReturn(refundParams);
		when(requestSender.createRefund(eq(refundParams), any())).thenThrow(mock(StripeException.class));
		
		assertNull(stripePaymentIntentProcessor.stripeRefundPayment(1, BigDecimal.TEN, "transaction"));
		
		verify(requestSender).paramBuilderRefunds(any(BigDecimal.class), anyString());
		verify(requestSender).createRefund(eq(refundParams), any());
	}

	@Test
	public void testGetPaymentIntent() throws StripeException
	{
		when(requestSender.retrieve(anyString(), any())).thenReturn(paymentIntent);
		when(paymentIntent.getId()).thenReturn("intentId");

		assertNotNull(stripePaymentIntentProcessor.getPaymentIntent(1, "intentId"));
	}

	@Test
	public void testGetPaymentIntent_NullIntent() throws StripeException
	{
		when(requestSender.retrieve(anyString(), any())).thenReturn(null);

		assertNull(stripePaymentIntentProcessor.getPaymentIntent(1, "intentId"));
	}

	@Test
	public void testGetPaymentIntent_NullIntent_StripeException() throws StripeException
	{
		when(requestSender.retrieve(anyString(), any())).thenThrow(mock(StripeException.class));

		assertNull(stripePaymentIntentProcessor.getPaymentIntent(1, "intentId"));
	}

	@Test
	public void testGetPaymentMethod() throws StripeException
	{
		Stripe.apiKey = STRIPE_API_SECRET_KEY;
		when(requestSender.retievePaymentMethod(any(), any())).thenReturn(paymentMethod);

		assertNotNull(stripePaymentIntentProcessor.getPaymentMethod(1, paymentIntent));
	}

	@Test
	public void testGetPaymentMethod_NullPaymentMethod() throws StripeException
	{
		Stripe.apiKey = STRIPE_API_SECRET_KEY;
		when(requestSender.retievePaymentMethod(any(), any())).thenReturn(null);

		assertNull(stripePaymentIntentProcessor.getPaymentMethod(1, paymentIntent));
	}

	@Test
	public void testGetPaymentMethod_NullPaymentMethod_StripeException() throws StripeException
	{
		Stripe.apiKey = STRIPE_API_SECRET_KEY;
		when(requestSender.retievePaymentMethod(any(), any())).thenThrow(mock(StripeException.class));

		assertNull(stripePaymentIntentProcessor.getPaymentMethod(1, paymentIntent));
	}
	
	@Test
	public void testUpdatePaymentIntentWithMetadata() throws StripeException
	{
		when(requestSender.buildParametersForMetadata(anyString(), anyString(), anyString(), anyString(), anyString(),
				anyString(), anyString(), anyString())).thenReturn(paymentIntentUpdateParams);
		when(requestSender.updateMetadata(any(), any(PaymentIntentUpdateParams.class), any())).thenReturn(paymentIntent);

		assertTrue(stripePaymentIntentProcessor.updatePaymentIntentWithMetadata("reference", "Dub", "email@email.com",
				"1234567890", "regPlate", "paymentIntentId", 1, "guid"));
	}

	@Test
	public void testUpdatePaymentIntentWithMetadata_WithMembership() throws StripeException
	{
		when(requestSender.buildParametersForMetadata(anyString(), anyString(), anyString(), anyString(), anyString(),
				anyString(), anyString(), anyString(), anyString())).thenReturn(paymentIntentUpdateParams);
		when(requestSender.updateMetadata(any(), any(PaymentIntentUpdateParams.class), any())).thenReturn(paymentIntent);

		assertTrue(stripePaymentIntentProcessor.updatePaymentIntentWithMetadata("reference", "Dub", "email@email.com",
				"1234567890", "regPlate", "paymentIntentId", 1, "guid", "mem123"));
	}

	@Test
	public void testUpdatePaymentIntentWithMetadata_NullPaymentIntent() throws StripeException
	{
		when(requestSender.buildParametersForMetadata(anyString(), anyString(), anyString(), anyString(), anyString(),
				anyString(), anyString(), anyString())).thenReturn(paymentIntentUpdateParams);

		assertFalse(stripePaymentIntentProcessor.updatePaymentIntentWithMetadata("reference", "Dub", "email@email.com",
				"1234567890", "regPlate", "paymentIntentId", 1, "guid"));
	}

	@Test
	public void testUpdatePaymentIntentWithMetadata_NullPaymentIntent_WithMembership() throws StripeException
	{
		when(requestSender.buildParametersForMetadata(anyString(), anyString(), anyString(), anyString(), anyString(),
				anyString(), anyString(), anyString(), anyString())).thenReturn(paymentIntentUpdateParams);

		assertFalse(stripePaymentIntentProcessor.updatePaymentIntentWithMetadata("reference", "Dub", "email@email.com",
				"1234567890", "regPlate", "paymentIntentId", 1, "guid", "mem123"));
	}
	
	@Test
	public void testUpdatePaymentIntentWithMetadata_StripeException() throws StripeException
	{
		when(requestSender.retrieve(anyString(), any())).thenThrow(stripeException);

		assertFalse(stripePaymentIntentProcessor.updatePaymentIntentWithMetadata(
				"reference", "Dub", "email@email.com", "1234567890", "regPlate", "paymentIntentId", 1, "guid"));

		verify(requestSender, never()).updateMetadata(any(), any(PaymentIntentUpdateParams.class), any());
	}

	@Test
	public void testUpdatePaymentIntentWithMembershipId() throws StripeException
	{
		String reference = "ref";
		int affiliateId = 123;
		String membershipId = "123456";
		when(requestSender.buildMembershipIdParameterForMetadata(membershipId)).thenReturn(paymentIntentUpdateParams);
		when(requestSender.updateMetadata(eq(paymentIntent), eq(paymentIntentUpdateParams), any(RequestOptions.class))).thenReturn(paymentIntent);

		assertTrue(stripePaymentIntentProcessor.updatePaymentIntentWithMembershipId(reference, affiliateId, paymentIntent, membershipId));
	}

	@Test
	public void testUpdatePaymentIntentWithMembershipId_UpdatedPaymentIntent_Null() throws StripeException
	{
		String reference = "ref";
		int affiliateId = 123;
		String membershipId = "123456";
		when(requestSender.buildMembershipIdParameterForMetadata(membershipId)).thenReturn(paymentIntentUpdateParams);
		when(requestSender.updateMetadata(eq(paymentIntent), eq(paymentIntentUpdateParams), any(RequestOptions.class))).thenReturn(null);

		assertFalse(stripePaymentIntentProcessor.updatePaymentIntentWithMembershipId(reference, affiliateId, paymentIntent, membershipId));
	}

	@Test
	public void testUpdatePaymentIntentWithMembershipId_StripeException() throws StripeException
	{
		String reference = "ref";
		int affiliateId = 123;
		String membershipId = "123456";
		when(requestSender.buildMembershipIdParameterForMetadata(membershipId)).thenReturn(paymentIntentUpdateParams);
		when(requestSender.updateMetadata(eq(paymentIntent), eq(paymentIntentUpdateParams), any(RequestOptions.class))).thenThrow(mock(StripeException.class));

		assertFalse(stripePaymentIntentProcessor.updatePaymentIntentWithMembershipId(reference, affiliateId, paymentIntent, membershipId));
	}
	
	@Test
	public void getCharge() throws StripeException
	{
		when(requestSender.retrieveCharge(anyString(), any())).thenReturn(mock(Charge.class));
		
		assertNotNull(stripePaymentIntentProcessor.getCharge("chargeId", 1, "paymentIntentId"));
	}
	
	@Test
	public void getCharge_NullCharge() throws StripeException
	{
		when(requestSender.retrieveCharge(anyString(), any())).thenReturn(null);
		
		assertNull(stripePaymentIntentProcessor.getCharge("chargeId", 1, "paymentIntentId"));
	}
	
	@Test
	public void getCharge_StripeException() throws StripeException
	{
		when(requestSender.retrieveCharge(anyString(), any())).thenThrow(stripeException);
		
		assertNull(stripePaymentIntentProcessor.getCharge("chargeId", 1, "paymentIntentId"));
	}
}
