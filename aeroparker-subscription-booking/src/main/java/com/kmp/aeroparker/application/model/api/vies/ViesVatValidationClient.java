package com.kmp.aeroparker.application.model.api.vies;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@Slf4j
@Component
public class ViesVatValidationClient
{
	private static final String VIES_VALIDATION_RESPONSE_NAME = "companyRegNumberValid";

	private CloseableHttpClient client;

	public ViesVatValidationClient(CloseableHttpClient client)
	{
		this.client = client;
	}

	public boolean sendViesVatValidationRequest(HttpRequestBase request)
	{
		boolean valid = false;
		try (CloseableHttpResponse apiResponse = client.execute(request))
		{
			if (apiResponse.getStatusLine()
					.getStatusCode() == HttpStatus.SC_OK)
			{
				String responseString = EntityUtils.toString(apiResponse.getEntity());
				if (responseString != null)
				{
					JsonObject response = new JsonParser().parse(responseString)
							.getAsJsonObject();
					valid = getJsonBoolean(response.get(VIES_VALIDATION_RESPONSE_NAME));
				}
			}
		}
		catch (IOException | JsonSyntaxException | IllegalStateException e)
		{
			log.error("Exception thrown when sending or unpacking a VAT Validation request or response: {}",
					e.getMessage(), e);
		}
		return valid;
	}

	private boolean getJsonBoolean(JsonElement element)
	{
		return element != null && !element.isJsonNull() ? element.getAsBoolean() : false;
	}
}
