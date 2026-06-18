package com.kmp.aeroparker.subscription.payments.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig
{
	@Bean
	public CloseableHttpClient httpClient()
	{
		/*
		 * Time out required to prevent issue where all the connections get used
		 * up, which then causes the consumer to backlog & stop responding.
		 * 
		 * Docs :
		 * https://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt
		 * .html PoolingHttpClientConnectionManager maintains a maximum limit of
		 * connections on a per route basis and in total. Per default this
		 * implementation will create no more than 2 concurrent connections per
		 * given route and no more 20 connections in total.
		 */
		final int timeout = 30; // 30 Seconds
		final RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(timeout * 1000)
				.setConnectionRequestTimeout(timeout * 1000)
				.setSocketTimeout(timeout * 1000)
				.build();
		final CloseableHttpClient client = HttpClientBuilder.create()
				.setDefaultRequestConfig(config)
				.build();

		return client;
	}
}
