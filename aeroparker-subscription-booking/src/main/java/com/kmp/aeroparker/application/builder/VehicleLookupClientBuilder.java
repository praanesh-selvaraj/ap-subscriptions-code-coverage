package com.kmp.aeroparker.application.builder;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class VehicleLookupClientBuilder
{
	public CloseableHttpClient buildClient()
	{
		return HttpClientBuilder.create()
				.setConnectionTimeToLive(20, TimeUnit.SECONDS)
				.build();
	}
}
