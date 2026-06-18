package com.kmp.aeroparker.subscription.payments.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentHandlerBean
{
	private boolean success;
	private boolean redirectRequired;
	private boolean isStripeRedirect;
	private Map<String, String> redirectParams;
	private String checkoutUrl;
}
