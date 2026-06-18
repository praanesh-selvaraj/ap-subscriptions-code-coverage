package com.kmp.aeroparker.application.builder;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReservationData;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

@Component
public class ReservationBuilderObjectFactory
{
	public SubscriptionBookingReservationData buildReservationData(SubscriptionBookingData bookingData)
	{
		SubscriptionBookingReservationData reservationData = new SubscriptionBookingReservationData();
		if (bookingData != null)
		{
			reservationData.setGuid(bookingData.getCustomerGuid());
			reservationData.setTitle(bookingData.getTitle());
			reservationData.setFirstName(bookingData.getFname());
			reservationData.setLastName(bookingData.getLname());
			reservationData.setPhoneNumber(bookingData.getTelno());
			reservationData.setAddress1(bookingData.getAddr1());
			reservationData.setAddress2(bookingData.getAddr2());
			reservationData.setTown(bookingData.getTown());
			reservationData.setCounty(bookingData.getCounty());
			reservationData.setPostcode(bookingData.getPostcode());
			reservationData.setCountry(bookingData.getCountry());
			reservationData.setEmailAddress(bookingData.getEmail());

			reservationData.setCarRegistration(bookingData.getCarReg());
			reservationData.setCarMake(bookingData.getCarmake());
			reservationData.setCarModel(bookingData.getCarmodel());
			reservationData.setCarCountry(bookingData.getCarCountry());
			reservationData.setCarColour(bookingData.getCarcol());

			reservationData.setCompanyName(bookingData.getCompany());
			reservationData.setTaxIdentificationNumber(bookingData.getTaxIdentificationNumber());
			reservationData.setAddress_1Receipt(bookingData.getAddress1Receipt());
			reservationData.setAddress_2Receipt(bookingData.getAddress2Receipt());
			reservationData.setPostcodeReceipt(bookingData.getPostcodeReceipt());
			reservationData.setCountyReceipt(bookingData.getCountyReceipt());
			reservationData.setCountryReceipt(bookingData.getCountryReceipt());
			reservationData.setTownReceipt(bookingData.getTownReceipt());
			reservationData.setCompanyVatRegistrationNumber(bookingData.getCompanyVatRegistrationNumber());
			reservationData.setCurrentLanguageId(bookingData.getCurrentLanguageId());
			reservationData.setInvoiceRequested(bookingData.isReceiptCheckbox());
			reservationData.setCountryCodeReceipt(bookingData.getCountryCodeReceipt());
			
			reservationData.setReferrerMembershipId(bookingData.getReferrerMembershipId());
			reservationData.setEmailOptIn(bookingData.isEmailOptIn());
		}

		return reservationData;
	}

	public void buildBookingData(SubscriptionBookingReservationData reservationData,
			SubscriptionBookingData bookingData)
	{
		bookingData.setTitle(reservationData.getTitle());
		bookingData.setFname(reservationData.getFirstName());
		bookingData.setLname(reservationData.getLastName());
		bookingData.setTelno(reservationData.getPhoneNumber());
		bookingData.setAddr1(reservationData.getAddress1());
		bookingData.setAddr2(reservationData.getAddress2());
		bookingData.setTown(reservationData.getTown());
		bookingData.setCounty(reservationData.getCounty());
		bookingData.setPostcode(reservationData.getPostcode());
		bookingData.setCountry(reservationData.getCountry());

		bookingData.setCarReg(reservationData.getCarRegistration());
		bookingData.setCarmake(reservationData.getCarMake());
		bookingData.setCarmodel(reservationData.getCarModel());
		bookingData.setCarCountry(reservationData.getCarCountry());
		bookingData.setCarcol(reservationData.getCarColour());

		bookingData.setCompany(reservationData.getCompanyName());
		bookingData.setTaxIdentificationNumber(reservationData.getTaxIdentificationNumber());
		bookingData.setAddress1Receipt(reservationData.getAddress_1Receipt());
		bookingData.setAddress2Receipt(reservationData.getAddress_2Receipt());
		bookingData.setPostcodeReceipt(reservationData.getPostcodeReceipt());
		bookingData.setCountyReceipt(reservationData.getCountyReceipt());
		bookingData.setCountryReceipt(reservationData.getCountryReceipt());
		bookingData.setTownReceipt(reservationData.getTownReceipt());
		bookingData.setCompanyVatRegistrationNumber(reservationData.getCompanyVatRegistrationNumber());
		bookingData.setReceiptCheckbox(reservationData.getInvoiceRequested());
		bookingData.setCountryCodeReceipt(reservationData.getCountryCodeReceipt());

		bookingData.setReferrerMembershipId(reservationData.getReferrerMembershipId());
		bookingData.setEmailOptIn(reservationData.getEmailOptIn());

		bookingData.setCurrentLanguageId(reservationData.getCurrentLanguageId());
	}
}
