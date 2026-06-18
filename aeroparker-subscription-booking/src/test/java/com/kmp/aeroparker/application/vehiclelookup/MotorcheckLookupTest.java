package com.kmp.aeroparker.application.vehiclelookup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.builder.VehicleLookupClientBuilder;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;

@ExtendWith(MockitoExtension.class)
public class MotorcheckLookupTest
{
	@Mock
	private VehicleLookupClientBuilder clientBuilder;
	@Mock
	private CloseableHttpClient httpClient;
	@Mock
	private CloseableHttpResponse response;
	@Mock
	private HttpEntity entity;
	@Mock
	private VehiclelookupAffiliateLogins loginDetails;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@InjectMocks
	private MotorcheckLookup motorcheckLookup;

	private static final String XML_RESPONSE = "<vehicle>\n"
			+ "<reg>TEST123</reg>\n"
			+ "<make>Ford</make>\n"
			+ "<model>Focus</model>\n"
			+ " <colour>Blue</colour>\n"
			+ "</vehicle>";

	@Test
	public void testDoLookup() throws ClientProtocolException, IOException
	{
		String reg = "TEST123";
		InputStream inputStream = new ByteArrayInputStream(XML_RESPONSE.getBytes());

		when(loginDetails.getUrl()).thenReturn("http://testurl.com");
		when(loginDetails.getUsername()).thenReturn("username");
		when(loginDetails.getPassword()).thenReturn("password");
		when(clientBuilder.buildClient()).thenReturn(httpClient);
		when(httpClient.execute(any(HttpHost.class), any(HttpGet.class), any(HttpClientContext.class)))
				.thenReturn(response);
		when(response.getEntity()).thenReturn(entity);
		when(entity.getContent()).thenReturn(inputStream);

		String actualResponse = motorcheckLookup.doLookup(loginDetails, reg, languageFieldsList);

		assertTrue(actualResponse.contains("TEST123"));
		assertTrue(actualResponse.contains("Ford"));
		assertTrue(actualResponse.contains("Focus"));
		assertTrue(actualResponse.contains("Blue"));
	}

	@Test
	public void testGetLookupServiceId()
	{
		assertEquals(2, motorcheckLookup.getLookupServiceId());
	}
}
