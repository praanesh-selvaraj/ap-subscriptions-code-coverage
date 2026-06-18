package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
@ExtendWith(SpringExtension.class)
@JooqTest
@ContextConfiguration(classes = LanguageDao.class)
@Testcontainers
class LanguageDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@Autowired
	private LanguageDao dao;

	@Test
	void testFetchLanguagesBySiteId()
	{
		assertThat(dao.fetchLanguagesByAffiliateId(17)).hasSize(1);
	}

	@Test
	void testFecthLanguageById()
	{
		assertThat(dao.fetchLanguageById(1)).isNotNull()
				.hasFieldOrPropertyWithValue("languageName", "English (United Kingdom)");
	}

	@Test
	void testFetchLanguageByDisplayCode()
	{
		assertThat(dao.fetchLanguageByDisplayCode("en")).isNotNull()
				.hasFieldOrPropertyWithValue("languageName", "English (United Kingdom)");
	}

	@Test
	void testFetchAffiliateDefaultLanguage()
	{
		assertThat(dao.fetchAffiliateDefaultLanguage(72)).isNotNull()
				.hasFieldOrPropertyWithValue("languageName", "English (United Kingdom)");
	}
}