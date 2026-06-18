package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.builder.BookingBuilder;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.InvoiceService;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.PurchaseRequestList;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.factory.ProcessorFactory;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionCustomValue;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionBookingProcessorTest
{
	@Mock
	private ProcessorFactory processorFactory;
	@Mock
	private BookingService service;
	@Mock
	private BookingBuilder builder;
	@Mock
	private ContactService contactService;
	@InjectMocks
	private SubscriptionBookingProcessor processor;

	@Mock
	private ContactProcessor contactProcessor;
	@Mock
	private SubscriptionGuidProcessor subscriptionGuidProcessor;
	@Mock
	private CustomerDetailsProcessor customerDetailsProcessor;
	@Mock
	private BookingItemProcessor bookingItemProcessor;
	@Mock
	private SubscriptionBookingVehicleDetailsProcessor vehicleDetailsProcessor;
	@Mock
	private SubscriptionBookingReceiptDetailsProcessor receiptDetailsProcessor;
	@Mock
	private PasswordChoiceProcessor passwordChoiceProcessor;
	@Mock
	private SubscriptionBookingLanguageProcessor languageProcessor;
	@Mock
	private BookingCarParkProcessor bookingCarParkProcessor;
	@Mock
	private SubscriptionBookingPaymentProcessor bookingPaymentprocessor;
	@Mock
	private SubscriptionBooking booking;
	@Mock
	private SubscriptionBookingCustomerDetails customerDetails;
	@Mock
	private SubscriptionBookingData bookingData;
	@Mock
	private SubscriptionBookingLanguage language;
	@Mock
	private SubscriptionBookingReceiptDetails receiptDetails;
	@Mock
	private SubscriptionBookingRecord bookingRecord;
	@Mock
	private SubscriptionBookingVehicleDetails vehicleDetails;
	@Mock
	private SubscriptionContactMembership subscriptionContactMembership;
	@Mock
	private SubscriptionMembershipGenerator subscriptionMembershipGenerator;
	@Mock
	private Basket basket;
	@Mock
	private SubscriptionPurchaseRequest purchaseRequest;
	@Mock
	private AffiliateService affiliateService;
	@Mock
	private InvoiceService invoiceService;
	@Mock
	private SubscriptionContactMembershipProcessor subscriptionContactMembershipProcessor;
	@Mock
	private SubscriptionBookingMembershipProcessor subscriptionBookingMembershipProcessor;

	private PurchaseRequestList purchaseRequestList;

	@BeforeEach
	public void setUp()
	{
		purchaseRequestList = new PurchaseRequestList();

		purchaseRequestList.add(purchaseRequest);
	}

	@Test
	void testProcess()
	{
		when(processorFactory.getInstance(ProcessorType.CONTACT, ContactProcessor.class)).thenReturn(contactProcessor);
		when(processorFactory.getInstance(ProcessorType.GUID_BOOKING, SubscriptionGuidProcessor.class)).thenReturn(subscriptionGuidProcessor);
		when(processorFactory.getInstance(ProcessorType.CUSTOMER_DETAILS, CustomerDetailsProcessor.class)).thenReturn(customerDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.VEHICLE_DETAILS, SubscriptionBookingVehicleDetailsProcessor.class)).thenReturn(vehicleDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.RECEIPT_DETAILS, SubscriptionBookingReceiptDetailsProcessor.class)).thenReturn(receiptDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.LANGUAGE, SubscriptionBookingLanguageProcessor.class)).thenReturn(languageProcessor);
		when(processorFactory.getInstance(ProcessorType.BOOKING_PAYMENT, SubscriptionBookingPaymentProcessor.class)).thenReturn(bookingPaymentprocessor);
		when(processorFactory.getInstance(ProcessorType.CAR_PARK, BookingCarParkProcessor.class)).thenReturn(bookingCarParkProcessor);
		when(processorFactory.getInstance(ProcessorType.ITEM_BOOKING, BookingItemProcessor.class)).thenReturn(bookingItemProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP,
				SubscriptionContactMembershipProcessor.class)).thenReturn(subscriptionContactMembershipProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP,
				SubscriptionBookingMembershipProcessor.class)).thenReturn(subscriptionBookingMembershipProcessor);
		when(contactProcessor.process(any())).thenReturn(1);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		when(service.saveBooking(any())).thenReturn(true);
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("guid");
		when(bookingData.getCustomerGuid()).thenReturn("customer_guid");
		when(bookingData.getBookingReference()).thenReturn("bookinfReference");
		when(subscriptionGuidProcessor.process(anyInt(), anyInt(), anyString(), anyString())).thenReturn(true);
		when(bookingData.getSiteId()).thenReturn(1);
		when(bookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(vehicleDetails);
		when(bookingRecord.getReceiptDetails()).thenReturn(receiptDetails);
		when(bookingRecord.getLanguage()).thenReturn(language);
		when(subscriptionContactMembershipProcessor.process(anyInt(), anyInt(), anyInt())).thenReturn("membership_id");
		List<SubscriptionCustomValue> customValues = new ArrayList<>();
		SubscriptionCustomValue customValue = new SubscriptionCustomValue();
		customValues.add(customValue);
		when(bookingRecord.getCustomValues()).thenReturn(customValues);
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem subscriptionProductQuote = EnhancedRandom.random(SubscriptionBookingItem.class);
		BookingSeasonTicket bookingSeasonTicket = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(subscriptionProductQuote, bookingSeasonTicket);
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		when(booking.getReference()).thenReturn("ref");
		when(bookingItemProcessor.process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), anyMap()))
				.thenReturn(true);
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		when(purchaseRequest.getProductId()).thenReturn(2);

		assertThat(processor.process(bookingData, basket)).isTrue();

		verify(service).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor).process(anyInt(), any());
		verify(subscriptionGuidProcessor).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(vehicleDetailsProcessor).process(anyInt(), any());
		verify(receiptDetailsProcessor).process(anyInt(), any());
		verify(bookingPaymentprocessor).process(anyInt(), anyString());
		verify(passwordChoiceProcessor, (times(0))).process(anyInt(), anyString());
		verify(bookingCarParkProcessor).process(anyInt(), anyInt());
		verify(subscriptionContactMembershipProcessor).process(anyInt(), anyInt(), anyInt());
		verify(subscriptionBookingMembershipProcessor).process(anyInt(), anyString());
		verify(processorFactory, times(11)).getInstance(any(), any());
		verify(service).saveEncryptedReference(any());
		verify(service).saveSubscriptionCustomValues(anyList());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				passwordChoiceProcessor, bookingPaymentprocessor);
	}

	@Test
	void testProcess_BookingItemMap_Empty()
	{
		when(processorFactory.getInstance(ProcessorType.CONTACT, ContactProcessor.class)).thenReturn(contactProcessor);
		when(processorFactory.getInstance(ProcessorType.GUID_BOOKING, SubscriptionGuidProcessor.class)).thenReturn(subscriptionGuidProcessor);
		when(processorFactory.getInstance(ProcessorType.CUSTOMER_DETAILS, CustomerDetailsProcessor.class)).thenReturn(customerDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.VEHICLE_DETAILS, SubscriptionBookingVehicleDetailsProcessor.class)).thenReturn(vehicleDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.RECEIPT_DETAILS, SubscriptionBookingReceiptDetailsProcessor.class)).thenReturn(receiptDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.BOOKING_PAYMENT, SubscriptionBookingPaymentProcessor.class)).thenReturn(bookingPaymentprocessor);
		when(processorFactory.getInstance(ProcessorType.CAR_PARK, BookingCarParkProcessor.class)).thenReturn(bookingCarParkProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP,
				SubscriptionContactMembershipProcessor.class)).thenReturn(subscriptionContactMembershipProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP,
				SubscriptionBookingMembershipProcessor.class)).thenReturn(subscriptionBookingMembershipProcessor);
		when(contactProcessor.process(any())).thenReturn(1);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		when(service.saveBooking(any())).thenReturn(true);
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getSiteId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("guid");
		when(bookingData.getCustomerGuid()).thenReturn("customer_guid");
		when(bookingData.getBookingReference()).thenReturn("bookinfReference");
		when(subscriptionGuidProcessor.process(anyInt(), anyInt(), anyString(), anyString())).thenReturn(true);
		when(bookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(vehicleDetails);
		when(bookingRecord.getReceiptDetails()).thenReturn(receiptDetails);
		List<SubscriptionCustomValue> customValues = new ArrayList<>();
		SubscriptionCustomValue customValue = new SubscriptionCustomValue();
		customValues.add(customValue);
		when(bookingRecord.getCustomValues()).thenReturn(customValues);
		when(subscriptionContactMembershipProcessor.process(anyInt(), anyInt(), anyInt())).thenReturn("membership_id");
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		when(purchaseRequest.getProductId()).thenReturn(2);

		assertThat(processor.process(bookingData, basket)).isFalse();

		verify(service).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor).process(anyInt(), any());
		verify(subscriptionGuidProcessor).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(vehicleDetailsProcessor).process(anyInt(), any());
		verify(receiptDetailsProcessor).process(anyInt(), any());
		verify(bookingPaymentprocessor).process(anyInt(), anyString());
		verify(subscriptionContactMembershipProcessor).process(anyInt(), anyInt(), anyInt());
		verify(subscriptionBookingMembershipProcessor).process(anyInt(), anyString());
		verify(processorFactory, times(9)).getInstance(any(), any());
		verify(service).saveEncryptedReference(any());
		verify(service).saveSubscriptionCustomValues(anyList());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				bookingPaymentprocessor);
	}

	@Test
	void testProcess_CustomerDetails_Empty()
	{
		when(processorFactory.getInstance(ProcessorType.CONTACT, ContactProcessor.class)).thenReturn(contactProcessor);
		when(processorFactory.getInstance(ProcessorType.GUID_BOOKING, SubscriptionGuidProcessor.class)).thenReturn(subscriptionGuidProcessor);
		when(processorFactory.getInstance(ProcessorType.VEHICLE_DETAILS, SubscriptionBookingVehicleDetailsProcessor.class)).thenReturn(vehicleDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.RECEIPT_DETAILS, SubscriptionBookingReceiptDetailsProcessor.class)).thenReturn(receiptDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.BOOKING_PAYMENT, SubscriptionBookingPaymentProcessor.class)).thenReturn(bookingPaymentprocessor);
		when(processorFactory.getInstance(ProcessorType.CAR_PARK, BookingCarParkProcessor.class)).thenReturn(bookingCarParkProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP,
				SubscriptionContactMembershipProcessor.class)).thenReturn(subscriptionContactMembershipProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP,
				SubscriptionBookingMembershipProcessor.class)).thenReturn(subscriptionBookingMembershipProcessor);
		when(contactProcessor.process(any())).thenReturn(1);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		when(service.saveBooking(any())).thenReturn(true);
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getSiteId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("guid");
		when(bookingData.getCustomerGuid()).thenReturn("customer_guid");
		when(bookingData.getBookingReference()).thenReturn("bookinfReference");
		when(subscriptionGuidProcessor.process(anyInt(), anyInt(), anyString(), anyString())).thenReturn(true);
		when(bookingRecord.getCustomerDetails()).thenReturn(null);
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(vehicleDetails);
		when(bookingRecord.getReceiptDetails()).thenReturn(receiptDetails);
		List<SubscriptionCustomValue> customValues = new ArrayList<>();
		SubscriptionCustomValue customValue = new SubscriptionCustomValue();
		customValues.add(customValue);
		when(bookingRecord.getCustomValues()).thenReturn(customValues);
		when(subscriptionContactMembershipProcessor.process(anyInt(), anyInt(), anyInt())).thenReturn("membership_id");
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		when(purchaseRequest.getProductId()).thenReturn(2);

		assertThat(processor.process(bookingData, basket)).isFalse();

		verify(service).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor, times(0)).process(anyInt(), any());
		verify(subscriptionGuidProcessor).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(processorFactory, times(8)).getInstance(any(), any());
		verify(vehicleDetailsProcessor).process(anyInt(), any());
		verify(receiptDetailsProcessor).process(anyInt(), any());
		verify(bookingPaymentprocessor).process(anyInt(), anyString());
		verify(subscriptionContactMembershipProcessor).process(anyInt(), anyInt(), anyInt());
		verify(subscriptionBookingMembershipProcessor).process(anyInt(), anyString());
		verify(service).saveEncryptedReference(any());
		verify(service).saveSubscriptionCustomValues(anyList());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				bookingPaymentprocessor);
	}

	@Test
	void testProcess_GuidProcessor_False()
	{
		when(processorFactory.getInstance(any(), any())).thenReturn(contactProcessor)
				.thenReturn(subscriptionGuidProcessor);
		when(contactProcessor.process(any())).thenReturn(1);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		when(service.saveBooking(any())).thenReturn(true);
		when(bookingData.getConfirmationGuid()).thenReturn("guid");
		when(bookingData.getCustomerGuid()).thenReturn("customer_guid");
		when(subscriptionGuidProcessor.process(anyInt(), anyInt(), anyString(), anyString())).thenReturn(false);
		assertThat(processor.process(bookingData, mock(Basket.class))).isFalse();
		verify(service).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor, times(0)).process(anyInt(), any());
		verify(subscriptionGuidProcessor).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(processorFactory, times(2)).getInstance(any(), any());
		verify(vehicleDetailsProcessor, times(0)).process(anyInt(), any());
		verify(bookingPaymentprocessor, times(0)).process(anyInt(), anyString());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				bookingPaymentprocessor);
	}

	@Test
	void testProcess_Booking_Not_Saved()
	{
		when(processorFactory.getInstance(any(), any())).thenReturn(contactProcessor)
				.thenReturn(subscriptionGuidProcessor);
		when(contactProcessor.process(any())).thenReturn(1);;
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		when(service.saveBooking(any())).thenReturn(false);
		assertThat(processor.process(bookingData, mock(Basket.class))).isFalse();
		verify(service).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor, times(0)).process(anyInt(), any());
		verify(subscriptionGuidProcessor, times(0)).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(processorFactory, times(1)).getInstance(any(), any());
		verify(vehicleDetailsProcessor, times(0)).process(anyInt(), any());
		verify(bookingPaymentprocessor, times(0)).process(anyInt(), anyString());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				bookingPaymentprocessor);
	}

	@Test
	void testProcess_Booking_Null()
	{
		when(processorFactory.getInstance(any(), any())).thenReturn(contactProcessor);
		when(contactProcessor.process(any())).thenReturn(1);
		when(bookingRecord.getBooking()).thenReturn(null);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		assertThat(processor.process(bookingData, mock(Basket.class))).isFalse();
		verify(service, times(0)).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor, times(0)).process(anyInt(), any());
		verify(subscriptionGuidProcessor, times(0)).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(processorFactory, times(1)).getInstance(any(), any());
		verify(vehicleDetailsProcessor, times(0)).process(anyInt(), any());
		verify(bookingPaymentprocessor, times(0)).process(anyInt(), anyString());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				bookingPaymentprocessor);
	}

	@Test
	void testProcess_Contact_Not_Processed()
	{
		when(processorFactory.getInstance(any(), any())).thenReturn(contactProcessor);
		when(contactProcessor.process(any())).thenReturn(0);
		assertThat(processor.process(bookingData, mock(Basket.class))).isFalse();
		verify(service, times(0)).saveBooking(any());
		verify(builder, times(0)).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor, times(0)).process(anyInt(), any());
		verify(subscriptionGuidProcessor, times(0)).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(processorFactory, times(1)).getInstance(any(), any());
		verify(vehicleDetailsProcessor, times(0)).process(anyInt(), any());
		verify(bookingPaymentprocessor, times(0)).process(anyInt(), anyString());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				bookingPaymentprocessor);
	}

	@Test
	void testProcess_VehicleDetails_Empty()
	{
		when(processorFactory.getInstance(ProcessorType.CONTACT, ContactProcessor.class)).thenReturn(contactProcessor);
		when(processorFactory.getInstance(ProcessorType.GUID_BOOKING, SubscriptionGuidProcessor.class)).thenReturn(subscriptionGuidProcessor);
		when(processorFactory.getInstance(ProcessorType.BOOKING_PAYMENT, SubscriptionBookingPaymentProcessor.class)).thenReturn(bookingPaymentprocessor);
		when(processorFactory.getInstance(ProcessorType.CAR_PARK, BookingCarParkProcessor.class)).thenReturn(bookingCarParkProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP,
				SubscriptionContactMembershipProcessor.class)).thenReturn(subscriptionContactMembershipProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP,
				SubscriptionBookingMembershipProcessor.class)).thenReturn(subscriptionBookingMembershipProcessor);
		when(contactProcessor.process(any())).thenReturn(1);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		when(service.saveBooking(any())).thenReturn(true);
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getSiteId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("guid");
		when(bookingData.getCustomerGuid()).thenReturn("customer_guid");
		when(bookingData.getBookingReference()).thenReturn("bookinfReference");
		when(subscriptionGuidProcessor.process(anyInt(), anyInt(), anyString(), anyString())).thenReturn(true);
		when(bookingRecord.getCustomerDetails()).thenReturn(null);
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(null);
		when(subscriptionContactMembershipProcessor.process(anyInt(), anyInt(), anyInt())).thenReturn("membership_id");
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		when(purchaseRequest.getProductId()).thenReturn(2);

		assertThat(processor.process(bookingData, basket)).isFalse();

		verify(service).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor, times(0)).process(anyInt(), any());
		verify(subscriptionGuidProcessor).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(bookingPaymentprocessor).process(anyInt(), anyString());
		verify(subscriptionContactMembershipProcessor).process(anyInt(), anyInt(), anyInt());
		verify(subscriptionBookingMembershipProcessor).process(anyInt(), anyString());
		verify(processorFactory, times(6)).getInstance(any(), any());
		verify(service).saveEncryptedReference(any());
		verify(service).saveSubscriptionCustomValues(anyList());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				bookingPaymentprocessor);
	}

	@Test
	void testProcess_ReceiptDetails_Empty()
	{
		when(processorFactory.getInstance(ProcessorType.CONTACT, ContactProcessor.class)).thenReturn(contactProcessor);
		when(processorFactory.getInstance(ProcessorType.GUID_BOOKING, SubscriptionGuidProcessor.class)).thenReturn(subscriptionGuidProcessor);
		when(processorFactory.getInstance(ProcessorType.BOOKING_PAYMENT, SubscriptionBookingPaymentProcessor.class)).thenReturn(bookingPaymentprocessor);
		when(processorFactory.getInstance(ProcessorType.CAR_PARK, BookingCarParkProcessor.class)).thenReturn(bookingCarParkProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP,
				SubscriptionContactMembershipProcessor.class)).thenReturn(subscriptionContactMembershipProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP,
				SubscriptionBookingMembershipProcessor.class)).thenReturn(subscriptionBookingMembershipProcessor);
		when(contactProcessor.process(any())).thenReturn(1);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		when(service.saveBooking(any())).thenReturn(true);
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getSiteId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("guid");
		when(bookingData.getCustomerGuid()).thenReturn("customer_guid");
		when(bookingData.getBookingReference()).thenReturn("bookinfReference");
		when(subscriptionGuidProcessor.process(anyInt(), anyInt(), anyString(), anyString())).thenReturn(true);
		when(bookingRecord.getCustomerDetails()).thenReturn(null);
		when(bookingRecord.getReceiptDetails()).thenReturn(null);
		when(subscriptionContactMembershipProcessor.process(anyInt(), anyInt(), anyInt())).thenReturn("membership_id");
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		when(purchaseRequest.getProductId()).thenReturn(2);

		assertThat(processor.process(bookingData, basket)).isFalse();

		verify(service).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor, times(0)).process(anyInt(), any());
		verify(subscriptionGuidProcessor).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor, times(0)).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(bookingPaymentprocessor).process(anyInt(), anyString());
		verify(processorFactory, times(6)).getInstance(any(), any());
		verify(bookingPaymentprocessor).process(anyInt(), anyString());
		verify(service).saveEncryptedReference(any());
		verify(service).saveSubscriptionCustomValues(anyList());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				bookingPaymentprocessor);
	}

	@Test
	void testProcess_NoMembershipId()
	{
		when(processorFactory.getInstance(ProcessorType.CONTACT, ContactProcessor.class)).thenReturn(contactProcessor);
		when(processorFactory.getInstance(ProcessorType.GUID_BOOKING, SubscriptionGuidProcessor.class)).thenReturn(subscriptionGuidProcessor);
		when(processorFactory.getInstance(ProcessorType.CUSTOMER_DETAILS, CustomerDetailsProcessor.class)).thenReturn(customerDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.VEHICLE_DETAILS, SubscriptionBookingVehicleDetailsProcessor.class)).thenReturn(vehicleDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.RECEIPT_DETAILS, SubscriptionBookingReceiptDetailsProcessor.class)).thenReturn(receiptDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.LANGUAGE, SubscriptionBookingLanguageProcessor.class)).thenReturn(languageProcessor);
		when(processorFactory.getInstance(ProcessorType.BOOKING_PAYMENT, SubscriptionBookingPaymentProcessor.class)).thenReturn(bookingPaymentprocessor);
		when(processorFactory.getInstance(ProcessorType.CAR_PARK, BookingCarParkProcessor.class)).thenReturn(bookingCarParkProcessor);
		when(processorFactory.getInstance(ProcessorType.ITEM_BOOKING, BookingItemProcessor.class)).thenReturn(bookingItemProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP,
				SubscriptionContactMembershipProcessor.class)).thenReturn(subscriptionContactMembershipProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP,
				SubscriptionBookingMembershipProcessor.class)).thenReturn(subscriptionBookingMembershipProcessor);
		when(contactProcessor.process(any())).thenReturn(1);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		when(service.saveBooking(any())).thenReturn(true);
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("guid");
		when(bookingData.getCustomerGuid()).thenReturn("customer_guid");
		when(bookingData.getBookingReference()).thenReturn("bookinfReference");
		when(subscriptionGuidProcessor.process(anyInt(), anyInt(), anyString(), anyString())).thenReturn(true);
		when(bookingData.getSiteId()).thenReturn(1);

		when(bookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(vehicleDetails);
		when(bookingRecord.getReceiptDetails()).thenReturn(receiptDetails);
		when(bookingRecord.getLanguage()).thenReturn(language);
		when(subscriptionContactMembershipProcessor.process(anyInt(), anyInt(), anyInt())).thenReturn("membership_id");
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem subscriptionProductQuote = EnhancedRandom.random(SubscriptionBookingItem.class);
		BookingSeasonTicket bookingSeasonTicket = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(subscriptionProductQuote, bookingSeasonTicket);
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		when(booking.getReference()).thenReturn("ref");
		when(bookingItemProcessor.process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), anyMap()))
				.thenReturn(true);
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		when(purchaseRequest.getProductId()).thenReturn(2);

		assertTrue(processor.process(bookingData, basket));
		
		verify(service).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor).process(anyInt(), any());
		verify(subscriptionGuidProcessor).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(vehicleDetailsProcessor).process(anyInt(), any());
		verify(receiptDetailsProcessor).process(anyInt(), any());
		verify(bookingPaymentprocessor).process(anyInt(), anyString());
		verify(passwordChoiceProcessor, (times(0))).process(anyInt(), anyString());
		verify(subscriptionContactMembershipProcessor).process(anyInt(), anyInt(), anyInt());
		verify(subscriptionBookingMembershipProcessor).process(anyInt(), anyString());
		verify(processorFactory, times(11)).getInstance(any(), any());
		verify(service).saveEncryptedReference(any());
		verify(service).saveSubscriptionCustomValues(anyList());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				passwordChoiceProcessor, bookingPaymentprocessor);
	}

	@Test
	void testProcess_MembershipId_NotSaved()
	{
		when(processorFactory.getInstance(ProcessorType.CONTACT, ContactProcessor.class)).thenReturn(contactProcessor);
		when(processorFactory.getInstance(ProcessorType.GUID_BOOKING, SubscriptionGuidProcessor.class)).thenReturn(subscriptionGuidProcessor);
		when(processorFactory.getInstance(ProcessorType.CUSTOMER_DETAILS, CustomerDetailsProcessor.class)).thenReturn(customerDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.VEHICLE_DETAILS, SubscriptionBookingVehicleDetailsProcessor.class)).thenReturn(vehicleDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.RECEIPT_DETAILS, SubscriptionBookingReceiptDetailsProcessor.class)).thenReturn(receiptDetailsProcessor);
		when(processorFactory.getInstance(ProcessorType.LANGUAGE, SubscriptionBookingLanguageProcessor.class)).thenReturn(languageProcessor);
		when(processorFactory.getInstance(ProcessorType.BOOKING_PAYMENT, SubscriptionBookingPaymentProcessor.class)).thenReturn(bookingPaymentprocessor);
		when(processorFactory.getInstance(ProcessorType.CAR_PARK, BookingCarParkProcessor.class)).thenReturn(bookingCarParkProcessor);
		when(processorFactory.getInstance(ProcessorType.ITEM_BOOKING, BookingItemProcessor.class)).thenReturn(bookingItemProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP,
				SubscriptionContactMembershipProcessor.class)).thenReturn(subscriptionContactMembershipProcessor);
		when(processorFactory.getInstance(ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP,
				SubscriptionBookingMembershipProcessor.class)).thenReturn(subscriptionBookingMembershipProcessor);
		when(contactProcessor.process(any())).thenReturn(1);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getId()).thenReturn(1);
		when(builder.build(anyInt(), any(), any())).thenReturn(bookingRecord);
		when(service.saveBooking(any())).thenReturn(true);
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("guid");
		when(bookingData.getCustomerGuid()).thenReturn("customer_guid");
		when(bookingData.getBookingReference()).thenReturn("bookinfReference");
		when(subscriptionGuidProcessor.process(anyInt(), anyInt(), anyString(), anyString())).thenReturn(true);
		when(bookingData.getSiteId()).thenReturn(1);
		when(bookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(vehicleDetails);
		when(bookingRecord.getReceiptDetails()).thenReturn(receiptDetails);
		when(bookingRecord.getLanguage()).thenReturn(language);
		when(subscriptionContactMembershipProcessor.process(anyInt(), anyInt(), anyInt())).thenReturn("membership_id");
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem subscriptionProductQuote = EnhancedRandom.random(SubscriptionBookingItem.class);
		BookingSeasonTicket bookingSeasonTicket = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(subscriptionProductQuote, bookingSeasonTicket);
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		when(booking.getReference()).thenReturn("ref");
		when(bookingItemProcessor.process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), anyMap()))
				.thenReturn(true);
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		when(purchaseRequest.getProductId()).thenReturn(2);
		when(bookingData.getRawServletAbsoluteUrl()).thenReturn("http://localhost:8448/subscription/");
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(invoiceService.saveInvoiceUrl(anyInt(), anyString())).thenReturn(true);

		assertThat(processor.process(bookingData, basket)).isTrue();

		verify(service).saveBooking(any());
		verify(builder).build(anyInt(), any(), any());
		verify(contactProcessor).process(any());
		verify(customerDetailsProcessor).process(anyInt(), any());
		verify(subscriptionGuidProcessor).process(anyInt(), anyInt(), anyString(), anyString());
		verify(bookingItemProcessor).process(anyInt(), anyInt(), anyInt(), anyInt(), anyString(), any());
		verify(vehicleDetailsProcessor).process(anyInt(), any());
		verify(receiptDetailsProcessor).process(anyInt(), any());
		verify(bookingPaymentprocessor).process(anyInt(), anyString());
		verify(subscriptionContactMembershipProcessor).process(anyInt(), anyInt(), anyInt());
		verify(subscriptionBookingMembershipProcessor).process(anyInt(), anyString());
		verify(passwordChoiceProcessor, (times(0))).process(anyInt(), anyString());
		verify(processorFactory, times(11)).getInstance(any(), any());
		verify(service).saveEncryptedReference(any());
		verify(service).saveSubscriptionCustomValues(anyList());
		verifyNoMoreInteractions(service, builder, processorFactory, contactProcessor, customerDetailsProcessor,
				subscriptionGuidProcessor, bookingItemProcessor, vehicleDetailsProcessor, receiptDetailsProcessor,
				passwordChoiceProcessor, bookingPaymentprocessor);
	}
}