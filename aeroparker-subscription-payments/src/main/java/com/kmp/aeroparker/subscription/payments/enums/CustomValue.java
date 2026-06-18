package com.kmp.aeroparker.subscription.payments.enums;

public enum CustomValue
{
	ACCEPTANCE_CODE("acceptanceCode"),
	AFFILIATION_NUMBER("affiliationNumber"),
	AUTH_TIME("authTime"),
	CURRENCY("currency"),
	ORIGINAL_RECEIPT("originalReceipt"),
	PAYMENT_TYPE("paymentType"),
	PARENT_TRANSACTION_ID("parentTransactionId"),
	PSP_ID("pspId"),
	SECURITY_KEY("securityKey"),
	TOKEN("token"),
	TRANSACTION_AUTH_NO("txAuthNo"),
	VENDOR_TX_CODE("vendorTxCode"),

	CARD_NUMBER("cardNumber"),
	CARD_HOLDER_NAME("cardHolderName"),
	CARD_EXPIRY_MONTH("cardExpiryMonth"),
	CARD_EXPIRY_YEAR("cardExpiryYear"),
	CARD_EXPIRY_DATE("cardExpiryDate"),
	CARD_SCHEME("cardScheme"),
	CARD_TYPE("cardType"),

	CUSTOMER_NAME("customerName"),
	FIRST_NAME("firstName"),
	LAST_NAME("lastName"),
	STREET1("street1"),
	COUNTY("county"),
	POSTAL_CODE("postcode"),
	EMAIL_ADDRESS("emailAddress"),
	BRAINTREE_SUB_ID("braintreeSubId");
	
	private String field;
	
	private CustomValue(String field)
	{
		this.field = field;
	}
	
	public String getField()
	{
		return field;
	}
}