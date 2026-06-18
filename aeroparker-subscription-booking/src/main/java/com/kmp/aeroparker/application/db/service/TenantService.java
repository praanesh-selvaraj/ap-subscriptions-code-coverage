package com.kmp.aeroparker.application.db.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.dao.TenantDao;
import com.kmp.aeroparker.subscription.booking.tenants.tables.pojos.Tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TenantService
{
	private final CustomConnectionProvider provider;
	private final TenantDao dao;

	public Tenant fetchByDomain(final String domain)
	{
		Tenant tenant = null;

		if (!StringUtils.isEmpty(domain))
		{
			tenant = dao.fetchByDomain(domain);
			log.debug("tenant fetched successfully : {}", tenant);
		}
		return tenant;
	}

	public boolean tenantSetup(final String domain)
	{
		final Tenant tenant = fetchByDomain(domain);
		if (tenant == null)
		{
			log.info("Tenant does not exist for domain {}", domain);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		else
		{
			final String schema = tenant.getSchema();
			log.debug("Tenant exists for domain {} setting schema {} in connection provider", domain, schema);
			provider.setSchema(schema);
			return true;
		}
	}
}