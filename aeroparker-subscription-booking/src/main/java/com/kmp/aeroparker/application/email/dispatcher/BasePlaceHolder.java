package com.kmp.aeroparker.application.email.dispatcher;

import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public abstract class BasePlaceHolder
{
	public String replacePlaceHolder(final String body, final Map<String, String> placeHolders)
	{
		StringSubstitutor substitutor = new StringSubstitutor(placeHolders, "{", "}");
		placeHolders.entrySet()
				.stream()
				.forEach(placeHolder -> log.debug("Message Body: {}", placeHolder));
		return substitutor.replace(body);
	}
}