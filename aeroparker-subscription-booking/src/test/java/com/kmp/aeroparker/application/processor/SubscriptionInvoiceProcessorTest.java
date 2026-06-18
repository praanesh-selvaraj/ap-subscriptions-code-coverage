package com.kmp.aeroparker.application.processor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.manager.AmazonS3FileManager;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.payments.service.PaymentInvoiceService;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentInvoice;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

@ExtendWith(MockitoExtension.class)
class SubscriptionInvoiceProcessorTest
{
	@Mock
	private AmazonS3FileManager s3;
	@Mock
	private PaymentService paymentService;
	@Mock
	private PaymentInvoiceService invoiceService;
	@Mock
	private SubscriptionBooking subBooking;
	@Mock
	private Payments payment;
	@Mock
	private PaymentInvoice invoice;
	@Mock
	private File file;
	@InjectMocks
	private SubscriptionInvoiceProcessor invoiceProcessor;

	@Test
	public void testProcessSubscriptionInvoice() throws IOException
	{
		when(subBooking.getReference()).thenReturn("test");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(payment.getId()).thenReturn(10);
		when(invoiceService.fetchLatestPaymentInvoiceByPaymentId(anyInt())).thenReturn(invoice);
		when(invoice.getS3Key()).thenReturn("123AB");
		when(s3.downloadFile(anyString(), anyString())).thenReturn(file);
		when(file.getName()).thenReturn("Test-name");
		when(file.getPath()).thenReturn("Test-Path.pdf");
		assertNotNull(invoiceProcessor.processSubscriptionInvoice(subBooking, ""));
	}

	@Test
	public void testProcessSubscriptionInvoice_NoPayment() throws IOException
	{
		when(subBooking.getReference()).thenReturn("test");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(null);
		assertNull(invoiceProcessor.processSubscriptionInvoice(subBooking, ""));
	}

	@Test
	public void testProcessSubscriptionInvoice_NoInvoice() throws IOException
	{
		when(subBooking.getReference()).thenReturn("test");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(payment.getId()).thenReturn(10);
		when(invoiceService.fetchLatestPaymentInvoiceByPaymentId(anyInt())).thenReturn(null);
		assertNull(invoiceProcessor.processSubscriptionInvoice(subBooking, ""));
	}

	@Test
	public void testProcessSubscriptionInvoice_NoInvoiceS3Key() throws IOException
	{
		when(subBooking.getReference()).thenReturn("test");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(payment.getId()).thenReturn(10);
		when(invoiceService.fetchLatestPaymentInvoiceByPaymentId(anyInt())).thenReturn(invoice);
		when(invoice.getS3Key()).thenReturn("");
		assertNull(invoiceProcessor.processSubscriptionInvoice(subBooking, ""));
	}

	@Test
	public void testProcessSubscriptionInvoice_NoValidFile() throws IOException
	{
		when(subBooking.getReference()).thenReturn("test");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(payment.getId()).thenReturn(10);
		when(invoiceService.fetchLatestPaymentInvoiceByPaymentId(anyInt())).thenReturn(invoice);
		when(invoice.getS3Key()).thenReturn("123AB");
		when(s3.downloadFile(anyString(), anyString())).thenReturn(null);
		assertNull(invoiceProcessor.processSubscriptionInvoice(subBooking, ""));
	}

	@Test
	public void testProcessSubscriptionInvoice_InvalidFileType() throws IOException
	{
		when(subBooking.getReference()).thenReturn("test");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(payment.getId()).thenReturn(10);
		when(invoiceService.fetchLatestPaymentInvoiceByPaymentId(anyInt())).thenReturn(invoice);
		when(invoice.getS3Key()).thenReturn("123AB");
		when(s3.downloadFile(anyString(), anyString())).thenReturn(file);
		when(file.getPath()).thenReturn("Test-Path.txt");
		assertNull(invoiceProcessor.processSubscriptionInvoice(subBooking, ""));
	}

	@Test
	public void testProcessSubscriptionInvoice_InvoiceGuidProvided() throws IOException
	{
		when(subBooking.getReference()).thenReturn("test");
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(payment);
		when(invoiceService.fetchInvoiceByIdentifier(anyString())).thenReturn(invoice);
		when(invoice.getS3Key()).thenReturn("123AB");
		when(s3.downloadFile(anyString(), anyString())).thenReturn(file);
		when(file.getName()).thenReturn("Test-name");
		when(file.getPath()).thenReturn("Test-Path.pdf");
		assertNotNull(invoiceProcessor.processSubscriptionInvoice(subBooking, "identifier"));
		verifyNoMoreInteractions(invoiceService);
	}
}
