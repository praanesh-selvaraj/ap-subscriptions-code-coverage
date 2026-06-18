package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.dao.TenantDao;
import com.kmp.aeroparker.subscription.booking.tenants.tables.pojos.Tenant;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest
{
	@Mock
	private CustomConnectionProvider provider;
	@Mock
	private TenantDao dao;
	@InjectMocks
	private TenantService service;
	@Mock
	private Tenant tenant;

	@Test
	void testFetchByDomain()
	{
		when(dao.fetchByDomain(anyString())).thenReturn(tenant);
		assertThat(service.fetchByDomain("tenant")).isNotNull()
				.isInstanceOf(Tenant.class);
	}

	@Test
	void testFetchByDomain_Empty_Domain()
	{
		assertThat(service.fetchByDomain("")).isNull();
	}

	@Test
	void testTenantSetup()
	{
		when(tenant.getSchema()).thenReturn("kmp_advancedbooker");
		when(dao.fetchByDomain(anyString())).thenReturn(tenant);
		assertThat(service.tenantSetup("domain")).isTrue();
	}

	@Test
	void testTenantSetup_Tenant_Null()
	{
		when(dao.fetchByDomain(anyString())).thenReturn(null);
		assertThrows(ResponseStatusException.class, () -> service.tenantSetup("domain"));
	}
}
