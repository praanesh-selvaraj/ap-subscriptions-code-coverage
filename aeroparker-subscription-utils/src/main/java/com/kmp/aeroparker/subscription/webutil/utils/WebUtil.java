package com.kmp.aeroparker.subscription.webutil.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class WebUtil
{
	private WebUtil()
	{

	}

	public static String urlEncode(String enc)
	{
		String url = "";
		try
		{
			url = URLEncoder.encode(enc, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			log.error("Error encoding URL: " + enc, e);
		}
		return url;
	}
}
