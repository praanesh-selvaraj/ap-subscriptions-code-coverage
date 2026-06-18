package com.kmp.aeroparker.subscription.html.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.trajano.commons.testing.UtilityClassTestUtil;

class HtmlUtilTest
{
	@Test
	void testUtililtyClass() throws ReflectiveOperationException
	{
		UtilityClassTestUtil.assertUtilityClassWellDefined(HtmlUtil.class);
	}

	@Test
	void testEscapeHtml()
	{
		assertThat(HtmlUtil.escapeHtml("script &amp; Test", "")).isEqualTo("script &amp;amp; Test");
	}

	@Test
	void testRemoveXSS()
	{
		assertThat(HtmlUtil.removeXSS("scriptTestalert")).isEqualTo("Test");
	}

	@Test
	void testRemoveXSS_Null()
	{
		assertThat(HtmlUtil.removeXSS(null)).isNull();
	}

	@Test
	void testIdentifyAffiliateCodeFromRequest()
	{
		assertThat(HtmlUtil.identifyAffiliateCodeFromRequest("http://localhost:8888/subscriptions/lba/dates")).isEqualTo("lba");
	}

	@Test
	void testIdentifyAffiliateCodeFromRequest_Length_Less_Three()
	{
		assertThat(HtmlUtil.identifyAffiliateCodeFromRequest("lba/Booking")).isEmpty();
	}
}