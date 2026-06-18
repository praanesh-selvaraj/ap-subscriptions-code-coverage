package com.kmp.aeroparker.subscription.security.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.trajano.commons.testing.UtilityClassTestUtil;

class SecurityUtilTest
{
	@Test
	void testUtililtyClass() throws ReflectiveOperationException
	{
		UtilityClassTestUtil.assertUtilityClassWellDefined(SecurityUtil.class);
	}

	@Test
	void testTosha256()
	{
		assertThat(SecurityUtil.tosha256("test")).isEqualTo("9F86D081884C7D659A2FEAA0C55AD015A3BF4F1B2B0B822CD15D6C15B0F00A08");
	}

	@Test
	void testEncodeStringBase64()
	{
		assertThat(SecurityUtil.encodeStringBase64("test")).isEqualTo("dGVzdA==");
	}

	@Test
	void testEncodeStringBase64_Param_Empty()
	{
		assertThat(SecurityUtil.encodeStringBase64("")).isEmpty();
	}

	@Test
	void testEncryptString()
	{
		String testString = SecurityUtil.encryptString("Ref1");
		assertEquals(testString, "5BCA40C016E4CFB3");
	}

	@Test
	void testDecryptString()
	{
		String testString = SecurityUtil.decryptString("5BCA40C016E4CFB3");
		assertEquals(testString, "Ref1");
	}
}