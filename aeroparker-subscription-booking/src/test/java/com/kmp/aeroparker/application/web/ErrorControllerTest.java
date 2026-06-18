package com.kmp.aeroparker.application.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;
import org.springframework.web.util.UrlPathHelper;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

@ExtendWith(SpringExtension.class)
class ErrorControllerTest
{
	@Mock
	private UrlPathHelper pathHelper;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@Mock
	private AffiliateService affService;
	@Mock
	private SiteService siteService;
	@InjectMocks
	private ErrorController controller;
	@Mock
	private HttpServletRequest httpRequest;
	@Mock
	private Model model;
	@Mock
	private Affiliates affiliates;
	@Mock
	private Sites sites;

	@Test
	void testHandleError()
	{
		String expectedErrorTitle = "Error Title";
		String expectedTranslatedErrorTitle = "Error title translated";

		when(httpRequest.getAttribute(eq(RequestDispatcher.ERROR_STATUS_CODE))).thenReturn(400);
		when(pathHelper.getOriginatingRequestUri(any())).thenReturn("/subscriptions/snn/date");
		when(affService.fetchAffiliateByCode(anyString())).thenReturn(affiliates);
		when(affiliates.getSiteid()).thenReturn(1);
		when(siteService.fetchSiteById(anyInt())).thenReturn(sites);
		when(sites.getDefaultLanguageId()).thenReturn(10);
		when(languageFieldsList.getTranslation(expectedErrorTitle)).thenReturn(expectedTranslatedErrorTitle);
		when(languageFieldsList.getTranslation(anyString(), anyString(), anyString())).thenReturn("Error message translated");
		controller.handleError(model, httpRequest, expectedErrorTitle);
		verify(languageFieldsList).populateTranslations(anyInt(), anyInt());
		verify(pathHelper).getOriginatingRequestUri(any());
		verify(affService).fetchAffiliateByCode(anyString());
		verify(siteService).fetchSiteById(anyInt());
		verify(model).addAttribute("errTitle", expectedTranslatedErrorTitle);
		verifyNoMoreInteractions(affService, siteService, pathHelper);
	}

	@Test
	void testHandleError_AffiliateCode_Empty()
	{
		when(httpRequest.getAttribute(eq(RequestDispatcher.ERROR_STATUS_CODE))).thenReturn(400);
		when(pathHelper.getOriginatingRequestUri(any())).thenReturn("/subscriptions");
		controller.handleError(model, httpRequest, "Error Title");
		verify(pathHelper).getOriginatingRequestUri(any());
		verify(affService, times(0)).fetchAffiliateByCode(anyString());
		verify(siteService, times(0)).fetchSiteById(anyInt());
		verifyNoMoreInteractions(affService, siteService, pathHelper);
	}

	@Test
	void testHandleError_UrlPath_Empty()
	{
		when(httpRequest.getAttribute(eq(RequestDispatcher.ERROR_STATUS_CODE))).thenReturn(400);
		when(pathHelper.getOriginatingRequestUri(any())).thenReturn("");
		controller.handleError(model, httpRequest, "Error Title");
		verify(pathHelper).getOriginatingRequestUri(any());
		verify(affService, times(0)).fetchAffiliateByCode(anyString());
		verify(siteService, times(0)).fetchSiteById(anyInt());
		verifyNoMoreInteractions(affService, siteService, pathHelper);
	}

	@Test
	void testHandleError_Affiliate_Null()
	{
		when(httpRequest.getAttribute(eq(RequestDispatcher.ERROR_STATUS_CODE))).thenReturn(400);
		when(pathHelper.getOriginatingRequestUri(any())).thenReturn("/subscriptions/snn/date");
		when(affService.fetchAffiliateByCode(anyString())).thenReturn(null);
		controller.handleError(model, httpRequest, "Error Title");
		verify(pathHelper).getOriginatingRequestUri(any());
		verify(affService).fetchAffiliateByCode(anyString());
		verify(siteService, times(0)).fetchSiteById(anyInt());
		verifyNoMoreInteractions(affService, siteService, pathHelper);
	}

	@Test
	void testHandleError_Site_Null()
	{
		when(httpRequest.getAttribute(eq(RequestDispatcher.ERROR_STATUS_CODE))).thenReturn(400);
		when(pathHelper.getOriginatingRequestUri(any())).thenReturn("/subscriptions/snn/date");
		when(affService.fetchAffiliateByCode(anyString())).thenReturn(affiliates);
		when(affiliates.getSiteid()).thenReturn(1);
		when(siteService.fetchSiteById(anyInt())).thenReturn(null);
		controller.handleError(model, httpRequest, "Error Title");
		verify(pathHelper).getOriginatingRequestUri(any());
		verify(affService).fetchAffiliateByCode(anyString());
		verify(siteService).fetchSiteById(anyInt());
		verifyNoMoreInteractions(affService, siteService, pathHelper);
	}
}