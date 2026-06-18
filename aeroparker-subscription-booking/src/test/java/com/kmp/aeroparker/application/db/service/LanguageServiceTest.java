package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.LanguageDao;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;

@ExtendWith(MockitoExtension.class)
class LanguageServiceTest
{
	@Mock
	private LanguageDao dao;
	@InjectMocks
	private LanguageService service;

	@Test
	void testFetchLanguagesByAffiliateId()
	{
		List<Languages> languages = new ArrayList<>();
		languages.add(mock(Languages.class));
		when(dao.fetchLanguagesByAffiliateId(anyInt())).thenReturn(languages);
		assertThat(service.fetchLanguagesByAffiliateId(1)).isNotEmpty()
				.hasOnlyElementsOfType(Languages.class);
	}

	@Test
	void testFetchLanguagesByAffiliateId_AffiliateId_Zero()
	{
		assertThat(service.fetchLanguagesByAffiliateId(0)).isEmpty();
	}

	@Test
	void testFecthLanguageById()
	{
		when(dao.fetchLanguageById(anyInt())).thenReturn(mock(Languages.class));
		assertThat(service.fetchLanguageById(1)).isNotNull()
				.isInstanceOf(Languages.class);
	}

	@Test
	void testFecthLanguageById_Incorrect_ID()
	{
		assertThat(service.fetchLanguageById(0)).isNull();
	}

	@Test
	void testFetchLanguageByDisplayCode()
	{
		Languages language = mock(Languages.class);
		when(dao.fetchLanguageByDisplayCode(anyString())).thenReturn(language);
		assertThat(service.fetchLanguageByDisplayCode("en")).isNotNull()
				.isInstanceOf(Languages.class);
	}

	@Test
	void testFetchLanguageByDisplayCode_Empty()
	{
		assertThat(service.fetchLanguageByDisplayCode("")).isNull();
	}

	@Test
	void testFetchAffiliateDefaultLanguage()
	{
		Languages language = mock(Languages.class);
		when(dao.fetchAffiliateDefaultLanguage(anyInt())).thenReturn(language);
		assertThat(service.fetchAffiliateDefaultLanguage(1)).isNotNull()
				.isInstanceOf(Languages.class);
	}

	@Test
	void testFetchAffiliateDefaultLanguage_Zero()
	{
		assertThat(service.fetchAffiliateDefaultLanguage(0)).isNull();
	}
}