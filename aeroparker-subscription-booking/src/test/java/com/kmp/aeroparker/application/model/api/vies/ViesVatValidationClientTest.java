package com.kmp.aeroparker.application.model.api.vies;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.hc.core5.http.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ViesVatValidationClientTest
{
	@Mock
	private CloseableHttpClient httpClient;
	@InjectMocks
	private ViesVatValidationClient validationClient;

	@Test
	public void testSendViesVatValidationRequest_Exception() throws ClientProtocolException, IOException
	{
		when(httpClient.execute(any())).thenThrow(IOException.class);

		assertFalse(validationClient.sendViesVatValidationRequest(mock(HttpRequestBase.class)));
	}

	@Test
	public void testSendViesValidationRequest_Unauthorised() throws ClientProtocolException, IOException
	{
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_UNAUTHORIZED);

		assertFalse(validationClient.sendViesVatValidationRequest(mock(HttpRequestBase.class)));

		verify(httpClient).execute(any());
	}

	@Test
	public void testSendViesVatValidationRequest_JsonNull() throws ClientProtocolException, IOException
	{
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		HttpEntity entity = EntityBuilder.create()
				.setText("invalidJson")
				.build();
		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(response.getEntity()).thenReturn(entity);

		assertFalse(validationClient.sendViesVatValidationRequest(mock(HttpRequestBase.class)));

		verify(httpClient).execute(any());
	}

	@Test
	public void testSendViesVatValidationRequest_Invalid() throws ClientProtocolException, IOException
	{
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		HttpEntity entity = EntityBuilder.create()

				.setText("{\"companyRegNumberValid\":false}")

				.build();
		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(response.getEntity()).thenReturn(entity);

		assertFalse(validationClient.sendViesVatValidationRequest(mock(HttpRequestBase.class)));

		verify(httpClient).execute(any());
	}

	@Test
	public void testSendViesVatValidationRequest_Valid() throws ClientProtocolException, IOException
	{
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		HttpEntity entity = EntityBuilder.create()
				.setText("{\"companyRegNumberValid\":true}")
				.build();
		when(httpClient.execute(any())).thenReturn(response);
		when(response.getStatusLine()).thenReturn(statusLine);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		when(response.getEntity()).thenReturn(entity);

		assertTrue(validationClient.sendViesVatValidationRequest(mock(HttpRequestBase.class)));

		verify(httpClient).execute(any());
	}
}
