package com.kmp.aeroparker.subscription.html.utils;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.kmp.aeroparker.subscription.string.utils.StringUtil;

public final class HtmlUtil extends HtmlUtils
{
	private HtmlUtil()
	{
	}

	public static String escapeHtml(String val, final String defaultValue)
	{
		val = StringUtil.parse(val, defaultValue);
		val = StringEscapeUtils.escapeHtml4(val);
		return val;
	}

	/**
	 * Remove common javascript values for XSS
	 * 
	 * @param val
	 * @return
	 */
	public static String removeXSS(final String val)
	{
		String newVal = val;
		if (newVal != null)
		{
			newVal = newVal.replace("alert", "");
			newVal = newVal.replace("script", "");
		}
		return newVal;
	}

	/**
	 * Identify the Affiliate code from the Request
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @return String
	 * @throws Exception
	 */
	public static String identifyAffiliateCodeFromRequest(final String url)
	{
		// Split the URI into parts by slashes
		String[] parts = StringUtils.delimitedListToStringArray(url, "/");
		// If we don't have at least 3 parts, then this is not a valid affiliate
		// request
		if (parts.length < 3)
		{
			return "";
		}
		// The site code part is always the 2nd to last part.
		String affiliateCode = parts[parts.length - 2];
		return affiliateCode;
	}
}