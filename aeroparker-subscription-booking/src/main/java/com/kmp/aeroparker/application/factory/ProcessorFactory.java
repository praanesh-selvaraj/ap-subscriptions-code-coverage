package com.kmp.aeroparker.application.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ProcessorFactory
{
	private final List<IProcessor> processors;
	private Map<ProcessorType, IProcessor> processorMap = new HashMap<>();

	@PostConstruct
	public void postConstruct()
	{
		processorMap.putAll(processors.stream()
				.collect(Collectors.toMap(cls -> cls.getType(), cls -> cls)));
	}

	public <T extends IProcessor> T getInstance(final ProcessorType type, final Class<T> cls)
	{
		IProcessor processor = null;
		if (type != null && cls != null)
		{
			processor = processorMap.getOrDefault(type, null);

			if (!cls.isInstance(processor))
			{
				processor = null;
			}
		}
		return processor == null ? null : cls.cast(processor);
	}
}