package com.kmp.aeroparker.application.processor;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.builder.SubscriptionGuidBuilder;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuid;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuidBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPurchaseData;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionGuidProcessor implements IProcessor
{
	private final BookingService service;
	private final SubscriptionGuidBuilder builder;

	public boolean process(final int affId, final int bookingId, final String guid, final String customerGuid)
	{
		boolean status = false;
		SubscriptionGuid subscriptionGuid = builder.build(guid);

		if (service.saveSubscriptionGuid(subscriptionGuid))
		{
			log.info("reservation saved");
			SubscriptionPurchaseData purchaseData = service.fetchPurchaseData(affId, customerGuid);
			if (purchaseData != null)
			{
				SubscriptionGuidBooking subscriptionGuidBooking = builder.buildGuidBooking(bookingId, subscriptionGuid.getId(), purchaseData.getId());
				if (service.saveSubscriptionGuidBooking(subscriptionGuidBooking))
				{
					status = true;
				}
			}
		}
		return status;
	}

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.GUID_BOOKING;
	}
}