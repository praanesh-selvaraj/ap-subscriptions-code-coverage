package com.kmp.aeroparker.application.engine;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.availability.PriceDetails;
import com.kmp.aeroparker.application.db.service.CarParkService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Carparks;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class PurchaseRequestBuilder
{
	private final SubscriptionService service;
	private final SiteService siteService;
	private final Localise localise;
	private final LanguageFieldsList languageFieldsList;
	private final CarParkService carParkService;

	public SubscriptionPurchaseRequest build(final SubscriptionProduct product, final int langId, final int defLangId,
			final LocalDate startDate, final Affiliates affiliate,
			final AffiliateSubscriptionSettings affiliateSubscriptionSettings)
	{
		// Get all car parks linked to selected product and store them in purchase request
		List<Carparks> carParks = carParkService.fetchAllCarParksLinkedToBookingProduct(product.getId());
		int carParkId = Optional.ofNullable(product.getCarParkId()).orElse(carParks.isEmpty() ? 0 :
			carParks.get(0).getId());
		SubscriptionPurchaseRequest purchaseRequest = new SubscriptionPurchaseRequest();
		purchaseRequest.setProductId(product.getId());
		purchaseRequest.setCarParkId(carParkId);
		purchaseRequest.setProduct(product);
		purchaseRequest.setStartDate(startDate);
		purchaseRequest.setLocalisedStartDate(localise.longDateTranslated(DateUtil.localDateToCalendar(startDate), languageFieldsList));
		purchaseRequest.setSubscriptionCarparks(carParks);
		SubscriptionProductTerms productTerms = service.fetchSubscriptionProductTermsBySubProductId(product.getId());
		if (productTerms != null)
		{
			purchaseRequest.setPurchaseAgreement(buildAgreement(productTerms, affiliate, affiliateSubscriptionSettings, startDate));
			LocalDate endDate = getEndDate(startDate, productTerms.getMinimumTerm());
			purchaseRequest.setEndDate(endDate);
			purchaseRequest.setLocalisedEndDate(localise.longDateTranslated(DateUtil.localDateToCalendar(endDate), languageFieldsList));
			purchaseRequest.setPeriodType(SubscriptionPeriodType.valueOf(productTerms.getPeriodType()));
			purchaseRequest.setMinimumTerm(SubscriptionMinimumTerm.valueOf(productTerms.getMinimumTerm()));
		}
		purchaseRequest.setCarPark(getCarPark(carParkId));
		purchaseRequest.setDisplayName(getDisplayName(product.getId(), langId, defLangId));
		return purchaseRequest;
	}

	private String getDisplayName(final int productId, final int langId, final int defLangId)
	{
		SubscriptionProductAppearance productAppearance = Optional
				.ofNullable(service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(productId, langId))
				.orElseGet(() -> service.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(productId, defLangId));
		String displayName = "";
		if (productAppearance != null)
		{
			displayName = productAppearance.getDisplayName();
		}
		return displayName;
	}

	private Carparks getCarPark(final int carParkId)
	{
		return carParkService.fetchCarParkById(carParkId);
	}

	private SubscriptionPurchaseAgreement buildAgreement(final SubscriptionProductTerms productTerms,
			final Affiliates affiliate, final AffiliateSubscriptionSettings affiliateSubscriptionSettings,
			final LocalDate startDate)
	{
		SubscriptionPurchaseAgreement purchaseAgreement = new SubscriptionPurchaseAgreement();
		PriceDetails price = new PriceDetails();
		price.setPrice(productTerms.getPrice());
		purchaseAgreement.setPriceDetails(price);
		purchaseAgreement.setVatRate(affiliate.getVatRate());
		purchaseAgreement.setStateTax(affiliate.getStateTaxRate());
		purchaseAgreement.setBookingFee(productTerms.getBookingFee());
		// Set recurring price when using lead time or fixed start day
		if (affiliateSubscriptionSettings != null)
		{
			if (!SubscriptionPeriodType.isFixedTicket(productTerms.getPeriodType()))
			{
				String timeZone = siteService.fetchSiteById(affiliate.getSiteid())
						.getTimezone();
				if (affiliateSubscriptionSettings.getLeadTime() != 0
						|| affiliateSubscriptionSettings.getFixedStartDay() != 0
						|| startDate.isAfter(DateUtil.nowLocalDate(timeZone)))
				{
					purchaseAgreement.setRecurringProductPrice(productTerms.getPrice());
				}
			}
		}
		return purchaseAgreement;
	}

	private LocalDate getEndDate(final LocalDate startDate, final String strMinimumTerm)
	{
		int minimumTerm = SubscriptionMinimumTerm.valueOf(strMinimumTerm)
				.getIntValue();
		LocalDate subscriptionEndDate = startDate.plusMonths(minimumTerm);
		return subscriptionEndDate;
	}
}