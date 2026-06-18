
package com.kmp.aeroparker.subscription.payments.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.jooq.DSLContext;
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

import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.tables.daos.RefundsDao;
import com.kmp.aeroparker.subscription.payments.tables.pojos.KlixSession;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPaymentAmount;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPaymentVersion;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentTransaction;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsCustomValues;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Refunds;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;

import io.github.benas.randombeans.api.EnhancedRandom;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ContextConfiguration(initializers = { PaymentDaoIT.Initializer.class }, classes = { PaymentDao.class })
@ExtendWith(SpringExtension.class)
@JooqTest
@Testcontainers
class PaymentDaoIT
{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Container
	public static final MySQLContainer mysql = (MySQLContainer) new MySQLContainer("mysql:5.6").withConnectTimeoutSeconds(300)
			.withInitScript("init/payment-dao-IT.sql")
			.withLogConsumer(new Slf4jLogConsumer(log))
			.withStartupTimeout(Duration.ofSeconds(30));

	@Autowired
	private PaymentDao dao;
	@Autowired
	private DSLContext dsl;

	@Test
	void testFetchSelectedPaymentGateway()
	{
		assertThat(dao.fetchSelectedPaymentGateway(1, 0)).hasSize(1)
				.extracting("gatewayType")
				.contains("AFFILIATE");
	}

	@Test
	void testFetchPaymentGatewayTypesById()
	{
		assertThat(dao.fetchPaymentGatewayTypesById(1)).isNotNull()
				.hasFieldOrPropertyWithValue("type", "Braintree");
	}

	@Test
	void testFetchAllCustomFields()
	{
		assertThat(dao.fetchAllCustomFields()).hasSize(27)
				.containsKeys("contactId", "affiliationNumber", "cardExpiryDate", "cardExpiryYear", "customerName", "emailAddress");
	}

	@Test
	void testSaveCustomValues()
	{
		assertThat(dao.fetchPaymentCustomValueByPaymentId(632)).isEmpty();
		PaymentsCustomValues paymentsCustomValues = new PaymentsCustomValues();
		paymentsCustomValues.setField(1);
		paymentsCustomValues.setPaymentId(632);
		paymentsCustomValues.setValue("5018XXXXXX2222");
		dao.saveCustomValues(Arrays.asList(paymentsCustomValues));
		assertThat(dao.fetchPaymentCustomValueByPaymentId(632)).isNotEmpty()
				.hasSize(1);
	}

	@Test
	void testSavePayment()
	{
		assertThat(dao.fetchPaymentByReference("SNNSS108130")).isNull();
		Payments payment = EnhancedRandom.random(Payments.class, "id");
		payment.setCarParkId(1);
		payment.setType(10);
		payment.setReference("SNNSS108130");
		dao.savePayment(payment);
		assertThat(dao.fetchPaymentByReference("SNNSS108130")).isNotNull()
				.hasFieldOrPropertyWithValue("reference", "SNNSS108130")
				.hasFieldOrPropertyWithValue("amount", payment.getAmount());
	}

	@Test
	void testFetchPaymentByReference()
	{
		assertThat(dao.fetchPaymentByReference("DWdff101663")).isNotNull()
				.hasFieldOrPropertyWithValue("reference", "DWdff101663")
				.hasFieldOrPropertyWithValue("amount", "29.56");
	}

	@Test
	void testInsertRefund()
	{
		assertThat(new RefundsDao(dsl.configuration()).fetchByReference("WW100039")).isEmpty();
		Refunds refund = new Refunds();
		refund.setType(1);
		refund.setPaymentId(633);
		refund.setAmount(BigDecimal.TEN.toString());
		refund.setReference("WW100039");
		dao.insertRefund(refund);
		assertThat(new RefundsDao(dsl.configuration()).fetchByReference("WW100039")).hasSize(1);
	}

	@Test
	void testFetchPaymentCustomValueByPaymentId()
	{
		assertThat(dao.fetchPaymentCustomValueByPaymentId(145)).isNotEmpty()
				.hasSize(3);
	}

	@Test
	void testFetchPaymentById()
	{
		assertThat(dao.fetchPaymentById(145)).isNotNull();
	}

	@Test
	void testInsertSubscriptionScheduledRecurringPayment()
	{
		SubscriptionScheduledRecurringPayment scheduledRecurringPayment = new SubscriptionScheduledRecurringPayment();
		scheduledRecurringPayment.setAffiliateId(70);
		scheduledRecurringPayment.setAmount(BigDecimal.TEN);
		scheduledRecurringPayment.setContactId(16962);
		scheduledRecurringPayment.setEnabled(true);
		scheduledRecurringPayment.setLastPaymentDate(null);
		scheduledRecurringPayment.setPaymentId(383);
		scheduledRecurringPayment.setSiteId(105);
		scheduledRecurringPayment.setSubscriptionBookingReference("DWTEST101820");
		scheduledRecurringPayment.setUpcomingPaymentDate(DateUtil.localDateToDate(DateUtil.nowLocalDate("")));
		assertThat(dao.insertSubscriptionScheduledRecurringPayment(scheduledRecurringPayment)).isTrue();
	}

