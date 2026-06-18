package com.kmp.aeroparker.db.jooq.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jooq.codegen.GeneratorStrategy.Mode;
import org.jooq.meta.Definition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.db.jooq.interfaces.JooqPojoInterface;
import com.kmp.aeroparker.db.jooq.interfaces.JooqRecordInterface;

@ExtendWith(MockitoExtension.class)
class SubscriptionPojoDaoStrategyTest
{
	@InjectMocks
	private SubscriptionPojoDaoStrategy pojoDaoStrategy;

	@Test
	void testGetJavaClassImplementsDefinitionMode_Pojo()
	{
		assertThat(pojoDaoStrategy.getJavaClassImplements(mock(Definition.class), Mode.POJO)).contains(JooqPojoInterface.class.getName())
				.doesNotContain(JooqRecordInterface.class.getName());
	}

	@Test
	void testGetJavaClassImplementsDefinitionMode_Record()
	{
		assertThat(pojoDaoStrategy.getJavaClassImplements(mock(Definition.class), Mode.RECORD)).contains(JooqRecordInterface.class.getName())
				.doesNotContain(JooqPojoInterface.class.getName());
	}

	@Test
	void testGetJavaClassName_Ab()
	{
		Definition definition = mock(Definition.class);
		when(definition.getOutputName()).thenReturn("ab_affiliates");
		assertThat(pojoDaoStrategy.getJavaClassName(definition, Mode.POJO)).isNotEmpty()
				.isEqualTo("Affiliates");
	}

	@Test
	void testGetJavaClassName_Crm()
	{
		Definition definition = mock(Definition.class);
		when(definition.getOutputName()).thenReturn("crm_emails");
		assertThat(pojoDaoStrategy.getJavaClassName(definition, Mode.POJO)).isNotEmpty()
				.isEqualTo("Emails");
	}

	@Test
	void testGetJavaMemberName()
	{
		Definition definition = mock(Definition.class);
		when(definition.getInputName()).thenReturn("affiliates_products");
		assertThat(pojoDaoStrategy.getJavaMemberName(definition, Mode.POJO)).isEqualTo("affiliatesProducts");
	}

	@Test
	void testGetJavaMemberName_isDigit()
	{
		Definition definition = mock(Definition.class);
		when(definition.getInputName()).thenReturn("affiliates_1products");
		assertThat(pojoDaoStrategy.getJavaMemberName(definition, Mode.POJO)).isEqualTo("affiliates_1products");
	}

	@Test
	void testGetJavaMemberName_No_Word()
	{
		Definition definition = mock(Definition.class);
		when(definition.getInputName()).thenReturn("_products");
		assertThat(pojoDaoStrategy.getJavaMemberName(definition, Mode.POJO)).isEqualTo("_Products");
	}

	@Test
	void testGetJavaGetterName()
	{
		Definition definition = mock(Definition.class);
		when(definition.getInputName()).thenReturn("affiliates_products");
		assertThat(pojoDaoStrategy.getJavaGetterName(definition, Mode.POJO)).isEqualTo("getAffiliatesProducts");
	}

	@Test
	void testGetJavaSetterName()
	{
		Definition definition = mock(Definition.class);
		when(definition.getInputName()).thenReturn("affiliates_products");
		assertThat(pojoDaoStrategy.getJavaSetterName(definition, Mode.POJO)).isEqualTo("setAffiliatesProducts");
	}
}