package com.kmp.aeroparker.application.db.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptionProducts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.AffiliateSubscriptionProductDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionProductDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.SubscriptionProductTermsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class SubscriptionDao
{
	private static final boolean ENABLED = true;
	private final DSLContext dsl;

	public List<SubscriptionProduct> fetchSubscriptionProductBySiteId(final int siteId)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_PRODUCT)
				.where(Tables.SUBSCRIPTION_PRODUCT.SITE_ID.eq(siteId)
						.and(Tables.SUBSCRIPTION_PRODUCT.ENABLED.eq(ENABLED)))
				.fetchInto(SubscriptionProduct.class);
	}

	public List<Integer> fetchAffiliateSubscriptionProductsIds(final int affId)
	{
		return new AffiliateSubscriptionProductDao(dsl.configuration()).fetchByAffiliateId(affId)
				.stream()
				.map(affSubProduct -> affSubProduct.getSubscriptionProductId())
				.collect(Collectors.toList());
	}

	public SubscriptionProductAppearance fetchSubscriptionProductAppearanceBySubProductIdAndLangId(final int subProductId, final int langId)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_PRODUCT_APPEARANCE)
				.where(Tables.SUBSCRIPTION_PRODUCT_APPEARANCE.SUBSCRIPTION_PRODUCT_ID.eq(subProductId)
						.and(Tables.SUBSCRIPTION_PRODUCT_APPEARANCE.LANGUAGE_ID.eq(langId)))
				.fetchOneInto(SubscriptionProductAppearance.class);
	}

	public SubscriptionProductTerms fetchSubscriptionProductTermsBySubProductId(final int subProductId)
	{
		return new SubscriptionProductTermsDao(dsl.configuration()).fetchBySubscriptionProductId(subProductId)
				.stream()
				.findAny()
				.orElse(null);
	}

	public SubscriptionProduct fetchSubscriptionProductById(final int id)
	{
		return new SubscriptionProductDao(dsl.configuration()).fetchOneById(id);
	}

	public List<SubscriptionProduct> fetchSubscriptionProductByIds(final Integer[] productIds)
	{
		return new SubscriptionProductDao(dsl.configuration()).fetchById(productIds);
	}
	
	public List<PaymentStepFieldsSubscriptionProducts> fetchPaymentStepFieldsSubscriptionProductByProductId(int productId)
	{
		return dsl.selectFrom(Tables.PAYMENT_STEP_FIELDS_SUBSCRIPTION_PRODUCTS)
				.where(Tables.PAYMENT_STEP_FIELDS_SUBSCRIPTION_PRODUCTS.SUBSCRIPTION_PRODUCT_ID.eq(productId))
				.fetchInto(PaymentStepFieldsSubscriptionProducts.class);
	}

	public SubscriptionBookingDetails fetchSubscriptionBookingDetailsByRef(String reference)
	{
		return dsl.selectFrom(Tables.SUBSCRIPTION_BOOKING_DETAILS)
				.where(Tables.SUBSCRIPTION_BOOKING_DETAILS.BOOKING_REFERENCE.eq(reference))
				.orderBy(Tables.SUBSCRIPTION_BOOKING_DETAILS.BOOKING_ID.desc())
				.limit(1)
				.fetchOneInto(SubscriptionBookingDetails.class);
	}
}