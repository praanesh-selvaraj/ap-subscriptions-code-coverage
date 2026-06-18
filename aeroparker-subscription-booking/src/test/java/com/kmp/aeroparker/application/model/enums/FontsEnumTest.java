package com.kmp.aeroparker.application.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.enums.FontsEnum;

class FontsEnumTest
{
	@Test
	void testGetAffiliateCode_Size()
	{
		assertThat(FontsEnum.values().length).isEqualTo(2);
	}

	@Test
	void testGetAffiliateCode()
	{
		assertThat(FontsEnum.SHANNON.getAffFontUrl()).isEqualTo("https://fonts.googleapis.com/css?family=Ubuntu:300,400,700");
	}

	@Test
	void testGetAffFontUrl()
	{
		assertThat(FontsEnum.SHANNON.getAffiliateCode()).isEqualTo("snn");
	}

	@Test
	void testGetUrlFromAffiliateCode()
	{
		assertThat(FontsEnum.getUrlFromAffiliateCode("snn")).isEqualTo("https://fonts.googleapis.com/css?family=Ubuntu:300,400,700");
	}

	@Test
	void testGetUrlFromAffiliateCode_Unknown()
	{
		assertThat(FontsEnum.getUrlFromAffiliateCode("ssn")).isEqualTo("");
	}

	@Test
	void testGetUrlFromAffiliateCode_Affiliate_Code_Empty()
	{
		assertThat(FontsEnum.getUrlFromAffiliateCode("")).isEqualTo("");
	}
}