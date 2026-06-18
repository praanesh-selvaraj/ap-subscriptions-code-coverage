package com.kmp.aeroparker.application.processor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kmp.aeroparker.application.builder.BookingBuilder;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.InvoiceService;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.factory.ProcessorFactory;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.i18n.stringutil.StringUtil;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingEncrypted;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionCustomValue;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.security.utils.SecurityUtil;
import com.kmp.aeroparker.application.builder.SubscriptionBookingUrlBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionBookingProcessor
{
	private static final String INVOICE_URL_FORMAT = "%s/subscriptions/%s/SubscriptionInvoice?encryptedRef=%s";
	private final BookingService service;
	private final BookingBuilder builder;
	private final ContactService contactService;
	private final InvoiceService invoiceService;
	private final AffiliateService affiliateService;
	private final ProcessorFactory processorFactory;

	public boolean process(final SubscriptionBookingData bookingData, final Basket basket)
	{
		boolean status = false;
		int affId = bookingData.getAffiliateId();
		int contactId = processorFactory.getInstance(ProcessorType.CONTACT, ContactProcessor.class)
				.process(bookingData);
		if (contactId != 0)
		{
			// Create subscription booking record
			SubscriptionBookingRecord bookingRecord = builder.build(contactId, bookingData, basket);
			// get the booking
			SubscriptionBooking booking = bookingRecord.getBooking();
			if (booking != null)
			{
				if (service.saveBooking(booking))
				{
					int bookingId = booking.getId();
					if (processorFactory.getInstance(ProcessorType.GUID_BOOKING, SubscriptionGuidProcessor.class)
							.process(affId, bookingId, bookingData.getConfirmationGuid(),
									bookingData.getCustomerGuid()))
					{
						int siteId = bookingData.getSiteId();
						SubscriptionBookingCustomerDetails customerDetails = bookingRecord.getCustomerDetails();
						processCustomerBookingDetails(bookingId, customerDetails);
						SubscriptionBookingVehicleDetails vehicleDetails =
								bookingRecord.getSubscriptionBookingVehicleDetails();
						SubscriptionBookingReceiptDetails receiptDetails = bookingRecord.getReceiptDetails();
						SubscriptionBookingLanguage language = bookingRecord.getLanguage();
						List<SubscriptionCustomValue> customValues = bookingRecord.getCustomValues();

						processVehicleDetails(bookingId, vehicleDetails);
						processReceiptDetails(bookingId, receiptDetails);
						processLanguage(bookingId, language);
						processBookingPayment(bookingId, bookingData.getBookingReference());
						SubscriptionBookingEncrypted subscriptionBookingEncrypted =
								buildEncryptedBookingReference(bookingId, bookingData.getBookingReference());
						service.saveEncryptedReference(subscriptionBookingEncrypted);
						saveCustomValues(bookingId, customValues);
						String membershipId = processContactMembership(affId, siteId, contactId);
						processBookingMembership(bookingId, membershipId);
						if (StringUtils.hasText(membershipId))
						{
							bookingData.setMembershipId(membershipId);
						}
						processBookingCarParks(bookingId, basket.getPurchaseRequestList().get(0).getProductId());
						Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = bookingRecord.getBookingItemMap();
						status = processBookingItem(bookingData, status, affId, booking, bookingId, bookingItemMap);

						String encryptedReference = subscriptionBookingEncrypted.getEncryptedReference();
						String invoiceUrl = createInvoiceUrl(bookingData, bookingId, encryptedReference);
						invoiceService.saveInvoiceUrl(bookingId, invoiceUrl);
					}
				}
			}
		}
		return status;
	}

	private void processCustomerBookingDetails(int bookingId, SubscriptionBookingCustomerDetails customerDetails)
	{
		if (customerDetails != null)
		{
			processorFactory.getInstance(ProcessorType.CUSTOMER_DETAILS, CustomerDetailsProcessor.class)
					.process(bookingId, customerDetails);
		}
	}

	private boolean processBookingItem(final SubscriptionBookingData bookingData, boolean status, int affId,
			SubscriptionBooking booking, int bookingId, Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap)
	{
		if (!bookingItemMap.isEmpty())
		{
			status = processorFactory.getInstance(ProcessorType.ITEM_BOOKING, BookingItemProcessor.class)
					.process(affId, bookingData.getSiteId(), bookingId, booking.getContactId(), booking.getReference(),
							bookingItemMap);
		}
		return status;
	}

	private void processVehicleDetails(int bookingId, SubscriptionBookingVehicleDetails vehicleDetails)
	{
		if (vehicleDetails != null)
		{
			processorFactory
					.getInstance(ProcessorType.VEHICLE_DETAILS, SubscriptionBookingVehicleDetailsProcessor.class)
					.process(bookingId, vehicleDetails);
		}
	}

	private void processReceiptDetails(int bookingId, SubscriptionBookingReceiptDetails receiptDetails)
	{
		if (receiptDetails != null)
		{
			processorFactory
					.getInstance(ProcessorType.RECEIPT_DETAILS, SubscriptionBookingReceiptDetailsProcessor.class)
					.process(bookingId, receiptDetails);
		}
	}

	private void processLanguage(int bookingId, SubscriptionBookingLanguage subscriptionBookingLanguage)
	{
		if (subscriptionBookingLanguage != null)
		{
			processorFactory
					.getInstance(ProcessorType.LANGUAGE, SubscriptionBookingLanguageProcessor.class)
					.process(bookingId, subscriptionBookingLanguage);
		}
	}

	private void processBookingPayment(int bookingId, String bookingReference)
	{
		processorFactory.getInstance(ProcessorType.BOOKING_PAYMENT, SubscriptionBookingPaymentProcessor.class)
				.process(bookingId, bookingReference);
	}

	private SubscriptionBookingEncrypted buildEncryptedBookingReference(int bookingId, String bookingReference)
	{
		SubscriptionBookingEncrypted subscriptionBookingEncrypted = new SubscriptionBookingEncrypted();
		subscriptionBookingEncrypted.setSubscriptionBookingId(bookingId);
		subscriptionBookingEncrypted.setEncryptedReference(SecurityUtil.encryptString(bookingReference));

		return subscriptionBookingEncrypted;
	}

	private void saveCustomValues(int bookingId, List<SubscriptionCustomValue> customValues)
	{
		customValues.stream()
				.forEach(customValue -> customValue.setBookingId(bookingId));
		service.saveSubscriptionCustomValues(customValues);
	}

	private String processContactMembership(int affiliateId, int siteId, int contactId)
	{
		return processorFactory
				.getInstance(ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP, SubscriptionContactMembershipProcessor.class)
				.process(affiliateId, siteId, contactId);
	}

	private void processBookingMembership(int bookingId, String membershipId)
	{
		processorFactory
				.getInstance(ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP, SubscriptionBookingMembershipProcessor.class)
				.process(bookingId, membershipId);
	}

	private void processBookingCarParks(int bookingId, int productId)
	{
		processorFactory
				.getInstance(ProcessorType.CAR_PARK, BookingCarParkProcessor.class)
				.process(bookingId, productId);
	}

	private String createInvoiceUrl(final SubscriptionBookingData bookingData, int bookingId, String encryptedReference)
	{
		String baseUrl = SubscriptionBookingUrlBuilder.extractBaseUrl(bookingData.getRawServletAbsoluteUrl());
		if (StringUtil.isNullOrEmpty(baseUrl) || bookingId <= 0 || StringUtil.isNullOrEmpty(encryptedReference))
		{
			return null;
		}

		String affiliateCode = affiliateService.fetchAffiliateCodeById(bookingData.getAffiliateId());
		return String.format(INVOICE_URL_FORMAT, baseUrl, affiliateCode,
				encryptedReference);
	}
}