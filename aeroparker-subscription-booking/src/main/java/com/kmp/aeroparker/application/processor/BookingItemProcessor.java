package com.kmp.aeroparker.application.processor;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BookingItemProcessor implements IProcessor
{
	private final BookingService service;
	private final RecurringTicketProcessor recurringTicketProcessor;
	private final SeasonTicketProcessor seasonTicketProcessor;

	public boolean process(final int affId, final int siteId, final int bookingId, final int contactId, final String bookingReference,
			final Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap)
	{
		boolean status = false;
		for (Map.Entry<SubscriptionBookingItem, IBookingTicket> bookingItemMapEntrySet : bookingItemMap.entrySet())
		{
			SubscriptionBookingItem subscriptionBookingItem = bookingItemMapEntrySet.getKey();
			if (subscriptionBookingItem != null)
			{
				subscriptionBookingItem.setSubBookingId(bookingId);
				if (service.saveSubscriptionBookingItem(subscriptionBookingItem))
				{
					int itemId = subscriptionBookingItem.getId();
					IBookingTicket bookingTicket = bookingItemMapEntrySet.getValue();
					if (bookingTicket != null)
					{
						if (SubscriptionPeriodType.FIXED.equals(bookingTicket.getType()))
						{
							status = seasonTicketProcessor.process(itemId, (BookingSeasonTicket) bookingTicket);
						}
						else
						{
							status = recurringTicketProcessor.process(affId, siteId, contactId, bookingReference, subscriptionBookingItem,
									(BookingRecurringTicket) bookingTicket);
						}
					}
				}
			}
		}
		return status;
	}

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.ITEM_BOOKING;
	}
}