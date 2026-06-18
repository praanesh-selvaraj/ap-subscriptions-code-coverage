package com.kmp.aeroparker.application.builder;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.model.ConfirmationDetails;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.application.model.interfaces.IBuilder;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ConfirmationBuilder implements IBuilder
{
	private final ConfirmationProductDetailsBuilder productDetailsBuilder;
	private final ConfirmationDetailsBuilder detailsBuilder;

	public ConfirmationDetails build(final SubscriptionBookingQuery bookingQuery,
			final SubscriptionBookingRecord bookingRecord, final HashMap<Integer, String> confirmationMessages)
	{
		ConfirmationDetails confirmationDetails = new ConfirmationDetails();
		Affiliates affiliate = bookingQuery.getAffiliate();

		if (bookingRecord != null)
		{
			confirmationDetails = detailsBuilder.build(bookingRecord, affiliate, confirmationMessages);
			Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = bookingRecord.getBookingItemMap();

			if (!bookingItemMap.isEmpty())
			{
				productDetailsBuilder.build(bookingItemMap, confirmationDetails);
			}
		}
		return confirmationDetails;
	}

	@Override
	public BuilderType getType()
	{
		return BuilderType.CONFIRMATION;
	}
}