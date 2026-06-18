package com.kmp.aeroparker.subscription.payments.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.time.Duration;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ContextConfiguration(initializers = { PaymentInvoiceDaoIT.Initializer.class }, classes = { PaymentInvoiceDao.class })
@ExtendWith(SpringExtension.class)
@JooqTest
@Testcontainers
public class PaymentInvoiceDaoIT
{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Container
	public static final MySQLContainer mysql =
			(MySQLContainer) new MySQLContainer("mysql:5.6").withConnectTimeoutSeconds(300)
					.withInitScript("init/payment-invoice-dao-it.sql")
					.withLogConsumer(new Slf4jLogConsumer(log))
					.withStartupTimeout(Duration.ofSeconds(30));

	@Autowired
	private PaymentInvoiceDao dao;

	@Test
	public void testFetchLatestPaymentInvoiceByPaymentId() throws DataAccessException, SQLException
	{
		assertNotNull(dao.fetchLatestPaymentInvoiceByPaymentId(534209));
	}

	@Test
	public void testFetchLatestPaymentInvoiceByPaymentId_NoMatchingRecord() throws DataAccessException, SQLException
	{
		assertNull(dao.fetchLatestPaymentInvoiceByPaymentId(1));
	}

	@Test
	public void testfetchInvoiceByIdentifier() throws DataAccessException, SQLException
	{
		assertNotNull(dao.fetchInvoiceByIdentifier("123"));
	}

	@Test
	public void testfetchInvoiceByIdentifier_NoMatch() throws DataAccessException, SQLException
	{
		assertNull(dao.fetchInvoiceByIdentifier("invalid"));
	}

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
	{
		@Override
		public void initialize(final ConfigurableApplicationContext configurableApplicationContext)
		{
			TestPropertyValues
					.of("spring.datasource.url=" + mysql.getJdbcUrl(),
							"spring.datasource.username=" + mysql.getUsername(),
							"spring.datasource.password=" + mysql.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
}
