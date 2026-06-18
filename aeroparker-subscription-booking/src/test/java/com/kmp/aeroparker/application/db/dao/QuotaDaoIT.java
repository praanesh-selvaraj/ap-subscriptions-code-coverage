package com.kmp.aeroparker.application.db.dao;

import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.util.Arrays;

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
@ContextConfiguration(classes = QuotaDao.class)
public class QuotaDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getQuotaInstance();
	@Autowired
	private QuotaDao dao;

	@Test
	public void testFetchActiveQuotasSubscriptionBySiteId()
	{
		assertEquals(1, dao.fetchActiveQuotasSubscriptionBySiteId(1)
				.size());
	}

	@Test
	public void testFetchQuotasSubscriptionProductProductIdsByQuotaId()
	{
		assertEquals(1, dao.fetchQuotasSubscriptionProductProductIdsByQuotaId(1)
				.size());
	}

	@Test
	public void testFetchSubscriptionProductIdBySiteId()
	{
		assertEquals(1, dao.fetchSubscriptionProductIdBySiteId(1)
				.size());
	}

	@Test
	public void fetchSubscriptionProductIdBySiteId()
	{
		assertEquals(1, dao.fetchActiveQuotasSubscriptionBySiteId(1));
	}

	@Test
	public void testFetchQuotaOccupancy()
	{
		assertEquals(1,
				dao.fetchQuotaOccupancy(Date.valueOf("2024-12-20"), Date.valueOf("2025-12-20"), Arrays.asList(1))
						.intValue());
	}
}
