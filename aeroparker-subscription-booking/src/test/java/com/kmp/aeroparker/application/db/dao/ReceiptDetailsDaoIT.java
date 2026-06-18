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
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ReceiptDetailsDao.class })
public class ReceiptDetailsDaoIT
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@Autowired
	private ReceiptDetailsDao dao;

	@Test
	void testSaveSubscriptionBookingReceiptDetails()
	{
		SubscriptionBookingReceiptDetails receiptDetails = new SubscriptionBookingReceiptDetails();
		receiptDetails.setSubBookingId(63);
		receiptDetails.setCompanyName("Test Name");
		receiptDetails.setTaxIdentificationNumber("1234");
		assertThat(dao.saveReceiptDetails(receiptDetails)).isTrue();
	}

	@Test
	void testFetchSubscriptionBookingVehicleDetailsByBookingId()
	{
		assertThat(dao.fetchSubscriptionBookingReceiptDetailsByBookingId(219)).isNotNull()
				.hasFieldOrPropertyWithValue("companyName", "Test");
	}
}