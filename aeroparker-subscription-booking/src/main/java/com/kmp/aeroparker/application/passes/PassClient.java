package com.kmp.aeroparker.application.passes;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassClient
{
	private final CloseableHttpClient client;

	private static final String SUBSCRIPTION_ENDPOINT = "/subscription";
	private static final String SUBSCRIPTION_TYPE = "subscription";

	public byte[] getPasskitPass(String token, String encryptedReference, String endpoint)
	{
		byte[] pass = null;
		final String url = endpoint + SUBSCRIPTION_ENDPOINT + "/" + encryptedReference + "?type=" + SUBSCRIPTION_TYPE;
		log.info("Getting Passkit pass from URL {}", url);
		final HttpGet get = new HttpGet(url);
		get.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		try (CloseableHttpResponse response = client.execute(get))
		{

			if (response.getStatusLine()
					.getStatusCode() == HttpStatus.SC_OK)
			{
				pass = EntityUtils.toByteArray(response.getEntity());
			}
		}
		catch (IOException e)
		{
			log.error("Error getting pass from Passkit API {}", e.getMessage(), e);
		}
		return pass;
	}

	public String getGooglePass(String encryptedReference, String token, String endpoint)
	{
		String googlePassesApiResponse = "";
		final String url = endpoint + SUBSCRIPTION_ENDPOINT + "/" + encryptedReference + "?response=" + "url" + "&pass="
				+ SUBSCRIPTION_TYPE;
		log.info("Getting Google Passes URL From {}", url);
		final HttpGet get = new HttpGet(url);
		get.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		try (CloseableHttpResponse response = client.execute(get))
		{
			if (response.getStatusLine()
					.getStatusCode() == HttpStatus.SC_OK)
			{
				googlePassesApiResponse = EntityUtils.toString(response.getEntity());
			}
		}
		catch (IOException e)
		{
			log.error("IOException when getting Google pass from Google Pass API {}", e.getMessage(), e);
		}
		return googlePassesApiResponse;
	}
}
