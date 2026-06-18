package com.kmp.aeroparker.subscription.payments.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.dao.PaymentInvoiceDao;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentInvoice;

@ExtendWith(MockitoExtension.class)
public class PaymentInvoiceServiceTest
{
	@Mock
	private PaymentInvoiceDao dao;
	@Mock
	private PaymentInvoice paymentInvoice;
	@InjectMocks
	private PaymentInvoiceService service;

	@Test
	public void testFetchLatestPaymentInvoiceByPaymentId() throws DataAccessException, SQLException
	{
		when(dao.fetchLatestPaymentInvoiceByPaymentId(anyInt())).thenReturn(paymentInvoice);
		assertNotNull(service.fetchLatestPaymentInvoiceByPaymentId(10));
	}

	@Test
	public void testFetchLatestPaymentInvoiceByPaymentId_InvalidId() throws DataAccessException, SQLException
	{
		assertNull(service.fetchLatestPaymentInvoiceByPaymentId(0));
	}

	@Test
	public void testFetchInvoiceByIdentifier()
	{
		when(dao.fetchInvoiceByIdentifier(any())).thenReturn(mock(PaymentInvoice.class));
		assertNotNull(service.fetchInvoiceByIdentifier("123"));
	}

	@Test
	public void testFetchInvoiceByIdentifier_EmptyString()
	{
		assertNull(service.fetchInvoiceByIdentifier(""));
		verifyNoInteractions(dao);
	}

	@Test
	public void testFetchInvoiceByIdentifier_WhiteSpace()
	{
		assertNull(service.fetchInvoiceByIdentifier("  "));
		verifyNoInteractions(dao);
	}

	@Test
	public void testFetchInvoiceByIdentifier_NullString()
	{
		assertNull(service.fetchInvoiceByIdentifier(null));
		verifyNoInteractions(dao);
	}
}
