package com.kmp.aeroparker.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.i18n.db.LanguageFieldsListService;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.l10n.db.LocaliseService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
public class GlobalisationConfig
{
	private final LocaliseService localiseService;
	private final LanguageFieldsListService languageFieldsListService;

	@Bean
	@RequestScope
	public Localise localise()
	{
		return new Localise(localiseService);
	}

	@Bean
	@RequestScope
	public LanguageFieldsList languageFieldsList()
	{
		return new LanguageFieldsList(languageFieldsListService);
	}
}