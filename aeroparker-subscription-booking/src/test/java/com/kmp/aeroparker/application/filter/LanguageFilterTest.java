package com.kmp.aeroparker.application.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UrlPathHelper;

import com.kmp.aeroparker.application.db.service.LanguageService;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class LanguageFilterTest
{
	@Mock
	private LanguageService langService;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private UrlPathHelper pathHelper;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@InjectMocks
	private LanguageFilter filter;
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
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getDefaultLanguageId()).thenReturn(1);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		List<Languages> languages = new ArrayList<>();
		languages.add(EnhancedRandom.random(Languages.class));
		Languages languages2 = EnhancedRandom.random(Languages.class);
		languages2.setId(1);
		languages2.setDisplayCode("en-gb");
		languages.add(languages2);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(langService.fetchAffiliateDefaultLanguage(anyInt())).thenReturn(defaultLanguage);
		when(langService.fetchLanguagesByAffiliateId(anyInt())).thenReturn(languages);
		when(langService.fetchLanguageByDisplayCode(anyString())).thenReturn(language);
		when(language.getId()).thenReturn(1);
		when(request.getParameter(eq("lang"))).thenReturn("en-gb");
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(langService).fetchLanguagesByAffiliateId(anyInt());
		verify(langService).fetchLanguageByDisplayCode(anyString());
		verify(response).addCookie(any());
		verify(languageFieldsList).populateTranslations(anyInt(), anyInt());
		verifyNoMoreInteractions(langService, response);
	}

	@Test
	void testDoFilter_DropDown_Language_Not_Exist() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getDefaultLanguageId()).thenReturn(1);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		List<Languages> languages = new ArrayList<>();
		languages.add(EnhancedRandom.random(Languages.class));
		Languages languages2 = EnhancedRandom.random(Languages.class);
		languages2.setId(1);
		languages2.setDisplayCode("en-gb");
		languages.add(languages2);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(langService.fetchAffiliateDefaultLanguage(anyInt())).thenReturn(defaultLanguage);
		when(langService.fetchLanguagesByAffiliateId(anyInt())).thenReturn(languages);
		when(langService.fetchLanguageByDisplayCode(anyString())).thenReturn(language);
		when(language.getId()).thenReturn(1);
		when(request.getParameter(eq("lang"))).thenReturn("en-g");
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(langService).fetchLanguagesByAffiliateId(anyInt());
		verify(langService).fetchLanguageByDisplayCode(anyString());
		verify(langService).fetchAffiliateDefaultLanguage(anyInt());
		verify(languageFieldsList).populateTranslations(anyInt(), anyInt());
		verifyNoMoreInteractions(langService, response);
	}

	@Test
	void testDoFilter_DisplayCode_Empty_Cookie_Not_Empty() throws IOException, ServletException
	{
		Cookie cookie = mock(Cookie.class);
		when(cookie.getName()).thenReturn("languageDisplayCode");
		when(cookie.getValue()).thenReturn("en");
		Cookie[] cookies = { cookie };
		when(request.getCookies()).thenReturn(cookies);
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		List<Languages> languages = new ArrayList<>();
		languages.add(EnhancedRandom.random(Languages.class));
		Languages languages2 = EnhancedRandom.random(Languages.class);
		languages2.setId(1);
		languages.add(languages2);
		when(langService.fetchLanguagesByAffiliateId(anyInt())).thenReturn(languages);
		when(langService.fetchLanguageByDisplayCode(anyString())).thenReturn(null);
		when(langService.fetchAffiliateDefaultLanguage(anyInt())).thenReturn(language);
		when(language.getId()).thenReturn(1);
		when(request.getParameter(eq("lang"))).thenReturn("");
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(langService).fetchLanguagesByAffiliateId(anyInt());
		verify(langService).fetchLanguageByDisplayCode(anyString());
		verify(langService).fetchAffiliateDefaultLanguage(anyInt());
		verify(languageFieldsList).populateTranslations(anyInt(), anyInt());
		verifyNoMoreInteractions(langService, response);
	}

	@Test
	void testDoFilter_GetLanguageFromLanguageList() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getDefaultLanguageId()).thenReturn(1);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		List<Languages> languages = new ArrayList<>();
		languages.add(EnhancedRandom.random(Languages.class));
		Languages languages2 = EnhancedRandom.random(Languages.class);
		languages2.setId(1);
		languages.add(languages2);
		Languages defaultLanguage = EnhancedRandom.random(Languages.class);
		when(langService.fetchAffiliateDefaultLanguage(anyInt())).thenReturn(defaultLanguage);
		when(langService.fetchLanguagesByAffiliateId(anyInt())).thenReturn(languages);
		when(langService.fetchLanguageByDisplayCode(anyString())).thenReturn(null);
		when(langService.fetchAffiliateDefaultLanguage(anyInt())).thenReturn(null);
		when(request.getParameter(eq("lang"))).thenReturn("");
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(langService).fetchLanguagesByAffiliateId(anyInt());
		verify(langService).fetchLanguageByDisplayCode(anyString());
		verify(langService).fetchAffiliateDefaultLanguage(anyInt());
		verify(langService).fetchLanguageById(anyInt());
		verify(languageFieldsList).populateTranslations(anyInt(), anyInt());
		verifyNoMoreInteractions(langService, response);
	}

	@Test
	void testDoFilter_Language_Null_FilterException() throws IOException, ServletException
	{
		when(affiliate.getId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliate);
		when(requestBean.getSite()).thenReturn(site);
		when(pathHelper.getPathWithinApplication(any())).thenReturn("http://localhost:8888/subscriptions/lba/dates");
		when(affiliate.getId()).thenReturn(1);
		when(langService.fetchLanguagesByAffiliateId(anyInt())).thenReturn(EnhancedRandom.randomListOf(0, Languages.class));
		when(langService.fetchLanguageByDisplayCode(anyString())).thenReturn(null);
		when(langService.fetchAffiliateDefaultLanguage(anyInt())).thenReturn(null);
		when(request.getParameter(eq("lang"))).thenReturn("");
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(any(), any());
		verify(langService).fetchLanguagesByAffiliateId(anyInt());
		verify(langService).fetchLanguageByDisplayCode(anyString());
		verify(langService).fetchAffiliateDefaultLanguage(anyInt());
		verify(langService).fetchLanguageById(anyInt());
		verifyNoMoreInteractions(langService, response);
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