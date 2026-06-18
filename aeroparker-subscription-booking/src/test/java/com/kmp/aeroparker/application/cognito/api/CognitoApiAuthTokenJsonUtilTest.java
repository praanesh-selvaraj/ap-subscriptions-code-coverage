package com.kmp.aeroparker.application.cognito.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class CognitoApiAuthTokenJsonUtilTest
{
	@Mock
	private ObjectMapper objectMapper;
	@InjectMocks
	private CognitoApiAuthTokenJsonUtil util;

	@Test
	public void testGetAccessToken_Exception() throws IOException
	{
		when(objectMapper.readTree(anyString())).thenThrow(JsonProcessingException.class);
		assertEquals("", util.getAccessToken("test_token_response"));
	}

	@Test
	public void testGetAccessToken() throws IOException
	{
		JsonNode node = mock(JsonNode.class, RETURNS_DEEP_STUBS);
		when(node.get("access_token").asText()).thenReturn("test_access_token");
		when(objectMapper.readTree(anyString())).thenReturn(node);
		assertEquals("test_access_token", util.getAccessToken("test_token_response"));
	}
}
