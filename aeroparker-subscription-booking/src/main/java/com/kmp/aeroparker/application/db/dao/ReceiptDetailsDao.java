package com.kmp.aeroparker.application.db.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionBookingReceiptDetailsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class ReceiptDetailsDao
{
	private final DSLContext dsl;
	
	public boolean saveReceiptDetails(final SubscriptionBookingReceiptDetails receiptDetails)
	{
		return receiptDetails.save(Tables.SUBSCRIPTION_BOOKING_RECEIPT_DETAILS, dsl);
	}

	public SubscriptionBookingReceiptDetails fetchSubscriptionBookingReceiptDetailsByBookingId(final int subBookingId)
	{
		return new SubscriptionBookingReceiptDetailsDao(dsl.configuration()).fetchBySubBookingId(subBookingId)
				.stream()
				.findFirst()
				.orElse(null);
	}
}