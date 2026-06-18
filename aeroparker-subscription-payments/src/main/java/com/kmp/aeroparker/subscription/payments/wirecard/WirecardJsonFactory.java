package com.kmp.aeroparker.subscription.payments.wirecard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@AllArgsConstructor
@Component
public class WirecardJsonFactory
{
	private static final String REFERENCED_PURCHASE = "referenced-purchase";
	private static final String RECURRING = "recurring";
	private static final String FIRST = "first";
	public static final int SOFORT = 6;
	public static final int PAYPAL = 2;

	private final WirecardReponseValidator wirecardValidator;

	/**
	 * Checks the user payment type and calls the appropriate Request generator
	 * method.
	 * 
	 * @param merchantId
	 * 
	 * @param req
	 * @param userChosenPayment
	 * @param credentials
	 * @param params
	 * @param affiliateName
	 * @return
	 */
	public JsonObject generateJsonRequestDataForPayment(final WirecardRequestParameters requestParameters, final boolean refund,
			final boolean isRecurring)
	{
		JsonObject request = null;
		log.debug("Wirecard payment chosen");
		request = generateJsonRequestDataForCardPayment(requestParameters, refund, isRecurring);
		return request;
	}

	/**
	 * Creates a json "payment" object required for Wirecard payments.
	 * 
	 * @param merchantId
	 * 
	 * @param params
	 * @return
	 */
	private JsonObject generateJsonRequestDataForCardPayment(final WirecardRequestParameters params, final boolean refund, final boolean isRecurring)
	{
		log.debug("Building json object for Wirecard payment");
		JsonObject json = new JsonObject();
		JsonObject payment = new JsonObject();
		JsonObject merchantAccountId = new JsonObject();
		merchantAccountId.addProperty("value", params.getMerchantId());
		payment.add("merchant-account-id", merchantAccountId);
		payment.addProperty("request-id", UUID.randomUUID()
				.toString());
		payment.addProperty("transaction-type", !refund ? "purchase" : "refund-purchase");
		JsonObject requestedAmount = new JsonObject();
		requestedAmount.addProperty("value", params.getAmount());
		requestedAmount.addProperty("currency", !refund ? params.getCurrency() : params.getRefundCurrency());
		payment.add("requested-amount", requestedAmount);
		payment.addProperty("parent-transaction-id", !refund ? params.getParentTransactionId() : params.getTransactionToRefund());
		if (!refund)
		{
			payment.addProperty("order-number", params.getReference());
		}
		if (isRecurring)
		{
			JsonObject periodic = new JsonObject();
			periodic.addProperty("periodic-type", RECURRING);
			periodic.addProperty("sequence-type", FIRST);
			payment.add("periodic", periodic);
		}
		json.add("payment", payment);
		log.debug("Finished building json object for Wirecard payment {}", json.toString());
		return json;
	}

	/**
	 * Used to simply record all Wirecard responses. Takes the whole response
	 * JsonObject
	 * 
	 * @param payment
	 */
	public WirecardPaymentResponse buildWirecardResponse(final JsonObject jsonResponse)
	{
		WirecardPaymentResponse paymentResponse = null;
		if (jsonResponse != null)
		{
			log.debug("Building Wirecard payment response from json object {}", jsonResponse.toString());
			if (wirecardValidator.validatePaymentJsonResponse(jsonResponse))
			{
				paymentResponse = new WirecardPaymentResponse();
				JsonObject payment = jsonResponse.getAsJsonObject("payment");
				JsonObject paymentStatus = payment.getAsJsonObject("statuses")
						.getAsJsonArray("status")
						.get(0)
						.getAsJsonObject();
				paymentResponse.setTransactionState(payment.get("transaction-state")
						.getAsString());
				paymentResponse.setStatusCode(paymentStatus.get("code")
						.getAsString());
				paymentResponse = populateResponse(paymentResponse, payment, paymentStatus);
				log.debug("Finished building Wirecard payment response {}", paymentResponse.toString());
			}
			else
			{
				log.info("Couldn't build Wirecard response, there validation failed: " + jsonResponse);
			}
		}
		return paymentResponse;
	}

