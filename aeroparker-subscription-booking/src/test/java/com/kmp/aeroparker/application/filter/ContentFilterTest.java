package com.kmp.aeroparker.application.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
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
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

@ExtendWith(MockitoExtension.class)
class ContentFilterTest
{
	@Mock
	private Localise localise;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private UrlPathHelper pathHelper;
	@Mock
	private AffiliateService affService;
	@Mock
	private SiteService siteService;
	@InjectMocks
	private ContentFilter filter;
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

	@Test
	void testDoFilter() throws IOException, ServletException
	{
		when(site.getLocationId()).thenReturn(1);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getIsEnabled()).thenReturn(1);
		when(affiliate.getSiteid()).thenReturn(1);
		when(affService.fetchAffiliateByCode(anyString())).thenReturn(affiliate);
		when(request.getRequestURI()).thenReturn("Subscriptions/lba/dates");
		when(siteService.fetchSiteById(anyInt())).thenReturn(site);
		filter.doFilter(request, response, chain);
		verify(affService).fetchAffiliateByCode(anyString());
		verify(siteService).fetchSiteById(anyInt());
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
		verifyNoMoreInteractions(affService, siteService, response);
	}

	@Test
	void testDoFilter_Site_Null() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getIsEnabled()).thenReturn(1);
		when(affiliate.getSiteid()).thenReturn(1);
		when(affService.fetchAffiliateByCode(anyString())).thenReturn(affiliate);
		when(request.getRequestURI()).thenReturn("Subscriptions/lba/dates");
		when(siteService.fetchSiteById(anyInt())).thenReturn(null);
		filter.doFilter(request, response, chain);
		verify(affService).fetchAffiliateByCode(anyString());
		verify(siteService).fetchSiteById(anyInt());
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
		verifyNoMoreInteractions(affService, siteService, response);
	}

	@Test
	void testDoFilter_Servlet_Not_Allowed() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/Unknown");
		filter.doFilter(request, response, chain);
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
		verifyNoInteractions(affService, response);
	}

	@Test
	void testDoFilter_Affiliate_Null() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/dates/dates");
		when(affService.fetchAffiliateByCode(anyString())).thenReturn(null);
		when(request.getRequestURI()).thenReturn("Subscriptions/Booking");
		filter.doFilter(request, response, chain);
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
		verify(affService).fetchAffiliateByCode(anyString());
		verify(request).setAttribute(anyString(), any());
		verifyNoMoreInteractions(affService, request, chain);
	}

	@Test
	void testDoFilter_Affiliate_Not_Enabled() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getIsEnabled()).thenReturn(0);
		when(affService.fetchAffiliateByCode(anyString())).thenReturn(affiliate);
		when(request.getRequestURI()).thenReturn("Subscriptions/lba/Booking");
		filter.doFilter(request, response, chain);
		verify(pathHelper).getPathWithinApplication(any());
		verify(chain).doFilter(any(), any());
		verify(request).setAttribute(anyString(), any());
	}
}