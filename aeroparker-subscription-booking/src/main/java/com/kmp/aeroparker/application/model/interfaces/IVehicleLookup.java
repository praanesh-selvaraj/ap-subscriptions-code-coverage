package com.kmp.aeroparker.application.model.interfaces;

import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;

public interface IVehicleLookup
{
	/**
	 * 
	 * @param VehiclelookupAffiliateLogins
	 *            the login details for using the service.
	 * @param String
	 *            the reg to lookup.
	 * @param LanguageFieldsList
	 *            If we want to translate what is returned.
	 * @return String a json representation of the vehicle details. Must include make, model and colour.
	 */
	public String doLookup(VehiclelookupAffiliateLogins loginDetails, String reg, LanguageFieldsList fields);

	int getLookupServiceId();
}