	private WirecardPaymentResponse populateResponse(final WirecardPaymentResponse paymentResponse, final JsonObject payment,
			final JsonObject paymentStatus)
	{
		String requestId = getJsonMemberSecurely(payment, "request-id");
		log.debug("Adding remaining wirecard paramenters : {} ", requestId);
		paymentResponse.setOrderNumber(getJsonMemberSecurely(payment, "order-number"));
		paymentResponse.setRequestId(getJsonMemberSecurely(payment, "request-id"));
		paymentResponse.setTransactionType(getJsonMemberSecurely(payment, "transaction-type"));
		paymentResponse.setTransactionId(getJsonMemberSecurely(payment, "transaction-id"));
		paymentResponse.setCompletionTypeStamp(getJsonMemberSecurely(payment, "completion-time-stamp"));
		paymentResponse.setMerchantAccountId(
				payment.has("merchant-account-id") ? getJsonMemberSecurely(payment.getAsJsonObject("merchant-account-id"), "value") : "");
		paymentResponse.setRequestId(getJsonMemberSecurely(payment, "request-id"));
		paymentResponse.setTokenId(getJsonMemberSecurely(paymentStatus, "provider-transaction-id"));
		JsonObject requestedAmount = payment.getAsJsonObject("requested-amount");
		String requestedValue = getJsonMemberSecurely(requestedAmount, "value");
		String requestedCurrency = getJsonMemberSecurely(requestedAmount, "currency");
		BigDecimal reqAmoundBigDecimal = new BigDecimal(requestedValue).setScale(2, RoundingMode.HALF_UP);
		paymentResponse.setRequestedAmount(reqAmoundBigDecimal.toPlainString());
		paymentResponse.setRequestedAmountCurrency(requestedCurrency);
		JsonObject accountHolder = payment.getAsJsonObject("account-holder");
		if (accountHolder != null)
		{
			paymentResponse.setFirstName(getJsonMemberSecurely(accountHolder, "first-name"));
			paymentResponse.setLastName(getJsonMemberSecurely(accountHolder, "last-name"));
			paymentResponse.setEmail(getJsonMemberSecurely(accountHolder, "email"));
			paymentResponse.setPhone(getJsonMemberSecurely(accountHolder, "phone"));
			if (accountHolder.has("address"))
			{
				JsonObject address = accountHolder.getAsJsonObject("address");
				paymentResponse.setStreet1(getJsonMemberSecurely(address, "street1"));
				paymentResponse.setCity(getJsonMemberSecurely(address, "city"));
				paymentResponse.setCountry(getJsonMemberSecurely(address, "country"));
				paymentResponse.setPostalCode(getJsonMemberSecurely(address, "postal-code"));
			}
		}
		paymentResponse.setIpAddress(getJsonMemberSecurely(payment, "ip-address"));
		JsonObject paymentMethods = payment.getAsJsonObject("payment-methods");
		paymentResponse.setPaymentMethodName(paymentMethods.getAsJsonArray("payment-method")
				.get(0)
				.getAsJsonObject()
				.get("name")
				.getAsString());
		// Custom fields
		if (payment.has("card-token"))
		{
			JsonObject cardToken = payment.getAsJsonObject("card-token");
			if (cardToken.has("masked-account-number"))
			{
				paymentResponse.setMaskedAccountNumber(getJsonMemberSecurely(cardToken, "masked-account-number"));
			}
		}

		if (payment.has("authorization-code"))
		{
			paymentResponse.setAuthorizationCode(getJsonMemberSecurely(payment, "authorization-code"));
		}
		if (payment.has("parent-transaction-id"))
		{
			paymentResponse.setParentTransactionId(getJsonMemberSecurely(payment, "parent-transaction-id"));
		}
		return paymentResponse;
	}

	private String getJsonMemberSecurely(final JsonObject jsonObject, final String memberName)
	{
		return jsonObject.has(memberName) ? jsonObject.get(memberName)
				.getAsString() : "";
	}

	public JsonObject generateJsonRequestDataForRecurringPayment(final WirecardRequestParameters params)
	{
		log.debug("Building json object for Wirecard recurring payment");
		JsonObject json = new JsonObject();
		JsonObject payment = new JsonObject();
		JsonObject merchantAccountId = new JsonObject();
		merchantAccountId.addProperty("value", params.getMerchantId());
		payment.add("merchant-account-id", merchantAccountId);
		payment.addProperty("request-id", UUID.randomUUID()
				.toString());
		payment.addProperty("transaction-type", REFERENCED_PURCHASE);
		JsonObject requestedAmount = new JsonObject();
		requestedAmount.addProperty("value", params.getAmount());
		requestedAmount.addProperty("currency", params.getCurrency());
		payment.add("requested-amount", requestedAmount);
		payment.addProperty("parent-transaction-id", params.getParentTransactionId());
		JsonObject periodic = new JsonObject();
		periodic.addProperty("periodic-type", RECURRING);
		periodic.addProperty("sequence-type", RECURRING);
		payment.add("periodic", periodic);
		json.add("payment", payment);
		log.debug("Finished building json object for Wirecard recurring payment {}", json.toString());
		return json;
	}
}