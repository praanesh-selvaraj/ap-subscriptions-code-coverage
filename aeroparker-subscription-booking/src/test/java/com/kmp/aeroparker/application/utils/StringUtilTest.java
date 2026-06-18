package com.kmp.aeroparker.application.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StringUtilTest
{
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
	public void testIsEqual()
	{
		String str1 = null;
		String str2 = null;
		assertFalse(StringUtil.isEqual(str1, str2));
		str1 = "test";
		assertFalse(StringUtil.isEqual(str1, str2));
		str2 = "test";
		assertTrue(StringUtil.isEqual(str1, str2));
	}
}
