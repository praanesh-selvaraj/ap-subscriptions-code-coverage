package com.kmp.aeroparker.subscription.payments.factory;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.RefundProcessorFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.RefundProcessor;
import com.kmp.aeroparker.subscription.payments.refund.processor.BraintreeRefundProcessor;

@ExtendWith(MockitoExtension.class)
class RefundProcessorFactoryTest
{
	@Mock
	private List<RefundProcessor> processors;
	@Mock
	private BraintreeRefundProcessor braintreeRefundProcessor;
	@InjectMocks
	private RefundProcessorFactory factory;

	@BeforeEach
	void init()
	{
		final List<RefundProcessor> processorList = Stream.of(braintreeRefundProcessor)
				.collect(Collectors.toList());
		when(braintreeRefundProcessor.getType()).thenCallRealMethod();
		when(processors.spliterator()).thenReturn(processorList.spliterator());
		when(processors.stream()).thenCallRealMethod();
		factory.postConstruct();
	}

	@Test
	void testGetInstance()
	{
		assertThat(factory.getInstance(PaymentGatewayType.BRAINTREE)).isNotNull()
				.isInstanceOf(BraintreeRefundProcessor.class);
	}

	@Test
	void testGetInstance_Invalid_Type()
	{
		assertThat(factory.getInstance(null)).isNull();
	}
}