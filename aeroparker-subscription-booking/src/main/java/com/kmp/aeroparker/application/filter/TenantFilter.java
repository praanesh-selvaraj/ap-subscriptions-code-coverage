package com.kmp.aeroparker.application.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import com.kmp.aeroparker.application.db.service.TenantService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter
{
	private static final String ELB_HEALTH_CHECK = "/actuator/health";
	private final TenantService service;
	private final UrlPathHelper pathHelper;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws ServletException, IOException
	{
		log.debug("Inside tenant filter");
		final String path = pathHelper.getPathWithinApplication(request);
		log.debug("Path received {}", path);
		if (!path.equals(ELB_HEALTH_CHECK))
		{
			if (service.tenantSetup(request.getServerName()))
			{
				log.debug("Passed Tenant Filter");
				filterChain.doFilter(request, response);
				return;
			}
		}
		filterChain.doFilter(request, response);
	}
}