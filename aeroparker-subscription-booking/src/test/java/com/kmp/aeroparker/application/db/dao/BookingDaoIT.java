package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteSubscriptionRecurringPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingEncrypted;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReservationData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuid;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuidBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPurchaseData;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

import io.github.benas.randombeans.api.EnhancedRandom;

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { BookingDao.class })
class BookingDaoIT
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getBookingInstance();
	@Autowired
	private BookingDao dao;

	@Test
	void testSaveBooking()
	{
		assertThat(dao.fetchBookingByReferenceAndAffiliateId("SNWSC1001123", 72)).isNull();
		SubscriptionBooking subscriptionBooking = EnhancedRandom.random(SubscriptionBooking.class, "id");
		subscriptionBooking.setReference("SNWSC1001123");
		subscriptionBooking.setAffiliateId(72);
		subscriptionBooking.setContactId(40812);
		dao.saveBooking(subscriptionBooking);
		assertThat(dao.fetchBookingByReferenceAndAffiliateId("SNWSC1001123", 72)).isNotNull()
				.hasFieldOrPropertyWithValue("reference", "SNWSC1001123")
				.hasFieldOrPropertyWithValue("affiliateId", 72);
	}

	@Test
	void testFetchBookingByReferenceAndAffiliateId()
	{
		assertThat(dao.fetchBookingByReferenceAndAffiliateId("SNWSC100150", 72)).hasFieldOrPropertyWithValue("reference", "SNWSC100150")
				.hasFieldOrPropertyWithValue("affiliateId", 72);
	}

	@Test
	void testFetchSubscriptionGuidByGuid()
	{
		assertThat(dao.fetchSubscriptionGuidByGuid("793f5b8b-65b5-4854-b872-dd1c50350ed8")).isNotNull()
				.hasFieldOrPropertyWithValue("guid", "793f5b8b-65b5-4854-b872-dd1c50350ed8")
				.hasFieldOrPropertyWithValue("id", 17);
	}

	@Test
	void testSaveSubscriptionGuid()
	{
		assertThat(dao.fetchSubscriptionGuidByGuid("12c8f281-4114-42a4-9bc8-232af1d4300a")).isNull();
		SubscriptionGuid subscriptionGuid = EnhancedRandom.random(SubscriptionGuid.class, "id");
		subscriptionGuid.setGuid("12c8f281-4114-42a4-9bc8-232af1d4300a");
		dao.saveSubscriptionGuid(subscriptionGuid);
		assertThat(dao.fetchSubscriptionGuidByGuid("12c8f281-4114-42a4-9bc8-232af1d4300a")).isNotNull()
				.hasFieldOrPropertyWithValue("guid", "12c8f281-4114-42a4-9bc8-232af1d4300a");
	}

	@Test
	void testFetchSubscriptionBookingByIdAndAffiliateId()
	{
		assertThat(dao.fetchSubscriptionBookingByIdAndAffiliateId(29, 72)).isNotNull()
				.hasFieldOrPropertyWithValue("reference", "SNWSC100150")
				.hasFieldOrPropertyWithValue("id", 29);
	}

	@Test
	void testSaveSubscriptionGuidBooking()
	{
		assertThat(dao.fetchSubscriptionGuidBookingByGuidId(13)).isNull();
		SubscriptionGuidBooking subscriptionGuidBooking = EnhancedRandom.random(SubscriptionGuidBooking.class, "id");
		subscriptionGuidBooking.setSubscriptionGuidId(13);
		subscriptionGuidBooking.setSubscriptionBookingId(49);
		subscriptionGuidBooking.setSubscriptionBookingId(49);
		subscriptionGuidBooking.setSubscriptionPurchaseDataId(288);
		dao.saveSubscriptionGuidBooking(subscriptionGuidBooking);
		assertThat(dao.fetchSubscriptionGuidBookingByGuidId(13)).isNotNull()
				.hasFieldOrPropertyWithValue("subscriptionBookingId", 49);
	}

	@Test
	void testFetchSubscriptionGuidBookingByGuidId()
	{
		assertThat(dao.fetchSubscriptionGuidBookingByGuidId(26)).isNotNull()
				.hasFieldOrPropertyWithValue("subscriptionGuidId", 26)
				.hasFieldOrPropertyWithValue("subscriptionBookingId", 24);
	}

	@Test
	void testInsertPurchaseData()
	{
		assertThat(dao.fetchPurchaseData(29, "7f468c37-7eeb-48cd-a515-d85e3b9sd825")).isNull();
		SubscriptionPurchaseData subscriptionPurchaseData = new SubscriptionPurchaseData();
		subscriptionPurchaseData.setAffiliateId(29);
		subscriptionPurchaseData.setCustomerGuid("7f468c37-7eeb-48cd-a515-d85e3b9sd825");
		subscriptionPurchaseData.setPurchaseData("[{\"productId\":13,\"startDate\":\"30/08/2019\"},{\"productId\":12,\"startDate\":\"30/08/2019\"}]");
		dao.insertPurchaseData(subscriptionPurchaseData);
		assertThat(dao.fetchPurchaseData(29, "7f468c37-7eeb-48cd-a515-d85e3b9sd825")).isNotNull()
				.hasFieldOrPropertyWithValue("customerGuid", "7f468c37-7eeb-48cd-a515-d85e3b9sd825");
	}

	@Test
	void testFetchPurchaseData()
	{
		assertThat(dao.fetchPurchaseData(29, "3482270b-5765-4f70-8425-d3ea2a29315a")).isNotNull()
				.hasFieldOrPropertyWithValue("customerGuid", "3482270b-5765-4f70-8425-d3ea2a29315a")
				.hasFieldOrPropertyWithValue("purchaseData", "[{\"productId\":26,\"startDate\":\"28.10.19\"}]");
	}

	@Test
	void testSaveSubscriptionBookingCustomerDetails()
	{
		assertThat(dao.fetchSubscriptionBookingCustomerDetails(49)).isNull();
		SubscriptionBookingCustomerDetails customerDetails = EnhancedRandom.random(SubscriptionBookingCustomerDetails.class, "id");
		customerDetails.setFirstName("Test");
		customerDetails.setLastName("LastName");
		customerDetails.setSubBookingId(49);
		customerDetails.setTitle("Mr");
		customerDetails.setLastName("last_name");
		customerDetails.setPostcode("M40 5RJ");
		dao.saveSubscriptionBookingCustomerDetails(customerDetails);
		assertThat(dao.fetchSubscriptionBookingCustomerDetails(49)).isNotNull()
				.hasFieldOrPropertyWithValue("firstName", "Test");
	}

	@Test
	void testSaveBookingSeasonTicket()
	{
		assertThat(dao.fetchSubscriptionBookingSeasonTicket(31)).isNull();
		BookingSeasonTicket bookingSeasonTicket = EnhancedRandom.random(BookingSeasonTicket.class, "id");
		bookingSeasonTicket.setItemId(31);
		bookingSeasonTicket.setPeriodTerm("FIXED");
		dao.saveBookingSeasonTicket(bookingSeasonTicket);
		assertThat(dao.fetchSubscriptionBookingSeasonTicket(31)).isNotNull()
				.hasFieldOrPropertyWithValue("periodTerm", "FIXED");
	}

	@Test
	void testSaveSubscriptionBookingItem()
	{
		assertThat(dao.fetchSubscriptionBookingItem(49)).hasSize(1);
		SubscriptionBookingItem subscriptionBookingItem = EnhancedRandom.random(SubscriptionBookingItem.class, "id");
		subscriptionBookingItem.setSubBookingId(49);
		subscriptionBookingItem.setProductId(13);
		subscriptionBookingItem.setPeriodType("FIXED");
		subscriptionBookingItem.setCarParkId(81);
		dao.saveSubscriptionBookingItem(subscriptionBookingItem);
		assertThat(dao.fetchSubscriptionBookingItem(49)).hasSize(2);
	}

	@Test
	void testFetchSubscriptionBookingCustomerDetails()
	{
		assertThat(dao.fetchSubscriptionBookingCustomerDetails(44)).isNotNull()
				.hasFieldOrPropertyWithValue("firstName", "Derrick");
	}

	@Test
	void testFetchSubscriptionBookingItem()
	{
		assertThat(dao.fetchSubscriptionBookingItem(33)).hasSize(1);
	}

	@Test
	void testFetchSubscriptionBookingSeasonTicket()
	{
		assertThat(dao.fetchSubscriptionBookingSeasonTicket(19)).isNotNull()
				.hasFieldOrPropertyWithValue("periodTerm", "FIXED")
				.hasFieldOrPropertyWithValue("minimumTerm", "FIVE_MONTHS");
	}

	@Test
	void testSaveBookingRecurringTicket()
	{
		assertThat(dao.fetchSubscriptionBookingRecurringTicket(31)).isNull();
		BookingRecurringTicket bookingTicket = new BookingRecurringTicket();
		bookingTicket.setItemId(31);
		bookingTicket.setMinimumTerm("THREE_MONTHS");
		bookingTicket.setMinimumTermDate(DateUtil.nowDate(""));
		bookingTicket.setPrice(BigDecimal.TEN);
		bookingTicket.setStartDate(DateUtil.nowDate(""));
		assertThat(dao.saveBookingRecurringTicket(bookingTicket)).isTrue();
		assertThat(dao.fetchSubscriptionBookingRecurringTicket(31)).isNotNull()
				.hasFieldOrPropertyWithValue("price", BigDecimal.TEN.setScale(2))
				.hasFieldOrPropertyWithValue("minimumTerm", "THREE_MONTHS");
	}

	@Test
	void testFetchSubscriptionBookingRecurringTicket()
	{
		assertThat(dao.fetchSubscriptionBookingRecurringTicket(50)).isNotNull()
				.hasFieldOrPropertyWithValue("price", BigDecimal.ZERO.setScale(2))
				.hasFieldOrPropertyWithValue("minimumTerm", "THREE_MONTHS");
	}

	@Test
	void testInsertSiteSubscriptionRecurringPayment()
	{
		assertThat(dao.fetchSiteSubscriptionRecurringPaymentBySiteId(12)).isNull();
		SiteSubscriptionRecurringPayment siteSubscriptionRecurringPayment = new SiteSubscriptionRecurringPayment();
		siteSubscriptionRecurringPayment.setSiteId(12);
		assertThat(dao.insertSiteSubscriptionRecurringPayment(siteSubscriptionRecurringPayment)).isTrue();
		assertThat(dao.fetchSiteSubscriptionRecurringPaymentBySiteId(12)).isNotNull();
	}

	@Test
	void testFetchSiteSubscriptionRecurringPaymentBySiteId()
	{
		assertThat(dao.fetchSiteSubscriptionRecurringPaymentBySiteId(17)).isNotNull()
				.hasFieldOrPropertyWithValue("siteId", 17);
	}

	@Test
	void testInsertSubscriptionBookingPayment()
	{
		SubscriptionBookingPayment bookingPayment = new SubscriptionBookingPayment();
		bookingPayment.setPaymentId(7999);
		bookingPayment.setSubBookingId(72);
		assertThat(dao.insertSubscriptionBookingPayment(bookingPayment)).isTrue();
	}

	@Test
	void testFetchSubscriptionBookingPayment()
	{
		assertThat(dao.fetchSubscriptionBookingPayment(75)).isNotNull()
				.hasFieldOrPropertyWithValue("paymentId", 8122);
	}

	@Test
	void testFetchSubscriptionScheduledRecurringPaymentByBookingId()
	{
		assertThat(dao.fetchSubscriptionScheduledRecurringPaymentByBookingId(49)).hasSize(1);
	}
	
	@Test
	void testFetchAllSubscriptionBookingDataByReferenceAndEmail()
	{
		assertThat(
				dao.fetchAllSubscriptionBookingDataByReferenceAndEmail("SNWSC100150", "derrick.feehi@aeroparker.com"))
						.isNotEmpty();
	}

	@Test
	public void testSaveReservationData()
	{
		assertTrue(dao.saveReservationData(mock(SubscriptionBookingReservationData.class)));
	}

	@Test
	public void testFetchReservationDataByGuid()
	{
		assertNotNull(dao.fetchReservationDataByGuid("guid"));
	}

	@Test
	public void testFetchBookingByEncryptedReference()
	{
		assertNotNull(dao.fetchBookingByEncryptedReference("C02BFD12B3641A0E4FB6AFED1F11473A"));
	}

	@Test
	public void testFetchBookingByEncryptedReference_NoMatchingRecord()
	{
		assertNotNull(dao.fetchBookingByEncryptedReference("123"));
	}

	@Test
	public void testSaveLanguage()
	{
		assertNotNull(dao.saveLanguage(mock(SubscriptionBookingLanguage.class)));
	}
	
	@Test
	void testFetchSubscriptionDetailsByEmail()
	{
		assertNotNull(dao.fetchSubscriptionDetailsByEmail("test@example.com"));
	}

	@Test
	void testFetchSubscriptionDiscountedRenewalProductId()
	{
		assertNotNull(dao.fetchSubscriptionDiscountedRenewalProductId(1));
	}

	public void testFetchFeatureFlagValueByKey()
	{
		assertEquals("1,2,3", dao.fetchFeatureFlagValueByKey("feature.flag"));
	}

	@Test
	public void testSaveEncryptedReference()
	{
		assertTrue(dao.saveEncryptedReference(mock(SubscriptionBookingEncrypted.class)));
	}

	@Test
	void testFetchEncryptedReferenceByBookingId()
	{
		assertEquals("C02BFD12B3641A0E4FB6AFED1F11473A", dao.fetchEncryptedReferenceByBookingId(24));
	}

	@Test
	void testFetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail()
	{
		assertThat(dao.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail("SNWSC100150",
				"derrick.feehi@aeroparker.com")).isNotEmpty();
	}

	@Test 
	void testSaveSubscriptionBookingMembership()
	{
		assertTrue(dao.saveSubscriptionBookingMembership(24, "MEM123"));
	}

	@Test
	public void testFetchBookingByReferenceAffiliateIdAndEmail()
	{
		assertNotNull(dao.fetchBookingByReferenceAffiliateIdAndEmail("BOOK44", 72 , "derrick.feehi@aeroparker.com"));
	}

	@Test
	public void testFetchBookingByReferenceAffiliateIdAndEmail_NoMatchingRecord()
	{
		assertNull(dao.fetchBookingByReferenceAffiliateIdAndEmail("1234", 72 , "invalid"));
	}

	@Test
	public void testFetchConfirmationGuidByCustomerGuid()
	{
		assertEquals("26-guid", dao.fetchConfirmationGuidByCustomerGuid("3482270b-5765-4f70-8425-d3ea2a29315a"));
	}

	@Test
	public void testFetchConfirmationGuidByCustomerGuid_NoMatchingRecord()
	{
		assertNull(dao.fetchConfirmationGuidByCustomerGuid(UUID.randomUUID()
				.toString()));
	}
}