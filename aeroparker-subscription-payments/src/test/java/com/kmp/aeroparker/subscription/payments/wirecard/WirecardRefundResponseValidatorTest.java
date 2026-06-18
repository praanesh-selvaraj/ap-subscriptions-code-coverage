package com.kmp.aeroparker.subscription.payments.wirecard;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonObject;

@ExtendWith(MockitoExtension.class)
class WirecardRefundResponseValidatorTest
{
	@InjectMocks
	private WirecardRefundResponseValidator validator;

	@Test
	void testValidate()
	{
		JsonObject testJson = new JsonObject();
		JsonObject payment = new JsonObject();
		payment.addProperty("transaction-state", "success");
		testJson.add("payment", payment);
		assertThat(validator.validate(testJson)).isTrue();
	}

	@Test
	void testValidate_No_TransactionState_Element()
	{
		JsonObject testJson = new JsonObject();
		JsonObject payment = new JsonObject();
		testJson.add("payment", payment);
		assertThat(validator.validate(testJson)).isFalse();
	}

	@Test
	void testValidate_No_Payment_Element()
	{
		JsonObject testJson = new JsonObject();
		assertThat(validator.validate(testJson)).isFalse();
	}

	@Test
	void testValidate_Response_Null()
	{
		assertThat(validator.validate(null)).isFalse();
	}

	@Test
	void testValidate_False()
	{
		JsonObject testJson = new JsonObject();
		JsonObject payment = new JsonObject();
		payment.addProperty("transaction-state", "failed");
		testJson.add("payment", payment);
		assertThat(validator.validate(testJson)).isFalse();
	}
}