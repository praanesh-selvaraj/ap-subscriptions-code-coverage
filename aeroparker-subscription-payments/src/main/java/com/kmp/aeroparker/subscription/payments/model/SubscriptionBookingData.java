package com.kmp.aeroparker.subscription.payments.model;

import java.math.BigDecimal;

import com.kmp.aeroparker.subscription.payments.klix.KlixResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// This booking data can contain any thing we want related to the booking data
// from step 4
public class SubscriptionBookingData
{
	private String title;
	private String fname;
	private String lname;
	private String email;
	private String confEmail;
	private String telno;
	private String addr1;
	private String addr2;
	private String town;
	private String county;
	private String country;
	private String postcode;
	private String carCountry;
	private String carReg;
	private String carmake;
	private String carmodel;
	private String carcol;
	private String passwordChoice;
	private String company;
	private String taxIdentificationNumber;
	private String address1Receipt;
	private String address2Receipt;
	private String postcodeReceipt;
	private String townReceipt;
	private String countyReceipt;
	private String countryReceipt;
	private String companyVatRegistrationNumber;
	private boolean receiptCheckbox;
	private String countryCodeReceipt;
	private String membershipId;
	//Additional Details
	private String referrerMembershipId;

	private int paymentGatewayType;
	private BigDecimal amount;
	private BigDecimal bookingFee;
	private String currency;
	private int carParkId;
	private String timeZone;
	private String dateFormat;
	private int productId;
	private String ConfirmationGuid;
	private int siteId;
	private int affiliateId;
	private String startDate;
	private String bookingReference;
	private String languageCode;
	private String customerGuid;
	private BigDecimal vatRate;
	private int currentLanguageId;
	private boolean emailOptIn;
	private boolean smsOptIn;
	private boolean rightToCancel;
	// Payment
	private String paymentReference;
	private String userChosenPayment;
	private int paymentId;
	private String cardholderName;
	private String paymentIntentId;
	private boolean isPartialPaymentsEnabled;
	private boolean isRenewal;
	private boolean nonCardPayment;
	// product
	private boolean isRecurring;
	private String paymentMethod;
	private String servletAbsoluteUrl;
	private String rawServletAbsoluteUrl;
	private String redirectUrl;
	private KlixResponse klixResponse;
	private boolean isPaymentSuccess;
	private boolean isPaymentFail;
	private String whitelistPaymentMethod;
}