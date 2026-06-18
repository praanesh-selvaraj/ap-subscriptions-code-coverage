package com.kmp.aeroparker.subscription.payments.wirecard;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

@ExtendWith(MockitoExtension.class)
class WirecardResponsePaymentElementValidatorTest
{
	@InjectMocks
	private WirecardResponsePaymentElementValidator validator;

	private final JsonObject payment = new JsonObject();;

	@BeforeEach
	void init()
	{
		JsonArray statusArray = new JsonArray();
		JsonArray paymentMethodArray = new JsonArray();
		JsonObject accountHolder = new JsonObject();
		JsonObject address = new JsonObject();
		JsonObject cardToken = new JsonObject();
		JsonObject firstStatus = new JsonObject();
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
		paymentMethod.addProperty("name", "creditcard");
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
		paymentValue.addProperty("value", "10");
		paymentValue.addProperty("currency", "EUR");
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
	}

	@Test
	void testValidatePaymentJsonResponse()
	{
		assertThat(validator.validate(payment)).isTrue();
	}

	@Test
	void testValidatePaymentJsonResponse_No_Transaction_State()
	{
		payment.remove("transaction-state");
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_Transaction_Failed()
	{
		payment.remove("transaction-state");
		payment.addProperty("transaction-state", "failed");
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_No_PaymentMethods()
	{
		payment.remove("payment-methods");
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_No_PaymentMethod()
	{
		JsonObject paymentMethod = payment.get("payment-methods")
				.getAsJsonObject();
		paymentMethod.remove("payment-method");
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_PaymentMethod_Empty()
	{
		JsonArray methods = payment.get("payment-methods")
				.getAsJsonObject()
				.get("payment-method")
				.getAsJsonArray();
		methods.remove(0);
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_PaymentMethod_Not_CreditCard()
	{
		JsonArray methods = payment.get("payment-methods")
				.getAsJsonObject()
				.get("payment-method")
				.getAsJsonArray();
		JsonObject payMethod = methods.get(0)
				.getAsJsonObject();
		payMethod.remove("name");
		payMethod.addProperty("name", "paypal");
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_PaymentMethod_Null()
	{
		JsonArray methods = payment.get("payment-methods")
				.getAsJsonObject()
				.get("payment-method")
				.getAsJsonArray();
		methods.remove(0);
		methods.add(JsonNull.INSTANCE);
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_No_Transaction_Id()
	{
		payment.remove("transaction-id");
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_No_Request_Id()
	{
		payment.remove("request-id");
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_No_Request_Amount()
	{
		payment.remove("requested-amount");
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_No_Amount_Value()
	{
		JsonObject amount = payment.getAsJsonObject("requested-amount");
		amount.remove("value");
		assertThat(validator.validate(payment)).isFalse();
	}

	@Test
	void testValidatePaymentJsonResponse_No_Amount_Currency()
	{
		JsonObject amount = payment.getAsJsonObject("requested-amount");
		amount.remove("currency");
		assertThat(validator.validate(payment)).isFalse();
	}
}