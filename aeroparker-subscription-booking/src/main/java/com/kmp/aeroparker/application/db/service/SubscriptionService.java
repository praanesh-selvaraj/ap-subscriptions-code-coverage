package com.kmp.aeroparker.application.db.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.SubscriptionDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptionProducts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionService
{
	private final SubscriptionDao dao;

	public List<SubscriptionProduct> fetchSubscriptionProductBySiteId(final int siteId)
	{
		List<SubscriptionProduct> subscriptionProducts = new ArrayList<>();
		if (siteId < 1)
		{
			log.info("Site ID is not greater than 0, subscription product will not be fetched");
		}
		else
		{
			subscriptionProducts = dao.fetchSubscriptionProductBySiteId(siteId);
			log.debug("Subscription product fetched: {}", subscriptionProducts);
		}
		return subscriptionProducts;
	}

	public List<Integer> fetchAffiliateSubscriptionProductsIds(final int affId)
	{
		List<Integer> productsIds = new ArrayList<>();
		if (affId < 1)
		{
			log.info("Affiliate ID is not greater than 0, affiliate subscription product ids will not be fetched");
		}
		else
		{
			productsIds = dao.fetchAffiliateSubscriptionProductsIds(affId);
			log.debug("Affiliate subscription product ids fetched: {}", productsIds.toString());
		}
		return productsIds;
	}

	public SubscriptionProductAppearance fetchSubscriptionProductAppearanceBySubProductIdAndLangId(final int subProductId, final int langId)
	{
		SubscriptionProductAppearance productAppearance = null;

		if (subProductId < 1 || langId < 1)
		{
			log.info("Subscription product ID/language ID is not greater than 0, subscription product appearance will not be fetched");
		}
		else
		{
			productAppearance = dao.fetchSubscriptionProductAppearanceBySubProductIdAndLangId(subProductId, langId);
			log.debug("Subscription product appearance fetched: {}", productAppearance);
		}
		return productAppearance;
	}

	public SubscriptionProductTerms fetchSubscriptionProductTermsBySubProductId(final int subProductId)
	{
		SubscriptionProductTerms productTerms = null;
		if (subProductId < 1)
		{
			log.info("Subscription product ID is not greater than 0,subscription product terms will not be fetched");
		}
		else
		{
			productTerms = dao.fetchSubscriptionProductTermsBySubProductId(subProductId);
			log.debug("Subscription product terms fetched: {}", productTerms);
		}
		return productTerms;
	}

	public SubscriptionProduct fetchSubscriptionProductById(final int id)
	{
		SubscriptionProduct subscriptionProducts = null;
		if (id < 1)
		{
			log.info("ID is not greater than 0, subscription product will not be fetched");
		}
		else
		{
			subscriptionProducts = dao.fetchSubscriptionProductById(id);
			log.debug("Subscription product fetched: {}", subscriptionProducts);
		}
		return subscriptionProducts;
	}

	public List<SubscriptionProduct> fetchSubscriptionProductByIds(final Integer[] productIds)
	{
		List<SubscriptionProduct> subscriptionProducts = new ArrayList<>();
		if (productIds.length < 1)
		{
			log.info("IDs is empty, subscription product will not be fetched");
		}
		else
		{
			subscriptionProducts = dao.fetchSubscriptionProductByIds(productIds);
			log.debug("Subscription product fetched: {}", subscriptionProducts.toArray());
		}
		return subscriptionProducts;
	}
	
	public List<PaymentStepFieldsSubscriptionProducts> fetchPaymentStepFieldsSubscriptionProductByProductId(int productId)
	{
		List<PaymentStepFieldsSubscriptionProducts> paymentStepFieldsSubscriptionProducts = new ArrayList<>();
		if (productId > 0)
		{
			paymentStepFieldsSubscriptionProducts = dao.fetchPaymentStepFieldsSubscriptionProductByProductId(productId);
		}
		else
		{
			log.debug("Subscription Product Payment Step Fields could not be fetched, ID is less than 1");
		}
		return paymentStepFieldsSubscriptionProducts;
	}

	public SubscriptionBookingDetails fetchSubscriptionBookingDetailsByRef(String reference)
	{
		SubscriptionBookingDetails details = null;

		if (!StringUtil.isNullOrEmpty(reference))
		{
			details = dao.fetchSubscriptionBookingDetailsByRef(reference);
		}
		else
		{
			log.debug("Unable to fetch Subscription booking details as reference was null or empty");
		}
		return details;
	}
}