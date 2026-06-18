package com.kmp.aeroparker.application.web;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.model.AffiliatesCustomFooterPages;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;

@ExtendWith(MockitoExtension.class)
class ContentControllerTest
{
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private AffiliateService service;

	@InjectMocks
	private ContentController controller;

	@Test
	void testGetContent()
	{
		AffiliatesCustomFooterPages affiliatesCustomFooterPages = mock(AffiliatesCustomFooterPages.class);
		when(affiliatesCustomFooterPages.getContent()).thenReturn("<p>content</p>");
		when(affiliatesCustomFooterPages.getLinkTitle()).thenReturn("title");
		when(service.fetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle(anyInt(), anyInt(), anyString()))
				.thenReturn(affiliatesCustomFooterPages);
		controller.getContent(mock(Model.class), "test");
		verify(service).fetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle(anyInt(), anyInt(), anyString());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testGetContent_AffiliatesCustomFooterPages_Null()
	{
		when(service.fetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle(anyInt(), anyInt(), anyString())).thenReturn(null);
		controller.getContent(mock(Model.class), "test");
		verify(service).fetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle(anyInt(), anyInt(), anyString());
		verifyNoMoreInteractions(service);
	}
}