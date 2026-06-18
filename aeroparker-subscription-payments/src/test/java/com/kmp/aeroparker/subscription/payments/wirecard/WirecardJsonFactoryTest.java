package com.kmp.aeroparker.subscription.payments.wirecard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.json.utils.JsonUtil;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class WirecardJsonFactoryTest
{
	@Mock
	private WirecardReponseValidator wirecardValidator;
	@InjectMocks
	private WirecardJsonFactory factory;

	@Test
	void testGenerateJsonRequestDataForPayment()
	{
		WirecardRequestParameters parameters = EnhancedRandom.random(WirecardRequestParameters.class);
		assertThat(factory.generateJsonRequestDataForPayment(parameters, false, true)).isNotNull()
				.isInstanceOf(JsonObject.class)
				.toString()
				.contains("order-number:" + parameters.getOriginalReference());
	}

	@Test
	void testGenerateJsonRequestDataForPayment_Refund()
	{
		WirecardRequestParameters parameters = EnhancedRandom.random(WirecardRequestParameters.class);
		assertThat(factory.generateJsonRequestDataForPayment(parameters, true, false)).isNotNull()
				.isInstanceOf(JsonObject.class)
				.toString()
				.contains("order-number:" + parameters.getOriginalReference());
	}

	@Test
	void testBuildWirecardResponse()
	{
		when(wirecardValidator.validatePaymentJsonResponse(any())).thenReturn(true);
		JsonObject jsonObject = JsonUtil.toJsonObject(
				"{\"payment\":{\"statuses\":{\"status\":[{\"code\":\"201.0000\",\"description\":\"3d-acquirer:The resource was successfully created.\",\"severity\":\"information\"}]},\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\",\"ref\":\"https://api-test.wirecard.com:443/engine/rest/config/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"transaction-id\":\"d842e9c8-d660-4f93-b083-676336d1d416\",\"request-id\":\"dd4dce13-c715-4320-afe8-ac97e8b785fa\",\"transaction-type\":\"purchase\",\"transaction-state\":\"success\",\"completion-time-stamp\":1572096220000,\"requested-amount\":{\"value\":11.00,\"currency\":\"EUR\"},\"parent-transaction-id\":\"82567644-617c-4d1b-8da0-2f2bfbbc4efd\",\"account-holder\":{\"first-name\":\"Derrick\",\"last-name\":\"Feehi\"},\"card-token\":{\"token-id\":\"4761217064680000\",\"masked-account-number\":\"420000******0000\"},\"order-number\":\"DTMWSC100581\",\"custom-fields\":{\"custom-field\":[{\"field-name\":\"elastic-api.merchant-origin\",\"field-value\":\"http://localhost:8443\"},{\"field-name\":\"elastic-api.ee.original_txn_type\",\"field-value\":\"authorization-only\"},{\"field-name\":\"elastic-api.integration\",\"field-value\":\"seamless\"}]},\"payment-methods\":{\"payment-method\":[{\"name\":\"creditcard\"}]},\"parent-transaction-amount\":{\"value\":0.000000,\"currency\":\"EUR\"},\"authorization-code\":\"153770\",\"api-id\":\"elastic-api\",\"provider-account-id\":\"56501\",\"self\":\"https://api-test.wirecard.com:443/engine/rest/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711/payments/d842e9c8-d660-4f93-b083-676336d1d416\"}}");
		assertThat(factory.buildWirecardResponse(jsonObject)).isNotNull()
				.hasNoNullFieldsOrPropertiesExcept("city", "country", "postalCode", "orderDetail", "providerTransactionRef", "street1");
		verify(wirecardValidator).validatePaymentJsonResponse(any());
		verifyNoMoreInteractions(wirecardValidator);
	}

	@Test
	void testBuildWirecardResponse_No_MaskedNumber()
	{
		when(wirecardValidator.validatePaymentJsonResponse(any())).thenReturn(true);
		JsonObject jsonObject = JsonUtil.toJsonObject(
				"{\"payment\":{\"statuses\":{\"status\":[{\"code\":\"201.0000\",\"description\":\"3d-acquirer:The resource was successfully created.\",\"severity\":\"information\"}]},\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\",\"ref\":\"https://api-test.wirecard.com:443/engine/rest/config/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"transaction-id\":\"d842e9c8-d660-4f93-b083-676336d1d416\",\"request-id\":\"dd4dce13-c715-4320-afe8-ac97e8b785fa\",\"transaction-type\":\"purchase\",\"transaction-state\":\"success\",\"completion-time-stamp\":1572096220000,\"requested-amount\":{\"value\":11.00,\"currency\":\"EUR\"},\"parent-transaction-id\":\"82567644-617c-4d1b-8da0-2f2bfbbc4efd\",\"account-holder\":{\"first-name\":\"Derrick\",\"last-name\":\"Feehi\"},\"card-token\":{\"token-id\":\"4761217064680000\"},\"order-number\":\"DTMWSC100581\",\"custom-fields\":{\"custom-field\":[{\"field-name\":\"elastic-api.merchant-origin\",\"field-value\":\"http://localhost:8443\"},{\"field-name\":\"elastic-api.ee.original_txn_type\",\"field-value\":\"authorization-only\"},{\"field-name\":\"elastic-api.integration\",\"field-value\":\"seamless\"}]},\"payment-methods\":{\"payment-method\":[{\"name\":\"creditcard\"}]},\"parent-transaction-amount\":{\"value\":0.000000,\"currency\":\"EUR\"},\"authorization-code\":\"153770\",\"api-id\":\"elastic-api\",\"provider-account-id\":\"56501\",\"self\":\"https://api-test.wirecard.com:443/engine/rest/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711/payments/d842e9c8-d660-4f93-b083-676336d1d416\"}}");
		assertThat(factory.buildWirecardResponse(jsonObject)).isNotNull()
				.hasNoNullFieldsOrPropertiesExcept("maskedAccountNumber", "city", "country", "postalCode", "orderDetail", "providerTransactionRef",
						"street1");
		verify(wirecardValidator).validatePaymentJsonResponse(any());
		verifyNoMoreInteractions(wirecardValidator);
	}

	@Test
	void testBuildWirecardResponse_No_MaskedNumdsfsdfber()
	{
		when(wirecardValidator.validatePaymentJsonResponse(any())).thenReturn(true);
		JsonObject jsonObject = JsonUtil.toJsonObject("				{\r\n" + "					\"payment\": {\r\n"
				+ "						\"statuses\": {\r\n" + "							\"status\": [{\r\n"
				+ "								\"code\": \"201.0000\",\r\n"
				+ "								\"description\": \"3 d - acquirer: The resource was successfully created.\",\r\n"
				+ "								\"severity\": \"information \"\r\n" + "							}]\r\n"
				+ "						},\r\n" + "						\"merchant - account - id\": {\r\n"
				+ "							\"value \": \"bb3e3b54 - fb94 - 41 eb - 92 fb - ddf5e0402711\",\r\n"
				+ "							\"ref \": \"https: //api-test.wirecard.com:443/engine/rest/config/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"\r\n"
				+ "						},\r\n" + "						\"transaction-id\": \"d842e9c8-d660-4f93-b083-676336d1d416\",\r\n"
				+ "						\"request-id\": \"dd4dce13-c715-4320-afe8-ac97e8b785fa\",\r\n"
				+ "						\"transaction-type\": \"purchase\",\r\n" + "						\"transaction-state\": \"success\",\r\n"
				+ "						\"completion-time-stamp\": \"1572096220000\",\r\n" + "						\"requested-amount\": {\r\n"
				+ "							\"value\": \"11.00\",\r\n" + "							\"currency\": \"EUR\"\r\n"
				+ "						},\r\n" + "						\"parent-transaction-id\": \"82567644-617c-4d1b-8da0-2f2bfbbc4efd\",\r\n"
				+ "						\"account-holder\": {\r\n" + "							\"first-name\": \"Derrick\",\r\n"
				+ "							\"last-name\": \"Feehi\",\r\n" + "							\"address\": {\r\n"
				+ "								\"city\": \"Manchester\",\r\n" + "								\"country\": \"Uk\"\r\n"
				+ "							}\r\n" + "						},\r\n" + "						\"card-token\": {\r\n"
				+ "							\"token-id\": \"4761217064680000\",\r\n"
				+ "							\"masked-account-number\": \"420000******0000\"\r\n" + "						},\r\n"
				+ "						\"order-number\": \"DTMWSC100581\",\r\n" + "						\"custom-fields\": {\r\n"
				+ "							\"custom-field\": [{\r\n"
				+ "								\"field-name\": \"elastic-api.merchant-origin\",\r\n"
				+ "								\"field-value\": \"http://localhost:8443\"\r\n" + "							}, {\r\n"
				+ "								\"field-name\": \"elastic-api.ee.original_txn_type\",\r\n"
				+ "								\"field-value\": \"authorization-only\"\r\n" + "							}, {\r\n"
				+ "								\"field-name\": \"elastic-api.integration\",\r\n"
				+ "								\"field-value\": \"seamless\"\r\n" + "							}]\r\n"
				+ "						},\r\n" + "						\"payment-methods\": {\r\n"
				+ "							\"payment-method\": [{\r\n" + "								\"name\": \"creditcard\"\r\n"
				+ "							}]\r\n" + "						},\r\n" + "						\"parent-transaction-amount\": {\r\n"
				+ "							\"value\": 0.000000,\r\n" + "							\"currency\": \"EUR\"\r\n"
				+ "						},\r\n" + "						\"authorization-code\": \"153770\",\r\n"
				+ "						\"api-id\": \"elastic-api\",\r\n" + "						\"provider-account-id\": \"56501\",\r\n"
				+ "						\"self\": \"https://api-test.wirecard.com:443/engine/rest/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711/payments/d842e9c8-d660-4f93-b083-676336d1d416\"\r\n"
				+ "					}\r\n" + "				}");
		assertThat(factory.buildWirecardResponse(jsonObject)).isNotNull()
				.hasNoNullFieldsOrPropertiesExcept("maskedAccountNumber", "city", "country", "postalCode", "orderDetail", "providerTransactionRef",
						"street1");
		verify(wirecardValidator).validatePaymentJsonResponse(any());
		verifyNoMoreInteractions(wirecardValidator);
	}

	@Test
	void testBuildWirecardResponse_NoCardToken_No_AuthorizationCode_No_parentTransactionId()
	{
		when(wirecardValidator.validatePaymentJsonResponse(any())).thenReturn(true);
		JsonObject jsonObject = JsonUtil.toJsonObject(
				"{\"payment\":{\"statuses\":{\"status\":[{\"code\":\"201.0000\",\"description\":\"3d-acquirer:The resource was successfully created.\",\"severity\":\"information\"}]},\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\",\"ref\":\"https://api-test.wirecard.com:443/engine/rest/config/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"transaction-id\":\"d842e9c8-d660-4f93-b083-676336d1d416\",\"request-id\":\"dd4dce13-c715-4320-afe8-ac97e8b785fa\",\"transaction-type\":\"purchase\",\"transaction-state\":\"success\",\"completion-time-stamp\":1572096220000,\"requested-amount\":{\"value\":11.00,\"currency\":\"EUR\"},\"account-holder\":{\"first-name\":\"Derrick\",\"last-name\":\"Feehi\"},\"order-number\":\"DTMWSC100581\",\"custom-fields\":{\"custom-field\":[{\"field-name\":\"elastic-api.merchant-origin\",\"field-value\":\"http://localhost:8443\"},{\"field-name\":\"elastic-api.ee.original_txn_type\",\"field-value\":\"authorization-only\"},{\"field-name\":\"elastic-api.integration\",\"field-value\":\"seamless\"}]},\"payment-methods\":{\"payment-method\":[{\"name\":\"creditcard\"}]},\"parent-transaction-amount\":{\"value\":0.000000,\"currency\":\"EUR\"},\"api-id\":\"elastic-api\",\"provider-account-id\":\"56501\",\"self\":\"https://api-test.wirecard.com:443/engine/rest/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711/payments/d842e9c8-d660-4f93-b083-676336d1d416\"}}");
		assertThat(factory.buildWirecardResponse(jsonObject)).isNotNull()
				.hasNoNullFieldsOrPropertiesExcept("maskedAccountNumber", "authorizationCode", "parentTransactionId", "city", "country", "postalCode",
						"orderDetail", "providerTransactionRef", "street1");
		verify(wirecardValidator).validatePaymentJsonResponse(any());
		verifyNoMoreInteractions(wirecardValidator);
	}

	@Test
	void testBuildWirecardResponse_No_account_Holder()
	{
		when(wirecardValidator.validatePaymentJsonResponse(any())).thenReturn(true);
		JsonObject jsonObject = JsonUtil.toJsonObject(
				"{\"payment\":{\"statuses\":{\"status\":[{\"code\":\"201.0000\",\"description\":\"3d-acquirer:The resource was successfully created.\",\"severity\":\"information\"}]},\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\",\"ref\":\"https://api-test.wirecard.com:443/engine/rest/config/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"transaction-id\":\"d842e9c8-d660-4f93-b083-676336d1d416\",\"request-id\":\"dd4dce13-c715-4320-afe8-ac97e8b785fa\",\"transaction-type\":\"purchase\",\"transaction-state\":\"success\",\"completion-time-stamp\":1572096220000,\"requested-amount\":{\"value\":11.00,\"currency\":\"EUR\"},\"parent-transaction-id\":\"82567644-617c-4d1b-8da0-2f2bfbbc4efd\",\"card-token\":{\"token-id\":\"4761217064680000\",\"masked-account-number\":\"420000******0000\"},\"order-number\":\"DTMWSC100581\",\"custom-fields\":{\"custom-field\":[{\"field-name\":\"elastic-api.merchant-origin\",\"field-value\":\"http://localhost:8443\"},{\"field-name\":\"elastic-api.ee.original_txn_type\",\"field-value\":\"authorization-only\"},{\"field-name\":\"elastic-api.integration\",\"field-value\":\"seamless\"}]},\"payment-methods\":{\"payment-method\":[{\"name\":\"creditcard\"}]},\"parent-transaction-amount\":{\"value\":0.000000,\"currency\":\"EUR\"},\"authorization-code\":\"153770\",\"api-id\":\"elastic-api\",\"provider-account-id\":\"56501\",\"self\":\"https://api-test.wirecard.com:443/engine/rest/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711/payments/d842e9c8-d660-4f93-b083-676336d1d416\"}}");
		assertThat(factory.buildWirecardResponse(jsonObject)).isNotNull()
				.hasNoNullFieldsOrPropertiesExcept("firstName", "lastName", "email", "phone", "city", "country", "postalCode", "orderDetail",
						"providerTransactionRef", "street1");
		verify(wirecardValidator).validatePaymentJsonResponse(any());
		verifyNoMoreInteractions(wirecardValidator);
	}

	@Test
	void testBuildWirecardResponse_No_Merchant_Id()
	{
		when(wirecardValidator.validatePaymentJsonResponse(any())).thenReturn(true);
		JsonObject jsonObject = JsonUtil.toJsonObject(
				"{\"payment\":{\"statuses\":{\"status\":[{\"code\":\"201.0000\",\"description\":\"3d-acquirer:The resource was successfully created.\",\"severity\":\"information\"}]},\"test-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\",\"ref\":\"https://api-test.wirecard.com:443/engine/rest/config/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"transaction-id\":\"d842e9c8-d660-4f93-b083-676336d1d416\",\"request-id\":\"dd4dce13-c715-4320-afe8-ac97e8b785fa\",\"transaction-type\":\"purchase\",\"transaction-state\":\"success\",\"completion-time-stamp\":1572096220000,\"requested-amount\":{\"value\":11.00,\"currency\":\"EUR\"},\"parent-transaction-id\":\"82567644-617c-4d1b-8da0-2f2bfbbc4efd\",\"account-holder\":{\"first-name\":\"Derrick\",\"last-name\":\"Feehi\"},\"card-token\":{\"token-id\":\"4761217064680000\",\"masked-account-number\":\"420000******0000\"},\"order-number\":\"DTMWSC100581\",\"custom-fields\":{\"custom-field\":[{\"field-name\":\"elastic-api.merchant-origin\",\"field-value\":\"http://localhost:8443\"},{\"field-name\":\"elastic-api.ee.original_txn_type\",\"field-value\":\"authorization-only\"},{\"field-name\":\"elastic-api.integration\",\"field-value\":\"seamless\"}]},\"payment-methods\":{\"payment-method\":[{\"name\":\"creditcard\"}]},\"parent-transaction-amount\":{\"value\":0.000000,\"currency\":\"EUR\"},\"authorization-code\":\"153770\",\"api-id\":\"elastic-api\",\"provider-account-id\":\"56501\",\"self\":\"https://api-test.wirecard.com:443/engine/rest/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711/payments/d842e9c8-d660-4f93-b083-676336d1d416\"}}");
		assertThat(factory.buildWirecardResponse(jsonObject)).isNotNull()
				.hasNoNullFieldsOrPropertiesExcept("city", "country", "postalCode", "orderDetail", "providerTransactionRef", "street1");
		verify(wirecardValidator).validatePaymentJsonResponse(any());
		verifyNoMoreInteractions(wirecardValidator);
	}

	@Test
	void testBuildWirecardResponse_Validation_Failed()
	{
		when(wirecardValidator.validatePaymentJsonResponse(any())).thenReturn(false);
		JsonObject jsonObject = JsonUtil.toJsonObject(
				"{\"payment\":{\"statuses\":{\"status\":[{\"code\":\"201.0000\",\"description\":\"3d-acquirer:The resource was successfully created.\",\"severity\":\"information\"}]},\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\",\"ref\":\"https://api-test.wirecard.com:443/engine/rest/config/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"transaction-id\":\"d842e9c8-d660-4f93-b083-676336d1d416\",\"request-id\":\"dd4dce13-c715-4320-afe8-ac97e8b785fa\",\"transaction-type\":\"purchase\",\"transaction-state\":\"success\",\"completion-time-stamp\":1572096220000,\"requested-amount\":{\"value\":11.00,\"currency\":\"EUR\"},\"parent-transaction-id\":\"82567644-617c-4d1b-8da0-2f2bfbbc4efd\",\"account-holder\":{\"first-name\":\"Derrick\",\"last-name\":\"Feehi\"},\"card-token\":{\"token-id\":\"4761217064680000\",\"masked-account-number\":\"420000******0000\"},\"order-number\":\"DTMWSC100581\",\"custom-fields\":{\"custom-field\":[{\"field-name\":\"elastic-api.merchant-origin\",\"field-value\":\"http://localhost:8443\"},{\"field-name\":\"elastic-api.ee.original_txn_type\",\"field-value\":\"authorization-only\"},{\"field-name\":\"elastic-api.integration\",\"field-value\":\"seamless\"}]},\"payment-methods\":{\"payment-method\":[{\"name\":\"creditcard\"}]},\"parent-transaction-amount\":{\"value\":0.000000,\"currency\":\"EUR\"},\"authorization-code\":\"153770\",\"api-id\":\"elastic-api\",\"provider-account-id\":\"56501\",\"self\":\"https://api-test.wirecard.com:443/engine/rest/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711/payments/d842e9c8-d660-4f93-b083-676336d1d416\"}}");
		assertThat(factory.buildWirecardResponse(jsonObject)).isNull();
		verify(wirecardValidator).validatePaymentJsonResponse(any());
		verifyNoMoreInteractions(wirecardValidator);
	}

	@Test
	void testBuildWirecardResponse_JsonObject_Null()
	{
		assertThat(factory.buildWirecardResponse(null)).isNull();
		verifyNoInteractions(wirecardValidator);
	}
}