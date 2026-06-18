package com.kmp.aeroparker.application.db.service;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.ReceiptDetailsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class ReceiptDetailsService
{
	private final ReceiptDetailsDao dao;

	public boolean saveReceiptDetais(final SubscriptionBookingReceiptDetails receiptDetails)
	{
		boolean saved = false;
		if (receiptDetails != null)
		{
			saved = dao.saveReceiptDetails(receiptDetails);
		}
		else
		{
			log.debug("Receipt Details are null, Receipt Details will not be saved");
		}
		return saved;
	}

	public SubscriptionBookingReceiptDetails fetchSubscriptionBookingReceiptDetailsByBookingId(final int subBookingId)
	{
		SubscriptionBookingReceiptDetails subscriptionBookingReceiptDetails = null;
		if (subBookingId > 0)
		{
			subscriptionBookingReceiptDetails = dao.fetchSubscriptionBookingReceiptDetailsByBookingId(subBookingId);
			log.debug("subscription booking reciept details fetched successfully");
		}
		else
		{
			log.debug("Subscription booking ID not valid, subscription reciept details will not be fetched");
		}
		return subscriptionBookingReceiptDetails;
	}
}