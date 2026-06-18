package com.kmp.aeroparker.application.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BuilderTypeTest
{
	@Test
	void test_Size()
	{
		assertThat(BuilderType.values()).hasSize(6);
	}

	@Test
	void test_ValuesOf()
	{
		assertThat(BuilderType.valueOf("PRODUCT_DISPLAY_ITEM")).isEqualTo(BuilderType.PRODUCT_DISPLAY_ITEM);
		assertThat(BuilderType.valueOf("CONFIG")).isEqualTo(BuilderType.CONFIG);
		assertThat(BuilderType.valueOf("REFERENCE")).isEqualTo(BuilderType.REFERENCE);
		assertThat(BuilderType.valueOf("BASKET")).isEqualTo(BuilderType.BASKET);
		assertThat(BuilderType.valueOf("CONFIRMATION")).isEqualTo(BuilderType.CONFIRMATION);
		assertThat(BuilderType.valueOf("PURCHASE_DATA")).isEqualTo(BuilderType.PURCHASE_DATA);
	}
}