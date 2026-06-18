package com.kmp.aeroparker.application.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.builder.ConfigBuilder;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.model.AnalyticsLocations;
import com.kmp.aeroparker.application.model.DetailsConfig;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.payment.handler.PaymentHandler;
import com.kmp.aeroparker.application.processor.SubscriptionAnalyticsProcessor;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesContent;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesDisplay;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class DetailsControllerTest
{
	@Mock
	private ConfigBuilder configBuilder;
	@Mock
	private PaymentHandler paymentHandler;
	@Mock
	private SubscriptionControllerService service;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@Mock
	private Basket basket;
	@Mock
	private SubscriptionAnalyticsProcessor analyticsProcessor;
	@InjectMocks
	private DetailsController controller;

	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	private Model model;

	@SuppressWarnings("unchecked")
	@Test
	void testDetails()
	{
		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);
		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "1");
		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(affiliatesContent.getTermsAndConditions()).thenReturn("terms and condition");
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");
		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		Sites site = mock(Sites.class);
		when(site.getTitle()).thenReturn("siteTitle");
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(requestBean.getCurrency()).thenReturn("�");
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(mock(DetailsConfig.class));
		when(requestBean.isMultiPurchase()).thenReturn(true);
		Map<String, Object> map = new HashMap<>();
		when(paymentHandler.setUpTransaction(any())).thenReturn(map);
		List<Integer> productIds = new ArrayList<>();
		productIds.add(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getAllProductIds()).thenReturn(productIds);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.getCountiesIfRequired(anyBoolean(), anyString())).thenReturn(Collections.emptyMap());
		when(requestBean.getSiteTitle()).thenReturn("TestSite");
		when(service.getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean())).thenReturn(Collections.emptyMap());
		controller.details(model, "[{productId: 13, startDate: \"13/08/2019\"}]", "", null, "");
		verify(model, times(25)).addAttribute(anyString(), any());
		verify(model, times(1)).addAllAttributes(any(HashMap.class));
		verify(configBuilder).build(anyInt(), anyInt(), any(), anyInt());
		verify(paymentHandler).setUpTransaction(any());
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).getCountiesIfRequired(anyBoolean(), anyString());
		verify(service).getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean());
		verify(service).formatRightToCancelText(any(), any());
		verify(service).getVehicleLookupLogin(anyInt());
		verify(service).shouldSkipProductSelection(any());
		verify(analyticsProcessor).processSubscriptionAnalytics(model, basket, requestBean.getCurrency(), 
				AnalyticsLocations.SUBSCRIPTION_PAYMENT_DETAIL.getId(), 1);
		verifyNoMoreInteractions(model, paymentHandler, configBuilder, service);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDetails_Redirect_From_Booking_With_Error()
	{
		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);
		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "1");
		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");
		when(affiliatesContent.getTermsAndConditions()).thenReturn("terms and condition");
		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		Sites site = mock(Sites.class);
		when(site.getTitle()).thenReturn("siteTitle");
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(requestBean.getCurrency()).thenReturn("�");
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(mock(DetailsConfig.class));
		when(requestBean.isMultiPurchase()).thenReturn(true);
		Map<String, Object> map = new HashMap<>();
		when(paymentHandler.setUpTransaction(any())).thenReturn(map);
		List<Integer> productIds = new ArrayList<>();
		productIds.add(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getAllProductIds()).thenReturn(productIds);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.getCountiesIfRequired(anyBoolean(), anyString())).thenReturn(Collections.emptyMap());
		when(requestBean.getSiteTitle()).thenReturn("TestSite");
		when(service.getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean())).thenReturn(Collections.emptyMap());
		controller.details(model, "[{productId: 13, startDate: \"13/08/2019\"}]", "", null, "Payment Error");
		verify(model, times(26)).addAttribute(anyString(), any());
		verify(model, times(1)).addAllAttributes(any(HashMap.class));
		verify(configBuilder).build(anyInt(), anyInt(), any(), anyInt());
		verify(paymentHandler).setUpTransaction(any());
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).getCountiesIfRequired(anyBoolean(), anyString());
		verify(service).formatRightToCancelText(any(), any());
		verify(service).getVehicleLookupLogin(anyInt());
		verify(service).shouldSkipProductSelection(any());
		verifyNoMoreInteractions(model, paymentHandler, configBuilder, service);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDetails_UseTermsUrl_Affiliate_Display_Null()
	{
		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);
		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "1");
		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(affiliatesContent.getTermsAndConditions()).thenReturn("");
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");
		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		when(requestBean.getAffiliatesDisplay()).thenReturn(null);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		Sites site = mock(Sites.class);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getTitle()).thenReturn("siteTitle");
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(requestBean.getCurrency()).thenReturn("�");
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(mock(DetailsConfig.class));
		when(requestBean.isMultiPurchase()).thenReturn(true);
		Map<String, Object> map = new HashMap<>();
		when(paymentHandler.setUpTransaction(any())).thenReturn(map);
		List<Integer> productIds = new ArrayList<>();
		productIds.add(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getAllProductIds()).thenReturn(productIds);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.getCountiesIfRequired(anyBoolean(), anyString())).thenReturn(Collections.emptyMap());
		when(requestBean.getSiteTitle()).thenReturn("TestSite");
		when(service.getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean())).thenReturn(Collections.emptyMap());
		controller.details(model, "[{productId: 13, startDate: \"13/08/2019\"}]", "", null, "");
		verify(model, times(25)).addAttribute(anyString(), any());
		verify(model, times(1)).addAllAttributes(any(HashMap.class));
		verify(configBuilder).build(anyInt(), anyInt(), any(), anyInt());
		verify(paymentHandler).setUpTransaction(any());
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).getCountiesIfRequired(anyBoolean(), anyString());
		verify(service).getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean());
		verify(service).formatRightToCancelText(any(), any());
		verify(service).getVehicleLookupLogin(anyInt());
		verify(service).shouldSkipProductSelection(any());
		verifyNoMoreInteractions(model, paymentHandler, configBuilder, service);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDetails_UseTermsUrl()
	{
		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);
		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "1");
		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(affiliatesContent.getTermsAndConditions()).thenReturn("");
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");
		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		AffiliatesDisplay affiliatesDisplay = mock(AffiliatesDisplay.class);
		when(affiliatesDisplay.getTermsUrl()).thenReturn("url");
		when(requestBean.getAffiliatesDisplay()).thenReturn(affiliatesDisplay);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		Sites site = mock(Sites.class);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getTitle()).thenReturn("siteTitle");
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(requestBean.getCurrency()).thenReturn("�");
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(mock(DetailsConfig.class));
		when(requestBean.isMultiPurchase()).thenReturn(true);
		Map<String, Object> map = new HashMap<>();
		when(paymentHandler.setUpTransaction(any())).thenReturn(map);
		List<Integer> productIds = new ArrayList<>();
		productIds.add(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getAllProductIds()).thenReturn(productIds);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.getCountiesIfRequired(anyBoolean(), anyString())).thenReturn(Collections.emptyMap());
		when(requestBean.getSiteTitle()).thenReturn("TestSite");
		when(service.getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean())).thenReturn(Collections.emptyMap());
		controller.details(model, "[{productId: 13, startDate: \"13/08/2019\"}]", "", null, "");
		verify(model, times(25)).addAttribute(anyString(), any());
		verify(model, times(1)).addAllAttributes(any(HashMap.class));
		verify(configBuilder).build(anyInt(), anyInt(), any(), anyInt());
		verify(paymentHandler).setUpTransaction(any());
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).getCountiesIfRequired(anyBoolean(), anyString());
		verify(service).formatRightToCancelText(any(), any());
		verify(service).getVehicleLookupLogin(anyInt());
		verify(service).shouldSkipProductSelection(any());
		verifyNoMoreInteractions(model, paymentHandler, configBuilder, service);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDetails_Show_Terms_False()
	{
		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);
		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "0");
		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");
		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		Sites site = mock(Sites.class);
		when(site.getTitle()).thenReturn("siteTitle");
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(requestBean.getCurrency()).thenReturn("�");
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(mock(DetailsConfig.class));
		when(requestBean.isMultiPurchase()).thenReturn(true);
		Map<String, Object> map = new HashMap<>();
		when(paymentHandler.setUpTransaction(any())).thenReturn(map);
		List<Integer> productIds = new ArrayList<>();
		productIds.add(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getAllProductIds()).thenReturn(productIds);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.getCountiesIfRequired(anyBoolean(), anyString())).thenReturn(Collections.emptyMap());
		when(requestBean.getSiteTitle()).thenReturn("TestSite");
		when(service.getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean())).thenReturn(Collections.emptyMap());
		controller.details(model, "[{productId: 13, startDate: \"13/08/2019\"}]", "", null, "");
		verify(model, times(23)).addAttribute(anyString(), any());
		verify(model, times(1)).addAllAttributes(any(HashMap.class));
		verify(configBuilder).build(anyInt(), anyInt(), any(), anyInt());
		verify(paymentHandler).setUpTransaction(any());
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).getCountiesIfRequired(anyBoolean(), anyString());
		verify(service).getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean());
		verify(service).formatRightToCancelText(any(), any());
		verify(service).getVehicleLookupLogin(anyInt());
		verify(service).shouldSkipProductSelection(any());
		verifyNoMoreInteractions(model, paymentHandler, configBuilder, service);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDetails_AffiliateContent_Null()
	{
		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);
		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "1");
		when(requestBean.getAffiliateContent()).thenReturn(null);		
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		Sites site = mock(Sites.class);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getTitle()).thenReturn("siteTitle");
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(requestBean.getCurrency()).thenReturn("�");
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(mock(DetailsConfig.class));
		when(requestBean.isMultiPurchase()).thenReturn(true);
		Map<String, Object> map = new HashMap<>();
		when(paymentHandler.setUpTransaction(any())).thenReturn(map);
		List<Integer> productIds = new ArrayList<>();
		productIds.add(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getAllProductIds()).thenReturn(productIds);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.getCountiesIfRequired(anyBoolean(), anyString())).thenReturn(Collections.emptyMap());
		when(requestBean.getSiteTitle()).thenReturn("TestSite");
		when(service.getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean())).thenReturn(Collections.emptyMap());
		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		controller.details(model, "[{productId: 13, startDate: \"13/08/2019\"}]", "", null, "");
		verify(model, times(25)).addAttribute(anyString(), any());
		verify(model, times(1)).addAllAttributes(any(HashMap.class));
		verify(configBuilder).build(anyInt(), anyInt(), any(), anyInt());
		verify(paymentHandler).setUpTransaction(any());
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).getCountiesIfRequired(anyBoolean(), anyString());
		verify(service).getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean());
		verify(service).formatRightToCancelText(any(), any());
		verify(service).getVehicleLookupLogin(anyInt());
		verify(service).shouldSkipProductSelection(any());
		verifyNoMoreInteractions(model, paymentHandler, configBuilder, service);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDetails_Redirect_From_Payment_Error()
	{
		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);
		Sites site = mock(Sites.class);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(site.getTitle()).thenReturn("siteTitle");
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(requestBean.getCurrency()).thenReturn("�");
		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "0");
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(mock(DetailsConfig.class));
		Map<String, Object> map = new HashMap<>();
		when(paymentHandler.setUpTransaction(any())).thenReturn(map);
		when(requestBean.isMultiPurchase()).thenReturn(true);
		List<Integer> productIds = new ArrayList<>();
		productIds.add(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getAllProductIds()).thenReturn(productIds);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.getCountiesIfRequired(anyBoolean(), anyString())).thenReturn(Collections.emptyMap());
		when(requestBean.getSiteTitle()).thenReturn("TestSite");
		when(service.getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean())).thenReturn(Collections.emptyMap());
		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		controller.details(model, "[{productId: 13, startDate: \"13/08/2019\"}]", "", EnhancedRandom.random(SubscriptionBookingData.class), "");
		verify(model, times(23)).addAttribute(anyString(), any());
		verify(model, times(1)).addAllAttributes(any(HashMap.class));
		verify(configBuilder).build(anyInt(), anyInt(), any(), anyInt());
		verify(paymentHandler).setUpTransaction(any());
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).getCountiesIfRequired(anyBoolean(), anyString());
		verify(service).getCountriesMap(anyString(), any(LanguageFieldsList.class), anyBoolean());
		verify(service).formatRightToCancelText(any(), any());
		verify(service).getVehicleLookupLogin(anyInt());
		verify(service).shouldSkipProductSelection(any());
		verifyNoMoreInteractions(model, paymentHandler, configBuilder, service);
	}

	@Test
	void testDetails_Basket_Null()
	{
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(null);
		controller.details(model, "[{productId: 13, startDate: \"13/08/2019\"}]", "", null, "");
		verify(model, times(2)).addAttribute(anyString(), any());
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testRedirectToBooking()
	{
		controller.redirectToBooking(redirectAttributes, EnhancedRandom.random(SubscriptionBookingData.class), "test-guid", "2025-01-01");
		verify(redirectAttributes).addFlashAttribute(anyString(), any(SubscriptionBookingData.class));
		verifyNoMoreInteractions(model);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDetails_WithAppliedPromoCode()
	{
		// Setup basket with promo discount
		when(basket.getPromoDiscount()).thenReturn(new java.math.BigDecimal("10.00"));
		when(basket.getPromoCode()).thenReturn("SAVE10");
		when(basket.getGrandTotal()).thenReturn(new java.math.BigDecimal("90.00"));
		when(basket.getAllProductIds()).thenReturn(Collections.singletonList(123));

		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);

		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "1");

		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(affiliatesContent.getTermsAndConditions()).thenReturn("terms");
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");

		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(requestBean.getSiteTitle()).thenReturn("siteTitle");
		when(requestBean.getSite()).thenReturn(mock(Sites.class));

		DetailsConfig config = mock(DetailsConfig.class);
		when(config.showCounties()).thenReturn(false);
		when(config.getPaymentStepFields()).thenReturn(new ArrayList<>());

		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(config);
		when(service.getCountriesMap(anyString(), any(), anyBoolean())).thenReturn(new HashMap<>());
		when(paymentHandler.setUpTransaction(any())).thenReturn(new HashMap<>());

		controller.details(model, "guid123", "01/01/2024", null, null);

		// Verify that appliedPromoCode is added to model
		verify(model).addAttribute("appliedPromoCode", "SAVE10");
		verify(model).addAttribute(eq("basket"), any(Basket.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDetails_WithoutPromoCode()
	{
		// Setup basket without promo discount
		when(basket.getPromoDiscount()).thenReturn(null);
		when(basket.getGrandTotal()).thenReturn(new java.math.BigDecimal("100.00"));
		when(basket.getAllProductIds()).thenReturn(Collections.singletonList(123));

		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);

		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "1");

		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(affiliatesContent.getTermsAndConditions()).thenReturn("terms");
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");

		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(requestBean.getSiteTitle()).thenReturn("siteTitle");
		when(requestBean.getSite()).thenReturn(mock(Sites.class));

		DetailsConfig config = mock(DetailsConfig.class);
		when(config.showCounties()).thenReturn(false);
		when(config.getPaymentStepFields()).thenReturn(new ArrayList<>());

		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(config);
		when(service.getCountriesMap(anyString(), any(), anyBoolean())).thenReturn(new HashMap<>());
		when(paymentHandler.setUpTransaction(any())).thenReturn(new HashMap<>());

		controller.details(model, "guid123", "01/01/2024", null, null);

		// Verify that appliedPromoCode is NOT added to model when no promo
		verify(model, times(0)).addAttribute(eq("appliedPromoCode"), anyString());
		verify(model).addAttribute(eq("basket"), any(Basket.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void testDetails_WithZeroPromoDiscount()
	{
		// Setup basket with zero promo discount (should not show promo code)
		when(basket.getPromoDiscount()).thenReturn(java.math.BigDecimal.ZERO);
		when(basket.getGrandTotal()).thenReturn(new java.math.BigDecimal("100.00"));
		when(basket.getAllProductIds()).thenReturn(Collections.singletonList(123));

		Locations location = mock(Locations.class);
		when(location.getName()).thenReturn("locationName");
		when(requestBean.getLocation()).thenReturn(location);

		AffiliateConfig affiliateConfig = new AffiliateConfig();
		affiliateConfig.put(AffiliateConfigKeys.DISPLAY_TCS_SECTION, "1");

		AffiliatesContent affiliatesContent = mock(AffiliatesContent.class);
		when(affiliatesContent.getTermsAndConditions()).thenReturn("terms");
		when(affiliatesContent.getPersonalData()).thenReturn("personal data");

		when(requestBean.getAffiliateContent()).thenReturn(affiliatesContent);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(requestBean.getSiteTitle()).thenReturn("siteTitle");
		when(requestBean.getSite()).thenReturn(mock(Sites.class));

		DetailsConfig config = mock(DetailsConfig.class);
		when(config.showCounties()).thenReturn(false);
		when(config.getPaymentStepFields()).thenReturn(new ArrayList<>());

		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(configBuilder.build(anyInt(), anyInt(), any(), anyInt())).thenReturn(config);
		when(service.getCountriesMap(anyString(), any(), anyBoolean())).thenReturn(new HashMap<>());
		when(paymentHandler.setUpTransaction(any())).thenReturn(new HashMap<>());

		controller.details(model, "guid123", "01/01/2024", null, null);

		// Verify that appliedPromoCode is NOT added when discount is zero
		verify(model, times(0)).addAttribute(eq("appliedPromoCode"), anyString());
		verify(model).addAttribute(eq("basket"), any(Basket.class));
	}
}