package com.kmp.aeroparker.application.processor;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@Component
public class CustomerDetailsProcessor implements IProcessor
{
	private final BookingService service;

	public boolean process(final int bookinId, @NonNull final SubscriptionBookingCustomerDetails customerDetails)
	{
		customerDetails.setSubBookingId(bookinId);
		return service.saveSubscriptionBookingCustomerDetails(customerDetails);
	}

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.CUSTOMER_DETAILS;
	}
}