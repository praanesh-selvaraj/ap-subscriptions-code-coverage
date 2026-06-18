package com.kmp.aeroparker.subscription.payments.wirecard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@ExtendWith(MockitoExtension.class)
class WirecardReponseValidatorTest
{
	@Mock
	private WirecardResponsePaymentElementValidator paymentElementValidator;
	@InjectMocks
	private WirecardReponseValidator validator;

	private final JsonObject testJson = new JsonObject();

	@BeforeEach
	void init()
	{
		JsonArray statusArray = new JsonArray();
		JsonArray paymentMethodArray = new JsonArray();
		JsonObject accountHolder = new JsonObject();
		JsonObject address = new JsonObject();
		JsonObject cardToken = new JsonObject();
		JsonObject firstStatus = new JsonObject();
		JsonObject payment = new JsonObject();
		JsonObject paymentMethod = new JsonObject();
		JsonObject paymentMethods = new JsonObject();
		JsonObject paymentValue = new JsonObject();
		JsonObject statuses = new JsonObject();
		JsonObject wallet = new JsonObject();

		firstStatus.addProperty("severity", "severity");
		firstStatus.addProperty("code", "200.0000");
		firstStatus.addProperty("description", "all ok");
		statusArray.add(firstStatus);
		statuses.add("status", statusArray);

		paymentMethodArray.add(paymentMethod);
		paymentMethods.add("payment-method", paymentMethodArray);

		accountHolder.addProperty("first-name", "first-name");
		accountHolder.addProperty("last-name", "last-name");
		accountHolder.addProperty("email", "test@email.com");
		accountHolder.addProperty("phone", "012345");

		address.addProperty("street1", "street1");
		address.addProperty("city", "city");
		address.addProperty("country", "country");
		address.addProperty("postal-code", "postal-code");

		accountHolder.add("address", address);

		wallet.addProperty("account-id", "paypalwailletid123");

		payment.add("statuses", statuses);
		payment.add("requested-amount", paymentValue);
		payment.add("payment-methods", paymentMethods);
		payment.add("account-holder", accountHolder);
		payment.addProperty("transaction-state", "success");

		payment.add("wallet", wallet);

		payment.addProperty("transaction-id", "test-id");
		payment.addProperty("transaction-type", "debit");
		payment.addProperty("transaction-state", "success");
		payment.addProperty("request-id", "request-id");

		cardToken.addProperty("token-id", "token-id");
		cardToken.addProperty("masked-account-number", "123456******1234");
		payment.add("card-token", cardToken);

		payment.addProperty("parent-transaction-id", "test");
		payment.addProperty("authorization-code", "authCode");

		testJson.add("payment", payment);
	}

	@Test
	void testValidatePaymentJsonResponse()
	{
		when(paymentElementValidator.validate(any())).thenReturn(true);
		assertThat(validator.validatePaymentJsonResponse(testJson)).isTrue();
	}

	@Test
	void testValidatePaymentJsonResponse_Response_Null()
	{
		assertThat(validator.validatePaymentJsonResponse(null)).isFalse();
		verifyNoInteractions(paymentElementValidator);
	}

	@Test
	void testValidatePaymentJsonResponse_No_Payment_Element()
	{
		testJson.remove("payment");
		assertThat(validator.validatePaymentJsonResponse(testJson)).isFalse();
		verifyNoInteractions(paymentElementValidator);
	}

	@Test
	void testValidatePaymentJsonResponse_No_Statuses_Element()
	{
		JsonObject payment = testJson.getAsJsonObject("payment");
		payment.remove("statuses");
		assertThat(validator.validatePaymentJsonResponse(testJson)).isFalse();
		verifyNoInteractions(paymentElementValidator);
	}

	@Test
	void testValidatePaymentJsonResponse_No_Status_Element()
	{
		JsonObject payment = testJson.getAsJsonObject("payment");
		JsonObject statuses = payment.get("statuses")
				.getAsJsonObject();
		statuses.remove("status");
		assertThat(validator.validatePaymentJsonResponse(testJson)).isFalse();
		verifyNoInteractions(paymentElementValidator);
	}

	@Test
	void testValidatePaymentJsonResponse_No_Code_Element()
	{
		JsonObject payment = testJson.getAsJsonObject("payment");
		JsonArray statuses = payment.get("statuses")
				.getAsJsonObject()
				.getAsJsonArray("status");
		JsonObject paymentStatus = statuses.get(0)
				.getAsJsonObject();
		paymentStatus.remove("code");
		assertThat(validator.validatePaymentJsonResponse(testJson)).isFalse();
		verifyNoInteractions(paymentElementValidator);
	}

	@Test
	void testValidatePaymentJsonResponse_Statuses_Empty()
	{
		JsonObject payment = testJson.getAsJsonObject("payment");
		JsonArray statuses = payment.get("statuses")
				.getAsJsonObject()
				.getAsJsonArray("status");
		statuses.remove(0);
		assertThat(validator.validatePaymentJsonResponse(testJson)).isFalse();
		verifyNoInteractions(paymentElementValidator);
	}

	@Test
	void testValidatePaymentJsonResponse_Code_Wromg_Pattern()
	{
		JsonObject payment = testJson.getAsJsonObject("payment");
		JsonArray statuses = payment.get("statuses")
				.getAsJsonObject()
				.getAsJsonArray("status");
		JsonObject paymentStatus = statuses.get(0)
				.getAsJsonObject();
		paymentStatus.remove("code");
		paymentStatus.addProperty("code", "0000");
		assertThat(validator.validatePaymentJsonResponse(testJson)).isFalse();
		verifyNoInteractions(paymentElementValidator);
	}

	@Test
	void testValidatePaymentJsonResponse_No_Description_Element()
	{
		JsonObject payment = testJson.getAsJsonObject("payment");
		JsonArray statuses = payment.get("statuses")
				.getAsJsonObject()
				.getAsJsonArray("status");
		JsonObject paymentStatus = statuses.get(0)
				.getAsJsonObject();
		paymentStatus.remove("description");
		assertThat(validator.validatePaymentJsonResponse(testJson)).isFalse();
		verifyNoInteractions(paymentElementValidator);
	}

	@Test
	void testValidatePaymentJsonResponse_Severity_Error()
	{
		JsonObject payment = testJson.getAsJsonObject("payment");
		JsonArray statuses = payment.get("statuses")
				.getAsJsonObject()
				.getAsJsonArray("status");
		JsonObject paymentStatus = statuses.get(0)
				.getAsJsonObject();
		paymentStatus.remove("severity");
		paymentStatus.addProperty("severity", "error");
		assertThat(validator.validatePaymentJsonResponse(testJson)).isFalse();
		verifyNoInteractions(paymentElementValidator);
	}

	@Test
	void testValidatePaymentJsonResponse_PaymentElementValidator_False()
	{
		when(paymentElementValidator.validate(any())).thenReturn(false);
		assertThat(validator.validatePaymentJsonResponse(testJson)).isFalse();
	}
}