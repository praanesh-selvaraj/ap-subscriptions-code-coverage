package com.kmp.aeroparker.subscription.string.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.trajano.commons.testing.UtilityClassTestUtil;

class StringUtilTest
{
	@Test
	void testUtililtyClass() throws ReflectiveOperationException
	{
		UtilityClassTestUtil.assertUtilityClassWellDefined(StringUtil.class);
	}

	@Test
	void testStrToInt()
	{
		assertThat(StringUtil.strToInt("12", 0)).isEqualTo(12);
	}

	@Test
	void testStrToInt_Exception()
	{
		assertThat(StringUtil.strToInt("ciao", 0)).isEqualTo(0);
	}

	@Test
	void testIsEqual()
	{
		assertThat(StringUtil.isEqual("test", "test")).isTrue();
	}

	@Test
	void testIsEqual_Str1_Null()
	{
		assertThat(StringUtil.isEqual(null, "test")).isFalse();
	}

	@Test
	void testIsEqual_Str2_Null()
	{
		assertThat(StringUtil.isEqual("test", null)).isFalse();
	}

	@Test
	void testIsEqualIgnoreCase()
	{
		assertThat(StringUtil.isEqualIgnoreCase("test", "TEST")).isTrue();
	}

	@Test
	void testIsEqualIgnoreCase_Null()
	{
		assertThat(StringUtil.isEqualIgnoreCase(null, null)).isFalse();
	}

	@Test
	void testIsEqualIgnoreCase_Str1_Null()
	{
		assertThat(StringUtil.isEqualIgnoreCase(null, "test")).isFalse();
	}

	@Test
	void testIsEqualIgnoreCase_Str2_Null()
	{
		assertThat(StringUtil.isEqualIgnoreCase("test", null)).isFalse();
	}

	@Test
	void testParseString()
	{
		assertThat(StringUtil.parse("test")).isEqualTo("test");
	}

	@Test
	void testParseStringString()
	{
		assertThat(StringUtil.parse("test", "")).isEqualTo("test");
	}

	@Test
	void testParseStringString_Null_Param()
	{
		assertThat(StringUtil.parse(null, "defaultValue")).isEqualTo("defaultValue");
	}

	@Test
	void testCoalesce()
	{
		assertEquals("tenants", StringUtil.coalesce("tenants", null, null, null));
		assertEquals("test_1", StringUtil.coalesce("tenants", "test_1", null, null));
		assertEquals("test_2", StringUtil.coalesce("tenants", null, "test_2", null));
		assertEquals("test_3", StringUtil.coalesce("tenants", null, null, "test_3"));
	}

	@Test
	void testContains_IgnoreCase()
	{
		assertThat(StringUtil.contains("Hello Word", "word", true)).isTrue();
	}

	@Test
	void testContains_CaseSensitive()
	{
		assertThat(StringUtil.contains("Hello Word", "word", false)).isFalse();
	}

	@Test
	void testContains_Null()
	{
		assertThat(StringUtil.contains(null, "word", false)).isFalse();
	}

	@Test
	void testIsEqualAtLeastOne()
	{
		assertThat(StringUtil.isEqualAtLeastOne("word", "hello", "word")).isTrue();
	}
	
	@Test
	public void testIsNullOrEmptyString()
	{
		String nullString = null;
		assertTrue(StringUtil.isNullOrEmpty(nullString));
		nullString = "null";
		assertTrue(StringUtil.isNullOrEmpty(nullString));
		String emptyString = "";
		assertTrue(StringUtil.isNullOrEmpty(emptyString));
		String testString = "test";
		assertFalse(StringUtil.isNullOrEmpty(testString));
	}

	@Test
	void testIntToStr()
	{
		assertThat(StringUtil.intToStr(1)).isEqualTo("1");
	}
}