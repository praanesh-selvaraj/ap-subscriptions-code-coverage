package com.kmp.aeroparker.application.builder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseAgreement;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.model.enums.SubscriptionKeyEnum;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionCustomValue;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

@Component
public class BookingBuilderObjectFactory
{
	public SubscriptionBookingItem buildBookingItemList(final Timestamp created, final SubscriptionPurchaseRequest purchaseRequest)
	{
		SubscriptionBookingItem bookingItem = new SubscriptionBookingItem();
		bookingItem.setCarParkId(purchaseRequest.getCarParkId());
		bookingItem.setProductId(purchaseRequest.getProductId());
		bookingItem.setSubTotal(purchaseRequest.getGrandTotal());
		bookingItem.setPeriodType(purchaseRequest.getPeriodType()
				.toString());
		SubscriptionPurchaseAgreement purchaseAgreement = purchaseRequest.getPurchaseAgreement();
		bookingItem.setVatRate(purchaseAgreement.getVatRate());
		bookingItem.setVatAmount(purchaseAgreement.getVatAmount());
		bookingItem.setProductDisplayName(purchaseRequest.getDisplayName());
		bookingItem.setCarParkName(purchaseRequest.getCarPark()
				.getName());
		bookingItem.setBookingFee(purchaseRequest.getBookingFee());
		bookingItem.setCreated(created);
		return bookingItem;
	}

	public SubscriptionBookingCustomerDetails buildCustomerDetails(final SubscriptionBookingData bookingData)
	{
		SubscriptionBookingCustomerDetails customerDetails = new SubscriptionBookingCustomerDetails();
		customerDetails.setTitle(bookingData.getTitle());
		customerDetails.setFirstName(bookingData.getFname());
		customerDetails.setLastName(bookingData.getLname());
		customerDetails.setEmailAddress(bookingData.getEmail());
		customerDetails.setPhoneNumber(bookingData.getTelno());
		customerDetails.setAddress1(bookingData.getAddr1());
		customerDetails.setAddress2(bookingData.getAddr2());
		customerDetails.setTown(bookingData.getTown());
		customerDetails.setCounty(bookingData.getCounty());
		customerDetails.setPostcode(bookingData.getPostcode());
		customerDetails.setCountry(bookingData.getCountry());
		return customerDetails;
	}

	public SubscriptionBooking buildBooking(final int affId, final Timestamp created, final int contactId,
			final Basket basket, final String bookingReference)
	{
		SubscriptionBooking booking = new SubscriptionBooking();
		booking.setAffiliateId(affId);
		booking.setCreated(created);
		booking.setContactId(contactId);
		booking.setReference(bookingReference);
		booking.setGrandTotal(basket.getGrandTotal());
		booking.setVatAmount(basket.getGrandTotalVatAmount());
		booking.setBookingFee(basket.getTotalBookingFee());
		return booking;
	}

	public SubscriptionBookingVehicleDetails buildVehicleDetails(final SubscriptionBookingData bookingData)
	{
		SubscriptionBookingVehicleDetails vehicleDetails = new SubscriptionBookingVehicleDetails();
		vehicleDetails.setCarRegistration(bookingData.getCarReg());
		vehicleDetails.setCarMake(bookingData.getCarmake());
		vehicleDetails.setCarModel(bookingData.getCarmodel());
		vehicleDetails.setCarCountry(bookingData.getCarCountry());
		vehicleDetails.setCarColour(bookingData.getCarcol());
		return vehicleDetails;
	}
	
	public SubscriptionBookingReceiptDetails buildReceiptDetails(final SubscriptionBookingData bookingData)
	{
		SubscriptionBookingReceiptDetails receiptDetails = new SubscriptionBookingReceiptDetails();
		receiptDetails.setCompanyName(bookingData.getCompany());
		receiptDetails.setTaxIdentificationNumber(bookingData.getTaxIdentificationNumber());
		receiptDetails.setAddress_1Receipt(bookingData.getAddress1Receipt());
		receiptDetails.setAddress_2Receipt(bookingData.getAddress2Receipt());
		receiptDetails.setPostcodeReceipt(bookingData.getPostcodeReceipt());
		receiptDetails.setCountyReceipt(bookingData.getCountyReceipt());
		receiptDetails.setCountryReceipt(bookingData.getCountryReceipt());
		receiptDetails.setTownReceipt(bookingData.getTownReceipt());
		receiptDetails.setCompanyVatRegistrationNumber(bookingData.getCompanyVatRegistrationNumber());
		receiptDetails.setCountryCodeReceipt(bookingData.getCountryCodeReceipt());
		return receiptDetails;
	}
	
	public SubscriptionBookingLanguage buildLanguage(final SubscriptionBookingData bookingData)
	{
		SubscriptionBookingLanguage language = new SubscriptionBookingLanguage();
		language.setLanguageId(bookingData.getCurrentLanguageId());
		return language;
	}

	public List<SubscriptionCustomValue> buildCustomValues(final SubscriptionBookingData bookingData)
	{
		List<SubscriptionCustomValue> customValues = new ArrayList<>();
		customValues.add(createCustomValue(SubscriptionKeyEnum.REFERRER_MEMBERSHIP_ID, bookingData.getReferrerMembershipId()));
		return customValues;
	}

	private SubscriptionCustomValue createCustomValue(SubscriptionKeyEnum key, String value)
	{
		SubscriptionCustomValue customValue = new SubscriptionCustomValue();
		customValue.setCustomKey(key.toString());
		customValue.setValue(value);
		return customValue;
	}
}