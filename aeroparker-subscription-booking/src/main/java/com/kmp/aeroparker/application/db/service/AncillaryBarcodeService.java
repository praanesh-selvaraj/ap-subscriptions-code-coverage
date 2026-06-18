package com.kmp.aeroparker.application.db.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.AncillaryBarcodeDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteAncillaryBarcodeConfiguration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class AncillaryBarcodeService
{
	private AncillaryBarcodeDao dao;

	public List<SiteAncillaryBarcodeConfiguration> fetchAncillaryBarcodeConfigurationBySubscriptionProductId(
			int productId)
	{
		List<SiteAncillaryBarcodeConfiguration> configurationList = new ArrayList<>();
		if (productId > 0)
		{
			configurationList = dao.fetchAncillaryBarcodeConfigurationBySubscriptionProductId(productId);
		}
		else
		{
			log.debug("Could not fetch site ancillary barcode configuration as product id was invalid");
		}
		return configurationList;
	}

}
