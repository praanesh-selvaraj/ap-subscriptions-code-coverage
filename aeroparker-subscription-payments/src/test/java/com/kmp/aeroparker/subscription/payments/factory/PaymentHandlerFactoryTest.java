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

import com.kmp.aeroparker.subscription.payments.braintree.BraintreeSubscriptionHandler;
import com.kmp.aeroparker.subscription.payments.braintree.BraintreeTransactionHandler;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.handler.BraintreePaymentHandler;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHandler;
import com.kmp.aeroparker.subscription.payments.processor.BraintreePaymentProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentHandlerFactoryTest
{
	@Mock
	private List<IPaymentHandler> handler;
	@InjectMocks
	private PaymentHandlerFactory factory;
	@Mock
	private PaymentService paymentService;
	@Mock
	private BraintreeObjectFactory braintreeObjectFactory;
	@Mock
	private GlobalProperties globalProperties;
	@Mock
	private BraintreePaymentProcessor paymentProcessor;
	@Mock
	private BraintreeTransactionHandler transactionHandler;
	@Mock
	private PaymentProcessorFactory paymentProcessorFactory;
	@Mock
	private BraintreeSubscriptionHandler braintreeSubscriptionHandler;

	@BeforeEach
	void init()
	{
		final List<IPaymentHandler> handlerList = Stream
				.of(new BraintreePaymentHandler(paymentService, transactionHandler, braintreeSubscriptionHandler, globalProperties, braintreeObjectFactory,
						paymentProcessorFactory))
				.collect(Collectors.toList());
		when(handler.spliterator()).thenReturn(handlerList.spliterator());
		when(handler.stream()).thenCallRealMethod();
	}

	@Test
	void testGetInstance()
	{
		assertThat(factory.getInstance(PaymentGatewayType.BRAINTREE)).isNotNull()
				.isInstanceOf(BraintreePaymentHandler.class);
	}

	@Test
	void testGetInstance_Invalid_Type()
	{
		assertThat(factory.getInstance(null)).isNull();
	}
}