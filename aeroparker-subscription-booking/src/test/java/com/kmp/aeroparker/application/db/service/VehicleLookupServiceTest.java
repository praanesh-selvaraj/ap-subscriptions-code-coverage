package com.kmp.aeroparker.application.db.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.VehicleLookupDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;

@ExtendWith(MockitoExtension.class)
public class VehicleLookupServiceTest
{
	@Mock
	private VehicleLookupDao dao;
	@InjectMocks
	private VehicleLookupService service;

	@Test
	public void testFetchActiveVehicleLookupAffiliateLoginsByAffiliateId()
	{
		when(dao.fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(anyInt()))
				.thenReturn(mock(VehiclelookupAffiliateLogins.class));

		assertNotNull(service.fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(1));

		verify(dao).fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(anyInt());
	}

	@Test
	public void testFetchActiveVehicleLookupAffiliateLoginsByAffiliateId_ZeroAffilaiteId()
	{
		assertNull(service.fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(0));

		verifyNoInteractions(dao);
	}
}
