package com.kmp.aeroparker.application.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class DateControllerTest
{
	@Mock
	private AffiliateService affiliateService;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@Mock
	private Localise localise;
	@Mock
	private SiteService siteService;
	@Mock
	private SubscriptionControllerService service;
	@Mock
	private SubscriptionConfigBean requestBean;
	@InjectMocks
	private DateController controller;
	@Mock
	private Affiliates affiliate;
	@Mock
	private Locations location;
	@Mock
	private Model model;
	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	private Sites site;

	@Test
	void testInit()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getAffiliateId()).thenReturn(1);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(10);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateSubscription()).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(localise.getDateFormatForDatePicker(anyBoolean(), anyBoolean())).thenReturn("MM-dd-yyyy");
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(affiliateService.fetchAffiliateById(anyInt())).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(1);
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(siteService.fetchLocationById(anyInt())).thenReturn(location);
		when(location.getCalendarStartDay()).thenReturn(Byte.valueOf("0"));
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

		controller.init(model, false);

		verify(model, times(15)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testInit_AffiliateSubscriptionSettings_Null()
	{
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(null);
		when(requestBean.getAffiliateSubscription()).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(localise.getDateFormatForDatePicker(anyBoolean(), anyBoolean())).thenReturn("MM-dd-yyyy");
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		controller.init(model, false);
		verify(model, times(14)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testInit_AffiliateSubscription_Null()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(10);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscription()).thenReturn(null);
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(localise.getDateFormatForDatePicker(anyBoolean(), anyBoolean())).thenReturn("MM-dd-yyyy");
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		controller.init(model, false);
		verify(model, times(14)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testRedirectToSelectProducts()
	{
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(service.validateBookingTimes(any())).thenReturn(true);
		controller.redirectToSelectProduct(redirectAttributes, "10/30/2019");
		verify(service).validateBookingTimes(any());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testRedirectToSelectProducts_Invalid_BookingQuery()
	{
		when(requestBean.getAffiliate()).thenReturn(mock(Affiliates.class));
		when(requestBean.getCurrentLanguage()).thenReturn(mock(Languages.class));
		when(service.validateBookingTimes(any())).thenReturn(false);
		controller.redirectToSelectProduct(redirectAttributes, "10/30/2019");
		verify(service).validateBookingTimes(any());
		verifyNoMoreInteractions(model, service);
	}

	@Test
	void testInit_AffiliateNull()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getAffiliateId()).thenReturn(1);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(10);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateSubscription()).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(localise.getDateFormatForDatePicker(anyBoolean(), anyBoolean())).thenReturn("MM-dd-yyyy");
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(affiliateService.fetchAffiliateById(anyInt())).thenReturn(null);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

		controller.init(model, false);

		verify(model, times(14)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testInit_AffiliateIdInvalid()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getAffiliateId()).thenReturn(1);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(10);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateSubscription()).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(localise.getDateFormatForDatePicker(anyBoolean(), anyBoolean())).thenReturn("MM-dd-yyyy");
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(affiliateService.fetchAffiliateById(anyInt())).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(0);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

		controller.init(model, false);

		verify(model, times(14)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testInit_SiteNull()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getAffiliateId()).thenReturn(1);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(10);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateSubscription()).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(localise.getDateFormatForDatePicker(anyBoolean(), anyBoolean())).thenReturn("MM-dd-yyyy");
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(affiliateService.fetchAffiliateById(anyInt())).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(1);
		when(siteService.fetchSiteById(anyInt())).thenReturn(null);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

		controller.init(model, false);

		verify(model, times(14)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testInit_SiteIdInvalid()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getAffiliateId()).thenReturn(1);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(10);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateSubscription()).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(localise.getDateFormatForDatePicker(anyBoolean(), anyBoolean())).thenReturn("MM-dd-yyyy");
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(affiliateService.fetchAffiliateById(anyInt())).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(1);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getId()).thenReturn(0);

		controller.init(model, false);

		verify(model, times(14)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testInit_LocationNull()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getAffiliateId()).thenReturn(1);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(10);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(requestBean.getAffiliateSubscription()).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(localise.getDateFormatForDatePicker(anyBoolean(), anyBoolean())).thenReturn("MM-dd-yyyy");
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(affiliateService.fetchAffiliateById(anyInt())).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(1);
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(siteService.fetchLocationById(anyInt())).thenReturn(null);

		controller.init(model, false);

		verify(model, times(14)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model);
	}

	@Test
	void testInit_LocationCalenderStartDayInvalid()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getAffiliateId()).thenReturn(1);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(10);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getAffiliateSubscriptionSettings()).thenReturn(affiliateSubscriptionSettings);
		when(requestBean.getAffiliateSubscription()).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(localise.getDateFormatForDatePicker(anyBoolean(), anyBoolean())).thenReturn("MM-dd-yyyy");
		when(service.setUpDate(any(), anyString())).thenReturn(DateUtil.nowLocalDate("Europe/London")
				.toString());
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
		when(affiliateService.fetchAffiliateById(anyInt())).thenReturn(affiliate);
		when(affiliate.getId()).thenReturn(1);
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(siteService.fetchLocationById(anyInt())).thenReturn(location);
		when(location.getCalendarStartDay()).thenReturn(null);
		ReflectionTestUtils.setField(controller, "languageFieldsList", languageFieldsList);
		when(languageFieldsList.getTranslation(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

		controller.init(model, false);

		verify(model, times(14)).addAttribute(anyString(), any());
		verify(service).setUpDate(any(), anyString());
		verifyNoMoreInteractions(model);
	}
}