package com.kmp.aeroparker.subscription.payments.klix;

import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class KlixRequest
{
	String successRedirect;
	String failureRedirect;
	String successCallback;
	String language;
	String productName;
	String amount;
	String email;
	Affiliates affiliate;
	String firstName;
	String originalBookingReference;
	String city;
	String zipCode;
	String streetAddress;
	KlixCredentials credentials;
	String currency;
	String phoneNumber;
	String country;
	String lastName;
	String guid;
	String bookingReference;
	int carParkId;
	String savedCardToken;
	boolean userLoggedIn;
	boolean isAmend;
	String paymentMethod;
}
