package com.kmp.aeroparker.application.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.kmp.aeroparker.application.engine.PurchaseRequestList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonArray;
import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetails;
import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetailsList;
import com.kmp.aeroparker.application.availability.SubscriptionProductFinder;
import com.kmp.aeroparker.application.builder.PurchaseDataBuilder;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.InvoiceService;
import com.kmp.aeroparker.application.db.service.PromotionService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.db.service.VehicleLookupService;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.SubscriptionBasketBuilder;
import com.kmp.aeroparker.application.factory.BuilderFactory;
import com.kmp.aeroparker.application.factory.LocaleFactory;
import com.kmp.aeroparker.application.model.PurchaseQuery;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.SubscriptionReferenceGenerator;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.presentation.SubscriptionProductDisplayItemBuilder;
import com.kmp.aeroparker.application.presentation.SubscriptionProductDisplayItemList;
import com.kmp.aeroparker.application.utils.JsonUtil;
import com.kmp.aeroparker.application.validator.BookingTimesValidator;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Company;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReservationData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionDiscountedRenewal;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPurchaseData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerServiceTest
{
	@Mock
	private SubscriptionService subscriptionService;
	@Mock
	private BuilderFactory factory;
	@Mock
	private BookingTimesValidator validator;
	@Mock
	private SubscriptionReferenceGenerator referenceGenerator;
	@Mock
	private SubscriptionProductDisplayItemBuilder subscriptionProductBuilder;
	@Mock
	private SubscriptionBasketBuilder basketBuilder;
	@Mock
	private BookingService bookingService;
	@Mock
	private JsonUtil jsonUtil;
	@Mock
	private LocaleFactory localeFactory;
	@Mock
	private SubscriptionProductFinder productFinder;
	@InjectMocks
	private SubscriptionControllerService service;
	@Mock
	private Affiliates affiliate;
	@Mock
	private SubscriptionBookingReservationData reservationData;
	@Mock
	private InvoiceService invoiceService;
	@Mock
	private SiteService siteService;
	@Mock
	private PurchaseDataBuilder purchaseDataBuilder;
	@Mock
	private VehicleLookupService vehicleLookupService;
	@Mock
	private PromotionService promotionService;
	@Mock
	private Localise localise;
	@Mock
	private AffiliateSubscriptionSettings settings;

	private static final LocalDate TEST_DATE = LocalDate.of(2020, 10, 20);
	
	@Test
	void testBuildProductList()
	{
		when(factory.getInstance(eq(BuilderType.PRODUCT_DISPLAY_ITEM), eq(SubscriptionProductDisplayItemBuilder.class)))
				.thenReturn(subscriptionProductBuilder);
		when(subscriptionProductBuilder.build(any())).thenReturn(mock(SubscriptionProductDisplayItemList.class));
		assertThat(service.buildProductDisplayItemList(mock(SubscriptionBookingQuery.class))).isNotNull()
				.isInstanceOf(SubscriptionProductDisplayItemList.class);
	}

	@Test
	void testGenerateReference()
	{
		when(factory.getInstance(eq(BuilderType.REFERENCE), eq(SubscriptionReferenceGenerator.class))).thenReturn(referenceGenerator);
		when(referenceGenerator.generate(any())).thenReturn("Reference");
		assertThat(service.generateReference(mock(Affiliates.class))).isNotNull()
				.isInstanceOf(String.class);
	}

	@Test
	void testValidateBookingTimes()
	{
		when(validator.validate(any())).thenReturn(true);
		assertThat(service.validateBookingTimes(mock(SubscriptionBookingQuery.class))).isTrue();
	}

	@Test
	void testBuildBasket()
	{
		when(factory.getInstance(eq(BuilderType.BASKET), eq(SubscriptionBasketBuilder.class))).thenReturn(basketBuilder);
		when(jsonUtil.jsonToPurchaseQuery(any())).thenReturn(EnhancedRandom.randomListOf(1, PurchaseQuery.class));
		when(basketBuilder.build(any(), anyInt(), anyInt(), any(), any())).thenReturn(mock(Basket.class));
		assertThat(service.buildBasket("purchase", 1, 1, affiliate, mock(AffiliateSubscriptionSettings.class))).isNotNull();
		verify(jsonUtil).jsonToPurchaseQuery(anyString());
		verify(basketBuilder).build(anyList(), anyInt(), anyInt(), any(), any());
		verifyNoMoreInteractions(jsonUtil, basketBuilder);
	}

	@Test
	void testGetBasket()
	{
		when(factory.getInstance(eq(BuilderType.BASKET), eq(SubscriptionBasketBuilder.class))).thenReturn(basketBuilder);
		SubscriptionPurchaseData purchaseData = mock(SubscriptionPurchaseData.class);
		when(bookingService.fetchPurchaseData(anyInt(), anyString())).thenReturn(purchaseData);
		when(jsonUtil.jsonToPurchaseQuery(any())).thenReturn(EnhancedRandom.randomListOf(1, PurchaseQuery.class));
		when(basketBuilder.build(any(), anyInt(), anyInt(), any(), any())).thenReturn(mock(Basket.class));
		when(purchaseData.getPurchaseData()).thenReturn("purchaseTest");
		assertThat(service.getBasket("purchase", 1, 1, affiliate, mock(AffiliateSubscriptionSettings.class))).isNotNull();
		verify(jsonUtil).jsonToPurchaseQuery(anyString());
		verify(basketBuilder).build(any(), anyInt(), anyInt(), any(), any());
		verify(bookingService).fetchPurchaseData(anyInt(), anyString());
		verifyNoMoreInteractions(jsonUtil, basketBuilder);
	}

	@Test
	void testGetBasket_SubscriptionPurchaseData()
	{
		when(bookingService.fetchPurchaseData(anyInt(), anyString())).thenReturn(null);
		assertThat(service.getBasket("purchase", 1, 1, affiliate, mock(AffiliateSubscriptionSettings.class))).isNull();
		verify(jsonUtil, times(0)).jsonToPurchaseQuery(anyString());
		verify(basketBuilder, times(0)).build(any(), anyInt(), anyInt(), any(), any());
		verify(bookingService).fetchPurchaseData(anyInt(), anyString());
		verifyNoMoreInteractions(jsonUtil, basketBuilder);
	}

	@Test
	public void testInsertPurchaseData()
	{
		assertThat(service.insertPurchaseData(1, "customerGuid", "purchaseData")).isNotEmpty();
		verify(bookingService).insertPurchaseData(anyInt(), anyString(), anyString());
		verifyNoMoreInteractions(bookingService);
	}

	@Test
	public void testFetchPurchaseData()
	{
		when(bookingService.fetchPurchaseData(anyInt(), anyString())).thenReturn(mock(SubscriptionPurchaseData.class));
		assertThat(service.fetchPurchaseData(1, "purchaseData")).isNotNull();
		verify(bookingService).fetchPurchaseData(anyInt(), anyString());
		verifyNoMoreInteractions(bookingService);
	}

	@Test
	public void testInsertPurchaseData_Empty_CustomerGuid()
	{
		assertThat(service.insertPurchaseData(1, "", "purchaseData")).isNotEmpty();
		verify(bookingService).insertPurchaseData(anyInt(), anyString(), anyString());
		verifyNoMoreInteractions(bookingService);
	}

	@Test
	public void testGetBooking()
	{
		when(bookingService.fetchSubscriptionBooking(anyString(), anyInt())).thenReturn(mock(SubscriptionBookingRecord.class));
		assertThat(service.getBooking(1, "confirmationGuid")).isNotNull();
		verify(bookingService).fetchSubscriptionBooking(anyString(), anyInt());
		verifyNoMoreInteractions(bookingService);
	}

	@Test
	public void testSetUpDate_Day()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getLeadTime()).thenReturn(2);
		when(affiliateSubscriptionSettings.getLeadTimeType()).thenReturn("DAY");
		assertThat(service.setUpDate(affiliateSubscriptionSettings, "Europe/London")).isNotNull()
				.isEqualTo(DateUtil.localDateToString(DateUtil.nowLocalDate("Europe/London")
						.plusDays(2), "MM/dd/yyyy"));
	}

	@Test
	public void testSetUpDate_Day_FixedStartDay()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(1);
		when(affiliateSubscriptionSettings.getLeadTime()).thenReturn(2);
		when(affiliateSubscriptionSettings.getLeadTimeType()).thenReturn("DAY");
		assertThat(service.setUpDate(affiliateSubscriptionSettings, "Europe/London")).isNotNull()
				.contains("01/");
	}

	@Test
	public void testSetUpDate_Month()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getLeadTime()).thenReturn(1);
		when(affiliateSubscriptionSettings.getLeadTimeType()).thenReturn("MONTH");
		assertThat(service.setUpDate(affiliateSubscriptionSettings, "Europe/London")).isNotNull()
				.isEqualTo(DateUtil.localDateToString(DateUtil.nowLocalDate("Europe/London")
						.plusMonths(1), "MM/dd/yyyy"));
	}

	@Test
	public void testSetUpDate_AffiliateSubscriptionSettings_Null()
	{
		assertThat(service.setUpDate(null, "Europe/London")).isNotNull()
				.isEqualTo(DateUtil.localDateToString(DateUtil.nowLocalDate("Europe/London"), "MM/dd/yyyy"));
	}

	@Test
	public void testGetCountiesIfRequired()
	{
		assertThat(service.getCountiesIfRequired(true, "shannon")).isNotEmpty()
				.containsValue("Antrim");
	}

	@Test
	public void testGetCountiesIfRequired_ShowCounties_False()
	{
		assertThat(service.getCountiesIfRequired(false, "shannon")).isEmpty();
	}

	@Test
	public void testGetCountiesIfRequired_Unknown_SiteTitle()
	{
		assertThat(service.getCountiesIfRequired(true, "unknown")).isEmpty();
	}
	
	@Test
	public void testGetCountriesMap()
	{
		LanguageFieldsList languageFieldsList = mock(LanguageFieldsList.class);
		String siteTitle = "TestSite";
		boolean sortValues = true;
		Locale[] locales = new Locale[] {new Locale("English", "UK")};
		when(localeFactory.getAvailableLocales()).thenReturn(locales);
		when(languageFieldsList.getTranslation(anyString())).thenReturn("UK");
		assertThat(service.getCountriesMap(siteTitle, languageFieldsList, sortValues));
		verify(languageFieldsList).getTranslation(anyString());
		verify(localeFactory).getAvailableLocales();
	}
	
	@Test
	public void testGetCountriesMap_WhenSortValuesIsFalse()
	{
		LanguageFieldsList languageFieldsList = mock(LanguageFieldsList.class);
		String siteTitle = "TestSite";
		boolean sortValues = false;
		Locale[] locales = new Locale[] {new Locale("English", "UK")};
		when(localeFactory.getAvailableLocales()).thenReturn(locales);
		when(languageFieldsList.getTranslation(anyString())).thenReturn("UK");
		assertThat(service.getCountriesMap(siteTitle, languageFieldsList, sortValues));
		verify(languageFieldsList).getTranslation(anyString());
		verify(localeFactory).getAvailableLocales();
	}
	
	@Test
	public void testGetCountriesMap_WhenCountryIsEmpty()
	{
		LanguageFieldsList languageFieldsList = mock(LanguageFieldsList.class);
		String siteTitle = "TestSite";
		boolean sortValues = false;
		Locale[] locales = new Locale[] {new Locale("English", "")};
		when(localeFactory.getAvailableLocales()).thenReturn(locales);
		assertThat(service.getCountriesMap(siteTitle, languageFieldsList, sortValues));
		verify(localeFactory).getAvailableLocales();
	}
	
	@Test
	public void testGetCountriesMap_WhenLanguageFieldsListIsEmpty()
	{
		LanguageFieldsList languageFieldsList = null;
		String siteTitle = "Dortmund";
		boolean sortValues = true;
		Locale[] locales = new Locale[] {new Locale("English", "UK")};
		when(localeFactory.getAvailableLocales()).thenReturn(locales);
		assertThat(service.getCountriesMap(siteTitle, languageFieldsList, sortValues));
		verify(localeFactory).getAvailableLocales();
	}
	
	@Test
	public void testGetCountriesMap_WhenSiteTitleIsNull()
	{
		LanguageFieldsList languageFieldsList = null;
		String siteTitle = null;
		boolean sortValues = true;
		Locale[] locales = new Locale[] {new Locale("English", "UK")};
		when(localeFactory.getAvailableLocales()).thenReturn(locales);
		assertThat(service.getCountriesMap(siteTitle, languageFieldsList, sortValues));
		verify(localeFactory).getAvailableLocales();
	}

	@Test
	public void testSaveReservationData()
	{
		when(bookingService.saveReservationData(any())).thenReturn(true);

		assertTrue(service.saveReservationData(reservationData));
	}

	@Test
	public void testSaveReservationData_False()
	{
		when(bookingService.saveReservationData(any())).thenReturn(false);

		assertFalse(service.saveReservationData(reservationData));
	}

	@Test
	public void testFetchReservationDataByGuid()
	{
		when(bookingService.fetchReservationDataByGuid(anyString())).thenReturn(reservationData);

		assertNotNull(service.fetchReservationDataByGuid("guid"));
	}

	@Test
	public void testFetchReservationDataByGuid_Null()
	{
		when(bookingService.fetchReservationDataByGuid(anyString())).thenReturn(null);

		assertNull(service.fetchReservationDataByGuid("guid"));
	}

	@Test
	public void testFetchSubscriptionDiscountedRenewalProductId()
	{
		when(bookingService.fetchSubscriptionDiscountedRenewalProductId(anyInt()))
				.thenReturn(mock(SubscriptionDiscountedRenewal.class));

		assertNotNull(service.fetchSubscriptionDiscountedRenewalProductId(123));
	}

	@Test
	public void testFetchSubscriptionDiscountedRenewalProductId_Null()
	{
		when(bookingService.fetchSubscriptionDiscountedRenewalProductId(anyInt())).thenReturn(null);

		assertNull(service.fetchSubscriptionDiscountedRenewalProductId(123));
	}

	public void testIsFreemarkerEnabled()
	{
		when(bookingService.fetchFeatureFlagValueByKey(anyString())).thenReturn("1,2,3");

		assertTrue(service.isFreemarkerEnabled(1));
	}

	@Test
	public void testIsFreemarkerEnabled_False()
	{
		when(bookingService.fetchFeatureFlagValueByKey(anyString())).thenReturn("1,2,3");

		assertFalse(service.isFreemarkerEnabled(5));
	}

	public void testIsPartialPaymentsEnabled()
	{
		when(bookingService.fetchFeatureFlagValueByKey(anyString())).thenReturn("1,2,3");

		assertTrue(service.isPartialPaymentsEnabled(1));
	}

	@Test
	public void testIsPartialPaymentsEnabled_False()
	{
		when(bookingService.fetchFeatureFlagValueByKey(anyString())).thenReturn("1,2,3");

		assertFalse(service.isPartialPaymentsEnabled(5));
	}

	@Test
	public void fetchPaymentStepFieldsSubscriptionBySiteId()
	{
		List<PaymentStepFieldsSubscriptions> list = new ArrayList<>();
		PaymentStepFieldsSubscriptions paymentStepFieldsSubscriptions = mock(PaymentStepFieldsSubscriptions.class);
		list.add(paymentStepFieldsSubscriptions);

		when(siteService.fetchPaymentStepFieldsSubscriptionBySiteId(anyInt())).thenReturn(list);

		assertEquals(1, service.fetchPaymentStepFieldsSubscriptionBySiteId(1)
				.size());
	}

	@Test
	public void testDoesCompanyRecordExist()
	{
		when(invoiceService.fetchCompanyBySiteId(anyInt(), anyInt())).thenReturn(mock(Company.class));

		assertTrue(service.doesCompanyRecordExist(1, 1));
	}

	@Test
	public void testDoesCompanyRecordExist_Null()
	{
		assertFalse(service.doesCompanyRecordExist(0, 0));
	}
	
	@Test
	public void testValidateProductIsAvailable_ProductIsAvailable()
	{
		SubscriptionProductAvailabilityDetailsList availableProducts = new SubscriptionProductAvailabilityDetailsList();
		SubscriptionProductAvailabilityDetails availableProductDetails = mock(SubscriptionProductAvailabilityDetails.class);
		SubscriptionProduct availableProduct = mock(SubscriptionProduct.class);
		availableProducts.add(availableProductDetails);
		
		when(availableProductDetails.getProduct()).thenReturn(availableProduct);
		when(availableProduct.getId()).thenReturn(1);
		when(productFinder.getAllAvailableProducts(anyInt(), anyInt(), anyInt(), anyInt(), any(LocalDate.class)))
				.thenReturn(availableProducts);
		
		assertTrue(service.validateProductIsAvailable(1, TEST_DATE, 1, 1, 1, 1));
		
		verify(productFinder).getAllAvailableProducts(anyInt(), anyInt(), anyInt(), anyInt(), any(LocalDate.class));
	}
	
	@Test
	public void testValidateProductIsAvailable_ProductNotAvailable()
	{
		when(productFinder.getAllAvailableProducts(anyInt(), anyInt(), anyInt(), anyInt(), any(LocalDate.class)))
				.thenReturn(new SubscriptionProductAvailabilityDetailsList());
		
		assertFalse(service.validateProductIsAvailable(1, TEST_DATE, 1, 1, 1, 1));
		
		verify(productFinder).getAllAvailableProducts(anyInt(), anyInt(), anyInt(), anyInt(), any(LocalDate.class));
	}
	
	@Test
	public void testBuildPurchaseDataFromThirdParty()
	{
		when(factory.getInstance(BuilderType.PURCHASE_DATA, PurchaseDataBuilder.class)).thenReturn(purchaseDataBuilder);
		JsonArray expectedJsonArray = new JsonArray();
		
		when(purchaseDataBuilder.buildPurchaseDataJson(anyInt(), any(LocalDate.class), anyString(), anyBoolean()))
				.thenReturn(expectedJsonArray);
		
		assertEquals(expectedJsonArray.toString(), service.buildPurchaseDataFromThirdParty(1, TEST_DATE, "dd/MM/yyyy"));
		
		verify(purchaseDataBuilder).buildPurchaseDataJson(anyInt(), any(LocalDate.class), anyString(), anyBoolean());
	}

	public void testIsSubscriptionRenewalEnabled()
	{
		when(bookingService.fetchFeatureFlagValueByKey(anyString())).thenReturn("1,2,3");

		assertTrue(service.isSubscriptionRenewalEnabled(1));
	}

	@Test
	public void testIsSubscriptionRenewalEnabled_False()
	{
		when(bookingService.fetchFeatureFlagValueByKey(anyString())).thenReturn("1,2,3");

		assertFalse(service.isSubscriptionRenewalEnabled(5));
	}

	@Test
	public void testIsSubscriptionRenewalEnabled_EmptyString()
	{
		when(bookingService.fetchFeatureFlagValueByKey(anyString())).thenReturn("");

		assertFalse(service.isSubscriptionRenewalEnabled(5));
	}

	@Test
	public void testIsSubscriptionRenewalEnabled_NullString()
	{
		assertFalse(service.isSubscriptionRenewalEnabled(5));
	}

	@Test
	public void testGetVehicleLookupLogin()
	{
		when(vehicleLookupService.fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(anyInt()))
				.thenReturn(mock(VehiclelookupAffiliateLogins.class));

		assertNotNull(vehicleLookupService.fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(1));

		verify(vehicleLookupService).fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(anyInt());
	}

	@Test
	public void testGetBasket_WithPromotion()
	{
		String customerGuid = "guid-123";
		int affiliateId = 1;
		int currentLanguageId = 1;
		int defaultLanguageId = 1;
		Affiliates affiliate = mock(Affiliates.class);
		when(affiliate.getId()).thenReturn(affiliateId);

		AffiliateSubscriptionSettings settings = mock(AffiliateSubscriptionSettings.class);

		// Setup purchase data
		SubscriptionPurchaseData purchaseData = new SubscriptionPurchaseData();
		purchaseData.setPurchaseData("{\"productId\":123}");

		// Setup session promotion
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid(customerGuid);
		sessionPromotion.setPromoCode("SAVE10");
		sessionPromotion.setDiscountAmount(new BigDecimal("10.00"));
		sessionPromotion.setValid(true);

		// Setup basket
		Basket basket = mock(Basket.class);
		PurchaseRequestList purchaseList = new PurchaseRequestList();
		when(basket.getPurchaseRequestList()).thenReturn(purchaseList);

		when(bookingService.fetchPurchaseData(affiliateId, customerGuid)).thenReturn(purchaseData);
		when(factory.getInstance(eq(BuilderType.BASKET), eq(SubscriptionBasketBuilder.class)))
				.thenReturn(basketBuilder);
		when(basketBuilder.build(anyList(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(promotionService.fetchSessionPromotionByGuid(customerGuid)).thenReturn(sessionPromotion);

		Basket result = service.getBasket(customerGuid, currentLanguageId, defaultLanguageId, affiliate, settings);

		assertNotNull(result);
		verify(promotionService).fetchSessionPromotionByGuid(customerGuid);
		verify(basket).setPromoDiscount(new BigDecimal("10.00"));
		verify(basket).setPromoCode("SAVE10");
		verify(basket).initialize(localise);
	}

	@Test
	public void testGetBasket_WithoutPromotion()
	{
		String customerGuid = "guid-456";
		int affiliateId = 1;
		int currentLanguageId = 1;
		int defaultLanguageId = 1;
		Affiliates affiliate = mock(Affiliates.class);
		when(affiliate.getId()).thenReturn(affiliateId);

		AffiliateSubscriptionSettings settings = mock(AffiliateSubscriptionSettings.class);

		SubscriptionPurchaseData purchaseData = new SubscriptionPurchaseData();
		purchaseData.setPurchaseData("{\"productId\":123}");

		Basket basket = mock(Basket.class);

		when(bookingService.fetchPurchaseData(affiliateId, customerGuid)).thenReturn(purchaseData);
		when(factory.getInstance(eq(BuilderType.BASKET), eq(SubscriptionBasketBuilder.class)))
				.thenReturn(basketBuilder);
		when(basketBuilder.build(anyList(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(promotionService.fetchSessionPromotionByGuid(customerGuid)).thenReturn(null);

		Basket result = service.getBasket(customerGuid, currentLanguageId, defaultLanguageId, affiliate, settings);

		assertNotNull(result);
		verify(promotionService).fetchSessionPromotionByGuid(customerGuid);
		verify(basket, times(0)).setPromoDiscount(any());
		verify(basket, times(0)).setPromoCode(anyString());
	}

	@Test
	public void testGetBasket_WithInvalidPromotion()
	{
		String customerGuid = "guid-789";
		int affiliateId = 1;
		int currentLanguageId = 1;
		int defaultLanguageId = 1;
		Affiliates affiliate = mock(Affiliates.class);
		when(affiliate.getId()).thenReturn(affiliateId);

		AffiliateSubscriptionSettings settings = mock(AffiliateSubscriptionSettings.class);

		SubscriptionPurchaseData purchaseData = new SubscriptionPurchaseData();
		purchaseData.setPurchaseData("{\"productId\":123}");

		// Setup invalid session promotion (null discount amount)
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid(customerGuid);
		sessionPromotion.setPromoCode("INVALID");
		sessionPromotion.setDiscountAmount(null);

		Basket basket = mock(Basket.class);

		when(bookingService.fetchPurchaseData(affiliateId, customerGuid)).thenReturn(purchaseData);
		when(factory.getInstance(eq(BuilderType.BASKET), eq(SubscriptionBasketBuilder.class)))
				.thenReturn(basketBuilder);
		when(basketBuilder.build(anyList(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(promotionService.fetchSessionPromotionByGuid(customerGuid)).thenReturn(sessionPromotion);

		Basket result = service.getBasket(customerGuid, currentLanguageId, defaultLanguageId, affiliate, settings);

		assertNotNull(result);
		verify(promotionService).fetchSessionPromotionByGuid(customerGuid);
		// Should not apply promo when discount amount is null
		verify(basket, times(0)).setPromoDiscount(any());
		verify(basket, times(0)).setPromoCode(anyString());
	}

	@Test
	public void testGetBasket_NullPurchaseData()
	{
		String customerGuid = "guid-null";
		int affiliateId = 1;
		int currentLanguageId = 1;
		int defaultLanguageId = 1;
		Affiliates affiliate = mock(Affiliates.class);
		when(affiliate.getId()).thenReturn(affiliateId);

		AffiliateSubscriptionSettings settings = mock(AffiliateSubscriptionSettings.class);

		when(bookingService.fetchPurchaseData(affiliateId, customerGuid)).thenReturn(null);

		Basket result = service.getBasket(customerGuid, currentLanguageId, defaultLanguageId, affiliate, settings);

		assertNull(result);
		verify(bookingService).fetchPurchaseData(affiliateId, customerGuid);
		verify(promotionService, times(0)).fetchSessionPromotionByGuid(anyString());
	}

	@Test
	public void testGetBasket_EmptyCustomerGuid()
	{
		String customerGuid = "";
		int affiliateId = 1;
		int currentLanguageId = 1;
		int defaultLanguageId = 1;
		Affiliates affiliate = mock(Affiliates.class);
		when(affiliate.getId()).thenReturn(affiliateId);

		AffiliateSubscriptionSettings settings = mock(AffiliateSubscriptionSettings.class);

		SubscriptionPurchaseData purchaseData = new SubscriptionPurchaseData();
		purchaseData.setPurchaseData("{\"productId\":123}");

		Basket basket = mock(Basket.class);

		when(bookingService.fetchPurchaseData(affiliateId, customerGuid)).thenReturn(purchaseData);
		when(factory.getInstance(eq(BuilderType.BASKET), eq(SubscriptionBasketBuilder.class)))
				.thenReturn(basketBuilder);
		when(basketBuilder.build(anyList(), anyInt(), anyInt(), any(), any())).thenReturn(basket);

		Basket result = service.getBasket(customerGuid, currentLanguageId, defaultLanguageId, affiliate, settings);

		assertNotNull(result);
		// Should not check for promotion with empty GUID
		verify(promotionService, times(0)).fetchSessionPromotionByGuid(anyString());
	}

	@Test
	public void testShouldSkipProductSelection_ReturnsTrueWhenEnabled()
	{
		when(settings.getSkipProductSelectionIfOneIsAvailable()).thenReturn(true);

		assertTrue(service.shouldSkipProductSelection(settings));
	}

	@Test
	public void testShouldSkipProductSelection_ReturnsFalseWhenDisabled()
	{
		when(settings.getSkipProductSelectionIfOneIsAvailable()).thenReturn(false);

		assertFalse(service.shouldSkipProductSelection(settings));
		assertFalse(service.shouldSkipProductSelection(null));
	}

	@Test
	public void testShouldSkipProductSelectionOnDatesPage_ReturnsTrueWhenOneProductEnabled()
	{
		when(settings.getSkipProductSelectionIfOneIsAvailable()).thenReturn(true);

		SubscriptionProductAvailabilityDetailsList availableProducts = new SubscriptionProductAvailabilityDetailsList();
		availableProducts.add(mock(SubscriptionProductAvailabilityDetails.class));

		when(productFinder.getAllAvailableProducts(anyInt(), anyInt(), anyInt(), anyInt(), any(LocalDate.class)))
				.thenReturn(availableProducts);

		assertTrue(service.shouldSkipProductSelectionOnDatesPage(settings, 1, 1, 1, 1));
	}

	@Test
	public void testShouldSkipProductSelectionOnDatesPage_ReturnsFalseWhenMultipleProducts()
	{
		when(settings.getSkipProductSelectionIfOneIsAvailable()).thenReturn(true);

		SubscriptionProductAvailabilityDetailsList availableProducts = new SubscriptionProductAvailabilityDetailsList();
		availableProducts.add(mock(SubscriptionProductAvailabilityDetails.class));
		availableProducts.add(mock(SubscriptionProductAvailabilityDetails.class));

		when(productFinder.getAllAvailableProducts(anyInt(), anyInt(), anyInt(), anyInt(), any(LocalDate.class)))
				.thenReturn(availableProducts);

		assertFalse(service.shouldSkipProductSelectionOnDatesPage(settings, 1, 1, 1, 1));
	}
	
	@Test
	void testFormatRightToCancelText_Success()
	{
		AffiliateSubscription affSub = new AffiliateSubscription();
		affSub.setRightToCancel("&lt;p&gt;I authorise to debit {{transaction-value}}.&lt;/p&gt;");
		BigDecimal amount = new BigDecimal("100.00");
		when(localise.price(100.00f, true)).thenReturn("€100.00");

		String result = service.formatRightToCancelText(affSub, amount);

		assertThat(result).isEqualTo("I authorise to debit €100.00.");
	}

	@Test
	void testFormatRightToCancelText_NullInputs_ReturnsNull()
	{
		assertNull(service.formatRightToCancelText(null, new BigDecimal("100")));
		assertNull(service.formatRightToCancelText(new AffiliateSubscription(), null));
	}

	@Test
	void testFormatRightToCancelText_EmptyText_ReturnsNull()
	{
		AffiliateSubscription affSub = new AffiliateSubscription();
		affSub.setRightToCancel("");

		assertNull(service.formatRightToCancelText(affSub, new BigDecimal("100")));
	}
}