package com.kmp.aeroparker.application.db.service;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.InvoiceDao;
import com.kmp.aeroparker.i18n.stringutil.StringUtil;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Company;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentInvoice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class InvoiceService
{
	private final InvoiceDao dao;

	public Company fetchCompanyBySiteId(int siteId, int productId)
	{
		Company company = null;

		if (siteId > 0 && productId > 0)
		{
			company = dao.fetchCompanyBySiteId(siteId, productId);
		}
		else
		{
			log.debug("Unable to fetch company as site Id or product Id was less than 1");
		}
		return company;
	}

	public boolean savePaymentInvoice(PaymentInvoice paymentInvoice)
	{
		boolean success = false;
		if (paymentInvoice != null)
		{
			success = dao.savePaymentInvoice(paymentInvoice);
		}
		else
		{
			log.debug("Unable to save payment invoice as payment invoice object was null");
		}
		return success;
	}

	public String getNextSequence(int siteId, int productId)
	{
		String sequence = "";

		if (siteId > 0 && productId > 0)
		{
			sequence = dao.getNextSequence(siteId, productId);
		}
		else
		{
			log.debug("Unable to fetch sequence as site Id or product Id was less than 1");
		}
		return sequence;
	}
	
	public boolean saveInvoiceUrl(int subscriptionBookingId, String invoiceUrl)
	{
		boolean success = false;
		if (subscriptionBookingId > 0 && !StringUtil.isNullOrEmpty(invoiceUrl))
		{
			success = dao.saveInvoiceUrl(subscriptionBookingId, invoiceUrl);
		}
		else
		{
			log.debug("Unable to save invoice url as either subscription booking ID is 0 or invoice Url is null");
		}
		return success;
	}
}
