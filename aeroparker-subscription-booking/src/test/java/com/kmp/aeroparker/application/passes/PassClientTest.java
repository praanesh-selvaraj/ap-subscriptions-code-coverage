package com.kmp.aeroparker.application.passes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.hc.core5.http.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PassClientTest
{
	@InjectMocks
	private PassClient client;
	@Mock
	private CloseableHttpClient httpClient;
	@Mock
	private CloseableHttpResponse response;
	@Mock
	private StatusLine statusLine;

	@Test
	public void testgetPasskitPass() throws IOException
	{
		HttpEntity entity = EntityBuilder.create()
				.setText("response")
				.build();

		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(response.getEntity()).thenReturn(entity);

		client.getPasskitPass("token", "ref", "endpoint");

		verify(httpClient).execute(any());
	}

	@Test
	public void testgetPasskitPass_IOException() throws IOException
	{
		when(httpClient.execute(any())).thenThrow(IOException.class);

		client.getPasskitPass("token", "ref", "endpoint");

		verify(httpClient).execute(any());
	}

	@Test
	public void testgetPasskitPass_Not_OK() throws IOException
	{
		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);

		client.getPasskitPass("token", "ref", "endpoint");

		verify(httpClient).execute(any());
	}

	@Test
	public void testGetGooglePass() throws IOException
	{
		HttpEntity entity = EntityBuilder.create()
				.setText("response")
				.build();

		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(response.getEntity()).thenReturn(entity);

		assertEquals("response", client.getGooglePass("ref", "token", "endpoint"));
	}

	@Test
	public void testGetGooglePass_IOException() throws IOException
	{
		when(httpClient.execute(any())).thenThrow(IOException.class);

		assertEquals("", client.getGooglePass("ref", "token", "endpoint"));
	}

	@Test
	public void testGetGooglePass_Not_OK() throws IOException
	{
		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);

		assertEquals("", client.getGooglePass("ref", "token", "endpoint"));
	}
}
