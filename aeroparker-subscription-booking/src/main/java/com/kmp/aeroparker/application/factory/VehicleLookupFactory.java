package com.kmp.aeroparker.application.factory;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.model.interfaces.IVehicleLookup;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class VehicleLookupFactory
{
	private final List<IVehicleLookup> vehicleLookup;

	@PostConstruct
	void postConstruct()
	{
		vehicleLookup.stream()
				.collect(Collectors.toList());
	}

	public IVehicleLookup getInstance(final int lookupServiceId)
	{
		if (lookupServiceId > 0)
		{
			return vehicleLookup.stream()
					.filter(p -> p.getLookupServiceId() == lookupServiceId)
					.findFirst()
					.orElse(null);
		}
		return null;
	}
}
