package com.kmp.aeroparker.application.builder;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

public class VehicleLookupClientBuilderTest
{
	private VehicleLookupClientBuilder clientBuilder = new VehicleLookupClientBuilder();

	@Test
	public void testBuildClient()
	{
		assertNotNull(clientBuilder.buildClient());
	}
}
