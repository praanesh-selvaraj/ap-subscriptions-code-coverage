package com.kmp.aeroparker.subscription.payments.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.payments.Tables;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentInvoice;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class PaymentInvoiceDao
{
	private final DSLContext dsl;

	public PaymentInvoice fetchLatestPaymentInvoiceByPaymentId(int paymentId)
	{
		return dsl.selectFrom(Tables.PAYMENT_INVOICE)
				.where(Tables.PAYMENT_INVOICE.PAYMENT_ID.eq(paymentId))
				.orderBy(Tables.PAYMENT_INVOICE.INVOICE_CREATED.desc())
				.limit(1)
				.fetchOneInto(PaymentInvoice.class);
	}

	public PaymentInvoice fetchInvoiceByIdentifier(String invoiceIdentifier)
	{
		return dsl.selectFrom(Tables.PAYMENT_INVOICE)
				.where(Tables.PAYMENT_INVOICE.INVOICE_GUID.eq(invoiceIdentifier))
				.limit(1)
				.fetchOneInto(PaymentInvoice.class);
	}
}
