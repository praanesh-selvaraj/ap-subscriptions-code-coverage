package com.kmp.aeroparker.application.builder;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.ISubscriptionBookingItem;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

@Component
public class RecurringTicketBookingBuilder implements ISubscriptionBookingItem
{
	@Override
	public SubscriptionPeriodType getPeriodType()
	{
		return SubscriptionPeriodType.RECURRING;
	}

	public BookingRecurringTicket build(final SubscriptionPurchaseRequest purchaseRequest)
	{
		BookingRecurringTicket bookingRecurringTicket = new BookingRecurringTicket();
		bookingRecurringTicket.setStartDate(DateUtil.localDateToDate(purchaseRequest.getStartDate()));
		bookingRecurringTicket.setMinimumTermDate(DateUtil.localDateToDate(purchaseRequest.getEndDate()));
		bookingRecurringTicket.setMinimumTerm(purchaseRequest.getMinimumTerm()
				.toString());
		bookingRecurringTicket.setPrice(purchaseRequest.getProductPrice());
		return bookingRecurringTicket;
	}
}