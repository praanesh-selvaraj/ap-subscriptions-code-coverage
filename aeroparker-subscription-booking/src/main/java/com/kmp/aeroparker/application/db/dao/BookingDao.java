package com.kmp.aeroparker.application.db.dao;

import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SiteSubscriptionRecurringPaymentDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionBookingCustomerDetailsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionBookingDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionBookingItemDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionBookingPaymentDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionBookingSeasonTicketDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionGuidBookingDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionGuidDao;
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
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionDiscountedRenewal;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuid;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuidBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPurchaseData;
import com.kmp.aeroparker.subscription.payments.tables.daos.SubscriptionScheduledRecurringPaymentDao;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class BookingDao
{
	private final DSLContext dsl;

	public void saveBooking(final SubscriptionBooking booking)
	{
		SubscriptionBookingDao bookingDao = new SubscriptionBookingDao(dsl.configuration());
		bookingDao.insert(booking);
	}

	public SubscriptionGuid fetchSubscriptionGuidByGuid(final String guid)
	{
		return new SubscriptionGuidDao(dsl.configuration()).fetchByGuid(guid)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public void saveSubscriptionGuid(final SubscriptionGuid subscriptionGuid)
	{
		SubscriptionGuidDao subscriptionGuidDao = new SubscriptionGuidDao(dsl.configuration());
		subscriptionGuidDao.insert(subscriptionGuid);
	}

	public SubscriptionBooking fetchSubscriptionBookingByIdAndAffiliateId(final int bookingId, final int affId)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_BOOKING)
				.where(Tables.SUBSCRIPTION_BOOKING.AFFILIATE_ID.eq(affId)
						.and(Tables.SUBSCRIPTION_BOOKING.ID.eq(bookingId)))
				.fetchOneInto(SubscriptionBooking.class);
	}

	public void saveSubscriptionGuidBooking(final SubscriptionGuidBooking subscriptionGuidBooking)
	{
		SubscriptionGuidBookingDao guidBookingDao = new SubscriptionGuidBookingDao(dsl.configuration());
		guidBookingDao.insert(subscriptionGuidBooking);
	}

	public SubscriptionGuidBooking fetchSubscriptionGuidBookingByGuidId(final int guidId)
	{
		return new SubscriptionGuidBookingDao(dsl.configuration()).fetchBySubscriptionGuidId(guidId)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public SubscriptionBooking fetchBookingByReferenceAndAffiliateId(final String reference, final int affId)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_BOOKING)
				.where(Tables.SUBSCRIPTION_BOOKING.AFFILIATE_ID.eq(affId)
						.and(Tables.SUBSCRIPTION_BOOKING.REFERENCE.eq(reference)))
				.fetchOneInto(SubscriptionBooking.class);
	}

	public boolean insertPurchaseData(final SubscriptionPurchaseData subscriptionPurchaseData)
	{
		return subscriptionPurchaseData.save(Tables.SUBSCRIPTION_PURCHASE_DATA, dsl);
	}

	public SubscriptionPurchaseData fetchPurchaseData(final int affId, final String customerGuid)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_PURCHASE_DATA)
				.where(Tables.SUBSCRIPTION_PURCHASE_DATA.AFFILIATE_ID.eq(affId)
						.and(Tables.SUBSCRIPTION_PURCHASE_DATA.CUSTOMER_GUID.eq(customerGuid)))
				.fetchOneInto(SubscriptionPurchaseData.class);
	}

	public void saveSubscriptionBookingCustomerDetails(final SubscriptionBookingCustomerDetails customerDetails)
	{
		SubscriptionBookingCustomerDetailsDao bookingCustomerDetailsDao = new SubscriptionBookingCustomerDetailsDao(dsl.configuration());
		bookingCustomerDetailsDao.insert(customerDetails);
	}

	public void saveBookingSeasonTicket(final BookingSeasonTicket bookingTicket)
	{
		SubscriptionBookingSeasonTicketDao bookingCustomerDetailsDao = new SubscriptionBookingSeasonTicketDao(dsl.configuration());
		bookingCustomerDetailsDao.insert(bookingTicket);
	}

	public void saveSubscriptionBookingItem(final SubscriptionBookingItem subscriptionBookingItem)
	{
		SubscriptionBookingItemDao bookingItemDao = new SubscriptionBookingItemDao(dsl.configuration());
		bookingItemDao.insert(subscriptionBookingItem);
	}

	public SubscriptionBookingCustomerDetails fetchSubscriptionBookingCustomerDetails(final int bookingId)
	{
		return new SubscriptionBookingCustomerDetailsDao(dsl.configuration()).fetchBySubBookingId(bookingId)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public List<SubscriptionBookingItem> fetchSubscriptionBookingItem(final int bookingId)
	{
		return new SubscriptionBookingItemDao(dsl.configuration()).fetchBySubBookingId(bookingId);
	}

	public BookingSeasonTicket fetchSubscriptionBookingSeasonTicket(final int itemId)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_BOOKING_SEASON_TICKET)
				.where(Tables.SUBSCRIPTION_BOOKING_SEASON_TICKET.ITEM_ID.eq(itemId))
				.fetchOneInto(BookingSeasonTicket.class);
	}

	public boolean saveBookingRecurringTicket(final BookingRecurringTicket bookingTicket)
	{
		return bookingTicket.save(Tables.SUBSCRIPTION_BOOKING_RECURRING_TICKET, dsl);
	}

	public BookingRecurringTicket fetchSubscriptionBookingRecurringTicket(final int itemId)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_BOOKING_RECURRING_TICKET)
				.where(Tables.SUBSCRIPTION_BOOKING_RECURRING_TICKET.ITEM_ID.eq(itemId))
				.fetchOneInto(BookingRecurringTicket.class);
	}

	public boolean insertSiteSubscriptionRecurringPayment(final SiteSubscriptionRecurringPayment siteSubscriptionRecurringPayment)
	{
		return siteSubscriptionRecurringPayment.save(Tables.SITE_SUBSCRIPTION_RECURRING_PAYMENT, dsl);
	}

	public SiteSubscriptionRecurringPayment fetchSiteSubscriptionRecurringPaymentBySiteId(final int siteId)
	{
		return new SiteSubscriptionRecurringPaymentDao(dsl.configuration()).fetchBySiteId(siteId)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public boolean insertSubscriptionBookingPayment(final SubscriptionBookingPayment bookingPayment)
	{
		return bookingPayment.save(Tables.SUBSCRIPTION_BOOKING_PAYMENT, dsl);
	}

	public SubscriptionBookingPayment fetchSubscriptionBookingPayment(final int subBookingId)
	{
		return new SubscriptionBookingPaymentDao(dsl.configuration()).fetchBySubBookingId(subBookingId)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public List<SubscriptionScheduledRecurringPayment> fetchSubscriptionScheduledRecurringPaymentByBookingId(final int subBookingId)
	{
		return new SubscriptionScheduledRecurringPaymentDao(dsl.configuration()).fetchBySubBookingId(subBookingId);
	}
	
	public List<SubscriptionAllBookingData> fetchAllSubscriptionBookingDataByReferenceAndEmail(final String bookingReference, final String email)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_ALL_BOOKING_DATA)
				.where(Tables.SUBSCRIPTION_ALL_BOOKING_DATA.SUBSCRIPTION_REFERENCE.eq(bookingReference))
				.and(Tables.SUBSCRIPTION_ALL_BOOKING_DATA.CUSTOMER_EMAIL.eq(email))
				.fetchInto(SubscriptionAllBookingData.class);
	}

	public boolean saveReservationData(SubscriptionBookingReservationData reservationData)
	{
		return reservationData.save(Tables.SUBSCRIPTION_BOOKING_RESERVATION_DATA, dsl);
	}

	public SubscriptionBookingReservationData fetchReservationDataByGuid(String guid)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_BOOKING_RESERVATION_DATA)
				.where(Tables.SUBSCRIPTION_BOOKING_RESERVATION_DATA.GUID.eq(guid))
				.orderBy(Tables.SUBSCRIPTION_BOOKING_RESERVATION_DATA.ID.desc())
				.limit(1)
				.fetchOneInto(SubscriptionBookingReservationData.class);
	}

	public boolean saveLanguage(final SubscriptionBookingLanguage language)
	{
		return language.save(Tables.SUBSCRIPTION_BOOKING_LANGUAGE, dsl);
	}

	public SubscriptionBookingDetails fetchSubscriptionDetailsByEmail(String email)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_BOOKING_DETAILS)
				.where(Tables.SUBSCRIPTION_BOOKING_DETAILS.CUSTOMER_EMAIL.eq(email))
				.orderBy(Tables.SUBSCRIPTION_BOOKING_DETAILS.BOOKING_ID.desc())
				.limit(1)
				.fetchOneInto(SubscriptionBookingDetails.class);
	}

	public SubscriptionDiscountedRenewal fetchSubscriptionDiscountedRenewalProductId(int productId)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_DISCOUNTED_RENEWAL)
				.where(Tables.SUBSCRIPTION_DISCOUNTED_RENEWAL.SUBSCRIPTION_PRODUCT_ID.eq(productId))
				.orderBy(Tables.SUBSCRIPTION_DISCOUNTED_RENEWAL.ID)
				.limit(1)
				.fetchOneInto(SubscriptionDiscountedRenewal.class);
	}

	public String fetchFeatureFlagValueByKey(String key)
	{
		return dsl.select(Tables.FEATURE_FLAG.VALUE)
				.from(Tables.FEATURE_FLAG)
				.where(Tables.FEATURE_FLAG.KEY.eq(key))
				.fetchOneInto(String.class);
	}

	public boolean saveEncryptedReference(SubscriptionBookingEncrypted subscriptionBookingEncrypted)
	{
		return dsl.insertInto(Tables.SUBSCRIPTION_BOOKING_ENCRYPTED)
				.set(dsl.newRecord(Tables.SUBSCRIPTION_BOOKING_ENCRYPTED, subscriptionBookingEncrypted))
				.execute() > 0;
	}

	public SubscriptionBooking fetchBookingByEncryptedReference(final String reference)
	{
		return dsl.select(Tables.SUBSCRIPTION_BOOKING.asterisk())
				.from(Tables.SUBSCRIPTION_BOOKING)
				.join(Tables.SUBSCRIPTION_BOOKING_ENCRYPTED)
				.on(Tables.SUBSCRIPTION_BOOKING.ID.eq(Tables.SUBSCRIPTION_BOOKING_ENCRYPTED.SUBSCRIPTION_BOOKING_ID))
				.where(Tables.SUBSCRIPTION_BOOKING_ENCRYPTED.ENCRYPTED_REFERENCE.eq(reference))
				.fetchOneInto(SubscriptionBooking.class);
	}
	
	public String fetchEncryptedReferenceByBookingId(final int bookingId)
	{
		return dsl.select(Tables.SUBSCRIPTION_BOOKING_ENCRYPTED.ENCRYPTED_REFERENCE)
				.from(Tables.SUBSCRIPTION_BOOKING_ENCRYPTED)
				.where(Tables.SUBSCRIPTION_BOOKING_ENCRYPTED.SUBSCRIPTION_BOOKING_ID.eq(bookingId))
				.fetchOneInto(String.class);
	}

	public boolean saveSubscriptionCustomValue(String key, String value, int bookingId)
	{
		return dsl.insertInto(Tables.SUBSCRIPTION_CUSTOM_VALUE)
				.columns(Tables.SUBSCRIPTION_CUSTOM_VALUE.CUSTOM_KEY, Tables.SUBSCRIPTION_CUSTOM_VALUE.VALUE, Tables.SUBSCRIPTION_CUSTOM_VALUE.BOOKING_ID)
				.values(key, value, bookingId)
				.onDuplicateKeyUpdate()
				.set(Tables.SUBSCRIPTION_CUSTOM_VALUE.VALUE, value)
				.execute() > 0;
	}

	public List<SubscriptionAllBookingData> fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(
			final String bookingReference, final String email)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_ALL_BOOKING_DATA)
				.where(Tables.SUBSCRIPTION_ALL_BOOKING_DATA.SUBSCRIPTION_REFERENCE.eq(bookingReference))
				.and(Tables.SUBSCRIPTION_ALL_BOOKING_DATA.CUSTOMER_EMAIL.eq(email))
				.groupBy(Tables.SUBSCRIPTION_ALL_BOOKING_DATA.PRODUCT_NAME)
				.fetchInto(SubscriptionAllBookingData.class);
	}

	public boolean saveSubscriptionBookingMembership(final int bookingId, final String membershipId)
	{
		return dsl.insertInto(Tables.SUBSCRIPTION_BOOKING_MEMBERSHIP)
				.columns(Tables.SUBSCRIPTION_BOOKING_MEMBERSHIP.SUBSCRIPTION_BOOKING_ID, Tables.SUBSCRIPTION_BOOKING_MEMBERSHIP.MEMBERSHIP_ID)
				.values(bookingId, membershipId)
				.execute() > 0;
	}

	public SubscriptionBooking fetchBookingByReferenceAffiliateIdAndEmail(String reference, int affiliateId, String email)
	{
		return dsl.select(Tables.SUBSCRIPTION_BOOKING.fields())
				.from(Tables.SUBSCRIPTION_BOOKING)
				.leftJoin(Tables.SUBSCRIPTION_BOOKING_CUSTOMER_DETAILS)
				.on(Tables.SUBSCRIPTION_BOOKING.ID.eq(Tables.SUBSCRIPTION_BOOKING_CUSTOMER_DETAILS.SUB_BOOKING_ID))
				.where(Tables.SUBSCRIPTION_BOOKING.REFERENCE.eq(reference))
				.and(Tables.SUBSCRIPTION_BOOKING.AFFILIATE_ID.eq(affiliateId))
				.and(Tables.SUBSCRIPTION_BOOKING_CUSTOMER_DETAILS.EMAIL_ADDRESS.eq(email))
				.fetchOneInto(SubscriptionBooking.class);
	}

	public String fetchConfirmationGuidByCustomerGuid(final String customerGuid)
	{
		return dsl.select(Tables.SUBSCRIPTION_GUID.GUID)
				.from(Tables.SUBSCRIPTION_GUID)
				.join(Tables.SUBSCRIPTION_GUID_BOOKING)
				.on(Tables.SUBSCRIPTION_GUID_BOOKING.SUBSCRIPTION_GUID_ID.eq(Tables.SUBSCRIPTION_GUID.ID))
				.join(Tables.SUBSCRIPTION_PURCHASE_DATA)
				.on(Tables.SUBSCRIPTION_PURCHASE_DATA.ID.eq(
						Tables.SUBSCRIPTION_GUID_BOOKING.SUBSCRIPTION_PURCHASE_DATA_ID))
				.where(Tables.SUBSCRIPTION_PURCHASE_DATA.CUSTOMER_GUID.eq(customerGuid))
				.orderBy(Tables.SUBSCRIPTION_GUID.ID.asc())
				.limit(1)
				.fetchOneInto(String.class);
	}
}