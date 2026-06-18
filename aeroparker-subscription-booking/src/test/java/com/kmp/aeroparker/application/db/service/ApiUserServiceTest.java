package com.kmp.aeroparker.application.db.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.dao.ApiUserDao;
import com.kmp.aeroparker.subscription.booking.api.tables.pojos.ApiUser;
import com.kmp.aeroparker.subscription.booking.api.tables.pojos.ApiUserSettings;

@ExtendWith(MockitoExtension.class)
class ApiUserServiceTest
{
	@InjectMocks
	private ApiUserService service;
	@Mock
	private ApiUserSettings apiUser;
	@Mock
	private ApiUserDao dao;
	@Mock
	private CustomConnectionProvider provider;

	@Test
	public void testFetchAeroparkerApiUserBySchemaAndSiteId()
	{
		when(dao.fetchAeroparkerApiUserBySchemaAndSiteId(anyString(), anyInt())).thenReturn(apiUser);

		assertNotNull(service.fetchAeroparkerApiUserBySchemaAndSiteId("schema", 1));
	}

	@Test
	public void testFetchAeroparkerApiUserBySchemaAndSiteId_Null_Schema()
	{
		assertNull(service.fetchAeroparkerApiUserBySchemaAndSiteId(null, 1));
	}

	@Test
	public void testFetchAeroparkerApiUserBySchemaAndSiteId_0_SiteId()
	{
		assertNull(service.fetchAeroparkerApiUserBySchemaAndSiteId("schema", 0));
	}
}
