package com.kmp.aeroparker.application.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.kmp.aeroparker.application.model.PurchaseQuery;
import com.kmp.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class JsonUtil
{
	private final ObjectMapper objectMapper;

	/**
	 * Take in a json response & convert it to a BasketUpdateQuery Object
	 * 
	 * @param Json
	 * 
	 * @return BasketUpdateQuery
	 */
	public List<PurchaseQuery> jsonToPurchaseQuery(final String responseJson)
	{
		log.debug("converting json response to TokenResponse Object");
		List<PurchaseQuery> purchaseQueries = new ArrayList<>();
		if (!StringUtils.isEmpty(responseJson))
		{
			try
			{
				purchaseQueries = objectMapper.readValue(responseJson, new TypeReference<List<PurchaseQuery>>()
				{
				});
			}
			catch (Exception e)
			{
				log.error("Error converting JSON to TokenResponse response: {}", e.getMessage(), e);
			}
		}
		return purchaseQueries;
	}

	public <T> T jsonToObjectWithRootElement(String jsonString, Class<T> cls)
	{
		T object = null;
		if (!StringUtil.isNullOrEmpty(jsonString))
		{
			try
			{
				object = objectMapper.readerFor(cls)
						.with(DeserializationFeature.UNWRAP_ROOT_VALUE)
						.readValue(jsonString);
			}
			catch (JsonProcessingException e)
			{
				log.error("Error converting JSON to pojo: {}", e.getMessage(), e);
			}
		}
		return object;
	}

	public String jsonObjectToStringWithRootElement(Object jsonObject)
	{
		String json = "";
		try
		{
			json = objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true)
					.writeValueAsString(jsonObject);
		}
		catch (JsonProcessingException e)
		{
			log.error("Error writing JSON object as string: {}", e.getMessage(), e);
		}
		return json;
	}
}