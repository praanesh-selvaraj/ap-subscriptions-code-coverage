package com.kmp.aeroparker.application.db.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;

@Testcontainers
@ContextConfiguration(classes = { ApiUserDao.class })
@JooqTest
@ExtendWith(SpringExtension.class)
class ApiUserDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getInstanceApiUser();
	@Autowired
	private ApiUserDao dao;

	@Test
	public void testFetchAeroparkerApiUserBySchemaAndSiteId()
	{
		assertNotNull(dao.fetchAeroparkerApiUserBySchemaAndSiteId("schema", 1));
	}
}
