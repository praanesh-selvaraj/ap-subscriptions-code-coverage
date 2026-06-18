package com.kmp.aeroparker.application.loginapi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.ApiUserService;
import com.kmp.aeroparker.subscription.booking.api.tables.pojos.ApiUser;
import com.kmp.aeroparker.subscription.booking.api.tables.pojos.ApiUserSettings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginClient
{
	private final CloseableHttpClient httpClient;
	private final ApiUserService service;

	public String getLoginToken(String loginEndpoint, int siteId, String schema)
	{
		String token = "";
		ApiUserSettings apiUser = service.fetchAeroparkerApiUserBySchemaAndSiteId(schema, siteId);

		String credentials = apiUser.getUsername() + ":" + apiUser.getPassword();
		String basicAuth = "Basic " + Base64.getEncoder()
				.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

		HttpPost postRequest = new HttpPost(loginEndpoint);
		postRequest.addHeader(HttpHeaders.AUTHORIZATION, basicAuth);
		token = sendHttpRequest(postRequest);

		return token;
	}

	private String sendHttpRequest(HttpUriRequest request)
	{
		String responseEntity = "";

		try (CloseableHttpResponse response = httpClient.execute(request))
		{
			int statusCode = response.getStatusLine()
					.getStatusCode();
			if (statusCode == HttpStatus.SC_OK)
			{
				responseEntity = EntityUtils.toString(response.getEntity());
			}
			else
			{
				log.info("Failed to get entity. Status code returned: {}", statusCode);
			}
		}
		catch (IOException e)
		{
			log.error("Error attempting to get entity from endpoint: {}.", request.getURI(), e);
		}
		return responseEntity;
	}
}
