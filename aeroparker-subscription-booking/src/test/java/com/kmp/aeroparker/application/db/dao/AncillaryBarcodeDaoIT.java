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
@ContextConfiguration(classes = { BookingDao.class })
class AncillaryBarcodeDaoIT
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@Autowired
	private AncillaryBarcodeDao dao;

	@Test
	void test()
	{
		assertThat(dao.fetchAncillaryBarcodeConfigurationBySubscriptionProductId(1)).isNotNull()
				.hasSize(1);
	}
}
