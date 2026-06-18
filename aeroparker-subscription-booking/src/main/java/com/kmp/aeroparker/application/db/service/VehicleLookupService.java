package com.kmp.aeroparker.application.db.service;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.VehicleLookupDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class VehicleLookupService
{
	private final VehicleLookupDao dao;

	public VehiclelookupAffiliateLogins fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(final int affiliateId)
	{
		VehiclelookupAffiliateLogins login = null;

		if (affiliateId > 0)
		{
			login = dao.fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(affiliateId);
		}
		else
		{
			log.debug("Affilate id was zero. Could not fetch vehicle look up service.");
		}
		return login;
	}
}
