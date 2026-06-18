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

import com.kmp.aeroparker.application.builder.SeasonTicketBookingBuilder;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.ISubscriptionBookingItem;

@ExtendWith(MockitoExtension.class)
class TicketBuilderFactoryTest
{
	@Mock
	private SeasonTicketBookingBuilder seasonTicketBuilder;
	@Mock
	private List<ISubscriptionBookingItem> ticketBuilders;
	@InjectMocks
	private TicketBuilderFactory factory;

	@BeforeEach
	public void init()
	{
		List<ISubscriptionBookingItem> builderList = Stream.of(seasonTicketBuilder)
				.collect(Collectors.toList());
		when(seasonTicketBuilder.getPeriodType()).thenCallRealMethod();
		when(ticketBuilders.spliterator()).thenReturn(builderList.spliterator());
		when(ticketBuilders.stream()).thenCallRealMethod();
		factory.postConstruct();
	}

	@Test
	public void testGetInstance()
	{
		assertThat(factory.getInstance(SubscriptionPeriodType.FIXED, SeasonTicketBookingBuilder.class)).isNotNull()
				.isInstanceOf(SeasonTicketBookingBuilder.class);
	}

	@Test
	public void testGetInstance_Unmatched_instance()
	{
		assertThat(factory.getInstance(SubscriptionPeriodType.RECURRING, SeasonTicketBookingBuilder.class)).isNull();
	}

	@Test
	public void testGetInstance_Unknown_Instance()
	{
		assertNull(factory.getInstance(SubscriptionPeriodType.FIXED, null));
	}

	@Test
	public void testGetInstance_Type_Null()
	{
		assertNull(factory.getInstance(null, null));
	}
}