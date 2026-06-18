package com.kmp.aeroparker.application.builder;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.factory.TicketBuilderFactory;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionCustomValue;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class BookingBuilder
{
	private final TicketBuilderFactory ticketFactory;
	private final BookingBuilderObjectFactory objectFactory;

	public SubscriptionBookingRecord build(final int contactId, final SubscriptionBookingData bookingData, final Basket basket)
	{
		SubscriptionBookingRecord bookingRecord = new SubscriptionBookingRecord();
		int affId = bookingData.getAffiliateId();
		String bookingReference = bookingData.getBookingReference();
		Timestamp created = DateUtil.localDateTimeToTimestamp(DateUtil.nowLocalDateTime(bookingData.getTimeZone()));
		SubscriptionBooking booking = objectFactory.buildBooking(affId, created, contactId, basket, bookingReference);

		SubscriptionBookingCustomerDetails bookingCustomerDetails = objectFactory.buildCustomerDetails(bookingData);
		SubscriptionBookingVehicleDetails bookingVehicleDetails = objectFactory.buildVehicleDetails(bookingData);
		SubscriptionBookingReceiptDetails receiptDetails = objectFactory.buildReceiptDetails(bookingData);
		SubscriptionBookingLanguage language = objectFactory.buildLanguage(bookingData);
		List<SubscriptionCustomValue> customValues = objectFactory.buildCustomValues(bookingData);

		SubscriptionBookingItem bookingItem = null;
		IBookingTicket bookingTicket = null;
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		for (SubscriptionPurchaseRequest purchaseRequest : basket.getPurchaseRequestList())
		{
			bookingItem = objectFactory.buildBookingItemList(created, purchaseRequest);
			if (SubscriptionPeriodType.FIXED.equals(purchaseRequest.getPeriodType()))
			{
				bookingTicket = ticketFactory.getInstance(SubscriptionPeriodType.FIXED, SeasonTicketBookingBuilder.class)
						.build(purchaseRequest);
			}
			else
			{
				bookingTicket = ticketFactory.getInstance(SubscriptionPeriodType.RECURRING, RecurringTicketBookingBuilder.class)
						.build(purchaseRequest);
			}
			bookingItemMap.put(bookingItem, bookingTicket);
		}
		bookingRecord.setBooking(booking);
		bookingRecord.setCustomerDetails(bookingCustomerDetails);
		bookingRecord.setSubscriptionBookingVehicleDetails(bookingVehicleDetails);
		bookingRecord.setReceiptDetails(receiptDetails);
		bookingRecord.setBookingItemMap(bookingItemMap);
		bookingRecord.setLanguage(language);
		bookingRecord.setCustomValues(customValues);
		return bookingRecord;
	}
}