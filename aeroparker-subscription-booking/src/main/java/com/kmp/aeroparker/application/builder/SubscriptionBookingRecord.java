package com.kmp.aeroparker.application.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactHashedPassword;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionCustomValue;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionBookingRecord
{
	private SubscriptionBooking booking;
	private SubscriptionBookingCustomerDetails customerDetails;
	private SubscriptionBookingVehicleDetails subscriptionBookingVehicleDetails;
	private ContactHashedPassword contactHashedPassword;
	private SubscriptionBookingReceiptDetails receiptDetails;
	private Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
	private SubscriptionBookingPayment bookingPayment;
	private List<SubscriptionScheduledRecurringPayment> subscriptionScheduledRecurringPayment = new ArrayList<>();
	private SubscriptionBookingLanguage language;
	private List<SubscriptionCustomValue> customValues;
}