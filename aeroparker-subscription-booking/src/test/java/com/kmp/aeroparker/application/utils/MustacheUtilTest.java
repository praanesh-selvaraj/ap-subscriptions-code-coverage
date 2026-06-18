package com.kmp.aeroparker.application.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class MustacheUtilTest
{
	@Test
	void testCompileSimpleTemplate()
	{
		List<String> contentList = Arrays.asList("Hello {{name}}!");
		Map<String, Object> scopes = new HashMap<>();
		scopes.put("name", "Alice");

		String result = MustacheUtil.compileMustacheString(contentList, scopes);

		assertEquals("Hello Alice!", result);
	}

	@Test
	void testCompileMultipleTemplates()
	{
		List<String> contentList = Arrays.asList("Hello {{name}}!", " You bought {{item}}.");
		Map<String, Object> scopes = new HashMap<>();
		scopes.put("name", "Bob");
		scopes.put("item", "a subscription");

		String result = MustacheUtil.compileMustacheString(contentList, scopes);

		assertEquals("Hello Bob! You bought a subscription.", result);
	}

	@Test
	void testEmptyContentList()
	{
		List<String> contentList = Collections.emptyList();
		Map<String, Object> scopes = new HashMap<>();

		String result = MustacheUtil.compileMustacheString(contentList, scopes);

		assertEquals("", result);
	}

	@Test
	void testNullScopes()
	{
		List<String> contentList = Arrays.asList("Welcome {{user}}!");

		String result = MustacheUtil.compileMustacheString(contentList, null);

		assertEquals("Welcome !", result);
	}

	@Test
	void testInvalidTemplate()
	{
		List<String> contentList = Arrays.asList("Hello {{name}");
		Map<String, Object> scopes = new HashMap<>();
		scopes.put("name", "Charlie");

		String result = MustacheUtil.compileMustacheString(contentList, scopes);

		assertEquals("", result);
	}
}
