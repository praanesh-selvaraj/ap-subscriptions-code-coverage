package com.kmp.aeroparker.application.cognito.api;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.GlobalPropertiesService;

@Component
public class CognitoApiAuthTokenRequestBuilder
{
	private static final String ENDPOINT_PROPERTY = "aeroparker.cognito.token.endpoint";
	private static final String CLIENT_ID_PROPERTY = "aeroparker.cognito.token.client_id";
	private static final String CLIENT_SECRET_PROPERTY = "aeroparker.cognito.token.client_secret";
	private static final String DEFAULT_ENDPOINT =
			"https://ap-api-staging.auth.eu-west-1.amazoncognito.com/oauth2/token";
	private static final String DEFAULT_CLIENT_ID = "4ifg1uvv24an84b2vvloeboop5";
	private static final String DEFAULT_CLIENT_SECRET = "oc4sq4r145rank2fcuqf0oaiqa0hqph9ucj1o171joasotumgrc";

	private final GlobalPropertiesService globalProperties;

	@Autowired
	public CognitoApiAuthTokenRequestBuilder(GlobalPropertiesService properties)
	{
		this.globalProperties = properties;
	}

	public HttpPost buildAccessTokenRequest()
	{
		String endpoint = globalProperties.fetchProperty(ENDPOINT_PROPERTY);
		if (StringUtils.isEmpty(endpoint))
		{
			endpoint = DEFAULT_ENDPOINT;
		}

		String clientId = globalProperties.fetchProperty(CLIENT_ID_PROPERTY);
		if (StringUtils.isEmpty(clientId))
		{
			clientId = DEFAULT_CLIENT_ID;
		}

		String clientSecret = globalProperties.fetchProperty(CLIENT_SECRET_PROPERTY);
		if (StringUtils.isEmpty(clientSecret))
		{
			clientSecret = DEFAULT_CLIENT_SECRET;
		}

		HttpPost post = new HttpPost(endpoint);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("grant_type", "client_credentials"));
		params.add(new BasicNameValuePair("client_id", clientId));
		params.add(new BasicNameValuePair("client_secret", clientSecret));

		post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

		return post;
	}

}
