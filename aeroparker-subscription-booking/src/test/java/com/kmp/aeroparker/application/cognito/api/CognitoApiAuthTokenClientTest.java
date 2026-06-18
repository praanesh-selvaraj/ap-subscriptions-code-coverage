package com.kmp.aeroparker.application.cognito.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CognitoApiAuthTokenClientTest
{
	@Mock
	private CognitoApiAuthTokenRequestBuilder builder;
	@Mock
	private CloseableHttpClient httpClient;
	@Mock
	private CognitoApiAuthTokenJsonUtil jsonUtil;
	@InjectMocks
	private CognitoApiAuthTokenClient client;

	@Test
	public void testGetAccessToken_Exception() throws ClientProtocolException, IOException
	{
		when(builder.buildAccessTokenRequest()).thenReturn(mock(HttpPost.class));
		when(httpClient.execute(any())).thenThrow(IOException.class);
		assertEquals("", client.getAccessToken());
	}

	@Test
	public void testGetAccessToken_NotOkStatus() throws ClientProtocolException, IOException
	{
		when(builder.buildAccessTokenRequest()).thenReturn(mock(HttpPost.class));
		CloseableHttpResponse response = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);
		when(response.getStatusLine()
				.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
		when(httpClient.execute(any())).thenReturn(response);
		assertEquals("", client.getAccessToken());
	}

	@Test
	public void testGetAccessToken() throws ClientProtocolException, IOException
	{
		when(builder.buildAccessTokenRequest()).thenReturn(mock(HttpPost.class));
		CloseableHttpResponse response = mock(CloseableHttpResponse.class, RETURNS_DEEP_STUBS);
		when(response.getStatusLine()
				.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		HttpEntity entity = EntityBuilder.create()
				.setText("test_access_token")
				.build();
		when(response.getEntity()).thenReturn(entity);
		when(httpClient.execute(any())).thenReturn(response);
		when(jsonUtil.getAccessToken(anyString())).thenReturn("test_access_token");
		assertEquals("test_access_token", client.getAccessToken());
	}
}
