package com.kmp.aeroparker.subscription.payments.service;

import com.kmp.aeroparker.subscription.string.utils.StringUtil;
import org.springframework.stereotype.Service;

import com.kmp.aeroparker.subscription.payments.dao.PaymentInvoiceDao;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentInvoice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentInvoiceService
{
	private final PaymentInvoiceDao dao;

	public PaymentInvoice fetchLatestPaymentInvoiceByPaymentId(int id)
	{
		PaymentInvoice invoice = null;
		if (id > 0)
		{
				invoice = dao.fetchLatestPaymentInvoiceByPaymentId(id);
		}
		else
		{
			log.info("The payment id was invalid, could not fetch latest payment invoice");
		}
		return invoice;
	}

	public PaymentInvoice fetchInvoiceByIdentifier(String invoiceIdentifier)
	{
		PaymentInvoice invoice = null;
		if (StringUtil.hasLength(invoiceIdentifier) && StringUtil.hasText(invoiceIdentifier))
		{
			invoice = dao.fetchInvoiceByIdentifier(invoiceIdentifier);
		}
		else
		{
			log.info("The invoiceIdentifier is invalid, could not fetch payment invoice using: {}", invoiceIdentifier);
		}
		return invoice;
	}
}
