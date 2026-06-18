package com.kmp.aeroparker.subscription.json.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import net.trajano.commons.testing.UtilityClassTestUtil;

class JsonUtilTest
{
	@Test
	void testUtililtyClass() throws ReflectiveOperationException
	{
		UtilityClassTestUtil.assertUtilityClassWellDefined(JsonUtil.class);
	}

	@Test
	void testToJsonObject()
	{
		assertThat(JsonUtil.toJsonObject("{payment={\"merchant-account-id\":{\"value\":\"bb3e3b54-fb94-41eb-92fb-ddf5e0402711\"}}}")).isNotNull()
				.isInstanceOf(JsonObject.class);
	}
}