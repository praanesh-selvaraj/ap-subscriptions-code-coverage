package com.kmp.aeroparker.application.web;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetailsList;
import com.kmp.aeroparker.application.availability.SubscriptionProductFinder;
import com.kmp.aeroparker.application.builder.PurchaseDataBuilder;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.counties.IrishCounties;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.InvoiceService;
import com.kmp.aeroparker.application.db.service.PromotionService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.db.service.VehicleLookupService;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.SubscriptionBasketBuilder;
import com.kmp.aeroparker.application.factory.BuilderFactory;
import com.kmp.aeroparker.application.factory.LocaleFactory;
import com.kmp.aeroparker.application.model.PurchaseQuery;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.SubscriptionReferenceGenerator;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.presentation.SubscriptionProductDisplayItemBuilder;
import com.kmp.aeroparker.application.presentation.SubscriptionProductDisplayItemList;
import com.kmp.aeroparker.application.utils.JsonUtil;
import com.kmp.aeroparker.application.validator.BookingTimesValidator;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReservationData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionDiscountedRenewal;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPurchaseData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;
import org.apache.commons.text.StringEscapeUtils;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.maputils.utils.MapUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;
import com.kmp.aeroparker.l10n.Localise;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionControllerService
{
	private static final String SHANNON = "shannon";
	private static final String DATEPICKER_DATE_FORMAT = "MM/dd/yyyy";
	private static final String FREEMARKER_FLAG = "crm.freemarker.sqs";
	private static final String PARTIAL_PAYMENT_FEATURE_FLAG = "partial.payments.sites";
	private static final String LEAD_TIME_TYPE_DAY = "DAY";
	private static final String LEAD_TIME_TYPE_MONTH = "MONTH";
	private static final String SUBSCRIPTION_RENEWAL_FLAG = "sub.webapp.renewal";
	private final BuilderFactory factory;
	private final BookingService bookingService;
	private final BookingTimesValidator validator;
	private final JsonUtil jsonUtil;
	private final LocaleFactory localeFactory;
	private final SiteService siteService;
	private final InvoiceService invoiceService;
	private final SubscriptionProductFinder productFinder;
	private final VehicleLookupService vehicleLookupService;
	private final PromotionService promotionService;
	private final Localise localise;

	public SubscriptionProductDisplayItemList buildProductDisplayItemList(final SubscriptionBookingQuery bookingQuery)
	{
		return factory.getInstance(BuilderType.PRODUCT_DISPLAY_ITEM, SubscriptionProductDisplayItemBuilder.class)
				.build(bookingQuery);
	}

	public String generateReference(final Affiliates affiliate)
	{
		return factory.getInstance(BuilderType.REFERENCE, SubscriptionReferenceGenerator.class)
				.generate(affiliate);
	}

	public boolean validateBookingTimes(final SubscriptionBookingQuery bookingQuery)
	{
		return validator.validate(bookingQuery);
	}

	public Basket buildBasket(final String purchaseData, final int currentLanguageId, final int defaultLanguageId,
			final Affiliates affiliate, AffiliateSubscriptionSettings affiliateSubscriptionSettings)
	{
		List<PurchaseQuery> purchaseQueries = jsonUtil.jsonToPurchaseQuery(purchaseData);
		return buildBasket(purchaseQueries, currentLanguageId, defaultLanguageId, affiliate,
				affiliateSubscriptionSettings);
	}

	public Basket getBasket(final String customerGuid, final int currentLaguageId, final int defaultLanguageId,
			final Affiliates affiliate, AffiliateSubscriptionSettings affiliateSubscriptionSettings)
	{
		SubscriptionPurchaseData subscriptionPurchaseData = fetchPurchaseData(affiliate.getId(), customerGuid);
		Basket basket = null;
		if (subscriptionPurchaseData != null)
		{
			basket = buildBasket(subscriptionPurchaseData.getPurchaseData(), currentLaguageId, defaultLanguageId,
					affiliate, affiliateSubscriptionSettings);

			// Fetch and apply promo discount to each purchase agreement if exists
			if (basket != null && !StringUtil.isNullOrEmpty(customerGuid))
			{
				SubscriptionSessionPromotion sessionPromotion = promotionService.fetchSessionPromotionByGuid(customerGuid);
				if (sessionPromotion != null && sessionPromotion.getDiscountAmount() != null)
				{
					BigDecimal promoDiscount = sessionPromotion.getDiscountAmount();
					String promoCode = sessionPromotion.getPromoCode();

					// Store promo at basket level for display purposes
					basket.setPromoDiscount(promoDiscount);
					basket.setPromoCode(promoCode);

					// Apply promo discount to each purchase agreemåent
					for (SubscriptionPurchaseRequest purchaseRequest : basket.getPurchaseRequestList())
					{
						if (purchaseRequest.getPurchaseAgreement() != null)
						{
							purchaseRequest.getPurchaseAgreement().setPromoDiscount(promoDiscount);
							purchaseRequest.getPurchaseAgreement().setPromoCode(promoCode);
						}
					}

					// Reinitialize basket to recalculate prices with promo applied
					basket.initialize(localise);

					log.debug("Applied promo {} with discount {} to {} purchase agreements for GUID {}",
							promoCode, promoDiscount, basket.getPurchaseRequestList().size(), customerGuid);
				}
			}
		}

		return basket;
	}

	public SubscriptionPurchaseData fetchPurchaseData(final int affId, final String customerGuid)
	{
		return bookingService.fetchPurchaseData(affId, customerGuid);
	}

	public Basket buildBasket(final List<PurchaseQuery> basketUpdateQueries, final int currentLanguageId,
			final int defaultLanguageId, final Affiliates affiliate,
			AffiliateSubscriptionSettings affiliateSubscriptionSettings)
	{
		return factory.getInstance(BuilderType.BASKET, SubscriptionBasketBuilder.class)
				.build(basketUpdateQueries, currentLanguageId, defaultLanguageId, affiliate,
						affiliateSubscriptionSettings);
	}

	public String insertPurchaseData(final int affid, String customerGuid, final String purchaseData)
	{
		customerGuid = StringUtil.isEmpty(customerGuid) ? UUID.randomUUID()
				.toString() : customerGuid;
		bookingService.insertPurchaseData(affid, customerGuid, purchaseData);
		return customerGuid;
	}

	public SubscriptionBookingRecord getBooking(final int affId, final String confirmationGuid)
	{
		return bookingService.fetchSubscriptionBooking(confirmationGuid, affId);
	}

	public String setUpDate(final AffiliateSubscriptionSettings affiliateSubscriptionSettings, final String timeZone)
	{
		LocalDate start = DateUtil.nowLocalDate(timeZone);
		if (affiliateSubscriptionSettings != null)
		{
			start = getLeadTimeHours(affiliateSubscriptionSettings, start);
		}
		return DateUtil.localDateToString(start, DATEPICKER_DATE_FORMAT);
	}

	private LocalDate getLeadTimeHours(final AffiliateSubscriptionSettings affiliateSubscriptionSettings,
			LocalDate start)
	{
		int leadTime = affiliateSubscriptionSettings.getLeadTime();
		switch (affiliateSubscriptionSettings.getLeadTimeType())
		{
			case LEAD_TIME_TYPE_DAY:
				start = start.plusDays(leadTime);
				break;
			case LEAD_TIME_TYPE_MONTH:
				start = start.plusMonths(leadTime);
				break;
		}

		int startFixedStartDay = affiliateSubscriptionSettings.getFixedStartDay();

		if (startFixedStartDay != 0)
		{
			Calendar calendarWithLeadTime = DateUtil.localDateToCalendar(start);
			Calendar calendarWithLeadTimeAndFixStartDay = DateUtil.localDateToCalendar(start);
			calendarWithLeadTimeAndFixStartDay.set(Calendar.DATE, startFixedStartDay);
			// if the fixed start date is before the lead time, we have date in
			// the past, so set the start date to next month
			if (calendarWithLeadTimeAndFixStartDay.before(calendarWithLeadTime))
			{
				// set the calendar to the following month
				calendarWithLeadTimeAndFixStartDay.add(Calendar.MONTH, 1);
			}
			start = DateUtil.calendarToLocalDate(calendarWithLeadTimeAndFixStartDay);
		}
		return start;
	}

	public Map<String, String> getCountiesIfRequired(final boolean showCounties, final String siteTitle)
	{
		Map<String, String> counties = new HashMap<String, String>();
		if (showCounties)
		{
			if (StringUtil.contains(siteTitle, SHANNON, true))
			{
				counties.putAll(IrishCounties.getIrishCounties());
			}
		}
		return counties;
	}

	public Map<String, String> getCountriesMap(final String siteTitle, final LanguageFieldsList languageFieldsList,
			final boolean sortValues)
	{
		Map<String, String> countries = new TreeMap<>();
		// List available countries from java Locale.
		Locale[] locales = localeFactory.getAvailableLocales();
		for (Locale locale : locales)
		{
			String country = locale.getDisplayCountry();
			String countryCode = locale.getCountry();
			if (!StringUtil.isEmpty(country))
			{
				if (languageFieldsList != null)
				{
					country = languageFieldsList.getTranslation(country);
				}
					countries.put(countryCode, country);
			}
		}
		// If we want "sorted by value", create a LinkedHashMap from an ordered country list.
		if (sortValues)
		{
			LinkedHashMap<String, String> orderedMap = new LinkedHashMap<>(countries.size());
			List<String> orderedCountries = new ArrayList<>(countries.values());
			Collections.sort(orderedCountries);
			for (String countryName : orderedCountries)
			{
				Entry<String, String> countryEntry = MapUtil.getEntryByValue(countries, countryName);
				orderedMap.put(countryEntry.getKey(), countryEntry.getValue());
			}
			countries = orderedMap;
		}
		return countries;
	}

	public boolean saveReservationData(SubscriptionBookingReservationData reservationData)
	{
		return bookingService.saveReservationData(reservationData);
	}

	public SubscriptionBookingReservationData fetchReservationDataByGuid(String guid)
	{
		return bookingService.fetchReservationDataByGuid(guid);
	}

	public SubscriptionDiscountedRenewal fetchSubscriptionDiscountedRenewalProductId(int productId)
	{
		return bookingService.fetchSubscriptionDiscountedRenewalProductId(productId);
	}

	public SubscriptionBookingDetails fetchSubscriptionBookingDetailsByEmail(String email)
	{
		return bookingService.fetchSubscriptionBookingDetailsByEmail(email);
	}
	
	public boolean isFeatureFlagEnabled(int siteId, String featureFlag)
	{
		String featureFlagEnabledSites = bookingService.fetchFeatureFlagValueByKey(featureFlag);
		if (!StringUtils.hasText(featureFlagEnabledSites))
		{
			return false;
		}

		List<Integer> siteIds = Arrays.stream(featureFlagEnabledSites.split(","))
				.mapToInt(Integer::parseInt)
				.boxed()
				.collect(Collectors.toList());

		return siteIds.contains(siteId);
	}

	public boolean isFreemarkerEnabled(int siteId)
	{
		return isFeatureFlagEnabled(siteId, FREEMARKER_FLAG);
	}

	public boolean isPartialPaymentsEnabled(int siteId)
	{
		return isFeatureFlagEnabled(siteId, PARTIAL_PAYMENT_FEATURE_FLAG);
	}

	public boolean isSubscriptionRenewalEnabled(int siteId)
	{
		return isFeatureFlagEnabled(siteId, SUBSCRIPTION_RENEWAL_FLAG);
	}

	public List<PaymentStepFieldsSubscriptions> fetchPaymentStepFieldsSubscriptionBySiteId(final int siteId)
	{
		return siteService.fetchPaymentStepFieldsSubscriptionBySiteId(siteId);
	}

	public boolean doesCompanyRecordExist(int siteId, int productId)
	{
		return invoiceService.fetchCompanyBySiteId(siteId, productId) != null;
	}
	
	/***
	 * Loads products which are enabled for a given affiliate and have a valid appearance and returns true if the selected product
	 * is one of those available products.
	 * @param selectedProductId product ID of product to check availability for
	 * @param startDate date for which availability should be checked
	 * @param affiliateId the ID of the affiliate for which the product needs to be checked as available
	 * @param siteId site ID of the product
	 * @param langId language ID from request
	 * @param defLangId default language ID from request
	 * @return true if the product is enabled for the given affiliate and has valid appearance and terms in Admin
	 */
	public boolean validateProductIsAvailable(int selectedProductId, LocalDate startDate, int affiliateId, 
			int siteId, int langId, int defLangId)
	{
		SubscriptionProductAvailabilityDetailsList availableProducts = productFinder.getAllAvailableProducts(
				affiliateId, siteId, langId, defLangId, startDate);
		
		return availableProducts.stream().anyMatch(product -> product.getProduct().getId().intValue() == selectedProductId);
	}
	
	/***
	 * Builds a purchaseData json string for a given productId for third parties who have a link to our webpage and just provide a productId.
	 * The json has the same format as the one we normally build on our front end and save in `subscription_purchase_data`.
	 * @param productId product ID for which purchase data needs to be built
	 * @param startDate start date of the subscription
	 * @param dateFormat date format from site location.
	 * @return purchase data JSON string to be saved in `subscription_purchase_data`.
	 */
	public String buildPurchaseDataFromThirdParty(int productId, LocalDate startDate, String dateFormat)
	{
		return factory.getInstance(BuilderType.PURCHASE_DATA, PurchaseDataBuilder.class)
				.buildPurchaseDataJson(productId, startDate, dateFormat, true).toString();
	}

	public VehiclelookupAffiliateLogins getVehicleLookupLogin(int affiliateId)
	{
		return vehicleLookupService.fetchActiveVehicleLookupAffiliateLoginsByAffiliateId(affiliateId);
	}

	/**
	 * Determines if product selection step should be skipped in the breadcrumb
	 *
	 * @param settings
	 *            the affiliate subscription settings
	 * @return true if product selection step should be skipped, false otherwise
	 */
	public boolean shouldSkipProductSelection(AffiliateSubscriptionSettings settings)
	{
		return settings != null && settings.getSkipProductSelectionIfOneIsAvailable();
	}

	/**
	 * Determines if product selection step should be skipped in the breadcrumb on the dates page (step 1)
	 *
	 * @param settings
	 *            the affiliate subscription settings
	 * @param affiliateId
	 *            the affiliate ID to check product count for
	 * @param siteId
	 *            the site ID
	 * @param langId
	 *            the language ID
	 * @param defLangId
	 *            the default language ID
	 * @return true if product selection step should be skipped, false otherwise
	 */
	public boolean shouldSkipProductSelectionOnDatesPage(AffiliateSubscriptionSettings settings, int affiliateId,
			int siteId, int langId, int defLangId)
	{
		if (settings == null || !settings.getSkipProductSelectionIfOneIsAvailable())
		{
			return false;
		}

		int enabledProductCount =
				getEnabledSubscriptionProductCountForAffiliate(affiliateId, siteId, langId, defLangId);
		return enabledProductCount == 1;
	}

	/**
	 * Gets the count of enabled subscription products for an affiliate
	 *
	 * @param affiliateId
	 *            the affiliate ID
	 * @param siteId
	 *            the site ID
	 * @param langId
	 *            the language ID
	 * @param defLangId
	 *            the default language ID
	 * @return the count of enabled products available for the affiliate
	 */
	private int getEnabledSubscriptionProductCountForAffiliate(int affiliateId, int siteId, int langId, int defLangId)
	{
		LocalDate today = LocalDate.now();
		return productFinder.getAllAvailableProducts(affiliateId, siteId, langId, defLangId, today)
				.size();
	}
	
	/**
	 * Formats the Right to Cancel text by replacing placeholders and cleaning HTML tags.
	 *
	 * @param affiliateSubscription the affiliate subscription containing the right to cancel text
	 * @param amount the transaction amount to display in the text
	 * @return formatted text with price placeholder replaced and <p> tags stripped, or null if text not available
	 */
	public String formatRightToCancelText(AffiliateSubscription affiliateSubscription, BigDecimal amount)
	{
		if (affiliateSubscription == null || StringUtil.isEmpty(affiliateSubscription.getRightToCancel()) || amount == null)
		{
			return null;
		}

		String rightToCancel = StringEscapeUtils.unescapeHtml4(affiliateSubscription.getRightToCancel());

		// Replace {{transaction-value}} placeholder with formatted price
		String formattedPrice = localise.price(amount.floatValue(), true);
		rightToCancel = rightToCancel.replace("{{transaction-value}}", formattedPrice);
		rightToCancel = rightToCancel.replaceAll("</?p>", "");

		return rightToCancel;
	}
}