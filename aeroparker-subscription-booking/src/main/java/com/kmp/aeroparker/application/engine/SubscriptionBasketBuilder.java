package com.kmp.aeroparker.application.engine;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.PurchaseQuery;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.model.interfaces.IBuilder;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class SubscriptionBasketBuilder implements IBuilder
{
	private final SubscriptionService service;
	private final PurchaseRequestBuilder purchaseRequestbuilder;
	private final Localise localise;

	public Basket build(final List<PurchaseQuery> basketUpdateQueries, final int langId, final int deflangId, final Affiliates affiliate,
			AffiliateSubscriptionSettings affiliateSubscriptionSettings)
	{
		String strStartDate = basketUpdateQueries.get(0)
				.getStartDate();

		Basket basket = new Basket();
		Integer[] productIds = basketUpdateQueries.stream()
				.map(x -> x.getProductId())
				.toArray(Integer[]::new);
		return buildBasket(basket, productIds, langId, deflangId, strStartDate, affiliate, affiliateSubscriptionSettings);
	}

	private Basket buildBasket(final Basket basket, final Integer[] productIds, final int langId, final int deflangId, final String strStartDate,
			final Affiliates affiliate, AffiliateSubscriptionSettings affiliateSubscriptionSettings)
	{
		List<SubscriptionProduct> products = service.fetchSubscriptionProductByIds(productIds);
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		for (SubscriptionProduct product : products)
		{
			LocalDate startDate = DateUtil.strToLocalDate(strStartDate, localise.getLocation()
					.getDateformat());
			SubscriptionPurchaseRequest purchaseRequest =
					purchaseRequestbuilder.build(product, langId, deflangId, startDate, affiliate, affiliateSubscriptionSettings);
			purchaseRequestList.add(purchaseRequest);
		}
		basket.setPurchaseRequestList(purchaseRequestList);
		basket.setPriceIncludePennyPlaceholdersHtml(buildPriceIncludePennyPlaceholdersHtml(basket.getGrandTotal()
				.floatValue()));
		return basket;
	}

	private String buildPriceIncludePennyPlaceholdersHtml(final float totalPrice)
	{
		return localise.priceIncludePennyPlaceholders(totalPrice, true)
				.replace("{pennies}", "<span class='booking-summary__item__val--total__pence'>")
				.replace("{/pennies}", "</span>");
	}

	@Override
	public BuilderType getType()
	{
		return BuilderType.BASKET;
	}
}