package com.kmp.aeroparker.application.db.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Company;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentInvoice;
import com.kmp.utils.StringUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class InvoiceDao
{
	private final DSLContext dsl;

	private static final String SUBSRIPTION_COMPANY_TYPE = "Subscription";

	public Company fetchCompanyBySiteId(int siteId, int productId)
	{
		return dsl.select(Tables.COMPANY.fields())
				.from(Tables.COMPANY)
				.join(Tables.COMPANY_SUBSCRIPTION)
				.on(Tables.COMPANY_SUBSCRIPTION.COMPANY_ID.eq(Tables.COMPANY.ID))
				.where(Tables.COMPANY.SITE_ID.eq(siteId))
				.and(Tables.COMPANY.TYPE.eq(SUBSRIPTION_COMPANY_TYPE))
				.and(Tables.COMPANY_SUBSCRIPTION.SUB_PRODUCT_ID.eq(productId))
				.fetchOneInto(Company.class);
	}

	public boolean savePaymentInvoice(PaymentInvoice paymentInvoice)
	{
		return dsl.insertInto(Tables.PAYMENT_INVOICE)
				.set(dsl.newRecord(Tables.PAYMENT_INVOICE, paymentInvoice))
				.execute() > 0;
	}

	public String getNextSequence(int siteId, int productId)
	{
		String sequence = dsl.select(Tables.COMPANY.SEQUENCE_CURRENT)
				.from(Tables.COMPANY)
				.join(Tables.COMPANY_SUBSCRIPTION)
				.on(Tables.COMPANY_SUBSCRIPTION.COMPANY_ID.eq(Tables.COMPANY.ID))
				.where(Tables.COMPANY.SITE_ID.eq(siteId))
				.and(Tables.COMPANY.TYPE.eq(SUBSRIPTION_COMPANY_TYPE))
				.and(Tables.COMPANY_SUBSCRIPTION.SUB_PRODUCT_ID.eq(productId))
				.forUpdate()
				.fetchOneInto(String.class);

		int seq = StringUtil.strToInt(sequence);
		seq++;
		String sequenceStr = StringUtil.intToStr(seq);

		if (sequenceStr.length() != sequence.length())
		{
			sequenceStr = StringUtil.zeroPad(seq, sequence.length());
		}

		dsl.update(Tables.COMPANY.join(Tables.COMPANY_SUBSCRIPTION)
				.on(Tables.COMPANY_SUBSCRIPTION.COMPANY_ID.eq(Tables.COMPANY.ID)))
				.set(Tables.COMPANY.SEQUENCE_CURRENT, sequenceStr)
				.where(Tables.COMPANY.SITE_ID.eq(siteId))
				.and(Tables.COMPANY.TYPE.eq(SUBSRIPTION_COMPANY_TYPE))
				.and(Tables.COMPANY_SUBSCRIPTION.SUB_PRODUCT_ID.eq(productId))
				.execute();

		return sequenceStr;
	}
	
	public boolean saveInvoiceUrl(int subscriptionBookingId, String invoiceUrl)
	{
		return dsl.insertInto(Tables.SUBSCRIPTION_INVOICE_URL)
				.set(Tables.SUBSCRIPTION_INVOICE_URL.SUBSCRIPTION_ID, subscriptionBookingId)
				.set(Tables.SUBSCRIPTION_INVOICE_URL.INVOICE_URL, invoiceUrl)
				.execute() > 0;
	}
}
