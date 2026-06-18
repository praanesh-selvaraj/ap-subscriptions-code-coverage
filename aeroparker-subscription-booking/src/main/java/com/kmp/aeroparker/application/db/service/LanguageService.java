package com.kmp.aeroparker.application.db.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.LanguageDao;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Service
@Slf4j
public class LanguageService
{
	private final LanguageDao dao;

	public List<Languages> fetchLanguagesByAffiliateId(final int affId)
	{
		List<Languages> languages = new ArrayList<>();
		if (affId > 0)
		{
			log.debug("Attempting to fetch languages using site ID {}", affId);
			languages = dao.fetchLanguagesByAffiliateId(affId);
			log.debug("Successfully fetched languages");
		}
		else
		{
			log.info("site ID is not greater than 0, no languages will be fetched");
		}
		return languages;
	}

	public Languages fetchLanguageById(final int defaultLangId)
	{
		Languages languages = null;
		if (defaultLangId > 0)
		{
			log.debug("Attempting to fetch language by ID {}", defaultLangId);
			languages = dao.fetchLanguageById(defaultLangId);
			log.debug("Successfully fetched language with ID {}", defaultLangId);
		}
		else
		{
			log.info("Language ID is not greater than 0, no language will be fetched");
		}
		return languages;
	}

	public Languages fetchLanguageByDisplayCode(final String languageDisplayCode)
	{
		Languages languages = null;
		if (!StringUtil.isEmpty(languageDisplayCode))
		{
			log.debug("Attempting to fetch language by code {}", languageDisplayCode);
			languages = dao.fetchLanguageByDisplayCode(languageDisplayCode);
			log.debug("Successfully fetched language with code {}", languageDisplayCode);
		}
		else
		{
			log.debug("Language code is empty, no language will be fetched");
		}
		return languages;
	}

	public Languages fetchAffiliateDefaultLanguage(final int affId)
	{
		Languages languages = null;
		if (affId > 0)
		{
			log.debug("Attempting to fetch affiliate default language by affiliate ID {}", affId);
			languages = dao.fetchAffiliateDefaultLanguage(affId);
			log.debug("Successfully fetched affilite default language with ID {}", affId);
		}
		else
		{
			log.debug("Affiliate language ID is not greater than 0, no affiliate default language will be fetched");
		}
		return languages;
	}
}