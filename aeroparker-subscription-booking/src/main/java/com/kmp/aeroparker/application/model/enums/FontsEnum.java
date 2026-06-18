package com.kmp.aeroparker.application.model.enums;

import java.util.Arrays;

import com.kmp.aeroparker.subscription.string.utils.StringUtil;

public enum FontsEnum
{
	LEEDS("lba", "https://fonts.googleapis.com/css?family=Exo:500,600,700"),
	SHANNON("snn", "https://fonts.googleapis.com/css?family=Ubuntu:300,400,700");

	private String affiliateCode;
	private String affFontUrl;

	private FontsEnum(final String affiliateCode, final String affFontUrl)
	{
		this.affiliateCode = affiliateCode;
		this.affFontUrl = affFontUrl;
	}

	public String getAffiliateCode()
	{
		return affiliateCode;
	}

	public String getAffFontUrl()
	{
		return affFontUrl;
	}

	public static String getUrlFromAffiliateCode(final String affCode)
	{
		String fontUrl = "";

		if (!StringUtil.isEmpty(affCode))
		{
			FontsEnum test = Arrays.asList(values())
					.stream()
					.filter(fontEnum -> fontEnum.getAffiliateCode()
							.equalsIgnoreCase(affCode))
					.findFirst()
					.orElse(null);
			fontUrl = test == null ? "" : test.getAffFontUrl();
		}
		return fontUrl;
	}
}