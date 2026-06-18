package com.kmp.aeroparker.application.external.api;

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
public class SubscriptionBookingClient
{
	private final CloseableHttpClient httpClient;
	private final SubscriptionBookingRequestBuilder requestBuilder;
	
	public String sendRequest(String endpoint, String requestBody, String username, String password)
	{
		String responseString = "";
		HttpPost request = requestBuilder.buildSubscriptionHttpRequest(endpoint, requestBody, username, password);
		
		log.info("Sending subscription request: {}", requestBody);
		try (CloseableHttpResponse response = httpClient.execute(request))
		{
			int statusCode = response.getStatusLine().getStatusCode();
			log.info("Response status code: {}", statusCode);
			responseString = EntityUtils.toString(response.getEntity());
			log.info("Response: {}", responseString);
			if (statusCode == HttpStatus.SC_OK)
			{
				log.info("Successfully received ok response");
			}
			else if (statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN)
			{
				log.info("User {} is not authorised to send request", username);
				responseString = "";
			}
			else
			{
				log.info("Invalid status code received: {}", statusCode);
				responseString = "";
			}
		}
		catch (IOException e)
		{
			log.error("Error sending request to external api: {}", e.getMessage(), e);
		}
		
		return responseString;
	}
}
