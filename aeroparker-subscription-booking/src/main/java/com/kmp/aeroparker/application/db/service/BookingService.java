package com.kmp.aeroparker.application.db.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.dao.BookingDao;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteSubscriptionRecurringPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingEncrypted;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReservationData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionCustomValue;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionDiscountedRenewal;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuid;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuidBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPurchaseData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class BookingService
{
	private final BookingDao dao;
	private final ReceiptDetailsService receiptDetailsService;
	private final VehicleDetailsService vehicleDetailsService;

	public SubscriptionGuid fetchSubscriptionGuidByGuid(final String guid)
	{
		SubscriptionGuid reservationSubscription = null;
		if (StringUtil.isEmpty(guid))
		{
			log.debug("Guid is empty, subscription guid will not be fetched");
		}
		else
		{
			reservationSubscription = dao.fetchSubscriptionGuidByGuid(guid);
			log.debug("Subscription guid fetched successfully");
		}
		return reservationSubscription;
	}

	public boolean saveBooking(final SubscriptionBooking booking)
	{
		boolean saved = false;

		if (booking != null)
		{
			log.debug("Saving subscription booking");
			dao.saveBooking(booking);
			saved = true;
		}
		log.debug(saved ? "Subscription booking saved" : "Subscription booking not saved");
		return saved;
	}

	public boolean saveSubscriptionGuid(final SubscriptionGuid subscriptionGuid)
	{
		boolean saved = false;
		if (subscriptionGuid != null)
		{
			log.debug("Saving subscription guid");
			dao.saveSubscriptionGuid(subscriptionGuid);
			saved = true;
		}
		log.debug(saved ? "Subscription guid saved" : "Subscription guid not saved");
		return saved;
	}

	public SubscriptionBooking fetchSubscriptionBookingByIdAndAffiliateId(final int bookingId, final int affId)
	{
		SubscriptionBooking booking = null;
		if (bookingId < 1 || affId < 1)
		{
			log.debug("Booking ID or affiliate ID is empty, subscription booking will not be fetched");
		}
		else
		{
			booking = dao.fetchSubscriptionBookingByIdAndAffiliateId(bookingId, affId);
			log.debug("Subscription booking fetched successfully");
		}
		return booking;
	}

	public SubscriptionGuidBooking fetchSubscriptionGuidBookingByGuidId(final int guidId)
	{
		SubscriptionGuidBooking guidBooking = null;
		if (guidId < 1)
		{
			log.debug("Guid ID not valid, Subscription guid booking  will not be fetched");
		}
		else
		{
			guidBooking = dao.fetchSubscriptionGuidBookingByGuidId(guidId);
			log.debug("Subscription guid fetched successfully");
		}
		return guidBooking;
	}

	public boolean saveSubscriptionGuidBooking(final SubscriptionGuidBooking subscriptionGuidBooking)
	{
		boolean saved = false;
		if (subscriptionGuidBooking != null)
		{
			log.debug("Saving subscription guid");
			dao.saveSubscriptionGuidBooking(subscriptionGuidBooking);
			saved = true;
		}
		log.debug(saved ? "Subscription guid saved" : "Subscription guid not saved");
		return saved;
	}

	public SubscriptionBooking fetchBookingByReferenceAndAffiliateId(final String reference, final int affId)
	{
		SubscriptionBooking booking = null;
		if (StringUtil.isEmpty(reference) || affId < 1)
		{
			log.debug("Reference or affiliate id is empty, subscription booking will not be fetched");
		}
		else
		{
			booking = dao.fetchBookingByReferenceAndAffiliateId(reference, affId);
			log.debug("Subscription booking fetched successfully");
		}
		return booking;
	}

	public boolean insertPurchaseData(final int affid, final String customerGuid, final String purchaseData)
	{
		boolean saved = false;
		if (affid <= 0 || StringUtil.isEmpty(purchaseData) || StringUtil.isEmpty(customerGuid))
		{
			log.debug("Purchase data / customer guid is empty, Purchase data will not be inserted");
		}
		else
		{
			log.debug("Saving Subscription purchase data");
			SubscriptionPurchaseData subscriptionPurchaseData = Optional.ofNullable(fetchPurchaseData(affid, customerGuid))
					.orElseGet(() -> new SubscriptionPurchaseData());
			subscriptionPurchaseData.setPurchaseData(purchaseData);
			subscriptionPurchaseData.setCustomerGuid(customerGuid);
			subscriptionPurchaseData.setAffiliateId(affid);
			saved = dao.insertPurchaseData(subscriptionPurchaseData);
		}
		log.debug(saved ? "Subscription purchase data saved" : "Subscription purchase data not saved");
		return saved;
	}

	public SubscriptionPurchaseData fetchPurchaseData(final int affId, final String customerGuid)
	{
		SubscriptionPurchaseData purchaseData = null;
		if (affId <= 0 || StringUtil.isEmpty(customerGuid))
		{
			log.debug("Customer guid is empty, subscription purchase data will not be fetched");
		}
		else
		{
			purchaseData = dao.fetchPurchaseData(affId, customerGuid);
			log.debug("Purchase data fetched successfully");
		}
		return purchaseData;
	}

	public boolean saveSubscriptionBookingCustomerDetails(final SubscriptionBookingCustomerDetails customerDetails)
	{
		boolean saved = false;
		if (customerDetails == null)
		{
			log.debug("Customer details is empty, customer details will not be inserted");
		}
		else
		{
			log.debug("Saving customer details");
			dao.saveSubscriptionBookingCustomerDetails(customerDetails);
			saved = true;
		}
		log.debug(saved ? "Subscription customer details saved" : "Subscription customer details not saved");
		return saved;
	}

	public boolean saveBookingSeasonTicket(final BookingSeasonTicket bookingTicket)
	{
		boolean saved = false;
		if (bookingTicket == null)
		{
			log.debug("Booking season ticket is empty, Booking season ticket will not be inserted");
		}
		else
		{
			log.debug("Saving season ticket");
			dao.saveBookingSeasonTicket(bookingTicket);
			saved = true;
		}
		log.debug(saved ? "Subscription season ticket saved" : "Subscription season ticket not saved");
		return saved;
	}

	public boolean saveSubscriptionBookingItem(final SubscriptionBookingItem subscriptionBookingItem)
	{
		boolean saved = false;
		if (subscriptionBookingItem == null)
		{
			log.debug("Booking item is empty, Booking item will not be inserted");
		}
		else
		{
			log.debug("Saving booking item");
			dao.saveSubscriptionBookingItem(subscriptionBookingItem);
			saved = true;
		}
		log.debug(saved ? "Subscription booking item saved" : "Subscription booking item not saved");
		return saved;
	}

	public SubscriptionBookingCustomerDetails fetchSubscriptionBookingCustomerDetails(final int bookingId)
	{
		SubscriptionBookingCustomerDetails customerDetails = null;
		if (bookingId < 1)
		{
			log.debug("Booking ID not valid, Subscription customer details will not be fetched");
		}
		else
		{
			customerDetails = dao.fetchSubscriptionBookingCustomerDetails(bookingId);
			log.debug("Subscription customer details fetched successfully");
		}
		return customerDetails;
	}

	public List<SubscriptionBookingItem> fetchSubscriptionBookingItem(final int bookingId)
	{
		List<SubscriptionBookingItem> bookingItems = new ArrayList<>();
		if (bookingId < 1)
		{
			log.debug("Booking ID not valid, Subscription booking item will not be fetched");
		}
		else
		{
			bookingItems = dao.fetchSubscriptionBookingItem(bookingId);
			log.debug("Subscription booking item fetched successfully");
		}
		return bookingItems;
	}

	public BookingSeasonTicket fetchSubscriptionBookingSeasonTicket(final int itemId)
	{
		BookingSeasonTicket seasonTicket = null;
		if (itemId < 1)
		{
			log.debug("Subscription Item ID not valid, Subscription booking season ticket will not be fetched");
		}
		else
		{
			seasonTicket = dao.fetchSubscriptionBookingSeasonTicket(itemId);
			log.debug("Subscription booking season ticket fetched successfully");
		}
		return seasonTicket;
	}

	public SubscriptionBookingRecord fetchSubscriptionBooking(final int subscriptionBookingId, final int affId)
	{
		SubscriptionBookingRecord bookingRecord = null;
		SubscriptionBooking booking = fetchSubscriptionBookingByIdAndAffiliateId(subscriptionBookingId, affId);
		if (booking != null)
		{
			// lets build the subscription booking
			int bookingId = booking.getId();
			SubscriptionBookingCustomerDetails customerDetails = fetchSubscriptionBookingCustomerDetails(bookingId);

			Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
			List<SubscriptionBookingItem> bookingItemList = fetchSubscriptionBookingItem(bookingId);
			for (SubscriptionBookingItem bookingItem : bookingItemList)
			{
				int itemId = bookingItem.getId();
				IBookingTicket bookingTicket;
				if (SubscriptionPeriodType.isFixedTicket(bookingItem.getPeriodType()))
				{
					bookingTicket = fetchSubscriptionBookingSeasonTicket(itemId);
				}
				else
				{
					bookingTicket = fetchSubscriptionBookingRecurringTicket(itemId);
				}

				if (bookingTicket != null)
				{
					bookingItemMap.put(bookingItem, bookingTicket);
				}
			}
			bookingRecord = new SubscriptionBookingRecord();
			bookingRecord.setBooking(booking);
			bookingRecord.setCustomerDetails(customerDetails);
			bookingRecord.setBookingItemMap(bookingItemMap);
			bookingRecord.setSubscriptionBookingVehicleDetails(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(bookingId));
			bookingRecord.setBookingPayment(fetchSubscriptionBookingPayment(bookingId));
			bookingRecord.setSubscriptionScheduledRecurringPayment(fetchSubscriptionScheduledRecurringPaymentByBookingId(bookingId));
			bookingRecord.setReceiptDetails(receiptDetailsService.fetchSubscriptionBookingReceiptDetailsByBookingId(bookingId));
		}
		return bookingRecord;
	}

	public BookingRecurringTicket fetchSubscriptionBookingRecurringTicket(final int itemId)
	{
		BookingRecurringTicket recurringTicket = null;
		if (itemId < 1)
		{
			log.debug("Subscription Item ID not valid, Subscription booking recurring ticket will not be fetched");
		}
		else
		{
			recurringTicket = dao.fetchSubscriptionBookingRecurringTicket(itemId);
			log.debug("Subscription booking recurring ticket fetched successfully");
		}
		return recurringTicket;
	}

	public SubscriptionBookingRecord fetchSubscriptionBooking(final String guid, final int affId)
	{
		SubscriptionBookingRecord bookingRecord = null;
		SubscriptionGuid subscriptionGuid = fetchSubscriptionGuidByGuid(guid);
		if (subscriptionGuid != null)
		{
			SubscriptionGuidBooking guidBooking = fetchSubscriptionGuidBookingByGuidId(subscriptionGuid.getId());

			if (guidBooking != null)
			{
				bookingRecord = fetchSubscriptionBooking(guidBooking.getSubscriptionBookingId(), affId);
			}
		}
		return bookingRecord;
	}

	public boolean saveBookingRecurringTicket(final BookingRecurringTicket bookingTicket)
	{
		boolean saved = false;
		if (bookingTicket == null)
		{
			log.debug("Booking recurring ticket is empty, Booking recurring ticket will not be inserted");
		}
		else
		{
			log.debug("Saving recurring ticket");
			saved = dao.saveBookingRecurringTicket(bookingTicket);
		}
		log.debug(saved ? "Subscription recurring ticket saved" : "Subscription recurring ticket not saved");
		return saved;
	}

	public SiteSubscriptionRecurringPayment fetchSiteSubscriptionRecurringPaymentBySiteId(final int siteId)
	{
		SiteSubscriptionRecurringPayment siteSubscriptionRecurringPayment = null;
		if (siteId < 1)
		{
			log.debug("Site ID not valid, subscription recurring payment will not be fetched");
		}
		else
		{
			siteSubscriptionRecurringPayment = dao.fetchSiteSubscriptionRecurringPaymentBySiteId(siteId);
			log.debug("subscription recurring payment fetched successfully");
		}
		return siteSubscriptionRecurringPayment;
	}

	public boolean insertSiteSubscriptionRecurringPayment(final SiteSubscriptionRecurringPayment siteSubscriptionRecurringPayment)
	{
		boolean saved = false;
		if (siteSubscriptionRecurringPayment == null)
		{
			log.debug("Site subscription recurring payment is null, subscription recurring payment will not be inserted");
		}
		else
		{
			log.debug("Saving site subscription recurring payment");
			saved = dao.insertSiteSubscriptionRecurringPayment(siteSubscriptionRecurringPayment);
		}
		log.debug(saved ? "Site subscription recurring payment saved" : "subscription recurring payment not saved");
		return saved;
	}

	public boolean insertSubscriptionBookingPayment(final SubscriptionBookingPayment bookingPayment)
	{
		boolean saved = false;
		if (bookingPayment != null)
		{
			saved = dao.insertSubscriptionBookingPayment(bookingPayment);
		}
		return saved;
	}

	public SubscriptionBookingPayment fetchSubscriptionBookingPayment(final int subBookingId)
	{
		SubscriptionBookingPayment bookingPayment = null;
		if (subBookingId > 0)
		{
			bookingPayment = dao.fetchSubscriptionBookingPayment(subBookingId);
			log.debug("subscription booking payment fetched successfully");
		}
		else
		{
			log.debug("Subscription booking ID not valid, subscription booking payment will not be fetched");
		}
		return bookingPayment;
	}

	public List<SubscriptionScheduledRecurringPayment> fetchSubscriptionScheduledRecurringPaymentByBookingId(final int subBookingId)
	{
		List<SubscriptionScheduledRecurringPayment> subscriptionScheduledRecurringPayments = new ArrayList<>();
		if (subBookingId > 0)
		{
			subscriptionScheduledRecurringPayments = dao.fetchSubscriptionScheduledRecurringPaymentByBookingId(subBookingId);
			log.debug("subscription scheduled recurring payment fetched successfully");
		}
		else
		{
			log.debug("Subscription booking ID not valid, subscription scheduled recurring payment will not be fetched");
		}
		return subscriptionScheduledRecurringPayments;
	}
	
	public List<SubscriptionAllBookingData> fetchAllSubscriptionBookingDataByReferenceAndEmail(final String bookingReference, final String email)
	{
		List<SubscriptionAllBookingData> bookingData = new ArrayList<>();
		if (!StringUtil.isEmpty(bookingReference) && !StringUtil.isEmpty(email))
		{
			bookingData = dao.fetchAllSubscriptionBookingDataByReferenceAndEmail(bookingReference, email);
			log.debug("All subscription booking data fetched successfully");
		}
		else
		{
			log.debug("Unable to fetch booking data. Either the booking reference or email was not valid");
		}
		
		return bookingData;
	}

	public boolean saveReservationData(SubscriptionBookingReservationData reservationData)
	{
		boolean success = false;
		if (reservationData != null)
		{
			success = dao.saveReservationData(reservationData);
		}
		else
		{
			log.debug("Unable to save reservation data as SubscriptionBookingReservationData object was null");
		}
		return success;
	}

	public SubscriptionBookingReservationData fetchReservationDataByGuid(String guid)
	{
		SubscriptionBookingReservationData reservationData = null;
		if (!StringUtil.isEmpty(guid))
		{
			reservationData = dao.fetchReservationDataByGuid(guid);
		}
		else
		{
			log.debug("Unable to fetch reservation data as guid is null or empty");
		}
		return reservationData;
	}
	
	public boolean saveLanguage(final SubscriptionBookingLanguage bookingLanguage)
	{
		boolean saved = false;
		if (bookingLanguage != null)
		{
			saved = dao.saveLanguage(bookingLanguage);
		}
		else
		{
			log.debug("Booking language is null, langauge will not be saved");
		}
		return saved;
	}

	public SubscriptionDiscountedRenewal fetchSubscriptionDiscountedRenewalProductId(int productId)
	{
		SubscriptionDiscountedRenewal renewal = null;

		if (productId > 0)
		{
			renewal = dao.fetchSubscriptionDiscountedRenewalProductId(productId);
		}
		else
		{
			log.debug("Unable to fetch Subscription Discounted Renewal object as productId was less than 1");
		}

		return renewal;
	}

	public SubscriptionBookingDetails fetchSubscriptionBookingDetailsByEmail(String email)
	{
		SubscriptionBookingDetails details = null;

		if (!StringUtil.isEmpty(email))
		{
			details = dao.fetchSubscriptionDetailsByEmail(email);
		}
		else
		{
			log.debug("Unable to fetch Subscription booking details as email was null or empty");
		}
		return details;
	}

	public String fetchFeatureFlagValueByKey(String key)
	{
		String values = "";

		if (!StringUtil.isEmpty(key))
		{
			values = dao.fetchFeatureFlagValueByKey(key);
		}
		else
		{
			log.debug("Unable to fetch feature flag as key is null or empty");
		}
		return values;
	}

	public boolean saveEncryptedReference(SubscriptionBookingEncrypted subscriptionBookingEncrypted)
	{
		boolean saved = false;
		if (subscriptionBookingEncrypted != null)
		{
			saved = dao.saveEncryptedReference(subscriptionBookingEncrypted);
		}
		else
		{
			log.debug("Unable to save as SubscriptionBookingEncrypted object is null");
		}
		return saved;
	}

	public SubscriptionBooking fetchBookingByEncryptedReference(final String reference)
	{
		SubscriptionBooking subscriptionBooking = null;
		if (StringUtil.isEmpty(reference))
		{
			log.debug("Encrypted booking reference is empty, cannot fetch a matching subscription booking");
		}
		else
		{
			subscriptionBooking = dao.fetchBookingByEncryptedReference(reference);
			log.debug("Subscription booking fetched successfully");
		}
		return subscriptionBooking;
	}

	public String fetchEncryptedReferenceByBookingId(final int bookingId)
	{
		String encryptedReference = "";
		if (bookingId > 1)
		{
			encryptedReference = dao.fetchEncryptedReferenceByBookingId(bookingId);
		}
		else
		{
			log.debug("Could not fetch encrypted reference as the subscription booking ID was invalid");
		}
		return encryptedReference;
	}

	public boolean saveSubscriptionCustomValues(List<SubscriptionCustomValue> customValues)
	{
		boolean saved = true;
		if (customValues == null || customValues.isEmpty())
		{
			saved = false;
			log.debug("Could not save/update subscription custom values. Custom values were null or empty.");
		}
		else
		{
			for (SubscriptionCustomValue subscriptionCustomValue : customValues)
			{
				String key = subscriptionCustomValue.getCustomKey();
				int bookingId = subscriptionCustomValue.getBookingId();
				if (bookingId < 1)
				{
					log.debug("Could not save subscription booking value, booking id was less than 1.");
					saved = false;
				}
				else
				{
					saved &= dao.saveSubscriptionCustomValue(key, subscriptionCustomValue.getValue(), bookingId);
				}
			}
		}
		return saved;
	}
	
	public List<SubscriptionAllBookingData> fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(final String bookingReference, final String email)
	{
		List<SubscriptionAllBookingData> bookingData = new ArrayList<>();
		if (StringUtils.hasText(bookingReference) && StringUtils.hasText(email))
		{
			bookingData = dao.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(bookingReference, email);
			log.debug("All subscription booking data fetched successfully");
		}
		else
		{
			log.debug("Unable to fetch booking data. Either the booking reference or email was not valid");
		}
		
		return bookingData;
	}

	public boolean saveSubscriptionBookingMembership(int bookingId, String membershipId)
	{
		boolean success = false;
		if (bookingId < 1)
		{
			log.debug("Could not save Subscription Booking Membership as booking id is less than 1");
		}
		else if (!StringUtils.hasText(membershipId))
		{
			log.debug("Could not save Subscription Booking Membership as booking membership Id is null or empty");
		}
		else
		{
			success = dao.saveSubscriptionBookingMembership(bookingId, membershipId);
		}
		return success;
	}

	public SubscriptionBooking fetchBookingByReferenceAffiliateIdAndEmail(String reference, int affiliateId, String email)
	{
		SubscriptionBooking booking = null;
		if (!StringUtils.hasText(reference) || !StringUtils.hasText(email) || affiliateId < 1)
		{
			log.debug("Reference or affiliate ID or email is empty, subscription booking will not be fetched");
		}
		else
		{
			booking = dao.fetchBookingByReferenceAffiliateIdAndEmail(reference, affiliateId, email);
		}
		return booking;
	}

	public String fetchConfirmationGuidByCustomerGuid(final String customerGuid)
	{
		if (StringUtil.isNullOrEmpty(customerGuid))
		{
			log.warn(
					"Unwilling to fetch confirmation GUID by customer GUID as the provided customer GUID is null/empty.");
			return null;
		}

		return dao.fetchConfirmationGuidByCustomerGuid(customerGuid);
	}
}