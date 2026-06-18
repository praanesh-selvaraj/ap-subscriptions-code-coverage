package com.kmp.aeroparker.application.db.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.InvoiceDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Company;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentInvoice;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest
{
	@InjectMocks
	private InvoiceService service;
	@Mock
	private InvoiceDao dao;

	@Test
	public void testFetchCompanyBySiteId()
	{
		when(dao.fetchCompanyBySiteId(anyInt(), anyInt())).thenReturn(mock(Company.class));

		assertNotNull(service.fetchCompanyBySiteId(1, 1));
	}

	@Test
	public void testFetchCompanyBySiteId_0_siteId()
	{
		assertNull(service.fetchCompanyBySiteId(0, 1));
	}

	@Test
	public void testFetchCompanyBySiteId_0_productId()
	{
		assertNull(service.fetchCompanyBySiteId(1, 0));
	}

	@Test
	public void testSavePaymentInvoice()
	{
		when(dao.savePaymentInvoice(any())).thenReturn(true);

		assertTrue(service.savePaymentInvoice(mock(PaymentInvoice.class)));
	}

	@Test
	public void testSavePaymentInvoice_Null_PaymentInvoice()
	{
		assertFalse(service.savePaymentInvoice(null));
	}

	@Test
	public void testGetNextSequence()
	{
		when(dao.getNextSequence(anyInt(), anyInt())).thenReturn("seq1");

		assertEquals("seq1", service.getNextSequence(1, 1));
	}

	@Test
	public void testGetNextSequence_0_SiteId()
	{
		assertEquals("", service.getNextSequence(0, 1));
	}

	@Test
	public void testGetNextSequence_0_ProductId()
	{
		assertEquals("", service.getNextSequence(1, 0));
	}

	@Test
	public void testSaveInvoiceUrl()
	{
		when(dao.saveInvoiceUrl(anyInt(), anyString())).thenReturn(true);
		assertTrue(service.saveInvoiceUrl(1, "http://example.com/invoice"));
	}
}
