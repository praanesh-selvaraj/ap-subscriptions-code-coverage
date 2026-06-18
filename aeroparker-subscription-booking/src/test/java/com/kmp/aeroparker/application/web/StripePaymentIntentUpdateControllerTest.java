package com.kmp.aeroparker.application.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.payments.stripe.StripePaymentIntentProcessor;
import com.stripe.exception.StripeException;

@ExtendWith(MockitoExtension.class)
class StripePaymentIntentUpdateControllerTest
{
	@Mock
	private StripePaymentIntentProcessor intentProcessor;
	@Mock
	private SubscriptionControllerService subscriptionService;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private Affiliates affiliate;
	@Mock
	private AffiliateSubscriptionSettings affiliateSubscriptionSettings;
	@Mock
	private Basket basket;

	@InjectMocks
	private StripePaymentIntentUpdateController controller;

	private static final String PAYMENT_INTENT_ID = "pi_test123";
	private static final Integer AFFILIATE_ID = 1;
	private static final String CUSTOMER_GUID = "guid-123";

	void setUp()
	{
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
	}

	@Test
	void testUpdatePaymentIntent_Success() throws StripeException
	{
		setUp();
		when(subscriptionService.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getGrandTotal()).thenReturn(new BigDecimal("100.50"));
		when(intentProcessor.updatePaymentIntent(anyString(), anyInt(), anyString())).thenReturn(true);

		ResponseEntity<Map<String, Object>> response = controller.updatePaymentIntent(
				PAYMENT_INTENT_ID, AFFILIATE_ID, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("success")).isEqualTo(true);

		verify(intentProcessor).updatePaymentIntent(eq(PAYMENT_INTENT_ID), eq(AFFILIATE_ID), eq("100.50"));
	}

	@Test
	void testUpdatePaymentIntent_ProcessorFails() throws StripeException
	{
		setUp();
		when(subscriptionService.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getGrandTotal()).thenReturn(new BigDecimal("100.50"));
		when(intentProcessor.updatePaymentIntent(anyString(), anyInt(), anyString())).thenReturn(false);

		ResponseEntity<Map<String, Object>> response = controller.updatePaymentIntent(
				PAYMENT_INTENT_ID, AFFILIATE_ID, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("success")).isEqualTo(false);
	}

	@Test
	void testUpdatePaymentIntent_MissingPaymentIntentId()
	{
		ResponseEntity<Map<String, Object>> response = controller.updatePaymentIntent(
				null, AFFILIATE_ID, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("success")).isEqualTo(false);
	}

	@Test
	void testUpdatePaymentIntent_InvalidAffiliateId()
	{
		ResponseEntity<Map<String, Object>> response = controller.updatePaymentIntent(
				PAYMENT_INTENT_ID, null, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("success")).isEqualTo(false);
	}

	@Test
	void testUpdatePaymentIntent_MissingCustomerGuid()
	{
		ResponseEntity<Map<String, Object>> response = controller.updatePaymentIntent(
				PAYMENT_INTENT_ID, AFFILIATE_ID, null);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("success")).isEqualTo(false);
	}

	@Test
	void testUpdatePaymentIntent_BasketNotFound()
	{
		setUp();
		when(subscriptionService.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(null);

		ResponseEntity<Map<String, Object>> response = controller.updatePaymentIntent(
				PAYMENT_INTENT_ID, AFFILIATE_ID, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("success")).isEqualTo(false);
	}

	@Test
	void testUpdatePaymentIntent_StripeException() throws StripeException
	{
		setUp();
		when(subscriptionService.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getGrandTotal()).thenReturn(new BigDecimal("100.50"));
		when(intentProcessor.updatePaymentIntent(anyString(), anyInt(), anyString()))
				.thenThrow(new StripeException("Stripe error", "request_id", "code", 400) {});

		ResponseEntity<Map<String, Object>> response = controller.updatePaymentIntent(
				PAYMENT_INTENT_ID, AFFILIATE_ID, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("success")).isEqualTo(false);
	}

	@Test
	void testUpdatePaymentIntent_WithPromoApplied() throws StripeException
	{
		setUp();
		when(subscriptionService.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getGrandTotal()).thenReturn(new BigDecimal("90.00")); // After promo discount
		when(intentProcessor.updatePaymentIntent(anyString(), anyInt(), anyString())).thenReturn(true);

		ResponseEntity<Map<String, Object>> response = controller.updatePaymentIntent(
				PAYMENT_INTENT_ID, AFFILIATE_ID, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("success")).isEqualTo(true);

		verify(intentProcessor).updatePaymentIntent(eq(PAYMENT_INTENT_ID), eq(AFFILIATE_ID), eq("90.00"));
	}

	@Test
	void testUpdatePaymentIntent_ZeroAmount() throws StripeException
	{
		setUp();
		when(subscriptionService.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.ZERO);

		ResponseEntity<Map<String, Object>> response = controller.updatePaymentIntent(
				PAYMENT_INTENT_ID, AFFILIATE_ID, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("success")).isEqualTo(false);
		verifyNoInteractions(intentProcessor);
	}
}
