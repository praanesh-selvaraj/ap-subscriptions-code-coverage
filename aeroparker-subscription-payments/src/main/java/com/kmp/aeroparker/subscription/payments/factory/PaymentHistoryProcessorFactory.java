package com.kmp.aeroparker.subscription.payments.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHistoryProcessor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PaymentHistoryProcessorFactory
{
	private final List<IPaymentHistoryProcessor> processors;

	public IPaymentHistoryProcessor getInstance(final PaymentGatewayType type)
	{
		return processors.stream()
				.filter(processor -> processor.getType() == type)
				.findFirst()
				.orElse(null);
	}
}