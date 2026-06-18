package com.kmp.aeroparker.application.cognito.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.kmp.aeroparker.application.db.service.GlobalPropertiesService;

@RunWith(MockitoJUnitRunner.class)
public class CognitoApiAuthTokenRequestBuilderTest
{
	@Mock
	private GlobalPropertiesService globalProperties;
	@InjectMocks
	private CognitoApiAuthTokenRequestBuilder builder;

	private static final String DEFAULT_ENDPOINT =
			"https://ap-api-staging.auth.eu-west-1.amazoncognito.com/oauth2/token";
	private static final String DEFAULT_CLIENT_ID = "4ifg1uvv24an84b2vvloeboop5";
	private static final String DEFAULT_CLIENT_SECRET = "oc4sq4r145rank2fcuqf0oaiqa0hqph9ucj1o171joasotumgrc";

	@Before
	public void init()
	{
		when(globalProperties.fetchProperty(eq("aeroparker.cognito.token.endpoint"))).thenReturn("test_endpoint");
		when(globalProperties.fetchProperty(eq("aeroparker.cognito.token.client_id"))).thenReturn("test_client_id");
		when(globalProperties.fetchProperty(eq("aeroparker.cognito.token.client_secret")))
				.thenReturn("test_client_secret");
	}

	@Test
	public void testBuildAccessTokenRequest_WithProperties() throws Exception
	{
		HttpPost post = builder.buildAccessTokenRequest();
		String content = EntityUtils.toString(post.getEntity());

		assertThat(post.getURI()
				.toString()).isEqualTo("test_endpoint");
		assertThat(post.getEntity()).isInstanceOf(UrlEncodedFormEntity.class);
		assertThat(content).contains("grant_type=client_credentials", "client_id=test_client_id",
				"client_secret=test_client_secret");
	}

	@Test
	public void testBuildAccessTokenRequest_WithEmptyProperties() throws Exception
	{
		when(globalProperties.fetchProperty(eq("aeroparker.cognito.token.endpoint"))).thenReturn("");
		when(globalProperties.fetchProperty(eq("aeroparker.cognito.token.client_id"))).thenReturn("");
		when(globalProperties.fetchProperty(eq("aeroparker.cognito.token.client_secret"))).thenReturn("");

		HttpPost post = builder.buildAccessTokenRequest();
		String content = EntityUtils.toString(post.getEntity());

		assertThat(post.getURI()
				.toString()).isEqualTo(DEFAULT_ENDPOINT);
		assertThat(content).contains("grant_type=client_credentials", "client_id=" + DEFAULT_CLIENT_ID,
				"client_secret=" + DEFAULT_CLIENT_SECRET);
	}
}