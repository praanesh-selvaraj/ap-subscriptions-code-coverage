package com.kmp.aeroparker.application.processor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.kmp.aeroparker.application.barcode.sender.AncillaryBarcodeGenerator;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.AncillaryBarcodeService;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteAncillaryBarcodeConfiguration;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class AncillaryBarcodeProcessor
{
	private final AncillaryBarcodeService service;
	private final AncillaryBarcodeGenerator barcodeGenerator;

	public void generateNewAncillaryBarcode(SubscriptionBookingRecord booking)
	{
		String reference = booking.getBooking()
				.getReference();
		log.info("Checking if ancillary barcode should be generated for booking reference {}", reference);

		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = booking.getBookingItemMap();

		for (Map.Entry<SubscriptionBookingItem, IBookingTicket> entry : bookingItemMap.entrySet())
		{
			SubscriptionBookingItem bookingItem = entry.getKey();
			int productId = bookingItem.getProductId() != null ? bookingItem.getProductId() : 0;

			if (productId < 1)
			{
				log.error("Product id was invalid for booking item record id {} for booking reference {}",
						bookingItem.getId(), reference);
				continue;
			}

			List<SiteAncillaryBarcodeConfiguration> configurationList =
					service.fetchAncillaryBarcodeConfigurationBySubscriptionProductId(productId);

			if (!CollectionUtils.isEmpty(configurationList))
			{
				barcodeGenerator.generate(reference, productId);
			}
		}
	}
}
