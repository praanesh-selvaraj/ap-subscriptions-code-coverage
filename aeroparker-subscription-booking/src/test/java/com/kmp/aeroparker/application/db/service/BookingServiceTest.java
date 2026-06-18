package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.dao.BookingDao;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteSubscriptionRecurringPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingEncrypted;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReservationData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionCustomValue;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionDiscountedRenewal;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuid;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuidBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPurchaseData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest
{
	@Mock
	private ReceiptDetailsService receiptDetailsService;
	@Mock
	private VehicleDetailsService vehicleDetailsService;
	@Mock
	private BookingDao dao;
	@Mock
	private SubscriptionBookingReservationData reservationData;
	@Mock
	private SubscriptionBookingLanguage language;
	@Mock
	private SubscriptionBooking subscriptionBookings;
	@InjectMocks
	private BookingService service;

	@Test
	void testFetchSubscriptionGuidByGuid()
	{
		when(dao.fetchSubscriptionGuidByGuid(anyString())).thenReturn(mock(SubscriptionGuid.class));
		assertThat(service.fetchSubscriptionGuidByGuid("guid")).isNotNull()
				.isInstanceOf(SubscriptionGuid.class);
	}

	@Test
	void testFetchSubscriptionGuidByGuid_Guid_Empty()
	{
		assertThat(service.fetchSubscriptionGuidByGuid("")).isNull();
	}

	@Test
	void testSaveBooking()
	{
		assertThat(service.saveBooking(mock(SubscriptionBooking.class))).isTrue();
		verify(dao).saveBooking(any());
	}

	@Test
	void testSaveBooking_SubscriptionBooking_Null()
	{
		assertThat(service.saveBooking(null)).isFalse();
		verifyNoInteractions(dao);
	}

	@Test
	void testSaveSubscriptionGuid()
	{
		assertThat(service.saveSubscriptionGuid(mock(SubscriptionGuid.class))).isTrue();
		verify(dao).saveSubscriptionGuid(any());
	}

	@Test
	void testSaveSubscriptionGuid_SubscriptionGuid_Null()
	{
		assertThat(service.saveSubscriptionGuid(null)).isFalse();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchSubscriptionBookingByIdAndAffiliateId()
	{
		when(dao.fetchSubscriptionBookingByIdAndAffiliateId(anyInt(), anyInt())).thenReturn(mock(SubscriptionBooking.class));
		assertThat(service.fetchSubscriptionBookingByIdAndAffiliateId(1, 1)).isNotNull()
				.isInstanceOf(SubscriptionBooking.class);
	}

	@Test
	void testFetchSubscriptionBookingByIdAndAffiliateId_Invalid_Params()
	{
		assertThat(service.fetchSubscriptionBookingByIdAndAffiliateId(0, 0)).isNull();
		assertThat(service.fetchSubscriptionBookingByIdAndAffiliateId(0, 1)).isNull();
		assertThat(service.fetchSubscriptionBookingByIdAndAffiliateId(1, 0)).isNull();
	}

	@Test
	void testFetchSubscriptionGuidBookingByGuidId()
	{
		when(dao.fetchSubscriptionGuidBookingByGuidId(anyInt())).thenReturn(mock(SubscriptionGuidBooking.class));
		assertThat(service.fetchSubscriptionGuidBookingByGuidId(1)).isNotNull();
		verify(dao).fetchSubscriptionGuidBookingByGuidId(anyInt());
	}

	@Test
	void testFetchSubscriptionGuidBookingByGuidId_Invalid_Params()
	{
		assertThat(service.fetchSubscriptionGuidBookingByGuidId(1)).isNull();
		assertThat(service.fetchSubscriptionGuidBookingByGuidId(0)).isNull();
	}

	@Test
	void testSaveSubscriptionGuidBooking()
	{
		assertThat(service.saveSubscriptionGuidBooking(mock(SubscriptionGuidBooking.class))).isTrue();
		verify(dao).saveSubscriptionGuidBooking(any());
	}

	@Test
	void testSaveSubscriptionGuidBooking_Invalid_Params()
	{
		assertThat(service.saveSubscriptionGuidBooking(null)).isFalse();
	}

	@Test
	void testFetchBookingByReferenceAndAffiliateId()
	{
		when(dao.fetchBookingByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(mock(SubscriptionBooking.class));
		assertThat(service.fetchBookingByReferenceAndAffiliateId("reference", 1)).isNotNull();
		verify(dao).fetchBookingByReferenceAndAffiliateId(anyString(), anyInt());
	}

	@Test
	void testFetchBookingByReferenceAndAffiliateId_Invalid_Params()
	{
		assertThat(service.fetchBookingByReferenceAndAffiliateId("reference", 0)).isNull();
		assertThat(service.fetchBookingByReferenceAndAffiliateId("", 1)).isNull();
		assertThat(service.fetchBookingByReferenceAndAffiliateId("", 0)).isNull();
	}

	@Test
	void testInsertPurchaseData_Empty_Params()
	{
		assertThat(service.insertPurchaseData(1, "", "")).isFalse();
		assertThat(service.insertPurchaseData(1, "purchaseData", "")).isFalse();
		assertThat(service.insertPurchaseData(1, "", "customerGuid")).isFalse();
		assertThat(service.insertPurchaseData(0, "", "customerGuid")).isFalse();
		verify(dao, times(0)).insertPurchaseData(any());
	}

	@Test
	void testInsertPurchaseData()
	{
		when(dao.insertPurchaseData(any())).thenReturn(true);
		assertThat(service.insertPurchaseData(1, "purchaseData", "customerGuid")).isTrue();
	}

	@Test
	void testFetchPurchaseData()
	{
		when(dao.fetchPurchaseData(anyInt(), anyString())).thenReturn(mock(SubscriptionPurchaseData.class));
		assertThat(service.fetchPurchaseData(1, "customerGuid")).isNotNull()
				.isInstanceOf(SubscriptionPurchaseData.class);
		verify(dao).fetchPurchaseData(anyInt(), anyString());
	}

	@Test
	void testFetchPurchaseData_Param_Empty()
	{
		assertThat(service.fetchPurchaseData(1, "")).isNull();
		verify(dao, times(0)).fetchPurchaseData(anyInt(), anyString());
	}

	@Test
	void testSaveSubscriptionBookingCustomerDetails()
	{
		assertThat(service.saveSubscriptionBookingCustomerDetails(mock(SubscriptionBookingCustomerDetails.class))).isTrue();
		verify(dao).saveSubscriptionBookingCustomerDetails(any());
	}

	@Test
	void testSaveSubscriptionBookingCustomerDetails_Customer_Details_Null()
	{
		assertThat(service.saveSubscriptionBookingCustomerDetails(null)).isFalse();
		verify(dao, times(0)).saveSubscriptionBookingCustomerDetails(any());
	}

	@Test
	void testSaveBookingSeasonTicket()
	{
		assertThat(service.saveBookingSeasonTicket(mock(BookingSeasonTicket.class))).isTrue();
		verify(dao).saveBookingSeasonTicket(any());
	}

	@Test
	void testSaveBookingSeasonTicket_Ticket_Null()
	{
		assertThat(service.saveBookingSeasonTicket(null)).isFalse();
		verify(dao, times(0)).saveBookingSeasonTicket(any());
	}

	@Test
	void testSaveSubscriptionBookingItem()
	{
		assertThat(service.saveSubscriptionBookingItem(mock(SubscriptionBookingItem.class))).isTrue();
		verify(dao).saveSubscriptionBookingItem(any());
	}

	@Test
	void testSaveSubscriptionBookingItem_Ticket_Null()
	{
		assertThat(service.saveSubscriptionBookingItem(null)).isFalse();
		verify(dao, times(0)).saveSubscriptionBookingItem(any());
	}

	@Test
	void testFetchSubscriptionBookingCustomerDetails()
	{
		when(dao.fetchSubscriptionBookingCustomerDetails(anyInt())).thenReturn(mock(SubscriptionBookingCustomerDetails.class));
		assertThat(service.fetchSubscriptionBookingCustomerDetails(1)).isNotNull()
				.isInstanceOf(SubscriptionBookingCustomerDetails.class);
		verify(dao).fetchSubscriptionBookingCustomerDetails(anyInt());
	}

	@Test
	void testFetchSubscriptionBookingCustomerDetails_Param_Empty()
	{
		assertThat(service.fetchSubscriptionBookingCustomerDetails(0)).isNull();
		verify(dao, times(0)).fetchSubscriptionBookingCustomerDetails(anyInt());
	}

	@Test
	void testFetchSubscriptionBookingItem()
	{
		when(dao.fetchSubscriptionBookingItem(anyInt())).thenReturn(EnhancedRandom.randomListOf(1, SubscriptionBookingItem.class));
		assertThat(service.fetchSubscriptionBookingItem(1)).isNotEmpty()
				.hasOnlyElementsOfType(SubscriptionBookingItem.class);
		verify(dao).fetchSubscriptionBookingItem(anyInt());
	}

	@Test
	void testFetchSubscriptionBookingItem_Param_Empty()
	{
		assertThat(service.fetchSubscriptionBookingItem(0)).isEmpty();
		verify(dao, times(0)).fetchSubscriptionBookingItem(anyInt());
	}

	@Test
	void testFetchSubscriptionBookingSeasonTicket()
	{
		when(dao.fetchSubscriptionBookingSeasonTicket(anyInt())).thenReturn(mock(BookingSeasonTicket.class));
		assertThat(service.fetchSubscriptionBookingSeasonTicket(1)).isNotNull()
				.isInstanceOf(BookingSeasonTicket.class);
		verify(dao).fetchSubscriptionBookingSeasonTicket(anyInt());
	}

	@Test
	void testFetchSubscriptionBookingSeasonTicket_Param_Zero()
	{
		assertThat(service.fetchSubscriptionBookingSeasonTicket(0)).isNull();
		verify(dao, times(0)).fetchSubscriptionBookingSeasonTicket(anyInt());
	}

	@Test
	void testFetchSubscriptionBookingRecurringTicket()
	{
		when(dao.fetchSubscriptionBookingRecurringTicket(anyInt())).thenReturn(mock(BookingRecurringTicket.class));
		assertThat(service.fetchSubscriptionBookingRecurringTicket(1)).isNotNull()
				.isInstanceOf(BookingRecurringTicket.class);
		verify(dao).fetchSubscriptionBookingRecurringTicket(anyInt());
	}

	@Test
	void testFetchSubscriptionBookingRecurringTicket_Param_Zero()
	{
		assertThat(service.fetchSubscriptionBookingRecurringTicket(0)).isNull();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchSubscriptionBooking()
	{
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(dao.fetchSubscriptionBookingByIdAndAffiliateId(anyInt(), anyInt())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		SubscriptionBookingCustomerDetails customerDetails = mock(SubscriptionBookingCustomerDetails.class);
		when(dao.fetchSubscriptionBookingCustomerDetails(anyInt())).thenReturn(customerDetails);
		List<SubscriptionBookingItem> bookingItems = new ArrayList<>();
		SubscriptionBookingItem bookingItem = mock(SubscriptionBookingItem.class);
		when(bookingItem.getId()).thenReturn(1);
		when(bookingItem.getPeriodType()).thenReturn("FIXED");
		when(dao.fetchSubscriptionBookingSeasonTicket(anyInt())).thenReturn(mock(BookingSeasonTicket.class));
		bookingItems.add(bookingItem);
		when(dao.fetchSubscriptionBookingItem(anyInt())).thenReturn(bookingItems);
		when(dao.fetchSubscriptionBookingPayment(anyInt())).thenReturn(EnhancedRandom.random(SubscriptionBookingPayment.class));
		when(dao.fetchSubscriptionScheduledRecurringPaymentByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, SubscriptionScheduledRecurringPayment.class));
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.random(SubscriptionBookingVehicleDetails.class));
		when(receiptDetailsService.fetchSubscriptionBookingReceiptDetailsByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.random(SubscriptionBookingReceiptDetails.class));
		assertThat(service.fetchSubscriptionBooking(1, 1)).isNotNull()
				.isInstanceOf(SubscriptionBookingRecord.class);
		verify(dao).fetchSubscriptionBookingByIdAndAffiliateId(anyInt(), anyInt());
		verify(dao).fetchSubscriptionBookingCustomerDetails(anyInt());
		verify(dao).fetchSubscriptionBookingSeasonTicket(anyInt());
		verify(dao).fetchSubscriptionBookingItem(anyInt());
		verify(dao).fetchSubscriptionBookingPayment(anyInt());
		verify(dao).fetchSubscriptionScheduledRecurringPaymentByBookingId(anyInt());
		verify(receiptDetailsService).fetchSubscriptionBookingReceiptDetailsByBookingId(anyInt());
		verify(vehicleDetailsService).fetchSubscriptionBookingVehicleDetailsByBookingId(anyInt());
		verifyNoMoreInteractions(dao, receiptDetailsService, vehicleDetailsService);
	}

	@Test
	void testFetchSubscriptionBooking_BookingRecurringTicket()
	{
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(dao.fetchSubscriptionBookingByIdAndAffiliateId(anyInt(), anyInt())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		SubscriptionBookingCustomerDetails customerDetails = mock(SubscriptionBookingCustomerDetails.class);
		when(dao.fetchSubscriptionBookingCustomerDetails(anyInt())).thenReturn(customerDetails);
		List<SubscriptionBookingItem> bookingItems = new ArrayList<>();
		SubscriptionBookingItem bookingItem = mock(SubscriptionBookingItem.class);
		when(bookingItem.getId()).thenReturn(1);
		when(bookingItem.getPeriodType()).thenReturn("RECURRING");
		when(dao.fetchSubscriptionBookingRecurringTicket(anyInt())).thenReturn(mock(BookingRecurringTicket.class));
		bookingItems.add(bookingItem);
		when(dao.fetchSubscriptionBookingPayment(anyInt())).thenReturn(EnhancedRandom.random(SubscriptionBookingPayment.class));
		when(dao.fetchSubscriptionBookingItem(anyInt())).thenReturn(bookingItems);
		when(dao.fetchSubscriptionBookingPayment(anyInt())).thenReturn(EnhancedRandom.random(SubscriptionBookingPayment.class));
		when(dao.fetchSubscriptionScheduledRecurringPaymentByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, SubscriptionScheduledRecurringPayment.class));
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.random(SubscriptionBookingVehicleDetails.class));
		when(receiptDetailsService.fetchSubscriptionBookingReceiptDetailsByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.random(SubscriptionBookingReceiptDetails.class));
		assertThat(service.fetchSubscriptionBooking(1, 1)).isNotNull()
				.isInstanceOf(SubscriptionBookingRecord.class);
		verify(dao).fetchSubscriptionBookingByIdAndAffiliateId(anyInt(), anyInt());
		verify(dao).fetchSubscriptionBookingCustomerDetails(anyInt());
		verify(dao, times(0)).fetchSubscriptionBookingSeasonTicket(anyInt());
		verify(dao).fetchSubscriptionBookingRecurringTicket(anyInt());
		verify(dao).fetchSubscriptionBookingItem(anyInt());
		verify(dao).fetchSubscriptionBookingPayment(anyInt());
		verify(dao).fetchSubscriptionScheduledRecurringPaymentByBookingId(anyInt());
		verify(receiptDetailsService).fetchSubscriptionBookingReceiptDetailsByBookingId(anyInt());
		verify(vehicleDetailsService).fetchSubscriptionBookingVehicleDetailsByBookingId(anyInt());
		verifyNoMoreInteractions(dao, receiptDetailsService, vehicleDetailsService);
	}

	@Test
	void testFetchSubscriptionBooking_BookingSeasonTicket_Null()
	{
		setUpFetchSubscriptionBooking();
		when(dao.fetchSubscriptionBookingPayment(anyInt())).thenReturn(EnhancedRandom.random(SubscriptionBookingPayment.class));
		when(dao.fetchSubscriptionBookingPayment(anyInt())).thenReturn(EnhancedRandom.random(SubscriptionBookingPayment.class));
		when(dao.fetchSubscriptionScheduledRecurringPaymentByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, SubscriptionScheduledRecurringPayment.class));
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.random(SubscriptionBookingVehicleDetails.class));
		when(receiptDetailsService.fetchSubscriptionBookingReceiptDetailsByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.random(SubscriptionBookingReceiptDetails.class));
		assertThat(service.fetchSubscriptionBooking(1, 1)).isNotNull()
				.extracting(SubscriptionBookingRecord::getBookingItemMap)
				.isEqualTo(Collections.emptyMap());
		verify(dao).fetchSubscriptionBookingByIdAndAffiliateId(anyInt(), anyInt());
		verify(dao).fetchSubscriptionBookingCustomerDetails(anyInt());
		verify(dao).fetchSubscriptionBookingSeasonTicket(anyInt());
		verify(dao).fetchSubscriptionBookingItem(anyInt());
		verify(dao).fetchSubscriptionBookingPayment(anyInt());
		verify(dao).fetchSubscriptionScheduledRecurringPaymentByBookingId(anyInt());
		verify(receiptDetailsService).fetchSubscriptionBookingReceiptDetailsByBookingId(anyInt());
		verify(vehicleDetailsService).fetchSubscriptionBookingVehicleDetailsByBookingId(anyInt());
		verifyNoMoreInteractions(dao, receiptDetailsService, vehicleDetailsService);
	}

	@Test
	void testFetchSubscriptionBooking_Booking_Null()
	{
		when(dao.fetchSubscriptionBookingByIdAndAffiliateId(anyInt(), anyInt())).thenReturn(null);
		assertThat(service.fetchSubscriptionBooking(1, 1)).isNull();
		verify(dao).fetchSubscriptionBookingByIdAndAffiliateId(anyInt(), anyInt());
		verify(dao, times(0)).fetchSubscriptionBookingCustomerDetails(anyInt());
		verify(dao, times(0)).fetchSubscriptionBookingSeasonTicket(anyInt());
		verify(dao, times(0)).fetchSubscriptionBookingItem(anyInt());
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFetchSubscriptionBookingWithGuidAndAffiliate()
	{
		SubscriptionGuid subscriptionGuid = mock(SubscriptionGuid.class);
		when(dao.fetchSubscriptionGuidByGuid(anyString())).thenReturn(subscriptionGuid);
		when(subscriptionGuid.getId()).thenReturn(1);
		SubscriptionGuidBooking guidBooking = mock(SubscriptionGuidBooking.class);
		when(dao.fetchSubscriptionGuidBookingByGuidId(anyInt())).thenReturn(guidBooking);
		when(guidBooking.getSubscriptionBookingId()).thenReturn(1);
		setUpFetchSubscriptionBooking();
		assertThat(service.fetchSubscriptionBooking("guid", 1)).isNotNull()
				.isInstanceOf(SubscriptionBookingRecord.class);
	}

	private void setUpFetchSubscriptionBooking()
	{
		SubscriptionBooking booking = mock(SubscriptionBooking.class);
		when(dao.fetchSubscriptionBookingByIdAndAffiliateId(anyInt(), anyInt())).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		SubscriptionBookingCustomerDetails customerDetails = mock(SubscriptionBookingCustomerDetails.class);
		when(dao.fetchSubscriptionBookingCustomerDetails(anyInt())).thenReturn(customerDetails);
		List<SubscriptionBookingItem> bookingItems = new ArrayList<>();
		SubscriptionBookingItem bookingItem = mock(SubscriptionBookingItem.class);
		when(bookingItem.getId()).thenReturn(1);
		when(bookingItem.getPeriodType()).thenReturn("FIXED");
		when(dao.fetchSubscriptionBookingSeasonTicket(anyInt())).thenReturn(null);
		bookingItems.add(bookingItem);
		when(dao.fetchSubscriptionBookingItem(anyInt())).thenReturn(bookingItems);
	}

	@Test
	void testFetchSubscriptionBookingWithGuidAndAffiliate_SubscriptionGuidBooking_Null()
	{
		SubscriptionGuid subscriptionGuid = mock(SubscriptionGuid.class);
		when(dao.fetchSubscriptionGuidByGuid(anyString())).thenReturn(subscriptionGuid);
		when(subscriptionGuid.getId()).thenReturn(1);
		when(dao.fetchSubscriptionGuidBookingByGuidId(anyInt())).thenReturn(null);
		assertThat(service.fetchSubscriptionBooking("guid", 1)).isNull();
	}

	@Test
	void testFetchSubscriptionBookingWithGuidAndAffiliate_SubscriptionGuid_Null()
	{
		when(dao.fetchSubscriptionGuidByGuid(anyString())).thenReturn(null);
		assertThat(service.fetchSubscriptionBooking("guid", 1)).isNull();
	}

	@Test
	void testSaveBookingRecurringTicket()
	{
		when(dao.saveBookingRecurringTicket(any())).thenReturn(true);
		assertThat(service.saveBookingRecurringTicket(mock(BookingRecurringTicket.class))).isTrue();
		verify(dao).saveBookingRecurringTicket(any());
	}

	@Test
	void testSaveBookingRecurringTicket_Ticket_Null()
	{
		assertThat(service.saveBookingRecurringTicket(null)).isFalse();
		verify(dao, times(0)).saveBookingSeasonTicket(any());
	}

	@Test
	void testFetchSiteSubscriptionRecurringPaymentBySiteId()
	{
		SiteSubscriptionRecurringPayment siteSubscriptionRecurringPayment = mock(SiteSubscriptionRecurringPayment.class);
		when(dao.fetchSiteSubscriptionRecurringPaymentBySiteId(anyInt())).thenReturn(siteSubscriptionRecurringPayment);
		assertThat(service.fetchSiteSubscriptionRecurringPaymentBySiteId(1)).isNotNull()
				.isInstanceOf(SiteSubscriptionRecurringPayment.class);
	}

	@Test
	void testFetchSiteSubscriptionRecurringPaymentBySiteId_Site_Id_Null()
	{
		assertThat(service.fetchSiteSubscriptionRecurringPaymentBySiteId(0)).isNull();
		verifyNoInteractions(dao);
	}

	@Test
	void testInsertSiteSubscriptionRecurringPayment()
	{
		SiteSubscriptionRecurringPayment siteSubscriptionRecurringPayment = mock(SiteSubscriptionRecurringPayment.class);
		when(dao.insertSiteSubscriptionRecurringPayment(any())).thenReturn(true);
		assertThat(service.insertSiteSubscriptionRecurringPayment(siteSubscriptionRecurringPayment)).isTrue();
	}

	@Test
	void testInsertSiteSubscriptionRecurringPayment_SiteSubscriptionRecurringPayment_Null()
	{
		assertThat(service.insertSiteSubscriptionRecurringPayment(null)).isFalse();
		verifyNoInteractions(dao);
	}

	@Test
	void testInsertSubscriptionBookingPayment()
	{
		when(dao.insertSubscriptionBookingPayment(any())).thenReturn(true);
		assertThat(service.insertSubscriptionBookingPayment(mock(SubscriptionBookingPayment.class))).isTrue();
	}

	@Test
	void testInsertSubscriptionBookingPayment_Null()
	{
		assertThat(service.insertSubscriptionBookingPayment(null)).isFalse();
	}

	@Test
	void testFetchSubscriptionBookingPayment()
	{
		SubscriptionBookingPayment bookingPayment = mock(SubscriptionBookingPayment.class);
		when(dao.fetchSubscriptionBookingPayment(anyInt())).thenReturn(bookingPayment);
		assertThat(service.fetchSubscriptionBookingPayment(1)).isNotNull()
				.isInstanceOf(SubscriptionBookingPayment.class);
	}

	@Test
	void testFetchSubscriptionBookingPayment_Id_Zero()
	{
		assertThat(service.fetchSubscriptionBookingPayment(0)).isNull();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchSubscriptionScheduledRecurringPaymentByBookingIdt()
	{
		SubscriptionScheduledRecurringPayment subscriptionScheduledRecurringPayment = mock(SubscriptionScheduledRecurringPayment.class);
		when(dao.fetchSubscriptionScheduledRecurringPaymentByBookingId(anyInt())).thenReturn(Arrays.asList(subscriptionScheduledRecurringPayment));
		assertThat(service.fetchSubscriptionScheduledRecurringPaymentByBookingId(1)).isNotNull()
				.hasOnlyElementsOfType(SubscriptionScheduledRecurringPayment.class);
	}

	@Test
	void testFetchSubscriptionScheduledRecurringPaymentByBookingId_Id_Zero()
	{
		assertThat(service.fetchSubscriptionScheduledRecurringPaymentByBookingId(0)).isEmpty();
		verifyNoInteractions(dao);
	}
	
	@Test
	void testFetchAllSubscriptionBookingDataByReferenceAndEmail()
	{
		List<SubscriptionAllBookingData> bookingDataList = new ArrayList<>();
		SubscriptionAllBookingData mockBookingData = mock(SubscriptionAllBookingData.class);
		bookingDataList.add(mockBookingData);
		when(dao.fetchAllSubscriptionBookingDataByReferenceAndEmail(anyString(), anyString())).thenReturn(bookingDataList);
		assertThat(service.fetchAllSubscriptionBookingDataByReferenceAndEmail("Reference", "Email")).isNotEmpty();
	}
	
	@Test
	void testFetchAllSubscriptionBookingDataByReferenceAndEmail_No_Email()
	{
		assertThat(service.fetchAllSubscriptionBookingDataByReferenceAndEmail("Reference", "")).isEmpty();
		verifyNoInteractions(dao);
	}
	
	@Test
	void testFetchAllSubscriptionBookingDataByReferenceAndEmail_No_Reference()
	{
		assertThat(service.fetchAllSubscriptionBookingDataByReferenceAndEmail("", "Email")).isEmpty();
		verifyNoInteractions(dao);
	}

	@Test
	public void testSaveReservationData()
	{
		when(dao.saveReservationData(any())).thenReturn(true);

		assertTrue(service.saveReservationData(reservationData));
	}

	@Test
	public void testSaveReservationData_False()
	{
		assertFalse(service.saveReservationData(null));
	}

	@Test
	public void testFetchReservationDataByGuid()
	{
		when(dao.fetchReservationDataByGuid(anyString())).thenReturn(reservationData);

		assertNotNull(service.fetchReservationDataByGuid("guid"));
	}

	@Test
	public void testFetchReservationDataByGuid_Null_Guid()
	{
		assertNull(service.fetchReservationDataByGuid(null));
	}

	@Test
	public void testFetchBookingByEncryptedReference()
	{
		when(dao.fetchBookingByEncryptedReference(anyString())).thenReturn(subscriptionBookings);

		assertNotNull(service.fetchBookingByEncryptedReference("test reference"));
	}

	@Test
	public void testFetchBookingByEncryptedReference_Null_Reference()
	{
		assertNull(service.fetchBookingByEncryptedReference(null));
	}

	@Test
	void testSaveLanguage()
	{
		when(dao.saveLanguage(any())).thenReturn(true);
		
		assertTrue(service.saveLanguage(language));
	}
	
	@Test
	void testSaveLanguage_False()
	{
		assertFalse(service.saveLanguage(null));
	}

	@Test
	void testFetchSubscriptionDiscountedRenewalProductId()
	{
		when(dao.fetchSubscriptionDiscountedRenewalProductId(anyInt()))
				.thenReturn(mock(SubscriptionDiscountedRenewal.class));
		
		assertNotNull(service.fetchSubscriptionDiscountedRenewalProductId(123));
	}

	@Test
	void testFetchSubscriptionDiscountedRenewalProductId_Null()
	{
		when(dao.fetchSubscriptionDiscountedRenewalProductId(anyInt())).thenReturn(null);

		assertNull(service.fetchSubscriptionDiscountedRenewalProductId(123));
	}

	@Test
	void testFetchSubscriptionBookingDetailsByEmail()
	{
		when(dao.fetchSubscriptionDetailsByEmail(anyString())).thenReturn(mock(SubscriptionBookingDetails.class));

		assertNotNull(service.fetchSubscriptionBookingDetailsByEmail("test@example.com"));
	}

	@Test
	void testFetchSubscriptionBookingDetailsByEmail_Null_Email()
	{
		assertNull(service.fetchSubscriptionBookingDetailsByEmail(null));
	}

	public void testFetchFeatureFlagValueByKey()
	{
		when(dao.fetchFeatureFlagValueByKey(anyString())).thenReturn("1,2,3");

		assertEquals("1,2,3", service.fetchFeatureFlagValueByKey("feature.flag.key"));
	}

	@Test
	public void testFetchFeatureFlagValueByKey_Empty_Key()
	{
		assertEquals("", service.fetchFeatureFlagValueByKey(""));
	}

	@Test
	public void saveEncryptedReference()
	{
		when(dao.saveEncryptedReference(any())).thenReturn(true);

		assertTrue(service.saveEncryptedReference(mock(SubscriptionBookingEncrypted.class)));
	}

	@Test
	public void saveEncryptedReference_False()
	{
		assertFalse(service.saveEncryptedReference(null));
	}

	@Test
	void testFetchEncryptedReferenceByBookingId()
	{
		when(dao.fetchEncryptedReferenceByBookingId(anyInt())).thenReturn("encryptedRef");
		
		assertEquals("encryptedRef", service.fetchEncryptedReferenceByBookingId(2));
	}
	
	@Test
	void testFetchEncryptedReferenceByBookingId_InvalidBookingId()
	{
		assertEquals("", service.fetchEncryptedReferenceByBookingId(0));
	}

	@Test
	void testSaveSubscriptionCustomValues_Success()
	{
		List<SubscriptionCustomValue> customValues = new ArrayList<>();
		SubscriptionCustomValue customValue1 = new SubscriptionCustomValue();
		customValue1.setCustomKey("key1");
		customValue1.setValue("value1");
		customValue1.setBookingId(1);
		customValues.add(customValue1);

		when(dao.saveSubscriptionCustomValue(anyString(), anyString(), anyInt())).thenReturn(true);

		boolean result = service.saveSubscriptionCustomValues(customValues);

		assertTrue(result);
		verify(dao).saveSubscriptionCustomValue("key1", "value1", 1);
	}

	@Test
	void testSaveSubscriptionCustomValues_Null()
	{
		assertFalse(service.saveSubscriptionCustomValues(null));

		verifyNoInteractions(dao);
	}

	@Test
	void testSaveSubscriptionCustomValues_EmptyList()
	{
		assertFalse(service.saveSubscriptionCustomValues(new ArrayList<>()));

		verifyNoInteractions(dao);
	}

	@Test
	void testSaveSubscriptionCustomValues_FailOnInvalidBookingId()
	{
		List<SubscriptionCustomValue> customValues = new ArrayList<>();
		SubscriptionCustomValue invalidCustomValue = new SubscriptionCustomValue();
		invalidCustomValue.setCustomKey("key1");
		invalidCustomValue.setValue("value1");
		invalidCustomValue.setBookingId(0);
		customValues.add(invalidCustomValue);

		boolean result = service.saveSubscriptionCustomValues(customValues);

		assertFalse(result);
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail()
	{
		List<SubscriptionAllBookingData> bookingDataList = new ArrayList<>();
		SubscriptionAllBookingData mockBookingData = mock(SubscriptionAllBookingData.class);
		bookingDataList.add(mockBookingData);

		when(dao.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(anyString(), anyString()))
				.thenReturn(bookingDataList);

		assertThat(service.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail("Reference", "Email"))
				.isNotEmpty();
	}

	@Test
	void testFetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail_No_Email()
	{
		assertThat(service.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail("Reference", "")).isEmpty();

		verifyNoInteractions(dao);
	}

	@Test
	void testFetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail_No_Reference()
	{
		assertThat(service.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail("", "Email")).isEmpty();

		verifyNoInteractions(dao);
	}

	@Test
	void testSaveSubscriptionBookingMembership()
	{
		when(dao.saveSubscriptionBookingMembership(anyInt(), anyString())).thenReturn(true);

		service.saveSubscriptionBookingMembership(1, "member123");

		verify(dao).saveSubscriptionBookingMembership(anyInt(), anyString());
	}

	@Test
	void testSaveSubscriptionBookingMembership_InvalidBookingId()
	{
		service.saveSubscriptionBookingMembership(0, "member123");

		verifyNoInteractions(dao);
	}

	@Test
	void testSaveSubscriptionBookingMembership_InvalidMembershipId()
	{
		service.saveSubscriptionBookingMembership(1, null);

		verifyNoInteractions(dao);
	}

	@Test
	void test_fetchBookingByReferenceAffiliateIdAndEmail()
	{
		when(dao.fetchBookingByReferenceAffiliateIdAndEmail(anyString(), anyInt(), anyString())).thenReturn(mock(SubscriptionBooking.class));
		assertThat(service.fetchBookingByReferenceAffiliateIdAndEmail("ref", 1, "email" )).isNotNull()
				.isInstanceOf(SubscriptionBooking.class);
	}

	@Test
	void test_fetchBookingByReferenceAffiliateIdAndEmail_InvalidRef()
	{
		assertNull(service.fetchBookingByReferenceAffiliateIdAndEmail("", 1, "email" ));
	}

	@Test
	void test_fetchBookingByReferenceAffiliateIdAndEmail_InvalidAffiliateId()
	{
		assertNull(service.fetchBookingByReferenceAffiliateIdAndEmail("ref", 0, "email" ));
	}

	@Test
	void test_fetchBookingByReferenceAffiliateIdAndEmail_InvalidEmail()
	{
		assertNull(service.fetchBookingByReferenceAffiliateIdAndEmail("ref", 1, "" ));
	}

	@Test
	void testFetchConfirmationGuidByCustomerGuid()
	{
		final String expectedUuid = UUID.randomUUID()
				.toString();
		when(dao.fetchConfirmationGuidByCustomerGuid(anyString())).thenReturn(expectedUuid);

		assertEquals(expectedUuid, service.fetchConfirmationGuidByCustomerGuid(UUID.randomUUID()
				.toString()));
	}

	@Test
	public void testFetchConfirmationGuidByCustomerGuid_InvalidCustomerGuid()
	{
		assertNull(service.fetchConfirmationGuidByCustomerGuid(null));
		verify(dao, never()).fetchConfirmationGuidByCustomerGuid(any());
	}
}