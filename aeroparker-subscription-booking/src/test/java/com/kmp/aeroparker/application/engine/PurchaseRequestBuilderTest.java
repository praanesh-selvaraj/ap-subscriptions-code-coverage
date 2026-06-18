package com.kmp.aeroparker.application.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.CarParkService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Carparks;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

@ExtendWith(MockitoExtension.class)
class PurchaseRequestBuilderTest
{
	@Mock
	private CarParkService carParkService;
	@Mock
	private Localise localise;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@Mock
	private SubscriptionService service;
	@Mock
	private Affiliates affiliate;
	@Mock
	private SiteService siteService;
	@InjectMocks
	private PurchaseRequestBuilder builder;

	@Test
	void testBuild()
	{
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn(" Wed, 11 September 19");
		when(affiliate.getVatRate()).thenReturn(BigDecimal.TEN);
		Sites site = mock(Sites.class);
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getTimezone()).thenReturn("Europe/London");
		SubscriptionProductTerms productTerms = mock(SubscriptionProductTerms.class);
		when(productTerms.getMinimumTerm()).thenReturn("ONE_MONTH");
		when(productTerms.getPrice()).thenReturn(BigDecimal.TEN);
		when(productTerms.getPeriodType()).thenReturn("RECURRING");
		when(service.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		SubscriptionProductAppearance productAppearance = mock(SubscriptionProductAppearance.class);
		when(productAppearance.getDisplayName()).thenReturn("displayName");
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt()))
				.thenReturn(productAppearance);
		when(carParkService.fetchCarParkById(anyInt())).thenReturn(mock(Carparks.class));
		SubscriptionProduct product = mock(SubscriptionProduct.class);
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getLeadTime()).thenReturn(3);
		assertThat(builder.build(product, 1, 1, DateUtil.strToLocalDate("13/08/2019", "dd/MM/yyyy"), affiliate,
				affiliateSubscriptionSettings));
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(service).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(carParkService).fetchCarParkById(anyInt());
		verify(carParkService).fetchAllCarParksLinkedToBookingProduct(anyInt());
		verifyNoMoreInteractions(service, carParkService);
	}

	@Test
	void testBuild_AffiliateSubscriptionSettings_Null()
	{
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn(" Wed, 11 September 19");
		when(affiliate.getVatRate()).thenReturn(BigDecimal.TEN);
		SubscriptionProductTerms productTerms = mock(SubscriptionProductTerms.class);
		when(productTerms.getMinimumTerm()).thenReturn("ONE_MONTH");
		when(productTerms.getPrice()).thenReturn(BigDecimal.TEN);
		when(productTerms.getPeriodType()).thenReturn("RECURRING");
		when(service.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		SubscriptionProductAppearance productAppearance = mock(SubscriptionProductAppearance.class);
		when(productAppearance.getDisplayName()).thenReturn("displayName");
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt()))
				.thenReturn(productAppearance);
		when(carParkService.fetchCarParkById(anyInt())).thenReturn(mock(Carparks.class));
		SubscriptionProduct product = mock(SubscriptionProduct.class);
		assertThat(builder.build(product, 1, 1, DateUtil.strToLocalDate("13/08/2019", "dd/MM/yyyy"), affiliate, null));
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(service).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(carParkService).fetchCarParkById(anyInt());
		verify(carParkService).fetchAllCarParksLinkedToBookingProduct(anyInt());
		verifyNoMoreInteractions(service, carParkService);
	}

	@Test
	void testBuild_Fetch_Appearance_Default_Lang()
	{
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn(" Wed, 11 September 19");
		when(affiliate.getVatRate()).thenReturn(BigDecimal.TEN);
		SubscriptionProductTerms productTerms = mock(SubscriptionProductTerms.class);
		when(productTerms.getMinimumTerm()).thenReturn("ONE_MONTH");
		when(productTerms.getPrice()).thenReturn(BigDecimal.TEN);
		when(productTerms.getPeriodType()).thenReturn("FIXED");
		when(service.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		SubscriptionProductAppearance productAppearance = mock(SubscriptionProductAppearance.class);
		when(productAppearance.getDisplayName()).thenReturn("displayName");
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt())).thenReturn(null)
				.thenReturn(productAppearance);
		when(carParkService.fetchCarParkById(anyInt())).thenReturn(mock(Carparks.class));
		SubscriptionProduct product = mock(SubscriptionProduct.class);
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		assertThat(builder.build(product, 1, 1, DateUtil.strToLocalDate("13/08/2019", "dd/MM/yyyy"), affiliate,
				affiliateSubscriptionSettings));
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(service, times(2)).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(carParkService).fetchCarParkById(anyInt());
		verify(carParkService).fetchAllCarParksLinkedToBookingProduct(anyInt());
		verifyNoMoreInteractions(service, carParkService);
	}

	@Test
	void testBuild_No_LeadTime_FixedStartDate()
	{
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn(" Wed, 11 September 19");
		when(affiliate.getVatRate()).thenReturn(BigDecimal.TEN);
		SubscriptionProductTerms productTerms = mock(SubscriptionProductTerms.class);
		Sites site = mock(Sites.class);
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getTimezone()).thenReturn("Europe/London");
		when(productTerms.getMinimumTerm()).thenReturn("ONE_MONTH");
		when(productTerms.getPrice()).thenReturn(BigDecimal.TEN);
		when(productTerms.getPeriodType()).thenReturn("RECURRING");
		when(service.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		SubscriptionProductAppearance productAppearance = mock(SubscriptionProductAppearance.class);
		when(productAppearance.getDisplayName()).thenReturn("displayName");
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt()))
				.thenReturn(productAppearance);
		when(carParkService.fetchCarParkById(anyInt())).thenReturn(mock(Carparks.class));
		SubscriptionProduct product = mock(SubscriptionProduct.class);
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getLeadTime()).thenReturn(0);
		when(affiliateSubscriptionSettings.getFixedStartDay()).thenReturn(1);
		assertThat(builder.build(product, 1, 1, DateUtil.strToLocalDate("13/08/2019", "dd/MM/yyyy"), affiliate,
				affiliateSubscriptionSettings));
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(service).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(carParkService).fetchCarParkById(anyInt());
		verify(carParkService).fetchAllCarParksLinkedToBookingProduct(anyInt());
		verifyNoMoreInteractions(service, carParkService);
	}

	@Test
	void testBuild_No_LeadTime_No_FixedStartDate()
	{
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn(" Wed, 11 September 19");
		when(affiliate.getVatRate()).thenReturn(BigDecimal.TEN);
		SubscriptionProductTerms productTerms = mock(SubscriptionProductTerms.class);
		Sites site = mock(Sites.class);
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		when(site.getTimezone()).thenReturn("Europe/London");
		when(productTerms.getMinimumTerm()).thenReturn("ONE_MONTH");
		when(productTerms.getPrice()).thenReturn(BigDecimal.TEN);
		when(productTerms.getPeriodType()).thenReturn("RECURRING");
		when(service.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		SubscriptionProductAppearance productAppearance = mock(SubscriptionProductAppearance.class);
		when(productAppearance.getDisplayName()).thenReturn("displayName");
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt()))
				.thenReturn(productAppearance);
		when(carParkService.fetchCarParkById(anyInt())).thenReturn(mock(Carparks.class));
		SubscriptionProduct product = mock(SubscriptionProduct.class);
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		when(affiliateSubscriptionSettings.getLeadTime()).thenReturn(0);
		assertThat(builder.build(product, 1, 1, DateUtil.strToLocalDate("13/08/2019", "dd/MM/yyyy"), affiliate,
				affiliateSubscriptionSettings));
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(service).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(carParkService).fetchCarParkById(anyInt());
		verify(carParkService).fetchAllCarParksLinkedToBookingProduct(anyInt());
		verifyNoMoreInteractions(service, carParkService);
	}

	@Test
	void testBuild_Apperance_terms_Null()
	{
		when(service.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(null);
		when(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt())).thenReturn(null);
		when(carParkService.fetchCarParkById(anyInt())).thenReturn(mock(Carparks.class));
		SubscriptionProduct product = mock(SubscriptionProduct.class);
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = mock(AffiliateSubscriptionSettings.class);
		assertThat(builder.build(product, 1, 1, DateUtil.strToLocalDate("13/08/2019", "dd/MM/yyyy"), affiliate,
				affiliateSubscriptionSettings));
		verify(service).fetchSubscriptionProductTermsBySubProductId(anyInt());
		verify(service, times(2)).fetchSubscriptionProductAppearanceBySubProductIdAndLangId(anyInt(), anyInt());
		verify(carParkService).fetchCarParkById(anyInt());
		verify(carParkService).fetchCarParkById(anyInt());
		verify(carParkService).fetchAllCarParksLinkedToBookingProduct(anyInt());
		verifyNoMoreInteractions(service, carParkService);
	}
}