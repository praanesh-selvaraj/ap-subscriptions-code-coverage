package com.kmp.aeroparker.application.model.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.processor.SubscriptionInvoiceProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(value = "/subscriptions/{affCode}")
public class SubscriptionInvoice
{
	private BookingService bookingService;
	private SubscriptionInvoiceProcessor invoiceProcessor;

	@GetMapping("/SubscriptionInvoice")
	@ResponseBody
	public ResponseEntity<InputStreamResource> fetchSubscriptionInvoice(
			@RequestParam(value = "encryptedRef", required = false, defaultValue = "") String encryptedRef,
			@RequestParam(value = "invoiceIdentifier", required = false, defaultValue = "") String invoiceIdentifier)
	{
		ResponseEntity<InputStreamResource> invoiceResponse = ResponseEntity.notFound()
				.build();
		SubscriptionBooking subBooking = bookingService.fetchBookingByEncryptedReference(encryptedRef);
		if (subBooking != null)
		{
			File file = invoiceProcessor.processSubscriptionInvoice(subBooking, invoiceIdentifier);
			if (file != null)
			{
				invoiceResponse = displayCompanyInvoiceTemplate(file, invoiceResponse);
			}
			else
			{
				log.info("Invoice was invalid for booking {}, could not display payment invoice",
						subBooking.getReference());
			}
		}
		else
		{
			log.info("No subscription booking could be found for encrypted reference {}", encryptedRef);
		}
		return invoiceResponse;
	}

	/**
	 * Builds the final PDF response entity containing the relevant File information
	 * 
	 * @param downloadedFile
	 * @param invoiceResponse
	 * @return a ResponseEntity containing an Input Stream Resource holding the relevant PDF information
	 */
	private ResponseEntity<InputStreamResource> displayCompanyInvoiceTemplate(File downloadedFile,
			ResponseEntity<InputStreamResource> invoiceResponse)
	{
		HttpHeaders headers = new HttpHeaders();
		try
		{
			FileInputStream fileIn = new FileInputStream(downloadedFile);
			String fileName = downloadedFile.getName();
			headers.add("Content-Disposition", "inline; filename=" + fileName);
			invoiceResponse = ResponseEntity.ok()
					.headers(headers)
					.contentLength(downloadedFile.length())
					.contentType(MediaType.APPLICATION_PDF)
					.body(new InputStreamResource(fileIn));
		}
		catch (FileNotFoundException e)
		{
			log.error("Error building FileInputStream for the downloaded invoice file: {}", e.getMessage(), e);
		}
		finally
		{
			// We always need to clean up the file once any processing has finished
			downloadedFile.delete();
		}
		return invoiceResponse;
	}
}
