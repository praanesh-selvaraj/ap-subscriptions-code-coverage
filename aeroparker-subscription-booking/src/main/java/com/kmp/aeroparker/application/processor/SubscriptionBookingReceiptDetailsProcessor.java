package com.kmp.aeroparker.application.processor;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.ReceiptDetailsService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@Component
public class SubscriptionBookingReceiptDetailsProcessor implements IProcessor
{
	private final ReceiptDetailsService service;

	public boolean process(final int bookingId, @NonNull final SubscriptionBookingReceiptDetails receiptDetails)
	{
		receiptDetails.setSubBookingId(bookingId);
		return service.saveReceiptDetais(receiptDetails);
	}

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.RECEIPT_DETAILS;
	}
}
