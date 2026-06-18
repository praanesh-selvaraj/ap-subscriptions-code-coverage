package com.kmp.aeroparker.subscription.payments.wirecard;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.json.utils.JsonUtil;
import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.security.utils.SecurityUtil;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class WirecardClient
{
	private final CloseableHttpClient client;

	public JsonObject sendPaymentRequest(final WirecardCredentials wirecardCredentials, final JsonObject jsonRequest)
	{
		log.info("Attempting to send wirecard payment request");

		String restAuth = SecurityUtil.encodeStringBase64(wirecardCredentials.getRestUser() + ":" + wirecardCredentials.getRestPassword());
		String restUrl = wirecardCredentials.getCreditCardRestUrl();

		String response = "";
		HttpPost httpPost = buildHttpPost(restUrl, restAuth);
		log.debug("Sending JSON: " + jsonRequest);
		httpPost.setEntity(new StringEntity(jsonRequest.toString(), ContentType.APPLICATION_JSON));
		try
		{
			try (CloseableHttpResponse httpResponse = client.execute(httpPost))
			{
				if (httpResponse != null)
				{
					response = EntityUtils.toString(httpResponse.getEntity());
					log.debug("Recieved response from wirecard payment: {}", response.toString());
				}
				else
				{
					log.warn("The response from the httpPost was null!");
				}
			}
		}
		catch (IOException e)
		{
			log.error("WirecardRequestPoster couldn't create a HttpClient, " + e.getMessage() + "; can't communicate with Wirecard!", e);
		}
		return StringUtil.isEmpty(response) ? null : JsonUtil.toJsonObject(response);
	}

	private HttpPost buildHttpPost(final String url, final String restAuth)
	{
		HttpPost post = new HttpPost(url);
		post.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
		post.addHeader(HttpHeaders.AUTHORIZATION, "Basic " + restAuth);
		post.addHeader(HttpHeaders.CONNECTION, "close");
		post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
		post.addHeader(HttpHeaders.HOST, "checkout.wirecard.com");
		post.addHeader(HttpHeaders.USER_AGENT, System.getProperty("http.agent"));
		return post;
	}
}