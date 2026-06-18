package com.kmp.aeroparker.subscription.payments.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHandler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PaymentHandlerFactory
{
	private final List<IPaymentHandler> handler;

	public IPaymentHandler getInstance(final PaymentGatewayType type)
	{
		return handler.stream()
				.filter(handler -> handler.getType() == type)
				.findFirst()
				.orElse(null);
	}
}