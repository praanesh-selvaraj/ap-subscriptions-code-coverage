package com.kmp.aeroparker.subscription.payments.klix;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KlixResponse
{
	private String id;
	private String bookingReferenceFullName;
	private String bookingReference;
	private String originalBookingReference;
	private String redirectUrl;
	private String issued;
	private String status;
	private String product;
	private String brandId;
	private String clientId;
	private String createdOn;
	private String paidOn;
	private String ip;
	private String referenceGenerated;
	private String eventType;
	private String city;
	private String email;
	private String phone;
	private String postCode;
	private String fullName;
	private String streetAddress;
	private String currency;
	private String brandName;
	private String paymentType;
	private String responseType;
	private String errorMessage;
	private String checkoutUrl;
	private String maskedPan;
	private String expiryDate;
	private int carParkId;
	private double amount;
	private boolean isRecurringToken;
	private boolean isRecurringExecute;
	private boolean isAmend;
}
