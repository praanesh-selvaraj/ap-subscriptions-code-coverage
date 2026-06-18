package com.kmp.aeroparker.application.db.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.tenants.Tables;
import com.kmp.aeroparker.subscription.booking.tenants.tables.pojos.Tenant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class TenantDao
{
	private final DSLContext dsl;

	public Tenant fetchByDomain(final String domain)
	{
		return dsl.selectFrom(Tables.TENANT)
				.where(Tables.TENANT.DOMAIN.eq(domain))
				.fetchOneInto(Tenant.class);
	}
}