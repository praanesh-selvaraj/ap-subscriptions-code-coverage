package com.kmp.aeroparker.application.availability;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionProductFinder
{
	private final SubscriptionService service;

	public SubscriptionProductAvailabilityDetailsList getAllAvailableProducts(final int affId, final int siteId, final int langId,
			final int defLangId, final LocalDate startDate)
	{
		log.debug("Loading available products");

		SubscriptionProductAvailabilityDetailsList availabilityDetailsList = new SubscriptionProductAvailabilityDetailsList();

		List<SubscriptionProduct> subscriptionProducts = service.fetchSubscriptionProductBySiteId(siteId);
		List<SubscriptionProduct> affSubProducts = new ArrayList<>();
		if (subscriptionProducts.isEmpty())
		{
			log.debug("No products available");
		}
		else
		{
			List<Integer> affSubProductsIds = service.fetchAffiliateSubscriptionProductsIds(affId);

			for (SubscriptionProduct subscriptionProduct : subscriptionProducts)
			{
				if (affSubProductsIds.contains(subscriptionProduct.getId()))
				{
					affSubProducts.add(subscriptionProduct);
				}
			}

			if (affSubProducts.isEmpty())
			{
				log.debug("No products available");
			}
			else
			{
				for (SubscriptionProduct affProduct : affSubProducts)
				{
					SubscriptionProductAvailabilityDetails availabilityDetails = new SubscriptionProductAvailabilityDetails();
					SubscriptionProductAppearance productAppearance =
							service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(affProduct.getId(), langId);

					if (productAppearance == null && langId != defLangId)
					{
						log.debug("Product appearance is null with langId: {}, attempting to fetch with default lang Id: {}", langId, defLangId);
						// just to be safe, if the appearance is null try
						// loading using the default language
						productAppearance = service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(affProduct.getId(), defLangId);
					}
					if (productAppearance == null)
					{
						log.debug("No products appearance available for ID: {}, removing product from available list", affProduct.getId());
						continue;
					}

					SubscriptionProductTerms productTerms = service.fetchSubscriptionProductTermsBySubProductId(affProduct.getId());
					if (productTerms == null)
					{
						log.debug("No products terms available for ID: {}, removing product from available list", affProduct.getId());
						continue;
					}
					log.debug("Adding product {} to available product list", affProduct.getId());
					availabilityDetails.setProduct(affProduct);
					availabilityDetails.setProductAppearance(productAppearance);
					availabilityDetails.setProductTerms(productTerms);
					availabilityDetails.setPriceDetails(buildPriceDetails(productTerms.getPrice()));
					availabilityDetails.setStartDate(startDate);
					availabilityDetails.setSeasonTicket(
							SubscriptionPeriodType.FIXED.compareTo(SubscriptionPeriodType.valueOf(productTerms.getPeriodType())) == 0);
					availabilityDetails.setRecurringTicket(
							SubscriptionPeriodType.RECURRING.compareTo(SubscriptionPeriodType.valueOf(productTerms.getPeriodType())) == 0);
					availabilityDetailsList.add(availabilityDetails);
				}
			}
		}
		return availabilityDetailsList;
	}

	private PriceDetails buildPriceDetails(final BigDecimal price)
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(price);
		return priceDetails;
	}
}