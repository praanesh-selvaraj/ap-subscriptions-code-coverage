package com.kmp.aeroparker.application.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.factory.VehicleLookupFactory;
import com.kmp.aeroparker.application.model.interfaces.IVehicleLookup;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;

@ExtendWith(MockitoExtension.class)
public class VehicleLookUpControllerTest
{
	@Mock
	private SubscriptionControllerService service;
	@Mock
	private VehicleLookupFactory factory;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@InjectMocks
	private VehicleLookUpController controller;

	@Mock
	private VehiclelookupAffiliateLogins login;
	@Mock
	private IVehicleLookup vehicleLookup;

	@Test
	public void testDoVehiclelookup()
	{
		when(service.getVehicleLookupLogin(anyInt())).thenReturn(login);
		when(login.getLookupService()).thenReturn(2);
		when(login.getLookupMode()).thenReturn("AUTO");
		when(factory.getInstance(anyInt())).thenReturn(vehicleLookup);
		when(vehicleLookup.doLookup(any(), any(), any())).thenReturn("Success");

		String response = controller.doVehiclelookup("reg", 1);

		assertEquals("Success", response);
		verify(service).getVehicleLookupLogin(anyInt());
		verify(vehicleLookup).doLookup(any(), any(), any());
	}

	@Test
	public void testDoVehiclelookup_EmptyReg()
	{
		String response = controller.doVehiclelookup("", 1);

		assertEquals("{}", response);
		verifyNoInteractions(service);
		verifyNoInteractions(vehicleLookup);
	}

	@Test
	public void testDoVehiclelookup_NullAffiliateId()
	{
		String response = controller.doVehiclelookup("reg", null);

		assertEquals("{}", response);
		verifyNoInteractions(service);
		verifyNoInteractions(vehicleLookup);
	}

	@Test
	public void testDoVehiclelookup_ZeroAffiliateId()
	{
		String response = controller.doVehiclelookup("reg", 0);

		assertEquals("{}", response);
		verifyNoInteractions(service);
		verifyNoInteractions(vehicleLookup);
	}

	@Test
	public void testDoVehiclelookup_NullLogin()
	{
		when(service.getVehicleLookupLogin(anyInt())).thenReturn(null);

		String response = controller.doVehiclelookup("reg", 1);

		assertEquals("{}", response);
		verify(service).getVehicleLookupLogin(anyInt());
		verifyNoInteractions(vehicleLookup);
	}

	@Test
	public void testDoVehiclelookup_ServiceNotSupported()
	{
		when(service.getVehicleLookupLogin(anyInt())).thenReturn(login);
		when(login.getLookupService()).thenReturn(2);
		when(factory.getInstance(anyInt())).thenReturn(null);

		String response = controller.doVehiclelookup("reg", 1);

		assertEquals("{}", response);
		verify(service).getVehicleLookupLogin(anyInt());
		verifyNoInteractions(vehicleLookup);
	}

	@Test
	public void testDoVehiclelookup_NoLookupMode()
	{
		when(service.getVehicleLookupLogin(anyInt())).thenReturn(login);
		when(login.getLookupService()).thenReturn(2);
		when(factory.getInstance(anyInt())).thenReturn(vehicleLookup);

		String response = controller.doVehiclelookup("reg", 1);

		assertEquals("{}", response);
		verify(service).getVehicleLookupLogin(anyInt());
		verifyNoInteractions(vehicleLookup);
	}
}
