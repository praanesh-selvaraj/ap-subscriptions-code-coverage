package com.kmp.aeroparker.application.factory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LocaleFactoryTest
{
	private LocaleFactory localFactory = new LocaleFactory();
	
	@Test
	void testGetAvailableLocales()
	{
		assertThat(localFactory.getAvailableLocales()).isNotEmpty().hasSize(160);
	}
}