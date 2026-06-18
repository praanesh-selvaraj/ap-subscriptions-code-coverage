package com.kmp.aeroparker.application.loginapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.kmp.aeroparker.application.db.service.ApiUserService;
import com.kmp.aeroparker.subscription.booking.api.tables.pojos.ApiUser;
import com.kmp.aeroparker.subscription.booking.api.tables.pojos.ApiUserSettings;

@ExtendWith(MockitoExtension.class)
class LoginClientTest
{
	@InjectMocks
	private LoginClient client;
	@Mock
	private CloseableHttpClient httpClient;
	@Mock
	private ApiUserService service;
	@Mock
	private ApiUserSettings apiUser;
	@Mock
	private CloseableHttpResponse response;
	@Mock
	private StatusLine statusLine;

	@Test
	public void testGetLoginToken() throws IOException
	{
		HttpEntity entity = EntityBuilder.create()
				.setText("response")
				.build();

		when(service.fetchAeroparkerApiUserBySchemaAndSiteId(anyString(), anyInt())).thenReturn(apiUser);
		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(response.getEntity()).thenReturn(entity);

		assertEquals("response", client.getLoginToken("endpoint", 1, "schema"));
	}

	@Test
	public void testGetLoginToken_IOException() throws IOException
	{
		when(service.fetchAeroparkerApiUserBySchemaAndSiteId(anyString(), anyInt())).thenReturn(apiUser);
		when(httpClient.execute(any())).thenThrow(IOException.class);

		assertEquals("", client.getLoginToken("endpoint", 1, "schema"));
	}

	@Test
	public void testGetLoginToken_Not_OK() throws IOException
	{
		when(service.fetchAeroparkerApiUserBySchemaAndSiteId(anyString(), anyInt())).thenReturn(apiUser);
		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);

		assertEquals("", client.getLoginToken("endpoint", 1, "schema"));
	}
}
