package com.kmp.aeroparker.application.db.dao;

import static org.junit.Assert.assertEquals;

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
@ContextConfiguration(classes = SequenceDao.class)
@JooqTest
@ExtendWith(SpringExtension.class)
class SequenceDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getSequenceInstance();
	@Autowired
	private SequenceDao dao;

	@Test
	void testGetNextId_SubscriptionSequence()
	{
		assertEquals(1, dao.getNextId(1).intValue());
		assertEquals(2, dao.getNextId(1).intValue());
	}
	
	@Test
	void testGetNextId_AbSequence()
	{
		assertEquals(100, dao.getNextId(2).intValue());
		assertEquals(101, dao.getNextId(2).intValue());
	}
}