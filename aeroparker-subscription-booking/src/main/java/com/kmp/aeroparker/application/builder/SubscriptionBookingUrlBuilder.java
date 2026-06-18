package com.kmp.aeroparker.application.builder;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubscriptionBookingUrlBuilder
{
	private static final String PORT_SEPERATOR = ":";
	private static final String PROTOCOL_SEPERATOR = "://";
	private static final int NO_PORT = -1;

	/**
	 * Extracts the base URL from a complete URL
	 * 
	 * @param absoluteUrl The complete URL string
	 * @return The base URL containing protocol, host and port (if specified)
	 */
	public static String extractBaseUrl(String absoluteUrl)
	{
		try
		{
			URL url = new URL(absoluteUrl);
			String protocol = url.getProtocol();
			String host = url.getHost();
			int port = url.getPort();

			String baseUrl = protocol + PROTOCOL_SEPERATOR + host;
			// If port is -1 then no port is specified
			if (port != NO_PORT)
			{
				baseUrl += PORT_SEPERATOR + port;
			}
			return baseUrl;
		}
		catch (MalformedURLException e)
		{
			log.error("Invalid URL: " + absoluteUrl, e.getMessage(), e);
			return null;
		}
	}
}
