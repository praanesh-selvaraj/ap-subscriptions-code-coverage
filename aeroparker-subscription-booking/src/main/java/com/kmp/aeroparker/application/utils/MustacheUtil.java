package com.kmp.aeroparker.application.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class MustacheUtil
{
	public static String compileMustacheString(List<String> contentList, Map<String, Object> scopes)
	{
		String compiledContent = "";
		for (String content : contentList)
		{
			compiledContent += getCompileContent(content, scopes);
		}
		return compiledContent;
	}

	private static String getCompileContent(String content, Map<String, Object> scopes)
	{
		if (scopes == null)
		{
			scopes = new HashMap<>();
		}
		try (Writer writer = new StringWriter(); StringReader reader = new StringReader(content))
		{
			final MustacheFactory mf = new DefaultMustacheFactory();
			final Mustache mustache = mf.compile(reader, content);
			final Writer newWriter = mustache.execute(writer, scopes);
			writer.flush();
			String result = newWriter.toString();
			return result;
		}
		catch (IOException | MustacheException e)
		{
			log.error("Error while getting Mustache compiled content: {}", e.getMessage(), e);
		}
		return "";
	}
}
