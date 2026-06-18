package com.kmp.aeroparker.subscription.payments.wirecard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WirecardAjaxResponse
{
	private String request_time_stamp;
	private String request_id;
	private String merchant_account_id;
	private String transaction_type;
	private String requested_amount;
	private String requested_amount_currency;
	private String request_signature;
	private String payment_method;
	private String amount;
	private String servletcontext;
}