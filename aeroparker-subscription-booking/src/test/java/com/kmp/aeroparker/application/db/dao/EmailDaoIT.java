package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.application.model.enums.SubscriptionEmailType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.EmailQueueDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.EmailQueue;

import io.github.benas.randombeans.api.EnhancedRandom;

@Testcontainers
@ContextConfiguration(classes = { EmailDao.class })
@JooqTest
@ExtendWith(SpringExtension.class)
class EmailDaoIT
{
	@Container
	private static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@Autowired
	private EmailDao dao;
	@Autowired
	private DSLContext dsl;

	@Test
	void testFetchSubscriptionEmailBySiteAndType()
	{
		assertThat(dao.fetchSubscriptionEmailBySiteAndType(17, SubscriptionEmailType.CONFIRMATION.toString()))
				.isNotNull()
				.hasFieldOrPropertyWithValue("type", "CONFIRMATION");
	}

	@Test
	void testFetchSubscriptionEmailAppearanceByEmailIdAndLanguageId()
	{
		assertThat(dao.fetchSubscriptionEmailAppearanceByEmailIdAndLanguageId(8, 1)).isNotNull()
				.hasFieldOrPropertyWithValue("senderName", "Notice  of termination");
	}

	@Test
	void testAddToQueue()
	{
		assertThat(new EmailQueueDao(dsl.configuration()).fetchBySiteid(17)).hasSize(3);
		EmailQueue emailQueue = EnhancedRandom.random(EmailQueue.class, "id");
		emailQueue.setSiteId(17);
		dao.addToQueue(emailQueue);
		assertThat(new EmailQueueDao(dsl.configuration()).fetchBySiteid(17)).hasSize(4);
	}

	@Test
	void TestFetchActivateAccountEmail()
	{
		assertThat(dao.fetchActivateAccountEmail(8, 7)).hasFieldOrPropertyWithValue("senderName", "testus");
	}

	@Test
	void TestFetchEmails()
	{
		assertThat(dao.fetchEmails(162)
				.get(0)).hasFieldOrPropertyWithValue("senderName", "Leeds Bradford Airport");
	}
}