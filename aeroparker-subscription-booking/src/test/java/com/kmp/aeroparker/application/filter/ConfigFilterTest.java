package com.kmp.aeroparker.application.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

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
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesMetadata;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Currencies;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

@ExtendWith(MockitoExtension.class)
class ConfigFilterTest
{
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private UrlPathHelper pathHelper;
	@Mock
	private AffiliateService affService;
	@Mock
	private SiteService siteService;
	@InjectMocks
	private ConfigFilter filter;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain chain;
	@Mock
	private Affiliates affiliate;
	@Mock
	private Sites site;
	@Mock
	private Languages language;

	@Test
	void testDoFilter() throws IOException, ServletException
	{
		when(affiliate.getCode()).thenReturn("snn");
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		AffiliatesMetadata affiliatesMetadata = mock(AffiliatesMetadata.class);
		when(affService.fetchAffiliateMetadataByAffiliateId(anyInt())).thenReturn(affiliatesMetadata);
		when(affService.fetchAffiliateConfigValues(anyInt())).thenReturn(mock(AffiliateConfig.class));
		Currencies currencies = mock(Currencies.class);
		when(currencies.getCode()).thenReturn("�");
		when(currencies.getHtmlSymbol()).thenReturn("0&et");
		when(siteService.fetchSiteCurrencyBySiteId(anyInt())).thenReturn(currencies);
		when(siteService.fetchLocationById(anyInt())).thenReturn(mock(Locations.class));
		filter.doFilter(request, response, chain);
		verify(affService).fetchAffiliateMetadataByAffiliateId(anyInt());
		verify(affService).fetchAffiliateConfigValues(anyInt());
		verify(siteService).fetchSiteCurrencyBySiteId(anyInt());
		verify(pathHelper).getPathWithinApplication(any());
		verify(siteService).fetchLocationById(anyInt());
		verify(siteService).fetchLocationById(anyInt());
		verify(chain).doFilter(any(), any());
		verifyNoMoreInteractions(affService, siteService, response);
	}

	@Test
	void testDoFilter_Language_Null() throws IOException, ServletException
	{
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		AffiliatesMetadata affiliatesMetadata = mock(AffiliatesMetadata.class);
		when(affService.fetchAffiliateMetadataByAffiliateId(anyInt())).thenReturn(affiliatesMetadata);
		filter.doFilter(request, response, chain);
		verify(affService).fetchAffiliateMetadataByAffiliateId(anyInt());
		verify(affService).fetchAffiliateConfigValues(anyInt());
		verify(siteService).fetchSiteCurrencyBySiteId(anyInt());
		verify(pathHelper).getPathWithinApplication(any());
		verify(siteService).fetchLocationById(anyInt());
		verify(chain).doFilter(any(), any());
		verifyNoMoreInteractions(affService, siteService, response);
	}

	@Test
	void testDoFilter_Affiliate_Config_Null() throws IOException, ServletException
	{
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		AffiliatesMetadata affiliatesMetadata = mock(AffiliatesMetadata.class);
		when(affService.fetchAffiliateMetadataByAffiliateId(anyInt())).thenReturn(affiliatesMetadata);
		when(affService.fetchAffiliateConfigValues(anyInt())).thenReturn(null);
		Currencies currencies = mock(Currencies.class);
		when(currencies.getCode()).thenReturn("�");
		when(currencies.getHtmlSymbol()).thenReturn("0&et");
		when(siteService.fetchSiteCurrencyBySiteId(anyInt())).thenReturn(currencies);
		when(siteService.fetchLocationById(anyInt())).thenReturn(mock(Locations.class));
		filter.doFilter(request, response, chain);
		verify(affService).fetchAffiliateMetadataByAffiliateId(anyInt());
		verify(affService).fetchAffiliateConfigValues(anyInt());
		verify(siteService).fetchSiteCurrencyBySiteId(anyInt());
		verify(pathHelper).getPathWithinApplication(any());
		verify(siteService).fetchLocationById(anyInt());
		verify(siteService).fetchLocationById(anyInt());
		verify(chain).doFilter(any(), any());
		verifyNoMoreInteractions(affService, siteService, response);
	}

	@Test
	void testDoFilter_AffiliateSubscription_Null() throws IOException, ServletException
	{
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		AffiliatesMetadata affiliatesMetadata = mock(AffiliatesMetadata.class);
		when(affService.fetchAffiliateMetadataByAffiliateId(anyInt())).thenReturn(affiliatesMetadata);
		when(affService.fetchAffiliateConfigValues(anyInt())).thenReturn(mock(AffiliateConfig.class));
		Currencies currencies = mock(Currencies.class);
		when(currencies.getCode()).thenReturn("�");
		when(currencies.getHtmlSymbol()).thenReturn("0&et");
		when(siteService.fetchSiteCurrencyBySiteId(anyInt())).thenReturn(currencies);
		when(siteService.fetchLocationById(anyInt())).thenReturn(mock(Locations.class));
		filter.doFilter(request, response, chain);
		verify(affService).fetchAffiliateMetadataByAffiliateId(anyInt());
		verify(affService).fetchAffiliateConfigValues(anyInt());
		verify(siteService).fetchSiteCurrencyBySiteId(anyInt());
		verify(siteService).fetchLocationById(anyInt());
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
		verifyNoMoreInteractions(affService, siteService, response);
	}

	@Test
	void testDoFilter_Metadata_Null() throws IOException, ServletException
	{
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(affService.fetchAffiliateMetadataByAffiliateId(anyInt())).thenReturn(null);
		when(affService.fetchAffiliateConfigValues(anyInt())).thenReturn(mock(AffiliateConfig.class));
		when(siteService.fetchSiteCurrencyBySiteId(anyInt())).thenReturn(null);
		when(siteService.fetchLocationById(anyInt())).thenReturn(mock(Locations.class));
		filter.doFilter(request, response, chain);
		verify(affService).fetchAffiliateMetadataByAffiliateId(anyInt());
		verify(affService).fetchAffiliateConfigValues(anyInt());
		verify(siteService).fetchSiteCurrencyBySiteId(anyInt());
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
		verify(siteService).fetchLocationById(anyInt());
		verifyNoMoreInteractions(affService, siteService, response);
	}

	@Test
	void testDoFilter_Servlet_Not_Allowed() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/Unknown");
		filter.doFilter(request, response, chain);
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
	}

	@Test
	void testDoFilter_Affiliate_Null() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/booking");
		when(requestBean.getAffiliate()).thenReturn(null);
		when(requestBean.getSite()).thenReturn(site);
		filter.doFilter(request, response, chain);
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
		verify(requestBean).getAffiliate();
		verify(requestBean).getSite();
		verify(request).setAttribute(anyString(), any());
		verifyNoMoreInteractions(affService, request, chain);
	}

	@Test
	void testDoFilter_Site_Null() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/booking");
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(null);
		filter.doFilter(request, response, chain);
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
		verify(requestBean).getAffiliate();
		verify(requestBean).getSite();
		verify(request).setAttribute(anyString(), any());
		verifyNoMoreInteractions(affService, request, chain);
	}
}