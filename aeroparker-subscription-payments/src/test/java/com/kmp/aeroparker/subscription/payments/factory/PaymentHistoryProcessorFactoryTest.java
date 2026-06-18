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
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHistoryProcessor;
import com.kmp.aeroparker.subscription.payments.processor.history.BraintreePaymentHistoryProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryProcessorFactoryTest
{
	@Mock
	private List<IPaymentHistoryProcessor> processors;
	@InjectMocks
	private PaymentHistoryProcessorFactory factory;

	@Mock
	private PaymentService paymentService;
	@Mock
	private BraintreeObjectFactory objectFactory;

	@BeforeEach
	void init()
	{
		final List<IPaymentHistoryProcessor> processorList = Stream.of(new BraintreePaymentHistoryProcessor(paymentService, objectFactory))
				.collect(Collectors.toList());
		when(processors.spliterator()).thenReturn(processorList.spliterator());
		when(processors.stream()).thenCallRealMethod();
	}

	@Test
	void testGetInstance()
	{
		assertThat(factory.getInstance(PaymentGatewayType.BRAINTREE)).isNotNull()
				.isInstanceOf(BraintreePaymentHistoryProcessor.class);
	}

	@Test
	void testGetInstance_Invalid_Type()
	{
		assertThat(factory.getInstance(null)).isNull();
	}
}