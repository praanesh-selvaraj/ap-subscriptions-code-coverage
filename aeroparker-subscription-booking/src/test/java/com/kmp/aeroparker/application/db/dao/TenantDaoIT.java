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

@JooqTest
@Testcontainers
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TenantDao.class)
class TenantDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@Autowired
	private TenantDao dao;

	@Test
	void testFetchByDomain()
	{
		assertThat(dao.fetchByDomain("localhost")).isNotNull()
				.hasFieldOrPropertyWithValue("schema", "kmp_advancebooker");
	}
}