package com.kmp.aeroparker.application.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.application.factory.VehicleLookupFactory;
import com.kmp.aeroparker.application.model.interfaces.IVehicleLookup;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;
import com.kmp.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping(value = "/subscriptions/{affCode}")
public class VehicleLookUpController
{
	private final SubscriptionControllerService service;
	private final VehicleLookupFactory factory;
	private final LanguageFieldsList languageFieldsList;

	@PostMapping(value = "/vehicle-lookup")
	public String doVehiclelookup(@RequestParam(name = "reg", required = true) final String reg,
			@RequestParam(name = "affiliateId", required = true) final Integer affiliateId)
	{
		JsonObject response = new JsonObject();
		String responseString = response.toString();

		if (StringUtil.isNullOrEmpty(reg) || affiliateId == null || affiliateId < 1)
		{
			log.info("Vehicle reg or affiliate id was null. Unable to perform vehicle lookup.");
			return responseString;
		}

		VehiclelookupAffiliateLogins vehicleLookupLogin = service.getVehicleLookupLogin(affiliateId);
		if (vehicleLookupLogin == null)
		{
			log.info("Vehicle Lookup login details are null or disabled. Unable to perform vehicle lookup.");
			return responseString;
		}
		
		int lookupType = vehicleLookupLogin.getLookupService();
		IVehicleLookup lookupService = factory.getInstance(lookupType);

		if (lookupService == null)
		{
			log.info("Vehicle lookup service could not be found or is not supported for subscriptions");
		}
		else if (StringUtil.isNullOrEmpty(vehicleLookupLogin.getLookupMode()))
		{
			log.info("Vehicle lookup mode is not specified, unable to complete lookup");
		}
		else
		{
			log.debug("Performing vehicle look up for reg: {} with service: {}", reg, lookupType);
			responseString = lookupService.doLookup(vehicleLookupLogin, reg, languageFieldsList);
		}

		return responseString;
	}
}
