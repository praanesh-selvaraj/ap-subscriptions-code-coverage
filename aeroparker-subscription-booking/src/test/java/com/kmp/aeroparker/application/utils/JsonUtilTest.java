package com.kmp.aeroparker.application.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kmp.aeroparker.application.model.PurchaseQuery;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class JsonUtilTest
{
	@Mock
	private ObjectMapper objectMapper;
	@InjectMocks
	private JsonUtil jsonUtil;
	@Mock
	private PurchaseQuery purchaseQueries;
	@Mock
	private Object object;
	@Mock
	private ObjectReader objectReader;

	private static final String TEST_JSON_STRING = "{json}";

	@Test
	void testJsonToPurchaseQuery() throws JsonParseException, JsonMappingException, IOException
	{
		List<PurchaseQuery> purchaseQueries = EnhancedRandom.randomListOf(1, PurchaseQuery.class);
		when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(purchaseQueries);
		List<PurchaseQuery> response = jsonUtil.jsonToPurchaseQuery("test");
		SoftAssertions.assertSoftly(softly ->
		{
			softly.assertThat(response)
					.isNotNull()
					.hasOnlyElementsOfTypes(PurchaseQuery.class);
			softly.assertThat(response)
					.hasSize(1);
		});
	}

	@Test
	void testJsonToPurchaseQuery_Response_Empty() throws JsonParseException, JsonMappingException, IOException
	{
		List<PurchaseQuery> response = jsonUtil.jsonToPurchaseQuery("");
		SoftAssertions.assertSoftly(softly ->
		{
			softly.assertThat(response)
					.isEmpty();
		});
	}

	@Test
	void testJsonToPurchaseQuery_Throw_Exception() throws JsonParseException, JsonMappingException, IOException
	{
		when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(JsonParseException.class);
		assertThat(jsonUtil.jsonToPurchaseQuery("test")).isEmpty();
	}

	@Test
	public void testJsonToObjectWithRootElement() throws JsonProcessingException
	{
		when(objectMapper.readerFor(eq(Object.class))).thenReturn(objectReader);
		when(objectReader.with(eq(DeserializationFeature.UNWRAP_ROOT_VALUE))).thenReturn(objectReader);
		when(objectReader.readValue(TEST_JSON_STRING)).thenReturn(object);

		assertEquals(object, jsonUtil.jsonToObjectWithRootElement(TEST_JSON_STRING, Object.class));
	}

	@Test
	public void testJsonToObjectWithRootElement_JsonProcessingException() throws JsonProcessingException
	{
		when(objectMapper.readerFor(eq(Object.class))).thenReturn(objectReader);
		when(objectReader.with(eq(DeserializationFeature.UNWRAP_ROOT_VALUE))).thenReturn(objectReader);
		when(objectReader.readValue(TEST_JSON_STRING)).thenThrow(JsonProcessingException.class);

		assertNull(jsonUtil.jsonToObjectWithRootElement(TEST_JSON_STRING, Object.class));
	}

	@Test
	public void testJsonObjectToStringWithRootElement() throws JsonProcessingException
	{
		when(objectMapper.configure(eq(SerializationFeature.WRAP_ROOT_VALUE), eq(true))).thenReturn(objectMapper);
		when(objectMapper.writeValueAsString(object)).thenReturn(TEST_JSON_STRING);

		assertEquals(TEST_JSON_STRING, jsonUtil.jsonObjectToStringWithRootElement(object));

		verify(objectMapper).writeValueAsString(object);
	}

	@Test
	public void testJsonObjectToStringWithRootElement_JsonProcessingException() throws JsonProcessingException
	{
		when(objectMapper.configure(eq(SerializationFeature.WRAP_ROOT_VALUE), eq(true))).thenReturn(objectMapper);
		when(objectMapper.writeValueAsString(object)).thenThrow(JsonProcessingException.class);

		assertTrue(jsonUtil.jsonObjectToStringWithRootElement(object)
				.isEmpty());

		verify(objectMapper).writeValueAsString(object);
	}
}