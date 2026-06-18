package com.kmp.aeroparker.subscription.maputils.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.trajano.commons.testing.UtilityClassTestUtil;

class MapUtilTest
{
	@Test
	void testUtililtyClass() throws ReflectiveOperationException
	{
		UtilityClassTestUtil.assertUtilityClassWellDefined(MapUtil.class);
	}
	
	@Test
	void testMapUtils()
	{
		Map<String, String> map = Collections.emptyMap();
		String value = "";
		assertThat(MapUtil.getEntryByValue(map, value)).isNull();
	}
	
	@Test
	void testMapUtils_WhenMapIsNull()
	{
		Map<String, String> map = null;
		String value = "";
		assertThat(MapUtil.getEntryByValue(map, value)).isNull();
	}

	@Test
	void testMapUtils_WhenMapContainsValue()
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("one", "Test");
		String value = "Test";
		assertThat(MapUtil.getEntryByValue(map, value)).isNotNull();
	}
}
