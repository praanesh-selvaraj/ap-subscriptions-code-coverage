package com.kmp.aeroparker.application.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.model.interfaces.IBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class BuilderFactory
{
	private final List<IBuilder> builders;
	private Map<BuilderType, IBuilder> buildersMap = new HashMap<>();

	@PostConstruct
	public void postConstruct()
	{
		buildersMap.putAll(builders.stream()
				.collect(Collectors.toMap(builder -> builder.getType(), builder -> builder)));
	}

	public <T extends IBuilder> T getInstance(final BuilderType type, final Class<T> cls)
	{
		IBuilder builder = null;
		if (type != null && cls != null)
		{
			builder = buildersMap.getOrDefault(type, null);

			if (!cls.isInstance(builder))
			{
				builder = null;
			}
		}
		return builder == null ? null : cls.cast(builder);
	}
}