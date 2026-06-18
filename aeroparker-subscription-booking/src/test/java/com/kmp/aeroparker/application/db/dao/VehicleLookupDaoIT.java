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

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { VehicleLookupDao.class })
public class VehicleLookupDaoIT
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getVehiclelookupInstance();
	@Autowired
	private VehicleLookupDao dao;

	@Test
	public void testFetchActiveVehicleLookupAffiliateLoginsByAffiliateId()
	{
		assertThat(dao.fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(211)).isNotNull()
				.hasFieldOrPropertyWithValue("isActive", true)
				.hasFieldOrPropertyWithValue("id", 2);
	}
}
