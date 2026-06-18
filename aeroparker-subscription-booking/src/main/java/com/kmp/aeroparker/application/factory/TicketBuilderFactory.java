package com.kmp.aeroparker.application.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.ISubscriptionBookingItem;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TicketBuilderFactory
{
	private final List<ISubscriptionBookingItem> ticketBuilders;
	private final Map<SubscriptionPeriodType, ISubscriptionBookingItem> instancesMap = new HashMap<>();;

	@PostConstruct
	void postConstruct()
	{
		instancesMap.putAll(ticketBuilders.stream()
				.collect(Collectors.toMap(p -> p.getPeriodType(), p -> p)));
	}

	public <T extends ISubscriptionBookingItem> T getInstance(final SubscriptionPeriodType type, final Class<T> cls)
	{
		ISubscriptionBookingItem builder = null;
		if (type != null && cls != null)
		{
			builder = instancesMap.getOrDefault(type, null);

			if (!cls.isInstance(builder))
			{
				builder = null;
			}
		}
		return builder == null ? null : cls.cast(builder);
	}
}