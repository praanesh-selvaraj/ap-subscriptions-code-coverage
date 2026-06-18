package com.kmp.aeroparker.subscription.payments.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.RefundProcessor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RefundProcessorFactory
{
	private final List<RefundProcessor> processors;
	private Map<PaymentGatewayType, RefundProcessor> processorMap = new HashMap<>();

	@PostConstruct
	public void postConstruct()
	{
		processorMap.putAll(processors.stream()
				.collect(Collectors.toMap(cls -> cls.getType(), cls -> cls)));
	}

	public RefundProcessor getInstance(final PaymentGatewayType type)
	{
		RefundProcessor processor = null;
		if (type != null)
		{
			processor = processorMap.getOrDefault(type, null);
		}
		return processor == null ? null : processor;
	}
}