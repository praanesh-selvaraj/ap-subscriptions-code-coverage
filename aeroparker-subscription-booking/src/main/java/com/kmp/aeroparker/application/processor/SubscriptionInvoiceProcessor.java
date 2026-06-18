package com.kmp.aeroparker.application.processor;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.manager.AmazonS3FileManager;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.payments.service.PaymentInvoiceService;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentInvoice;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionInvoiceProcessor
{
	private static final String BUCKET_NAME = "aeroparker-pdf";
	private static final String PDF = "pdf";

	private AmazonS3FileManager s3Client;
	private PaymentService paymentService;
	private PaymentInvoiceService invoiceService;

	public File processSubscriptionInvoice(SubscriptionBooking booking, String invoiceIdentifier)
	{
		String reference = booking.getReference();
		Payments payment = paymentService.fetchPaymentByReference(reference);
		File file = null;
		if (payment != null)
		{
			PaymentInvoice invoice = null;
			if (StringUtil.hasLength(invoiceIdentifier))
			{
				invoice = invoiceService.fetchInvoiceByIdentifier(invoiceIdentifier);
			}
			if (invoice == null)
			{
				int paymentId = payment.getId();
				invoice = invoiceService.fetchLatestPaymentInvoiceByPaymentId(paymentId);
			}

			if (invoice != null && StringUtil.hasText(invoice.getS3Key()))
			{
				file = validateSubscriptionInvoice(invoice, file);
			}
			else
			{
				log.info("No valid invoice could be fetched for booking {}", reference);
			}
		}
		else
		{
			log.info("No matching payment for reference {}, could not fetch invoice", reference);
		}
		return file;
	}

	/**
	 * Check that the file returned is a PDF file, otherwise we cannot display the invoice
	 * 
	 * @param paymentInvoice
	 * @param file
	 * @return the file fetched from the S3 bucket, or null if it is invalid
	 */
	private File validateSubscriptionInvoice(PaymentInvoice paymentInvoice, File file)
	{
		File invoiceFile = getFileFromInvoice(paymentInvoice);
		if (invoiceFile != null && invoiceFile.getPath()
				.contains(PDF))
		{
			log.info("{} is a valid payment invoice", invoiceFile.getName());
			file = invoiceFile;
		}
		else
		{
			log.info("File {} was not a valid PDF invoice", paymentInvoice.getS3Key());
		}
		return file;
	}

	/**
	 * Fetches the associated invoice file from AWS S3 and returns this file to be served by the request
	 * 
	 * @param paymentInvoice
	 * @return a new File containing the values fetched from the S3 bucket
	 */
	private File getFileFromInvoice(PaymentInvoice paymentInvoice)
	{
		File file = null;
		String s3Key = paymentInvoice.getS3Key();
		try
		{
			log.info("Downloading pdf ...");
			file = s3Client.downloadFile(BUCKET_NAME, s3Key);
			log.info("Downloaded file using key: {}", s3Key);
		}
		catch (IOException e)
		{
			log.error("Error downloading PDF from S3 using key {}: {}", s3Key, e.getMessage(), e);
		}
		return file;
	}

}
