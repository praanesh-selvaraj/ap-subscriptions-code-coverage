package com.kmp.aeroparker.application.model;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.InvoiceService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Company;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentInvoice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class InvoiceCreator
{
	private final InvoiceService invoiceService;

	private static final int NEW_INVOICE = 1;

	public boolean savePaymentInvoice(int paymentId, int siteId, int productId)
	{
		log.info("Creating invoice for payment id {}", paymentId);
		Company company = invoiceService.fetchCompanyBySiteId(siteId, productId);

		String invoiceSequence = invoiceService.getNextSequence(siteId, productId);
		String invoiceReference = (company != null ? company.getSequencePrefix() : "") + invoiceSequence;
		log.info("Generated invoice reference {}", invoiceReference);

		PaymentInvoice paymentInvoice = new PaymentInvoice();
		paymentInvoice.setPaymentId(paymentId);
		paymentInvoice.setReference(invoiceReference);
		paymentInvoice.setType(NEW_INVOICE);

		return invoiceService.savePaymentInvoice(paymentInvoice);
	}
}
