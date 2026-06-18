package com.kmp.aeroparker.application.db.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.api.tables.pojos.ApiUserSettings;
import com.kmp.aeroparker.subscription.booking.api.Tables;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class ApiUserDao
{
	private final DSLContext dsl;

	public ApiUserSettings fetchAeroparkerApiUserBySchemaAndSiteId(String schema, int siteId)
	{
		return dsl.selectFrom(Tables.API_USER_SETTINGS)
				.where(Tables.API_USER_SETTINGS.SCHEMA.eq(schema))
				.and(Tables.API_USER_SETTINGS.SITE_ID.eq(siteId))
				.and(Tables.API_USER_SETTINGS.AEROPARKER.eq(true))
				.limit(1)
				.fetchOneInto(ApiUserSettings.class);
	}
}
