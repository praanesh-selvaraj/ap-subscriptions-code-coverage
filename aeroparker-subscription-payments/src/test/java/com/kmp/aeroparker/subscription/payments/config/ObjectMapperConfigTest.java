package com.kmp.aeroparker.subscription.payments.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;

class ObjectMapperConfigTest
{
	private final ObjectMapperConfig config = new ObjectMapperConfig();

	@Test
	void testObjectMapper() throws ParserConfigurationException
	{
		assertNotNull(config.objectMapper());
	}
}