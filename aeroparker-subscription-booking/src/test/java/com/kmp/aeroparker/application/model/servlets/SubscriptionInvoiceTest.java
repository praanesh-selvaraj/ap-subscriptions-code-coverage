package com.kmp.aeroparker.application.model.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.processor.SubscriptionInvoiceProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;

@ExtendWith(MockitoExtension.class)
public class SubscriptionInvoiceTest
{
	@Mock
	private BookingService bookingService;
	@Mock
	private SubscriptionBooking subBooking;
	@Mock
	private File file;
	@Mock
	private SubscriptionInvoiceProcessor invoiceProcessor;
	@InjectMocks
	private SubscriptionInvoice subscriptionInvoice;

	@Test
	public void testFetchSubscriptionInvoice()
	{
		when(bookingService.fetchBookingByEncryptedReference(anyString())).thenReturn(subBooking);
		when(invoiceProcessor.processSubscriptionInvoice(any(), anyString())).thenReturn(file);
		when(file.getName()).thenReturn("Test-name");
		when(file.getPath()).thenReturn("Test-Path.pdf");
		when(file.length()).thenReturn(10L);
		ResponseEntity<InputStreamResource> invalidResponse = ResponseEntity.notFound()
				.build();
		ResponseEntity<InputStreamResource> validResponse = subscriptionInvoice.fetchSubscriptionInvoice("TEST", "");
		assertNotEquals(validResponse, invalidResponse);
	}

	@Test
	public void testFetchSubscriptionInvoice_NoSubscriptionBooking()
	{
		when(bookingService.fetchBookingByEncryptedReference(anyString())).thenReturn(null);
		ResponseEntity<InputStreamResource> invalidResponse = ResponseEntity.notFound()
				.build();
		assertEquals(subscriptionInvoice.fetchSubscriptionInvoice("TEST", ""), invalidResponse);
	}

	@Test
	public void testFetchSubscriptionInvoice_NoValidFile()
	{
		when(bookingService.fetchBookingByEncryptedReference(anyString())).thenReturn(subBooking);
		when(invoiceProcessor.processSubscriptionInvoice(any(), anyString())).thenReturn(null);
		ResponseEntity<InputStreamResource> invalidResponse = ResponseEntity.notFound()
				.build();
		assertEquals(subscriptionInvoice.fetchSubscriptionInvoice("TEST", ""), invalidResponse);
	}
}
