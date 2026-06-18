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
import com.kmp.aeroparker.subscription.payments.factory.PaymentProcessorFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentProcessor;
import com.kmp.aeroparker.subscription.payments.processor.BraintreePaymentProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorFactoryTest
{
	@Mock
	private List<IPaymentProcessor> processors;
	@InjectMocks
	private PaymentProcessorFactory factory;

	@Mock
	private PaymentService paymentService;

	@BeforeEach
	void init()
	{
		final List<IPaymentProcessor> processorList = Stream.of(new BraintreePaymentProcessor(paymentService))
				.collect(Collectors.toList());
		when(processors.spliterator()).thenReturn(processorList.spliterator());
		when(processors.stream()).thenCallRealMethod();
	}

	@Test
	void testGetInstance()
	{
		assertThat(factory.getInstance(PaymentGatewayType.BRAINTREE)).isNotNull()
				.isInstanceOf(BraintreePaymentProcessor.class);
	}

	@Test
	void testGetInstance_Invalid_Type()
	{
		assertThat(factory.getInstance(null)).isNull();
	}
}