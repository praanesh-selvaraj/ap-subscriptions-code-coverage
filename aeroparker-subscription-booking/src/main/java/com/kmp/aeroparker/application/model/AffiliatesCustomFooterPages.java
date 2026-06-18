package com.kmp.aeroparker.application.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesFooterPages;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AffiliatesCustomFooterPages extends AffiliatesFooterPages
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getUrlTitle()
	{
		String urlLink = "";
		try
		{
			urlLink = URLEncoder.encode(getLinkTitle(), StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException e)
		{
			log.error("Exception encoding {} with charset: {}", getLinkTitle(), StandardCharsets.UTF_8.name(), e.getMessage(), e);
		}
		return urlLink;
	}
}