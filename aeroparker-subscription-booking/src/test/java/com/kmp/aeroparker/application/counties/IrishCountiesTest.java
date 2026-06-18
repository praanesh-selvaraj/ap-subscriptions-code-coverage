package com.kmp.aeroparker.application.counties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.trajano.commons.testing.UtilityClassTestUtil;

class IrishCountiesTest
{
	@Test
	void testUtililtyClass() throws ReflectiveOperationException
	{
		UtilityClassTestUtil.assertUtilityClassWellDefined(IrishCounties.class);
	}

	@Test
	void testGetIrishCountries()
	{
		assertThat(IrishCounties.getIrishCounties()).isNotEmpty()
				.containsEntry("Meath", "Meath");
	}
}