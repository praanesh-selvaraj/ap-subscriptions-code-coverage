package com.kmp.aeroparker.application.model;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.kmp.aeroparker.application.db.service.InvoiceService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Company;

@ExtendWith(MockitoExtension.class)
class InvoiceCreatorTest
{
	@InjectMocks
	private InvoiceCreator creator;
	@Mock
	private InvoiceService service;

	@Test
	public void testSavePaymentInvoice()
	{
		when(service.fetchCompanyBySiteId(anyInt(), anyInt())).thenReturn(mock(Company.class));
		when(service.getNextSequence(anyInt(), anyInt())).thenReturn("seq1");
		when(service.savePaymentInvoice(any())).thenReturn(true);

		assertTrue(creator.savePaymentInvoice(1, 1, 1));
	}

	@Test
	public void testSavePaymentInvoice_Null_Company()
	{
		when(service.fetchCompanyBySiteId(anyInt(), anyInt())).thenReturn(null);
		when(service.getNextSequence(anyInt(), anyInt())).thenReturn("seq1");
		when(service.savePaymentInvoice(any())).thenReturn(true);

		assertTrue(creator.savePaymentInvoice(1, 1, 1));
	}
}
