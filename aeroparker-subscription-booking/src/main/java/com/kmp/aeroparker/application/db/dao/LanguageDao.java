package com.kmp.aeroparker.application.db.dao;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.tables.AffiliatesLanguages;
import com.kmp.aeroparker.subscription.payments.Tables;
import com.kmp.aeroparker.subscription.payments.tables.daos.LanguagesDao;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.records.LanguagesRecord;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class LanguageDao
{
	private final DSLContext dsl;

	public List<Languages> fetchLanguagesByAffiliateId(final int affId)
	{
		SelectConditionStep<LanguagesRecord> sql = dsl.selectFrom(Tables.LANGUAGES)
				.where(Tables.LANGUAGES.ID.in(dsl.select(AffiliatesLanguages.AB_AFFILIATES_LANGUAGES.LANGUAGEID)
						.from(AffiliatesLanguages.AB_AFFILIATES_LANGUAGES)
						.where(AffiliatesLanguages.AB_AFFILIATES_LANGUAGES.AFFILIATEID.eq(affId))));
		return sql.orderBy(Tables.LANGUAGES.LANGUAGENAME)
				.fetchInto(Languages.class);
	}

	public Languages fetchLanguageById(final int langId)
	{
		return new LanguagesDao(dsl.configuration()).fetchById(langId)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public Languages fetchLanguageByDisplayCode(final String languageDisplayCode)
	{
		return new LanguagesDao(dsl.configuration()).fetchByDisplaycode(languageDisplayCode)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public Languages fetchAffiliateDefaultLanguage(final int affId)
	{
		return dsl.selectFrom(Tables.LANGUAGES)
				.where(Tables.LANGUAGES.ID.in(dsl.select(AffiliatesLanguages.AB_AFFILIATES_LANGUAGES.LANGUAGEID)
						.from(AffiliatesLanguages.AB_AFFILIATES_LANGUAGES)
						.where(AffiliatesLanguages.AB_AFFILIATES_LANGUAGES.AFFILIATEID.eq(affId)
								.and(AffiliatesLanguages.AB_AFFILIATES_LANGUAGES.PRIMARYLANGUAGE.eq(1)))))
				.fetchOneInto(Languages.class);
	}
}