package com.kmp.aeroparker.subscription.json.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JsonUtil
{
	private JsonUtil()
	{
	}

	public static JsonObject toJsonObject(final String string)
	{
		JsonObject responseJson = null;
		try
		{
			responseJson = new JsonParser().parse(string)
					.getAsJsonObject();
		}
		catch (JsonSyntaxException e)
		{
			log.error("JsonSyntaxException thrown parsing JsonObject from string: {}, {}", string, e, e.getMessage());
		}
		return responseJson;
	}
}