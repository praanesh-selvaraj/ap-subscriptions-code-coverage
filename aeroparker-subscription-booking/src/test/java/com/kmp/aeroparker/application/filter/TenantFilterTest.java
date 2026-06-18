package com.kmp.aeroparker.application.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UrlPathHelper;

import com.kmp.aeroparker.application.db.service.TenantService;
import com.kmp.aeroparker.subscription.booking.tenants.tables.pojos.Tenant;

@ExtendWith(MockitoExtension.class)
class TenantFilterTest
{
	@Mock
	private TenantService service;
	@InjectMocks
	private TenantFilter tenantFilter;
	@Mock
	private Tenant tenant;
	@Mock
	private HttpServletRequest request;
	@Mock
	private UrlPathHelper pathHelper;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain chain;

	@Test
	void testDoFilter() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("/booking");
		when(request.getServerName()).thenReturn("servlet_name");
		when(service.tenantSetup(anyString())).thenReturn(true);
		tenantFilter.doFilterInternal(request, response, chain);
		verify(service).tenantSetup(anyString());
		verify(chain).doFilter(any(), any());
	}

	@Test
	void testDoFilter_Acuator() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("/actuator/health");
		tenantFilter.doFilterInternal(request, response, chain);
		verifyNoInteractions(service);
		verify(chain).doFilter(any(), any());
		verifyNoInteractions(service, request, response);
	}

	@Test
	void testDoFilter_Tenant_Null() throws IOException, ServletException
	{
		when(pathHelper.getPathWithinApplication(any())).thenReturn("/booking");
		when(request.getServerName()).thenReturn("servlet_name");
		when(service.tenantSetup(anyString())).thenReturn(false);
		tenantFilter.doFilterInternal(request, response, chain);
		verify(service).tenantSetup(anyString());
		verify(chain).doFilter(any(), any());
		verifyNoMoreInteractions(chain);
	}
}