package com.kmp.aeroparker.subscription.payments.klix;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;

@ExtendWith(MockitoExtension.class)
class KlixHttpClientTest
{
	@InjectMocks
	private KlixHttpClient klixClient;
	@Mock
	private CloseableHttpClient client;
	@Mock
	private KlixCredentials credentials;

	@BeforeEach
	public void init()
	{
		when(credentials.getSecretKey()).thenReturn("secretKey");
		when(credentials.getApiUrl()).thenReturn("apiUrl");
	}

	@Test
	public void testSendPaymentRequest() throws ClientProtocolException, IOException
	{
		CloseableHttpResponse response = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);
		HttpEntity entity = EntityBuilder.create()
				.setText("{\"id\":1}")
				.build();

		when(client.execute(any(HttpPost.class))).thenReturn(response);
		when(response.getEntity()).thenReturn(entity);

		assertNotNull(klixClient.sendPaymentRequest(credentials, new JsonObject()));
	}

	@Test
	public void testSendPaymentRequest_Null_Response() throws ClientProtocolException, IOException
	{
		when(client.execute(any(HttpPost.class))).thenReturn(null);

		assertNull(klixClient.sendPaymentRequest(credentials, new JsonObject()));
	}

	@Test
	public void testSendRefundRequest() throws ClientProtocolException, IOException
	{
		CloseableHttpResponse response = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);
		HttpEntity entity = EntityBuilder.create()
				.setText("{\"id\":1}")
				.build();

		when(client.execute(any(HttpPost.class))).thenReturn(response);
		when(response.getEntity()).thenReturn(entity);

		assertNotNull(klixClient.sendRefundRequest(credentials, "id", new JsonObject()));
	}

	@Test
	public void testGetKlixResponse() throws ClientProtocolException, IOException
	{
		CloseableHttpResponse response = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);
		HttpEntity entity = EntityBuilder.create()
				.setText("{\"id\":1}")
				.build();

		when(client.execute(any(HttpGet.class))).thenReturn(response);
		when(response.getEntity()).thenReturn(entity);

		assertNotNull(klixClient.getKlixResponse(credentials, "id"));
	}

	@Test
	public void testGetPaymentMethods() throws ClientProtocolException, IOException
	{
		CloseableHttpResponse response = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);
		HttpEntity entity = EntityBuilder.create()
				.setText("{\"id\":1}")
				.build();

		when(client.execute(any(HttpGet.class))).thenReturn(response);
		when(response.getEntity()).thenReturn(entity);

		assertNotNull(klixClient.getPaymentMethods(credentials, "id"));
	}

	@Test
	public void testSendPaymentRequest_IOException() throws ClientProtocolException, IOException
	{
		when(client.execute(any(HttpPost.class))).thenThrow(IOException.class);

		assertNull(klixClient.sendPaymentRequest(credentials, new JsonObject()));
	}

	@Test
	public void testSendPaymentRequest_Null_body() throws ClientProtocolException, IOException
	{
		assertNull(klixClient.sendPaymentRequest(credentials, null));
	}

	@Test
	public void testGetKlixResponse_IOException() throws ClientProtocolException, IOException
	{
		when(client.execute(any(HttpGet.class))).thenThrow(IOException.class);

		assertNull(klixClient.getKlixResponse(credentials, "id"));
	}

	@Test
	public void testGetPaymentMethods_Null_Response() throws ClientProtocolException, IOException
	{
		assertNull(klixClient.getPaymentMethods(credentials, "id"));
	}
}
