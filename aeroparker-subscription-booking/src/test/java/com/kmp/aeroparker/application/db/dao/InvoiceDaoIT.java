package com.kmp.aeroparker.application.db.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentInvoice;

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { InvoiceDao.class })
public class InvoiceDaoIT
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getInvoiceInstance();
	@Autowired
	private InvoiceDao dao;

	@Test
	public void testFetchCompanyBySiteId()
	{
		assertNotNull(dao.fetchCompanyBySiteId(1, 1));
	}

	@Test
	public void testSavePaymentInvoice()
	{
		assertTrue(dao.savePaymentInvoice(mock(PaymentInvoice.class)));
	}

	@Test
	public void testGetNextSequence()
	{
		assertEquals("seq1", dao.getNextSequence(1, 1));
	}
}
