package com.kmp.aeroparker.application.cognito.api;

import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@AllArgsConstructor
public class CognitoApiAuthTokenJsonUtil
{
	private final ObjectMapper objectMapper;

	public String getAccessToken(String tokenResponse)
	{
		String accessToken = "";
		try
		{
			JsonNode node = objectMapper.readTree(tokenResponse);
			accessToken = node.get("access_token")
					.asText();
		}
		catch (IOException e)
		{
			log.error("Error getting access token from token response JSON: {}", e.getMessage(), e);
		}
		return accessToken;
	}
}