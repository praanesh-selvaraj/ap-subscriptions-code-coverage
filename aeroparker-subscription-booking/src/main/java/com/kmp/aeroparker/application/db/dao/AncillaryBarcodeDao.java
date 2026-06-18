package com.kmp.aeroparker.application.db.dao;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteAncillaryBarcodeConfiguration;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class AncillaryBarcodeDao
{
	private DSLContext dsl;

	public List<SiteAncillaryBarcodeConfiguration> fetchAncillaryBarcodeConfigurationBySubscriptionProductId(
			int productId)
	{
		return dsl.select(Tables.SITE_ANCILLARY_BARCODE_CONFIGURATION.asterisk())
				.from(Tables.SITE_ANCILLARY_BARCODE_CONFIGURATION)
				.join(Tables.SITE_ANCILLARY_BARCODE_CONFIGURATION_SUBSCRIPTION_PRODUCT)
				.on(Tables.SITE_ANCILLARY_BARCODE_CONFIGURATION.ID
						.eq(Tables.SITE_ANCILLARY_BARCODE_CONFIGURATION_SUBSCRIPTION_PRODUCT.CONFIGURATION_ID))
				.where(Tables.SITE_ANCILLARY_BARCODE_CONFIGURATION_SUBSCRIPTION_PRODUCT.SUBSCRIPTION_PRODUCT_ID
						.eq(productId))
				.fetchInto(SiteAncillaryBarcodeConfiguration.class);
	}
}
