package com.kmp.aeroparker.application.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.i18n.db.LanguageFieldsListService;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.l10n.db.LocaliseService;

@ExtendWith(MockitoExtension.class)
class GlobalisationConfigTest
{
	@Mock
	private LocaliseService localiseService;
	@Mock
	private LanguageFieldsListService languageFieldsListService;
	@InjectMocks
	private GlobalisationConfig config;

	@Test
	void testLanguageFieldsList()
	{
		assertThat(config.localise()).isNotNull()
				.isInstanceOf(Localise.class);
	}

	@Test
	void testGlobalisationConfig()
	{
		assertThat(config.languageFieldsList()).isNotNull()
				.isInstanceOf(LanguageFieldsList.class);
	}
}