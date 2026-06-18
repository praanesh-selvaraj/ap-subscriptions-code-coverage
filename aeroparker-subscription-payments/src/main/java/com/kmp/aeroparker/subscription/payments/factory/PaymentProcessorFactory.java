package com.kmp.aeroparker.subscription.payments.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentProcessor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PaymentProcessorFactory
{
	private final List<IPaymentProcessor> processors;

	public IPaymentProcessor getInstance(final PaymentGatewayType type)
	{
		return processors.stream()
				.filter(processor -> processor.getType() == type)
				.findFirst()
				.orElse(null);
	}
}