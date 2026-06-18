package com.kmp.aeroparker.subscription.payments.wirecard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.json.utils.JsonUtil;
import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;

@ExtendWith(MockitoExtension.class)
class WirecardClientTest
{
	@Mock
	private CloseableHttpClient httpClient;

	@InjectMocks
	private WirecardClient sendRequest;

	@Mock
	private CloseableHttpResponse response;
	@Mock
	private HttpPost post;
	@Mock
	private WirecardCredentials wirecardCredentials;

	@Test
	void testSendPaymentRequest() throws ClientProtocolException, IOException
	{
		when(wirecardCredentials.getRestUser()).thenReturn("");
		when(wirecardCredentials.getRestPassword()).thenReturn("");
		when(wirecardCredentials.getCreditCardRestUrl()).thenReturn("");
		StringEntity entity = new StringEntity(
				"{\"payment\":{\"statuses\":{\"status\":[{\"code\":\"201.0000\",\"description\":\"3d-acquirer:The resource was successfully created.\",\"severity\":\"information\"}]},\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\",\"ref\":\"https://api-test.wirecard.com:443/engine/rest/config/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"transaction-id\":\"d842e9c8-d660-4f93-b083-676336d1d416\",\"request-id\":\"dd4dce13-c715-4320-afe8-ac97e8b785fa\",\"transaction-type\":\"purchase\",\"transaction-state\":\"success\",\"completion-time-stamp\":1572096220000,\"requested-amount\":{\"value\":11.00,\"currency\":\"EUR\"},\"parent-transaction-id\":\"82567644-617c-4d1b-8da0-2f2bfbbc4efd\",\"account-holder\":{\"first-name\":\"Derrick\",\"last-name\":\"Feehi\"},\"card-token\":{\"token-id\":\"4761217064680000\",\"masked-account-number\":\"420000******0000\"},\"order-number\":\"DTMWSC100581\",\"custom-fields\":{\"custom-field\":[{\"field-name\":\"elastic-api.merchant-origin\",\"field-value\":\"http://localhost:8443\"},{\"field-name\":\"elastic-api.ee.original_txn_type\",\"field-value\":\"authorization-only\"},{\"field-name\":\"elastic-api.integration\",\"field-value\":\"seamless\"}]},\"payment-methods\":{\"payment-method\":[{\"name\":\"creditcard\"}]},\"parent-transaction-amount\":{\"value\":0.000000,\"currency\":\"EUR\"},\"authorization-code\":\"153770\",\"api-id\":\"elastic-api\",\"provider-account-id\":\"56501\",\"self\":\"https://api-test.wirecard.com:443/engine/rest/merchants/bb3e3b54-fb94-41eb-92fb-ddf5e0402711/payments/d842e9c8-d660-4f93-b083-676336d1d416\"}}");
		JsonObject jsonObject = JsonUtil.toJsonObject(
				"{payment={\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"request-id\":\"5c9da03b-d2e6-4d82-b068-d24db3441804\",\"transaction-type\":\"purchase\",\"requested-amount\":{\"value\":\"11.00\",\"currency\":\"EUR\"},\"parent-transaction-id\":\"1f7383ab-2bb4-4f11-aba4-e7894633fad1\",\"order-number\":\"DTMWSC100582\"}}");
		when(response.getEntity()).thenReturn(entity);
		when(httpClient.execute(any(HttpPost.class))).thenReturn(response);
		assertThat(sendRequest.sendPaymentRequest(wirecardCredentials, jsonObject)).isNotNull()
				.isInstanceOf(JsonObject.class);
	}

	@Test
	void testSendPaymentRequest_Response_Null() throws ClientProtocolException, IOException
	{
		when(wirecardCredentials.getRestUser()).thenReturn("");
		when(wirecardCredentials.getRestPassword()).thenReturn("");
		when(wirecardCredentials.getCreditCardRestUrl()).thenReturn("");
		JsonObject jsonObject = JsonUtil.toJsonObject(
				"{payment={\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"request-id\":\"5c9da03b-d2e6-4d82-b068-d24db3441804\",\"transaction-type\":\"purchase\",\"requested-amount\":{\"value\":\"11.00\",\"currency\":\"EUR\"},\"parent-transaction-id\":\"1f7383ab-2bb4-4f11-aba4-e7894633fad1\",\"order-number\":\"DTMWSC100582\"}}");
		when(httpClient.execute(any(HttpPost.class))).thenReturn(null);
		assertThat(sendRequest.sendPaymentRequest(wirecardCredentials, jsonObject)).isNull();
	}

	@Test
	void testSendPaymentRequest_IOException() throws ClientProtocolException, IOException
	{
		when(wirecardCredentials.getRestUser()).thenReturn("");
		when(wirecardCredentials.getRestPassword()).thenReturn("");
		when(wirecardCredentials.getCreditCardRestUrl()).thenReturn("");
		JsonObject jsonObject = JsonUtil.toJsonObject(
				"{payment={\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"},\"request-id\":\"5c9da03b-d2e6-4d82-b068-d24db3441804\",\"transaction-type\":\"purchase\",\"requested-amount\":{\"value\":\"11.00\",\"currency\":\"EUR\"},\"parent-transaction-id\":\"1f7383ab-2bb4-4f11-aba4-e7894633fad1\",\"order-number\":\"DTMWSC100582\"}}");
		when(httpClient.execute(any(HttpPost.class))).thenThrow(IOException.class);
		assertThat(sendRequest.sendPaymentRequest(wirecardCredentials, jsonObject)).isNull();
	}
}