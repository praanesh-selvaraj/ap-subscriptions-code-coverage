package com.kmp.aeroparker.application.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.presentation.SubscriptionProductDisplayItem;
import com.kmp.aeroparker.application.presentation.SubscriptionProductDisplayItemList;
import com.kmp.aeroparker.application.quotas.SubscriptionQuotaChecker;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.l10n.location.Location;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest
{
	@Mock
	private Localise localise;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private SubscriptionControllerService service;
	@Mock
	private SubscriptionQuotaChecker quotaChecker;
	@InjectMocks
	private ProductController controller;
	@Mock
	private Model model;
	@Mock
	private Locations location;
	@Mock
	private Location localisedLocation;
	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	private AffiliateSubscriptionSettings affiliateSubscriptionSettings;
	@Mock
	private SubscriptionProductDisplayItemList productDisplayItemList;
	@Mock
	private Iterator<SubscriptionProductDisplayItem> displayIterator;
	@Mock
	private LanguageFieldsList languageFieldsList;

	@Test
	void testSelectProducts()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(3);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(true);
		bookingQuery.setStartDate("03/10/2019");
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		when(productDisplayItemList.iterator()).thenReturn(displayIterator);
		when(requestBean.getDateFormat()).thenReturn("dd/MM/yyyy");
		when(requestBean.isMultiPurchase()).thenReturn(true);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		controller.selectProducts(model, redirectAttributes, bookingQuery, "", "");
		verify(localise, times(2)).priceIncludePennyPlaceholders(anyFloat(), anyBoolean());
		verify(model, times(16)).addAttribute(anyString(), any());
		verify(service).buildProductDisplayItemList(any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_AffiliateSubscriptionSettings_Null()
	{
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(null);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(true);
		bookingQuery.setStartDate("03/10/2019");
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		when(productDisplayItemList.iterator()).thenReturn(displayIterator);
		when(requestBean.getDateFormat()).thenReturn("dd/MM/yyyy");
		when(requestBean.isMultiPurchase()).thenReturn(true);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		controller.selectProducts(model, redirectAttributes, bookingQuery, "", "");
		verify(localise, times(2)).priceIncludePennyPlaceholders(anyFloat(), anyBoolean());
		verify(model, times(16)).addAttribute(anyString(), any());
		verify(service).buildProductDisplayItemList(any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_Invalid_Booking_Query()
	{
		when(requestBean.getLocation()).thenReturn(location);
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(false);
		bookingQuery.setStartDate("03/10/2019");
		controller.selectProducts(model, redirectAttributes, bookingQuery, "10/30/2019", "");
		verify(model, times(0)).addAttribute(anyString(), any());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testSelectProducts_Invalid_Booking_Query_Empty_Start_Date()
	{
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(false);
		bookingQuery.setStartDate("03/10/2019");
		controller.selectProducts(model, redirectAttributes, bookingQuery, "", "");
		verify(model, times(0)).addAttribute(anyString(), any());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testSelectProducts_Valid_Booking_Query_Not_Empty_Start_Date()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(3);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		when(requestBean.getLocation()).thenReturn(location);
		when(requestBean.getDateFormat()).thenReturn("MM/dd/yyyy");
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(false);
		bookingQuery.setStartDate("03/10/2019");
		when(service.validateBookingTimes(any())).thenReturn(true);
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		when(productDisplayItemList.iterator()).thenReturn(displayIterator);
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(requestBean.isMultiPurchase()).thenReturn(true);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		controller.selectProducts(model, redirectAttributes, bookingQuery, "10/30/2019", "");
		verify(localise, times(2)).priceIncludePennyPlaceholders(anyFloat(), anyBoolean());
		verify(model, times(16)).addAttribute(anyString(), any());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_Back_To_Select_Products()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(3);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getLocation()).thenReturn(location);
		when(requestBean.getDateFormat()).thenReturn("MM/dd/yyyy");
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		Basket basket = mock(Basket.class);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(false);
		bookingQuery.setStartDate("03/10/2019");
		when(service.validateBookingTimes(any())).thenReturn(true);
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		when(productDisplayItemList.iterator()).thenReturn(displayIterator);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(requestBean.isMultiPurchase()).thenReturn(true);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(basket.getAllProductIds()).thenReturn(EnhancedRandom.randomListOf(1, Integer.class));
		controller.selectProducts(model, redirectAttributes, bookingQuery, "10/30/2019", "customerGuid");
		verify(model, times(18)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_Back_To_Select_Products_Not_MultiPurchase()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(3);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getLocation()).thenReturn(location);
		when(requestBean.getDateFormat()).thenReturn("MM/dd/yyyy");
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(false);
		bookingQuery.setStartDate("03/10/2019");
		when(service.validateBookingTimes(any())).thenReturn(true);
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		when(productDisplayItemList.iterator()).thenReturn(displayIterator);
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(requestBean.isMultiPurchase()).thenReturn(false);
		controller.selectProducts(model, redirectAttributes, bookingQuery, "10/30/2019", "customerGuid");
		verify(model, times(16)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_Back_To_Select_Products_Booking_Null()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(3);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		when(requestBean.getLocation()).thenReturn(location);
		when(requestBean.getDateFormat()).thenReturn("MM/dd/yyyy");
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(false);
		bookingQuery.setStartDate("03/10/2019");
		when(service.validateBookingTimes(any())).thenReturn(true);
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		when(productDisplayItemList.iterator()).thenReturn(displayIterator);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(null);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(requestBean.isMultiPurchase()).thenReturn(true);
		controller.selectProducts(model, redirectAttributes, bookingQuery, "10/30/2019", "customerGuid");
		verify(localise, times(2)).priceIncludePennyPlaceholders(anyFloat(), anyBoolean());
		verify(model, times(16)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_Back_To_Select_Products_Total_Zero()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(3);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getLocation()).thenReturn(location);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		Basket basket = mock(Basket.class);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.ZERO);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(false);
		bookingQuery.setStartDate("03/10/2019");
		when(service.validateBookingTimes(any())).thenReturn(true);
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		when(productDisplayItemList.iterator()).thenReturn(displayIterator);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(requestBean.isMultiPurchase()).thenReturn(true);
		controller.selectProducts(model, redirectAttributes, bookingQuery, "10/30/2019", "customerGuid");
		verify(model, times(18)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_Booking_Query_Null()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(3);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getLocation()).thenReturn(location);
		when(service.validateBookingTimes(any())).thenReturn(true);
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		when(productDisplayItemList.iterator()).thenReturn(displayIterator);
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		controller.selectProducts(model, redirectAttributes, null, "10/30/2019", "");
		verify(model, times(16)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testRedirectToDetails()
	{
		when(service.insertPurchaseData(anyInt(), anyString(), anyString())).thenReturn("customerGuid");
		controller.redirectToDetails(redirectAttributes, "purchaseData", "", "");
		verify(redirectAttributes, times(2)).addAttribute(anyString(), any());
		verify(service).insertPurchaseData(anyInt(), anyString(), anyString());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testRedirectToDetails_Purchase_Data_Empty()
	{
		controller.redirectToDetails(redirectAttributes, "", "", "");
		verify(redirectAttributes, times(2)).addAttribute(anyString(), any());
		verify(service).insertPurchaseData(anyInt(), anyString(), anyString());
		verifyNoMoreInteractions(model);
	}
	
	@Test
	void testRedirectToDates_ProductDisplayItemList_Empty()
	{
		SubscriptionProductDisplayItemList productDisplayItemList = new SubscriptionProductDisplayItemList();
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		SubscriptionBookingQuery bookingQuery = mock(SubscriptionBookingQuery.class);
		when(bookingQuery.isValid()).thenReturn(true);

		assertThat(controller.selectProducts(model, redirectAttributes, bookingQuery, "", "")).isEqualTo("redirect:dates");
		verify(redirectAttributes).addAttribute(anyString(), anyBoolean());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testSelectProducts_SkipStepTwo_RedirectToStepOne()
	{
		SubscriptionProductDisplayItemList displayItemList = new SubscriptionProductDisplayItemList();
		SubscriptionProductDisplayItem displayItem = mock(SubscriptionProductDisplayItem.class);

		displayItemList.add(displayItem);

		when(service.validateBookingTimes(any())).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		when(service.buildProductDisplayItemList(any())).thenReturn(displayItemList);
		when(localise.getLocation()).thenReturn(localisedLocation);
		when(localisedLocation.getDateformat()).thenReturn("dd-MM-yyyy");
		when(quotaChecker.checkProductOccupancy(anyInt(), any(), anyInt())).thenReturn(true);
		when(requestBean.isMultiPurchase()).thenReturn(true);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(affiliateSubscriptionSettings.getSkipProductSelectionIfOneIsAvailable()).thenReturn(true);

		assertEquals("redirect:dates",
				controller.selectProducts(model, redirectAttributes, null, "10/30/2019", ""));

		verify(localise, times(2)).priceIncludePennyPlaceholders(anyFloat(), anyBoolean());
		verify(model, never()).addAttribute(anyString(), any());
		verify(service).buildProductDisplayItemList(any());
		verify(service, never()).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_SkipStepTwo_RedirectToStepThree()
	{
		SubscriptionProductDisplayItemList displayItemList = new SubscriptionProductDisplayItemList();
		SubscriptionProductDisplayItem displayItem = mock(SubscriptionProductDisplayItem.class);
		SubscriptionBookingQuery bookingQuery = mock(SubscriptionBookingQuery.class);

		displayItemList.add(displayItem);

		when(bookingQuery.isValid()).thenReturn(true);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		when(service.buildProductDisplayItemList(any())).thenReturn(displayItemList);
		when(requestBean.isMultiPurchase()).thenReturn(true);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(affiliateSubscriptionSettings.getSkipProductSelectionIfOneIsAvailable()).thenReturn(true);
		when(localise.getLocation()).thenReturn(localisedLocation);
		when(localisedLocation.getDateformat()).thenReturn("dd-MM-yyyy");
		when(quotaChecker.checkProductOccupancy(anyInt(), any(), anyInt())).thenReturn(true);

		assertEquals("redirect:details",
				controller.selectProducts(model, redirectAttributes, bookingQuery, "10/30/2019", ""));

		verify(localise, times(2)).priceIncludePennyPlaceholders(anyFloat(), anyBoolean());
		verify(model, never()).addAttribute(anyString(), any());
		verify(service).buildProductDisplayItemList(any());
		verify(service, never()).setUpDate(any(), anyString());
		verify(service).insertPurchaseData(anyInt(), anyString(), anyString());
		verify(redirectAttributes, times(2)).addAttribute(anyString(), any());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_NullBookingQuery_ValidBookingTimes()
	{
		SubscriptionProductDisplayItemList displayItemList = new SubscriptionProductDisplayItemList();
		SubscriptionProductDisplayItem displayItem = mock(SubscriptionProductDisplayItem.class);

		displayItemList.add(displayItem);

		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(service.validateBookingTimes(any())).thenReturn(true);
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		when(service.buildProductDisplayItemList(any())).thenReturn(displayItemList);
		when(requestBean.isMultiPurchase()).thenReturn(true);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(localise.getLocation()).thenReturn(localisedLocation);
		when(localisedLocation.getDateformat()).thenReturn("dd-MM-yyyy");
		when(quotaChecker.checkProductOccupancy(anyInt(), any(), anyInt())).thenReturn(true);

		assertEquals("subscription-select-multiple-products",
				controller.selectProducts(model, redirectAttributes, null, "10/30/2019", ""));

		verify(localise, times(2)).priceIncludePennyPlaceholders(anyFloat(), anyBoolean());
		verify(model, times(16)).addAttribute(anyString(), any());
		verify(service).buildProductDisplayItemList(any());
		verify(service).setUpDate(any(), anyString());
		verify(service, never()).insertPurchaseData(anyInt(), anyString(), anyString());
		verify(redirectAttributes, never()).addAttribute(anyString(), any());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testSelectProducts_SingleProductSkip_StepTwoDisabled()
	{
		SubscriptionProductDisplayItemList displayItemList = new SubscriptionProductDisplayItemList();
		SubscriptionProductDisplayItem displayItem = mock(SubscriptionProductDisplayItem.class);
		SubscriptionBookingQuery bookingQuery = mock(SubscriptionBookingQuery.class);

		displayItemList.add(displayItem);

		when(bookingQuery.isValid()).thenReturn(true);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		when(service.buildProductDisplayItemList(any())).thenReturn(displayItemList);
		when(requestBean.isMultiPurchase()).thenReturn(true);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(localise.getLocation()).thenReturn(localisedLocation);
		when(localisedLocation.getDateformat()).thenReturn("dd-MM-yyyy");
		when(quotaChecker.checkProductOccupancy(anyInt(), any(), anyInt())).thenReturn(true);

		assertEquals("subscription-select-multiple-products",
				controller.selectProducts(model, redirectAttributes, bookingQuery, "10/30/2019", ""));

		verify(localise, times(2)).priceIncludePennyPlaceholders(anyFloat(), anyBoolean());
		verify(model, times(16)).addAttribute(anyString(), any());
		verify(service).buildProductDisplayItemList(any());
		verify(service).setUpDate(any(), anyString());
		verify(service, never()).insertPurchaseData(anyInt(), anyString(), anyString());
		verify(redirectAttributes, never()).addAttribute(anyString(), any());
		verifyNoMoreInteractions(model, service);
	}
	
	@Test
	public void testSelectProductsFromThirdParty()
	{
		String testPurchaseData = "[{purchaseData}]";
		String expectedCustomerGuid = "guid";
		
		when(requestBean.getDateFormat()).thenReturn("dd/MM/yyyy");
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getSiteId()).thenReturn(1);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		
		when(service.validateProductIsAvailable(anyInt(), any(LocalDate.class), anyInt(), anyInt(), anyInt(), anyInt()))
				.thenReturn(true);
		when(service.buildPurchaseDataFromThirdParty(anyInt(), any(LocalDate.class), anyString())).thenReturn(testPurchaseData);
		when(service.insertPurchaseData(anyInt(), eq(""), eq(testPurchaseData))).thenReturn(expectedCustomerGuid);
		when(quotaChecker.checkProductOccupancy(anyInt(), any(), anyInt())).thenReturn(true);
		
		assertEquals("redirect:details", controller.selectProductsFromThirdParty(redirectAttributes, 1));
		
		verify(service).validateProductIsAvailable(anyInt(), any(LocalDate.class), anyInt(), anyInt(), anyInt(), anyInt());
		verify(service).buildPurchaseDataFromThirdParty(anyInt(), any(LocalDate.class), anyString());
		verify(service).insertPurchaseData(anyInt(), eq(""), eq(testPurchaseData));
		verify(redirectAttributes).addAttribute(eq("startDate"), anyString());
		verify(redirectAttributes).addAttribute("customerGuid", expectedCustomerGuid);
	}
	
	@Test
	public void testSelectProductsFromThirdParty_ProductNotAvailable()
	{
		when(requestBean.getSiteTitle()).thenReturn("");
		when(service.validateProductIsAvailable(anyInt(), any(LocalDate.class), anyInt(), anyInt(), anyInt(), anyInt()))
				.thenReturn(false);
		
		assertEquals("redirect:error", controller.selectProductsFromThirdParty(redirectAttributes, 1));
		
		verify(service).validateProductIsAvailable(anyInt(), any(LocalDate.class), anyInt(), anyInt(), anyInt(), anyInt());
		verify(redirectAttributes, never()).addAttribute(eq("errorTitle"), anyString());
		verifyNoMoreInteractions(service);
		verifyNoInteractions(redirectAttributes);
	}

	@Test
	public void testSelectProductsFromThirdParty_ProductNotAvailable_DublinErrorMessage()
	{
		when(requestBean.getSiteTitle()).thenReturn("Dublin Airport");
		when(service.validateProductIsAvailable(anyInt(), any(LocalDate.class), anyInt(), anyInt(), anyInt(), anyInt()))
				.thenReturn(false);

		assertEquals("redirect:error", controller.selectProductsFromThirdParty(redirectAttributes, 1));

		verify(redirectAttributes).addAttribute(eq("errorTitle"), anyString());
	}

	@Test
	public void testSelectProductsFromThirdParty_QuotaExceeded()
	{
		when(requestBean.getSiteTitle()).thenReturn("");
		when(service.validateProductIsAvailable(anyInt(), any(LocalDate.class), anyInt(), anyInt(), anyInt(), anyInt()))
				.thenReturn(true);
		when(quotaChecker.checkProductOccupancy(anyInt(), any(), anyInt())).thenReturn(false);

		assertEquals("redirect:error", controller.selectProductsFromThirdParty(redirectAttributes, 1));

		verify(service).validateProductIsAvailable(anyInt(), any(LocalDate.class), anyInt(), anyInt(), anyInt(),
				anyInt());
		verify(quotaChecker).checkProductOccupancy(anyInt(), any(), anyInt());
		verify(redirectAttributes, never()).addAttribute(eq("errorTitle"), anyString());
		verifyNoMoreInteractions(service);
		verifyNoInteractions(redirectAttributes);
	}

	@Test
	public void testSelectProductsFromThirdParty_QuotaExceeded_DublinErrorMessage()
	{
		when(requestBean.getSiteTitle()).thenReturn("Dublin Airport");
		when(service.validateProductIsAvailable(anyInt(), any(LocalDate.class), anyInt(), anyInt(), anyInt(), anyInt()))
				.thenReturn(true);
		when(quotaChecker.checkProductOccupancy(anyInt(), any(), anyInt())).thenReturn(false);

		assertEquals("redirect:error", controller.selectProductsFromThirdParty(redirectAttributes, 1));

		verify(redirectAttributes).addAttribute(eq("errorTitle"), anyString());
	}

	@Test
	public void testSelectProducts_ExceededQuota()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(3);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		SubscriptionBookingQuery bookingQuery = EnhancedRandom.random(SubscriptionBookingQuery.class);
		bookingQuery.setValid(true);
		bookingQuery.setStartDate("03/10/2019");
		when(service.buildProductDisplayItemList(any())).thenReturn(productDisplayItemList);
		when(productDisplayItemList.iterator()).thenReturn(displayIterator);
		when(displayIterator.hasNext()).thenReturn(true, false);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		SubscriptionProductDisplayItem productDisplayItem = mock(SubscriptionProductDisplayItem.class);
		when(displayIterator.next()).thenReturn(productDisplayItem);
		when(localise.getLocation()).thenReturn(localisedLocation);
		when(localisedLocation.getDateformat()).thenReturn("dd-MM-yyyy");
		when(quotaChecker.checkProductOccupancy(anyInt(), any(), anyInt())).thenReturn(false);
		when(requestBean.getDateFormat()).thenReturn("dd/MM/yyyy");
		when(requestBean.isMultiPurchase()).thenReturn(true);
		controller.selectProducts(model, redirectAttributes, bookingQuery, "", "");
		verify(localise, times(2)).priceIncludePennyPlaceholders(anyFloat(), anyBoolean());
		verify(model, times(16)).addAttribute(anyString(), any());
		verify(service).buildProductDisplayItemList(any());
		verify(service).setUpDate(any(), anyString());
		verify(quotaChecker).checkProductOccupancy(anyInt(), any(), anyInt());
		verify(displayIterator).remove();
		verifyNoMoreInteractions(model, service);
	}
}