package com.kmp.aeroparker.application.external.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionBookingClientTest
{
	@Mock
	private CloseableHttpClient client;
	@Mock
	private SubscriptionBookingRequestBuilder requestBuilder;
	@InjectMocks
	private SubscriptionBookingClient subscriptionBookingClient;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private CloseableHttpResponse response;

	@Test
	public void testSendRequest() throws ClientProtocolException, IOException
	{
		HttpEntity entity = EntityBuilder.create()
				.setText("subscription_booking")
				.build();

		when(requestBuilder.buildSubscriptionHttpRequest(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(mock(HttpPost.class));
		when(client.execute(any())).thenReturn(response);
		when(response.getStatusLine()
				.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(response.getEntity()).thenReturn(entity);

		assertEquals("subscription_booking",
				subscriptionBookingClient.sendRequest("endpoint", "requestBody", "username", "password"));
	}

	@Test
	public void testSendRequest_IOException() throws ClientProtocolException, IOException
	{
		when(requestBuilder.buildSubscriptionHttpRequest(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(mock(HttpPost.class));
		when(client.execute(any())).thenThrow(IOException.class);

		assertEquals("", subscriptionBookingClient.sendRequest("endpoint", "requestBody", "username", "password"));
	}

	@Test
	public void testSendRequest_Unauthorised() throws ClientProtocolException, IOException
	{
		when(requestBuilder.buildSubscriptionHttpRequest(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(mock(HttpPost.class));
		when(client.execute(any())).thenReturn(response);
		when(response.getStatusLine()
				.getStatusCode()).thenReturn(HttpStatus.SC_UNAUTHORIZED);
		when(response.getEntity()).thenReturn(mock(HttpEntity.class));

		assertEquals("", subscriptionBookingClient.sendRequest("endpoint", "requestBody", "username", "password"));
	}

	@Test
	public void testSendRequest_Forbidden() throws ClientProtocolException, IOException
	{
		when(requestBuilder.buildSubscriptionHttpRequest(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(mock(HttpPost.class));
		when(client.execute(any())).thenReturn(response);
		when(response.getStatusLine()
				.getStatusCode()).thenReturn(HttpStatus.SC_FORBIDDEN);
		when(response.getEntity()).thenReturn(mock(HttpEntity.class));

		assertEquals("", subscriptionBookingClient.sendRequest("endpoint", "requestBody", "username", "password"));
	}
}
