package com.kmp.aeroparker.subscription.payments.wirecard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WirecardPaymentResponse
{
	private String merchantAccountId;
	private String requestId;
	private String transactionId;
	private String transactionType;
	private String transactionState;
	private String completionTypeStamp;
	private String statusCode;
	private String requestedAmount;
	private String requestedAmountCurrency;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String street1;
	private String city;
	private String country;
	private String postalCode;
	private String tokenId;
	private String ipAddress;
	private String orderNumber;
	private String orderDetail;
	private String paymentMethodName;
	private String providerTransactionRef;
	private String maskedAccountNumber;
	private String authorizationCode;
	private String parentTransactionId;
}