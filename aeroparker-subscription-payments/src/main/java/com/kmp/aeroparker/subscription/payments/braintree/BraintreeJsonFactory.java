package com.kmp.aeroparker.subscription.payments.braintree;

import org.springframework.stereotype.Component;

import com.braintreegateway.Subscription;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.json.utils.JsonUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class BraintreeJsonFactory
{
	public JsonObject convertPaymentToJson(Payments payment)
	{
		JsonObject response = new JsonObject();
		if (payment != null)
		{
			Gson gson = new Gson();
			response = JsonUtil.toJsonObject(gson.toJson(payment));
		}
		return response;
	}

	public JsonObject convertSubscriptionToJson(Subscription subscription)
	{
		JsonObject response = new JsonObject();
		if (subscription != null)
		{
			Gson gson = new Gson();
			response = JsonUtil.toJsonObject(gson.toJson(subscription));
		}
		return response;
	}
}