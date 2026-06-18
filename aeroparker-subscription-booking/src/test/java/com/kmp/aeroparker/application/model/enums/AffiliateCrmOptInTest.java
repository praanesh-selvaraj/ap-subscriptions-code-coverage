package com.kmp.aeroparker.application.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.enums.AffiliateCrmOptIn;

class AffiliateCrmOptInTest
{
	@Test
	void testLength()
	{
		assertThat(AffiliateCrmOptIn.values().length).isEqualTo(2);
	}

	@Test
	void testGetId()
	{
		assertThat(AffiliateCrmOptIn.TYPE_EMAIL.getId()).isEqualTo(1);
		assertThat(AffiliateCrmOptIn.TYPE_SMS.getId()).isEqualTo(2);
	}

	@Test
	void testGetName()
	{
		assertThat(AffiliateCrmOptIn.TYPE_EMAIL.getName()).isEqualTo("emailOptIn");
		assertThat(AffiliateCrmOptIn.TYPE_SMS.getName()).isEqualTo("smsOptIn");
	}

	@Test
	void testGetById()
	{
		assertThat(AffiliateCrmOptIn.getById(2)).isEqualTo(AffiliateCrmOptIn.TYPE_SMS);
		assertThat(AffiliateCrmOptIn.getById(1)).isEqualTo(AffiliateCrmOptIn.TYPE_EMAIL);
	}

	@Test
	void testCustomValueOf()
	{
		assertThat(AffiliateCrmOptIn.valueOf("TYPE_EMAIL")).isEqualTo(AffiliateCrmOptIn.TYPE_EMAIL);
		assertThat(AffiliateCrmOptIn.valueOf("TYPE_SMS")).isEqualTo(AffiliateCrmOptIn.TYPE_SMS);
	}
}