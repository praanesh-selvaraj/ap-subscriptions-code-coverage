package com.kmp.aeroparker.application.cognito.api;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class CognitoApiAuthTokenClient
{
	private final CognitoApiAuthTokenRequestBuilder builder;
	private final CloseableHttpClient client;
	private final CognitoApiAuthTokenJsonUtil jsonUtil;

	public String getAccessToken()
	{
		String accessToken = "";
		HttpPost post = builder.buildAccessTokenRequest();
		try (CloseableHttpResponse response = client.execute(post))
		{
			if (response.getStatusLine()
					.getStatusCode() == HttpStatus.SC_OK)
			{
				accessToken = jsonUtil.getAccessToken(EntityUtils.toString(response.getEntity()));
			}
		}
		catch (IOException e)
		{
			log.error("Error getting access token from cognito: {}", e.getMessage(), e);
		}
		return accessToken;
	}
}