	@Test
	void testFetchRecurringPaymentSentByBookingReference()
	{
		assertThat(dao.fetchRecurringPaymentSentByBookingReference("SNWSC100402")).isNotEmpty();
	}

	@Test
	public void testSaveKlixSession()
	{
		assertTrue(dao.saveKlixSession(mock(KlixSession.class)));
	}

	@Test
	public void testFetchKlixSessionByReferenceAndAffiliateId()
	{
		assertNotNull(dao.fetchKlixSessionByReferenceAndAffiliateId("ref", 1));
	}

	@Test
	public void testFetchAffiliatesbyId()
	{
		assertNotNull(dao.fetchAffiliatesbyId(1));
	}
	
	@Test
	public void testFetchPartialPaymentsByTransactionIdReferenceAndType() throws DataAccessException, SQLException
	{
		assertNotNull(dao.fetchPartialPaymentsByTransactionIdReferenceAndType("txID", "ref", 1));
		assertNull(dao.fetchPartialPaymentsByTransactionIdReferenceAndType("", "ref", 1));
		assertNull(dao.fetchPartialPaymentsByTransactionIdReferenceAndType("txID", "", 1));
		assertNull(dao.fetchPartialPaymentsByTransactionIdReferenceAndType("txID", "ref", 2));
	}
	
	@Test
	public void testSavePartialPayment() throws DataAccessException, SQLException
	{
		PartialPayments partialPayment = new PartialPayments();
		partialPayment.setTransactionId("transactionId");
		partialPayment.setType(15);
		partialPayment.setReference("ref");
		partialPayment.setAmount(BigDecimal.TEN);
		partialPayment.setCreated(Timestamp.valueOf(LocalDateTime.now()));
		partialPayment.setParentPayment(1);
		
		assertTrue(dao.savePartialPayment(partialPayment));
	}
	
	@Test
	public void testSavePartialPaymentAmount()
	{
		PartialPaymentAmount partialPaymentAmount = new PartialPaymentAmount();
		partialPaymentAmount.setAmount("0.00");
		partialPaymentAmount.setReference("reference");
		partialPaymentAmount.setPartialPaymentId(1);
		partialPaymentAmount.setRefundId(1);
		
		assertTrue(dao.savePartialPaymentAmount(partialPaymentAmount));
	}
	
	@Test
	public void testSavePartialPaymentVersion()
	{
		PartialPaymentVersion partialPaymentVersion = new PartialPaymentVersion();
		partialPaymentVersion.setPartialPaymentAmountId(1);
		partialPaymentVersion.setReference("reference");
		partialPaymentVersion.setVersion(1);
		assertTrue(dao.savePartialPaymentVersion(partialPaymentVersion));
	}
	
	@Test
	public void testFetchLatestPartialPaymentAmountIdByReference() throws DataAccessException, SQLException
	{
		assertNotNull(dao.fetchLatestPartialPaymentAmountIdByReference("TEST"));
	}
	
	@Test
	public void testFetchLatestPartialPaymentVersionByReference() throws DataAccessException, SQLException
	{
		assertThat(dao.fetchLatestPartialPaymentVersionByReference("TEST")).isEqualTo(2);
	}
	
	@Test
	public void testSavePaymentCustomValues() throws DataAccessException, SQLException
	{
		PaymentsCustomValues customValues = new PaymentsCustomValues();
		customValues.setValue("testValue");
		customValues.setPaymentId(2);
		customValues.setPartialPaymentId(2);
		
		assertTrue(dao.savePaymentCustomValues(customValues, "test"));
	}
	
	@Test
	public void testFetchAllPartialPaymentsByReferenceAndType() throws DataAccessException, SQLException
	{
		assertFalse(dao.fetchAllPartialPaymentsByReferenceAndType("ref", 1)
				.isEmpty());
	}
	
	@Test
	public void testSavePaymentTransaction()
	{
		PaymentTransaction transaction = new PaymentTransaction();
		transaction.setTransactionId("transactionId2");
		transaction.setCreatedUpdatedDateTime(Timestamp.valueOf("2025-09-05 10:00:00"));
		
		assertTrue(dao.savePaymentTransaction(transaction));
	}
	
	@Test
	public void testSavePaymentTransaction_UpdateOnDuplicate()
	{
		PaymentTransaction transaction = new PaymentTransaction();
		transaction.setTransactionId("transactionId1");
		transaction.setCreatedUpdatedDateTime(Timestamp.valueOf("2025-09-05 11:00:00"));
		
		assertTrue(dao.savePaymentTransaction(transaction));
	}
	
	@Test
	public void testSavePaymentTransaction_Fail()
	{
		PaymentTransaction transaction = new PaymentTransaction();
		
		assertFalse(dao.savePaymentTransaction(transaction));
	}

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
	{
		@Override
		public void initialize(final ConfigurableApplicationContext configurableApplicationContext)
		{
			TestPropertyValues
					.of("spring.datasource.url=" + mysql.getJdbcUrl(), "spring.datasource.username=" + mysql.getUsername(),
							"spring.datasource.password=" + mysql.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
}