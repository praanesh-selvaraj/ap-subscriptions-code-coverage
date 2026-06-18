package com.kmp.aeroparker.application.builder;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.ISubscriptionBookingItem;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

@Component
public class SeasonTicketBookingBuilder implements ISubscriptionBookingItem
{
	@Override
	public SubscriptionPeriodType getPeriodType()
	{
		return SubscriptionPeriodType.FIXED;
	}

	public BookingSeasonTicket build(final SubscriptionPurchaseRequest purchaseRequest)
	{
		BookingSeasonTicket bookingSeasonTicket = new BookingSeasonTicket();
		bookingSeasonTicket.setStartDate(DateUtil.localDateToDate(purchaseRequest.getStartDate()));
		bookingSeasonTicket.setEndDate(DateUtil.localDateToDate(purchaseRequest.getEndDate()));
		bookingSeasonTicket.setMinimumTerm(purchaseRequest.getMinimumTerm()
				.toString());
		bookingSeasonTicket.setPrice(purchaseRequest.getProductPrice());
		return bookingSeasonTicket;
	}
}