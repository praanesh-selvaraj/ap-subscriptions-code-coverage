package com.kmp.aeroparker.application.db.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.tenants.Tables;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class GlobalPropertiesDao
{
	private final DSLContext dsl;
	
	public String fetchProperty(final String key)
	{
		return dsl.select(Tables.GLOBAL_PROPERTIES.VALUE)
				.from(Tables.GLOBAL_PROPERTIES)
				.where(Tables.GLOBAL_PROPERTIES.KEY.eq(key))
				.limit(1)
				.fetchOneInto(String.class);
	}
}
