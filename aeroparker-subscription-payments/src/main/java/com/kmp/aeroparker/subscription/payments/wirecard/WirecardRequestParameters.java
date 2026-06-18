package com.kmp.aeroparker.subscription.payments.wirecard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WirecardRequestParameters
{
	private int affiliateId;
	private int amend;
	private String amount;
	private String currency;
	private String email;
	private String guid;
	private String languageCode;
	private String merchantId;
	private String originalReference;
	private String parentTransactionId;
	private String reference;
	private String transactionId;

	// Refunds
	private String firstName = "";
	private String lastName = "";
	private String refundCurrency = "";
	private String refundEmail = "";
	private String refundWallet = "";
	private String transactionToRefund = "";

	private String redirectUrl;
	private String servletUrl;
	private String reservationGuid;
	private String affiliateName;
	private int userChosenPayment;
}