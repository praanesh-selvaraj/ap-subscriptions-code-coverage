package com.kmp.aeroparker.application.factory;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.model.interfaces.IVehicleLookup;
import com.kmp.aeroparker.application.vehiclelookup.MotorcheckLookup;

@ExtendWith(MockitoExtension.class)
public class VehicleLookupFactoryTest
{
	private MotorcheckLookup motorcheckLookup = new MotorcheckLookup();
	private VehicleLookupFactory factory;

	@BeforeEach
	public void init()
	{
		List<IVehicleLookup> lookupList = Stream.of(motorcheckLookup)
				.collect(Collectors.toList());
		factory = new VehicleLookupFactory(lookupList);
		factory.postConstruct();
	}

	@Test
	public void testGetInstance_MotorcheckLookup()
	{
		assertEquals(motorcheckLookup, factory.getInstance(2));
	}

	@Test
	public void testGetInstance_InvalidId()
	{
		assertEquals(null, factory.getInstance(100));
	}
}
