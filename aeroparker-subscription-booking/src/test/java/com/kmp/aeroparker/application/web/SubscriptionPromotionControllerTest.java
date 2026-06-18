package com.kmp.aeroparker.application.web;

import com.kmp.aeroparker.application.builder.PromotionBuilder;
import com.kmp.aeroparker.application.db.service.PromotionService;
import com.kmp.aeroparker.application.external.api.SubscriptionBookingRequestHandler;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.external.api.datatypes.Promotion;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionPromotionResponse;
import com.kmp.aeroparker.application.model.promotion.PromotionRequest;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PromotionsPromoCodes;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionPromotionControllerTest
{
	@Mock
	private SubscriptionBookingRequestHandler requestHandler;
	@Mock
	private PromotionService promotionService;
	@Mock
	private PromotionBuilder promotionBuilder;
	@Mock
	private SubscriptionRenewalService renewalService;
	@Mock
	private Localise localise;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private AffiliateConfig affiliateConfig;
	@Mock
	private SubscriptionControllerService service;
	@InjectMocks
	private SubscriptionPromotionController controller;

	private static final String CUSTOMER_GUID = "guid-123";
	private static final String PROMO_CODE = "SAVE10";
	private static final String EMAIL = "test@example.com";
	private static final int PRODUCT_ID = 1;

	@Test
	void testValidatePromotion_Success() throws Exception
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(PRODUCT_ID);
		request.setPromoCode(PROMO_CODE);
		request.setEmail(EMAIL);

		Promotion promotion = new Promotion();
		promotion.setValid(true);
		promotion.setPromoCode(PROMO_CODE);
		promotion.setDiscount(new BigDecimal("10.00"));
		promotion.setDiscountedPrice(new BigDecimal("90.00"));

		SubscriptionPromotionResponse promotionResponse = new SubscriptionPromotionResponse();
		promotionResponse.setPromotion(promotion);

		PromotionsPromoCodes promoCodeEntity = new PromotionsPromoCodes();
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();

		Map<String, Object> renewalResponse = new HashMap<>();

		when(renewalService.validateSubscription(any(SubscriptionBookingData.class), anyString()))
				.thenReturn(renewalResponse);
		when(requestHandler.validatePromotion(any(), anyInt(), anyString(), anyString()))
				.thenReturn(promotionResponse);
		when(promotionService.fetchPromoByPromocodeAndSiteId(anyString(), anyInt())).thenReturn(promoCodeEntity);
		when(promotionBuilder.buildSessionPromotion(anyString(), any(), any())).thenReturn(sessionPromotion);
		when(localise.price(anyFloat())).thenReturn("£10.00");
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean())).thenReturn("£10.00");
		when(languageFieldsList.getTranslation(anyString())).thenReturn("Promo code");
		when(languageFieldsList.getTranslation(anyString())).thenReturn("applied");
		when(service.formatRightToCancelText(any(), any())).thenReturn("I authorise to debit £90.00");

		ResponseEntity<Map<String, Object>> response = controller.validatePromotion(request, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("valid")).isEqualTo(true);
		assertThat(body.get("promoCode")).isEqualTo(PROMO_CODE);
		assertThat(body.get("discount")).isEqualTo(new BigDecimal("10.00"));
		assertThat(body.get("discountedPrice")).isEqualTo(new BigDecimal("90.00"));

		verify(promotionService).saveSessionPromotion(sessionPromotion);
	}

	@Test
	void testValidatePromotion_InvalidPromoCode() throws Exception
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(PRODUCT_ID);
		request.setPromoCode(PROMO_CODE);
		request.setEmail(EMAIL);

		Promotion promotion = new Promotion();
		promotion.setValid(false);

		SubscriptionPromotionResponse promotionResponse = new SubscriptionPromotionResponse();
		promotionResponse.setPromotion(promotion);

		PromotionsPromoCodes promoCodeEntity = new PromotionsPromoCodes();
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		SubscriptionSessionPromotion invalidSessionPromotion = new SubscriptionSessionPromotion();

		Map<String, Object> renewalResponse = new HashMap<>();

		when(renewalService.validateSubscription(any(SubscriptionBookingData.class), anyString()))
				.thenReturn(renewalResponse);
		when(requestHandler.validatePromotion(any(), anyInt(), anyString(), anyString()))
				.thenReturn(promotionResponse);
		when(promotionBuilder.buildInvalidSessionPromotion(anyString(), anyString())).thenReturn(invalidSessionPromotion);
		when(languageFieldsList.getTranslation(anyString()))
				.thenReturn("Invalid or expired promo code");

		ResponseEntity<Map<String, Object>> response = controller.validatePromotion(request, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("valid")).isEqualTo(false);
		assertThat(body.get("errorMessage")).isEqualTo("Invalid or expired promo code");

		verify(promotionService).saveSessionPromotion(invalidSessionPromotion);
	}

	@Test
	void testValidatePromotion_MissingProductId() throws Exception
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(0);
		request.setPromoCode(PROMO_CODE);
		request.setEmail(EMAIL);

		when(languageFieldsList.getTranslation("Invalid request parameters"))
				.thenReturn("Invalid request parameters");

		ResponseEntity<Map<String, Object>> response = controller.validatePromotion(request, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("valid")).isEqualTo(false);
		assertThat(body.get("errorMessage")).isEqualTo("Invalid request parameters");

		verify(requestHandler, never()).validatePromotion(any(), anyInt(), anyString(), anyString());
	}

	@Test
	void testValidatePromotion_MissingPromoCode() throws Exception
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(PRODUCT_ID);
		request.setPromoCode("");
		request.setEmail(EMAIL);

		when(languageFieldsList.getTranslation("Invalid request parameters"))
				.thenReturn("Invalid request parameters");

		ResponseEntity<Map<String, Object>> response = controller.validatePromotion(request, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("valid")).isEqualTo(false);
		assertThat(body.get("errorMessage")).isEqualTo("Invalid request parameters");

		verify(requestHandler, never()).validatePromotion(any(), anyInt(), anyString(), anyString());
	}

	@Test
	void testValidatePromotion_MissingGuid() throws Exception
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(PRODUCT_ID);
		request.setPromoCode(PROMO_CODE);
		request.setEmail(EMAIL);

		when(languageFieldsList.getTranslation("Session expired, please refresh"))
				.thenReturn("Session expired, please refresh");

		ResponseEntity<Map<String, Object>> response = controller.validatePromotion(request, "");

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("valid")).isEqualTo(false);
		assertThat(body.get("errorMessage")).isEqualTo("Session expired, please refresh");

		verify(requestHandler, never()).validatePromotion(any(), anyInt(), anyString(), anyString());
	}

	@Test
	void testValidatePromotion_RenewalDiscountAlreadyApplied() throws Exception
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(PRODUCT_ID);
		request.setPromoCode(PROMO_CODE);
		request.setEmail(EMAIL);

		Map<String, Object> renewalResponse = new HashMap<>();
		renewalResponse.put("newStartDate", "2025-12-01");

		when(renewalService.validateSubscription(any(SubscriptionBookingData.class), anyString()))
				.thenReturn(renewalResponse);
		when(languageFieldsList.getTranslation("Unable to validate promo code"))
				.thenReturn("Unable to validate promo code");

		ResponseEntity<Map<String, Object>> response = controller.validatePromotion(request, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("valid")).isEqualTo(false);
		assertThat(body.get("errorMessage")).isEqualTo("Unable to validate promo code");

		verify(requestHandler, never()).validatePromotion(any(), anyInt(), anyString(), anyString());
	}

	@Test
	void testValidatePromotion_NullPromotionResponse() throws Exception
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(PRODUCT_ID);
		request.setPromoCode(PROMO_CODE);
		request.setEmail(EMAIL);

		Map<String, Object> renewalResponse = new HashMap<>();

		when(renewalService.validateSubscription(any(SubscriptionBookingData.class), anyString()))
				.thenReturn(renewalResponse);
		when(languageFieldsList.getTranslation(anyString()))
				.thenReturn("Unable to validate promo code at this time");

		ResponseEntity<Map<String, Object>> response = controller.validatePromotion(request, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("valid")).isEqualTo(false);
		assertThat(body.get("errorMessage")).isEqualTo("Unable to validate promo code at this time");
	}

	@Test
	void testValidatePromotion_NullPromotion() throws Exception
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(PRODUCT_ID);
		request.setPromoCode(PROMO_CODE);
		request.setEmail(EMAIL);

		SubscriptionPromotionResponse promotionResponse = new SubscriptionPromotionResponse();
		promotionResponse.setPromotion(null);

		Map<String, Object> renewalResponse = new HashMap<>();

		when(renewalService.validateSubscription(any(SubscriptionBookingData.class), anyString()))
				.thenReturn(renewalResponse);
		when(requestHandler.validatePromotion(any(), anyInt(), anyString(), anyString())).thenReturn(promotionResponse);
		when(languageFieldsList.getTranslation(anyString()))
				.thenReturn("Unable to validate promo code at this time");

		ResponseEntity<Map<String, Object>> response = controller.validatePromotion(request, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("valid")).isEqualTo(false);
		assertThat(body.get("errorMessage")).isEqualTo("Unable to validate promo code at this time");
	}

	@Test
	void testValidatePromotion_ExceptionThrown() throws Exception
	{
		PromotionRequest request = new PromotionRequest();
		request.setSubscriptionProductId(PRODUCT_ID);
		request.setPromoCode(PROMO_CODE);
		request.setEmail(EMAIL);

		when(renewalService.validateSubscription(any(SubscriptionBookingData.class), anyString()))
				.thenThrow(new RuntimeException("Test exception"));
		when(languageFieldsList.getTranslation("An error occurred while validating the promo code"))
				.thenReturn("An error occurred while validating the promo code");

		ResponseEntity<Map<String, Object>> response = controller.validatePromotion(request, CUSTOMER_GUID);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		Map<String, Object> body = response.getBody();
		assertThat(body).isNotNull();
		assertThat(body.get("valid")).isEqualTo(false);
		assertThat(body.get("errorMessage")).isEqualTo("An error occurred while validating the promo code");
	}

}
