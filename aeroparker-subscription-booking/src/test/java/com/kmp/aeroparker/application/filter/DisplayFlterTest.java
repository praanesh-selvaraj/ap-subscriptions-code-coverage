package com.kmp.aeroparker.application.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UrlPathHelper;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.LanguageService;
import com.kmp.aeroparker.application.model.AffiliatesCustomFooterPages;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionMedia;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesContent;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesDisplay;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class DisplayFlterTest
{
	@Mock
	private AffiliateService affService;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private UrlPathHelper pathHelper;
	@Mock
	private LanguageService langService;
	@InjectMocks
	private DisplayFlter filter;
	@Mock
	private Affiliates affiliate;
	@Mock
	private Sites site;
	@Mock
	private FilterChain chain;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;

	@Test
	void testDoFilter() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		Languages language = EnhancedRandom.random(Languages.class);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(requestBean.getCurrentLanguage()).thenReturn(language);
		when(requestBean.getDefaultLanguage()).thenReturn(defaultLanguage);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.random(AffiliatesDisplay.class));
		when(affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCustomFooterPages.class));
		when(affService.fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(affService.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscriptionMedia.class));
		when(affService.fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt())).thenReturn(mock(AffiliatesContent.class));
		when(affService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(mock(AffiliateSubscriptionSettings.class));
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(affService).fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt());
		verify(affService).fetchSubscriptionSettingsByAffiliateId(anyInt());
		verifyNoMoreInteractions(affService, response);
	}

	@Test
	void testDoFilter_AffiliateSubscriptionSettings_AffiliateSubscriptionMedia_Null() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		Languages language = EnhancedRandom.random(Languages.class);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(requestBean.getCurrentLanguage()).thenReturn(language);
		when(requestBean.getDefaultLanguage()).thenReturn(defaultLanguage);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.random(AffiliatesDisplay.class));
		when(affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCustomFooterPages.class));
		when(affService.fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(affService.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt())).thenReturn(null).thenReturn(EnhancedRandom.random(AffiliateSubscriptionMedia.class));
		when(affService.fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt())).thenReturn(mock(AffiliatesContent.class));
		when(affService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(null);
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(affService).fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt());
		verify(affService, times(2)).fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt());
		verify(affService).fetchSubscriptionSettingsByAffiliateId(anyInt());
		verifyNoMoreInteractions(affService, response);
	}

	@Test
	void testDoFilter_DefaultLanguage_Null() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		Languages language = EnhancedRandom.random(Languages.class);
		when(requestBean.getCurrentLanguage()).thenReturn(language);
		when(requestBean.getDefaultLanguage()).thenReturn(null);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.random(AffiliatesDisplay.class));
		when(affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCustomFooterPages.class));
		when(affService.fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(affService.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscriptionMedia.class));
		when(affService.fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt())).thenReturn(mock(AffiliatesContent.class));
		when(affService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(mock(AffiliateSubscriptionSettings.class));
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(affService).fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt());
		verify(affService).fetchSubscriptionSettingsByAffiliateId(anyInt());
		verifyNoMoreInteractions(affService, response);
	}

	@Test
	void testDoFilter_CurrentLanguage_AffiliatesDisplay_Null() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		Languages language = EnhancedRandom.random(Languages.class);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(requestBean.getCurrentLanguage()).thenReturn(language);
		when(requestBean.getDefaultLanguage()).thenReturn(defaultLanguage);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt())).thenReturn(null)
				.thenReturn(EnhancedRandom.random(AffiliatesDisplay.class));
		when(affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCustomFooterPages.class));
		when(affService.fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(affService.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscriptionMedia.class));
		when(affService.fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt())).thenReturn(mock(AffiliatesContent.class));
		when(affService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(mock(AffiliateSubscriptionSettings.class));
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(affService, times(2)).fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt());
		verify(affService).fetchSubscriptionSettingsByAffiliateId(anyInt());
		verifyNoMoreInteractions(affService, response);
	}

	@Test
	void testDoFilter_CurrentLanguage_AffiliatesContent_Null() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		Languages language = EnhancedRandom.random(Languages.class);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(requestBean.getCurrentLanguage()).thenReturn(language);
		when(requestBean.getDefaultLanguage()).thenReturn(defaultLanguage);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.random(AffiliatesDisplay.class));
		when(affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCustomFooterPages.class));
		when(affService.fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(affService.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscriptionMedia.class));
		when(affService.fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt())).thenReturn(null)
				.thenReturn(mock(AffiliatesContent.class));
		when(affService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(mock(AffiliateSubscriptionSettings.class));
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(affService).fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService, times(2)).fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt());
		verify(affService).fetchSubscriptionSettingsByAffiliateId(anyInt());
		verifyNoMoreInteractions(affService, response);
	}

	@Test
	void testDoFilter_CurrentLanguage_AffiliateSubscription_Null() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		Languages language = EnhancedRandom.random(Languages.class);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(requestBean.getCurrentLanguage()).thenReturn(language);
		when(requestBean.getDefaultLanguage()).thenReturn(defaultLanguage);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.random(AffiliatesDisplay.class));
		when(affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCustomFooterPages.class));
		when(affService.fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt())).thenReturn(null)
				.thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(affService.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscriptionMedia.class));
		when(affService.fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt())).thenReturn(mock(AffiliatesContent.class));
		when(affService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(mock(AffiliateSubscriptionSettings.class));
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(affService).fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService, times(2)).fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt());
		verify(affService).fetchSubscriptionSettingsByAffiliateId(anyInt());
		verifyNoMoreInteractions(affService, response);
	}

	@Test
	void testDoFilter_AffiliateSubscription_Null() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		Languages language = EnhancedRandom.random(Languages.class);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(requestBean.getCurrentLanguage()).thenReturn(language);
		when(requestBean.getDefaultLanguage()).thenReturn(defaultLanguage);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.random(AffiliatesDisplay.class));
		when(affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCustomFooterPages.class));
		when(affService.fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt())).thenReturn(null);
		when(affService.fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt())).thenReturn(mock(AffiliatesContent.class));
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(affService).fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService, times(2)).fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt());
		verify(affService, times(0)).fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt());
		verify(affService).fetchSubscriptionSettingsByAffiliateId(anyInt());
		verifyNoMoreInteractions(affService, response);
	}

	@Test
	void testDoFilter_AffiliatesFooterPages_CurrentLanguage_Empty() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		Languages language = EnhancedRandom.random(Languages.class);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(requestBean.getCurrentLanguage()).thenReturn(language);
		when(requestBean.getDefaultLanguage()).thenReturn(defaultLanguage);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.random(AffiliatesDisplay.class));
		when(affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(new ArrayList<AffiliatesCustomFooterPages>())
				.thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCustomFooterPages.class));
		when(affService.fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		when(affService.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscriptionMedia.class));
		when(affService.fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt())).thenReturn(mock(AffiliatesContent.class));
		when(affService.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(mock(AffiliateSubscriptionSettings.class));
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(affService).fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt());
		verify(affService).fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt());
		verify(affService, times(2)).fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt());
		verify(affService).fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt());
		verify(affService).fetchSubscriptionSettingsByAffiliateId(anyInt());
		verifyNoMoreInteractions(affService, response);
	}

	@Test
	void testDoFilter_Affiliate_Null() throws IOException, ServletException
	{
		when(requestBean.getAffiliate()).thenReturn(null);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verifyNoInteractions(langService);
		verifyNoMoreInteractions(langService, response);
	}

	@Test
	void testDoFilter_Site_Affiliate_Null() throws IOException, ServletException
	{
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(null);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verifyNoInteractions(langService);
		verifyNoMoreInteractions(langService, response);
	}

	@Test
	void testDoFilter_Servlet_Not_Allowed() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/Unknown");
		filter.doFilter(request, response, chain);
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
	}
}