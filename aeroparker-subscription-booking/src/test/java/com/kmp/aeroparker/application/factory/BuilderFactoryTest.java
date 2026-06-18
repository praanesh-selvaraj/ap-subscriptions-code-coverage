package com.kmp.aeroparker.application.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.builder.ConfigBuilder;
import com.kmp.aeroparker.application.builder.ConfirmationBuilder;
import com.kmp.aeroparker.application.engine.SubscriptionBasketBuilder;
import com.kmp.aeroparker.application.model.SubscriptionReferenceGenerator;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.model.interfaces.IBuilder;
import com.kmp.aeroparker.application.presentation.SubscriptionProductDisplayItemBuilder;

@ExtendWith(MockitoExtension.class)
class BuilderFactoryTest
{
	@Mock
	private SubscriptionProductDisplayItemBuilder subscriptionProductBuilder;
	@Mock
	private ConfigBuilder configBuilder;
	@Mock
	private SubscriptionReferenceGenerator referenceBuilder;
	@Mock
	private SubscriptionBasketBuilder basketBuilder;
	@Mock
	private ConfirmationBuilder confirmationBuilder;
	@Mock
	private List<IBuilder> builders;
	@InjectMocks
	private BuilderFactory factory;

	@BeforeEach
	public void init()
	{
		List<IBuilder> builderList = Stream.of(subscriptionProductBuilder, configBuilder, referenceBuilder, basketBuilder, confirmationBuilder)
				.collect(Collectors.toList());
		when(subscriptionProductBuilder.getType()).thenCallRealMethod();
		when(configBuilder.getType()).thenCallRealMethod();
		when(referenceBuilder.getType()).thenCallRealMethod();
		when(basketBuilder.getType()).thenCallRealMethod();
		when(confirmationBuilder.getType()).thenCallRealMethod();
		when(builders.spliterator()).thenReturn(builderList.spliterator());
		when(builders.stream()).thenCallRealMethod();
		factory.postConstruct();
	}

	@Test
	public void testGetInstance()
	{
		assertThat(factory.getInstance(BuilderType.PRODUCT_DISPLAY_ITEM, SubscriptionProductDisplayItemBuilder.class)).isNotNull()
				.isInstanceOf(SubscriptionProductDisplayItemBuilder.class);
		assertThat(factory.getInstance(BuilderType.CONFIG, ConfigBuilder.class)).isNotNull()
				.isInstanceOf(ConfigBuilder.class);
		assertThat(factory.getInstance(BuilderType.REFERENCE, SubscriptionReferenceGenerator.class)).isNotNull()
				.isInstanceOf(SubscriptionReferenceGenerator.class);
		assertThat(factory.getInstance(BuilderType.BASKET, SubscriptionBasketBuilder.class)).isNotNull()
				.isInstanceOf(SubscriptionBasketBuilder.class);
		assertThat(factory.getInstance(BuilderType.CONFIRMATION, ConfirmationBuilder.class)).isNotNull()
				.isInstanceOf(ConfirmationBuilder.class);
	}

	@Test
	public void testGetInstance_Type_Null()
	{
		assertThat(factory.getInstance(null, SubscriptionProductDisplayItemBuilder.class)).isNull();
	}

	@Test
	public void testGetInstance_Unknown_Instance()
	{
		assertThat(factory.getInstance(BuilderType.REFERENCE, SubscriptionProductDisplayItemBuilder.class)).isNull();
	}

	@Test
	public void testGetInstance_Class_Null()
	{
		assertNull(factory.getInstance(BuilderType.REFERENCE, null));
	}
}