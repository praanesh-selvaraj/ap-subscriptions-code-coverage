package com.kmp.aeroparker.application.db.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.VehiclelookupAffiliateLoginsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class VehicleLookupDao
{
	private final DSLContext dsl;

	public VehiclelookupAffiliateLogins fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(final int affiliateId)
	{
		return new VehiclelookupAffiliateLoginsDao(dsl.configuration()).fetchByAffiliate(affiliateId)
				.stream()
				.filter(VehiclelookupAffiliateLogins::getIsActive)
				.findFirst()
				.orElse(null);
	}
}
