package com.kmp.aeroparker.subscription.payments.klix;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.KlixSession;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class KlixResponseBuilder
{
	private static final String KLIX_IMAGE_BASE_URL = "https://portal.klix.app";
	private static final String KLIX_CARD = "klix_card";
	private static final String KLIX_APPLE_PAY_METHOD = "klix_apple_pay";
	private static final String KLIX_GOOGLE_PAY_METHOD = "klix_google_pay";

	private final PaymentService paymentService;
	private final KlixHttpClient client;

	public KlixResponse buildResponseParameters(JsonObject klixJson)
	{
		KlixResponse parameters = new KlixResponse();

		// Get values set in full json object
		parameters.setId(getString(klixJson.get("id")));
		parameters.setIssued(getString(klixJson.get("issued")));
		parameters.setStatus(getString(klixJson.get("status")));
		parameters.setProduct(getString(klixJson.get("product")));
		parameters.setBrandId(getString(klixJson.get("brand_id")));
		parameters.setClientId(getString(klixJson.get("client_id")));
		parameters.setCreatedOn(getString(klixJson.get("created_on")));
		parameters.setEventType(getString(klixJson.get("event_type")));
		parameters.setIp(getString(klixJson.get("created_from_ip")));
		parameters.setReferenceGenerated(getString(klixJson.get("reference_generated")));
		parameters.setResponseType(getString(klixJson.get("type")));
		parameters.setBookingReferenceFullName(getString(klixJson.get("reference")));
		parameters.setCheckoutUrl(getString(klixJson.get("checkout_url")));
		parameters.setRecurringToken(getBoolean(klixJson.get("is_recurring_token")));

		// Get values set in client json object
		JsonObject client = getJsonObject("client", klixJson);
		parameters.setCity(getString(client.get("city")));
		parameters.setEmail(getString(client.get("email")));
		parameters.setPhone(getString(client.get("phone")));
		parameters.setPostCode(getString(client.get("zip_code")));
		parameters.setFullName(getString(client.get("full_name")));
		parameters.setStreetAddress(getString(client.get("street_address")));

		// Get values set in issuer details json object
		JsonObject issuerDetails = getJsonObject("issuer_details", klixJson);
		parameters.setBrandName(getString(issuerDetails.get("brand_name")));

		// Get values set in payment json object
		JsonObject payment = getJsonObject("payment", klixJson);
		parameters.setPaymentType(getString(payment.get("payment_type")));
		parameters.setPaidOn(getString(payment.get("paid_on")));
		parameters.setCurrency(getString(payment.get("currency")));
		BigDecimal amount = getBigDecimal(payment.get("amount"));
		parameters.setAmount(amount.movePointLeft(2)
				.doubleValue());

		// Get error message and recurring execute from attempts array
		JsonObject transactionData = getJsonObject("transaction_data", klixJson);
		JsonArray attempts = getJsonArray("attempts", transactionData);
		if (attempts.size() > 0)
		{
			JsonObject latestAttempt = attempts.get(attempts.size() - 1)
					.getAsJsonObject();
			parameters.setRecurringExecute("recurring_execute".equals(getString((latestAttempt).get("type"))));
			if (!getBoolean(latestAttempt.get("successful")))
			{
				parameters.setErrorMessage(getString(latestAttempt.get("error")
						.getAsJsonObject()
						.get("message")));
			}
			JsonObject extraData = getJsonObject("extra", latestAttempt);
			parameters.setMaskedPan(getString(extraData.get("masked_pan")));
			parameters.setExpiryDate(getString(extraData.get("card_expiring")));
		}
		else
		{
			parameters.setRecurringExecute(false);
		}
		return parameters;
	}

	public List<KlixPaymentMethodGroup> buildPaymentMethods(JsonObject paymentMethodsJson)
	{
		List<KlixPaymentMethodGroup> paymentMethods = new ArrayList<>();

		JsonObject paymentMethodLogos = getJsonObject("logos", paymentMethodsJson);
		JsonObject paymentMethodNames = getJsonObject("names", paymentMethodsJson);
		JsonArray paymentMethodGroups = getJsonArray("payment_method_groups", paymentMethodsJson);

		for (JsonElement paymentMethod : paymentMethodGroups)
		{
			JsonObject paymentMethodGroup = paymentMethod.getAsJsonObject();
			JsonArray methodsInGroup = getJsonArray("methods", paymentMethodGroup);
			List<String> logoUrls = new ArrayList<>();
			String paymentMethodGroupName = getString(paymentMethodGroup.get("name"));

			if (methodsInGroup.size() > 0)
			{
				// If only one payment method in method group, use logo url provided within group JsonObject
				// Otherwise, use payment method label to find logo urls for each named provider
				if (methodsInGroup.size() == 1)
				{
					String methodName = getString(methodsInGroup.get(0));
					if (!isAppleOrGooglePay(methodName, paymentMethodGroupName))
					{
						logoUrls.add(KLIX_IMAGE_BASE_URL + getString(paymentMethodGroup.get("logo")));
					}
				}
				else
				{
					HashSet<String> uniqueProviders = new HashSet<>();
					for (int y = 0; y < methodsInGroup.size(); ++y)
					{
						String methodName = getString(methodsInGroup.get(y));
						String methodLabel = getString(paymentMethodNames.get(methodName));
						if (isAppleOrGooglePay(methodName, paymentMethodGroupName))
						{
							continue;
						}
						if (uniqueProviders.add(methodLabel))
						{
							String logoUrl = getString(paymentMethodLogos.get(methodName));
							if (!StringUtil.isEmpty(logoUrl))
							{
								logoUrls.add(KLIX_IMAGE_BASE_URL + logoUrl);
							}
						}
					}
				}
				paymentMethods.add(new KlixPaymentMethodGroup(getString(paymentMethodGroup.get("name")), logoUrls,
						getString(paymentMethodGroup.get("label")), methodsInGroup));
			}
		}
		return paymentMethods;
	}

	private JsonObject getJsonObject(String objectName, JsonObject container)
	{
		return !StringUtil.isEmpty(objectName) && container.get(objectName) != null && !container.get(objectName)
				.isJsonNull() ? container.getAsJsonObject(objectName) : new JsonObject();
	}

	private JsonArray getJsonArray(String arrayName, JsonObject container)
	{
		return !StringUtil.isEmpty(arrayName) && container.get(arrayName) != null && !container.get(arrayName)
				.isJsonNull() ? container.getAsJsonArray(arrayName) : new JsonArray();
	}

	private boolean getBoolean(JsonElement element)
	{
		return element != null && !element.isJsonNull() && !element.isJsonArray() ? element.getAsBoolean() : false;
	}

	private String getString(JsonElement element)
	{
		return element != null && !element.isJsonNull() && !element.isJsonArray() ? element.getAsString() : "";
	}

	private BigDecimal getBigDecimal(JsonElement element)
	{
		return element != null && !element.isJsonNull() && !element.isJsonArray() ? element.getAsBigDecimal()
				: BigDecimal.ZERO;
	}
	
	private boolean isAppleOrGooglePay(String methodName, String paymentMethodGroupName)
	{
		return paymentMethodGroupName.equals(KLIX_CARD)
				&& (methodName.equals(KLIX_APPLE_PAY_METHOD) || methodName.equals(KLIX_GOOGLE_PAY_METHOD));
	}
}
