package com.kmp.aeroparker.subscription.payments.klix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class KlixHttpClient
{
	private final static String PURCHASES_ENDPOINT = "purchases/";
	private final static String REFUND_ENDPOINT = "/refund/";
	private final static String PAYMENT_METHODS_ENDPOINT = "payment_methods/";

	private final CloseableHttpClient client;

	public JsonObject sendPaymentRequest(final KlixCredentials credentials, final JsonObject jsonRequest)
	{
		log.info("Attempting to send a Klix payment request");

		return sendPostRequest(credentials.getApiUrl() + PURCHASES_ENDPOINT, credentials.getSecretKey(), jsonRequest);
	}

	public JsonObject sendRefundRequest(final KlixCredentials credentials, final String paymentId,
			final JsonObject body)
	{
		log.info("Attempting to send klix refund request for Klix payment id {} ", paymentId);
		return sendPostRequest(credentials.getApiUrl() + PURCHASES_ENDPOINT + paymentId + REFUND_ENDPOINT,
				credentials.getSecretKey(), body);
	}

	public JsonObject getKlixResponse(final KlixCredentials credentials, final String paymentId)
	{
		log.info("Attempting to get Klix payment response");
		return sendGetRequest(credentials.getApiUrl() + PURCHASES_ENDPOINT + paymentId + "/",
				credentials.getSecretKey());
	}

	public JsonObject getPaymentMethods(final KlixCredentials credentials, final String currency)
	{
		log.info("Attempting to get available Klix payment methods");
		return sendGetRequest(credentials.getApiUrl() + PAYMENT_METHODS_ENDPOINT + "?currency=" + currency
				+ "&brand_id=" + credentials.getBrandId(), credentials.getSecretKey());
	}

	private JsonObject sendPostRequest(final String url, final String secretKey, final JsonObject body)
	{
		String response = "";
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey);
		try
		{
			StringEntity entity = body != null ? new StringEntity(body.toString(), StandardCharsets.UTF_8) : null;
			if (entity != null)
			{
				entity.setContentType(ContentType.APPLICATION_JSON.toString());
				httpPost.setEntity(entity);
			}
			try (CloseableHttpResponse klixHttpResponse = client.execute(httpPost))
			{
				if (klixHttpResponse != null)
				{
					response = EntityUtils.toString(klixHttpResponse.getEntity());
					log.info("Recieved response from Klix gateway: " + response);
				}
				else
				{
					log.info("Response from Klix gateway was null");
				}
			}
		}
		catch (IOException e)
		{
			log.error("Klix client couldn't create a HttpClient: " + e.getMessage(), e);
		}
		return !StringUtil.isEmpty(response) ? StringUtil.toJsonObject(response) : null;
	}

	private JsonObject sendGetRequest(final String url, final String secretKey)
	{
		String response = "";
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey);
		try (CloseableHttpResponse klixHttpResponse = client.execute(httpGet))
		{
			if (klixHttpResponse != null)
			{
				response = EntityUtils.toString(klixHttpResponse.getEntity());
				log.info("Recieved response from klix gateway: " + response);
			}
			else
			{
				log.info("Response from Klix gateway was null");
			}
		}
		catch (IOException e)
		{
			log.error("Klix client couldn't create a HttpCient: " + e.getMessage(), e);
		}
		return !StringUtil.isEmpty(response) ? StringUtil.toJsonObject(response) : null;
	}
}
