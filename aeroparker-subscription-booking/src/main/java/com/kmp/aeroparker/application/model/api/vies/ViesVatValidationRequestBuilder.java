package com.kmp.aeroparker.application.model.api.vies;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.GlobalPropertiesService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ViesVatValidationRequestBuilder
{
	private static final String AFFILIATE_ID_PARAM = "&affiliateId=";
	private static final String COUNTRY_CODE_PARAM = "&countryCode=";
	private static final String REG_NUMBER_PARAM = "?regNumber=";
	private static final String VIES_VALIDATION_API_ROUTE = "/companyregnumber";
	private static final String VIES_VALIDATION_API_ENDPOINT = "aeroparker.api.viesvalidation.endpoint";
	private static final String BEARER_HEADER = "Bearer ";

	private GlobalPropertiesService globalProperties;

	public HttpRequestBase buildRequest(final String countryCode, final String vatNumber, final int affiliateId,
			final String accessToken)
	{

		String requestUrl = globalProperties.fetchProperty(VIES_VALIDATION_API_ENDPOINT) + VIES_VALIDATION_API_ROUTE
				+ REG_NUMBER_PARAM + vatNumber + COUNTRY_CODE_PARAM + countryCode + AFFILIATE_ID_PARAM + affiliateId;
		log.info("Validating Company Reg Number with URL: {} ", requestUrl);
		HttpRequestBase request = new HttpGet(requestUrl);
		request.addHeader(HttpHeaders.AUTHORIZATION, BEARER_HEADER + accessToken);
		return request;
	}
}