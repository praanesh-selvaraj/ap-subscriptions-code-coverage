package com.kmp.aeroparker.application.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.application.processor.BookingItemProcessor;
import com.kmp.aeroparker.application.processor.ContactProcessor;
import com.kmp.aeroparker.application.processor.CustomerDetailsProcessor;
import com.kmp.aeroparker.application.processor.SubscriptionGuidProcessor;

@ExtendWith(MockitoExtension.class)
class ProcessorFactoryTest
{
	@Mock
	private SubscriptionGuidProcessor guidProcessor;
	@Mock
	private BookingItemProcessor bookingItemProcessor;
	@Mock
	private CustomerDetailsProcessor customerDetailsProcessor;
	@Mock
	private ContactProcessor contactProcessor;
	@Mock
	private List<IProcessor> processors;
	@InjectMocks
	private ProcessorFactory factory;

	@BeforeEach
	public void init()
	{
		List<IProcessor> processorList = Stream.of(contactProcessor, guidProcessor, bookingItemProcessor, customerDetailsProcessor)
				.collect(Collectors.toList());
		when(contactProcessor.getType()).thenCallRealMethod();
		when(guidProcessor.getType()).thenCallRealMethod();
		when(bookingItemProcessor.getType()).thenCallRealMethod();
		when(customerDetailsProcessor.getType()).thenCallRealMethod();

		when(processors.spliterator()).thenReturn(processorList.spliterator());
		when(processors.stream()).thenCallRealMethod();
		factory.postConstruct();
	}

	@Test
	public void testGetInstance()
	{
		assertThat(factory.getInstance(ProcessorType.CONTACT, ContactProcessor.class)).isNotNull()
				.isInstanceOf(ContactProcessor.class);
		assertThat(factory.getInstance(ProcessorType.GUID_BOOKING, SubscriptionGuidProcessor.class)).isNotNull()
				.isInstanceOf(SubscriptionGuidProcessor.class);
		assertThat(factory.getInstance(ProcessorType.CUSTOMER_DETAILS, CustomerDetailsProcessor.class)).isNotNull()
				.isInstanceOf(CustomerDetailsProcessor.class);
		assertThat(factory.getInstance(ProcessorType.ITEM_BOOKING, BookingItemProcessor.class)).isNotNull()
				.isInstanceOf(BookingItemProcessor.class);
	}

	@Test
	public void testGetInstance_Type_Null()
	{
		assertThat(factory.getInstance(null, ContactProcessor.class)).isNull();
	}

	@Test
	public void testGetInstance_Unknown_Instance()
	{
		assertThat(factory.getInstance(ProcessorType.CONTACT, CustomerDetailsProcessor.class)).isNull();
	}

	@Test
	public void testGetInstance_Class_Null()
	{
		assertNull(factory.getInstance(ProcessorType.CONTACT, null));
	}
}