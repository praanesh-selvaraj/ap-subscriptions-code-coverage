package com.kmp.aeroparker.application.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionMedia;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesContent;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesDisplay;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesMetadata;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionConfigBean
{
	private Affiliates affiliate;
	private AffiliatesDisplay affiliatesDisplay;
	private Sites site;
	private AffiliatesMetadata affiliatesMetadata;
	private Languages currentLanguage;
	private List<AffiliatesCustomFooterPages> affiliatesFooterPages = new ArrayList<>();
	@Value("${global.properties.uploadUrl}")
	private String uploadUrl;
	private AffiliateSubscription affiliateSubscription;
	private AffiliateSubscriptionMedia affiliateSubscriptionMedia;
	public String currency;
	public String currencySymbol;
	private Locations location;
	private int currentYear;
	private AffiliateConfig affiliateConfig;
	private String affiliateFontUrl;
	private String subscriptionStyleSheetUrl;
	private AffiliatesContent affiliateContent;
	private Languages defaultLanguage;
	private AffiliateSubscriptionSettings affiliateSubscriptionSettings;
	@Value("${aws.freemarker.sqs.url}")
	private String freemarkerQueueUrl;
	@Value("${global.properties.ancillary.barcode.sqs.queue.url}")
	private String ancillaryBarcodeQueueUrl;
	@Value("${aws.reports.sqs.url}")
	private String reportsQueueUrl;

	public String getTimeZone()
	{
		String timeZone = "";
		if (site != null)
		{
			timeZone = site.getTimezone();
		}
		return timeZone;
	}

	public int getAffiliateId()
	{
		int affId = 0;
		if (affiliate != null)
		{
			affId = affiliate.getId();
		}
		return affId;
	}

	public int getSiteId()
	{
		int siteId = 0;
		if (site != null)
		{
			siteId = site.getId();
		}
		return siteId;
	}

	public int getCurrentLanguageId()
	{
		int langId = 0;
		if (currentLanguage != null)
		{
			langId = currentLanguage.getId();
		}
		return langId;
	}

	public int getDefaultLanguageId()
	{
		int langId = 0;
		if (defaultLanguage != null)
		{
			langId = defaultLanguage.getId();
		}
		return langId;
	}

	public String getDateFormat()
	{
		String dateFormat = "dd/MM/yyyy";
		if (location != null)
		{
			dateFormat = location.getDateFormat();
		}
		return dateFormat;
	}

	public BigDecimal getAffiliateVatRate()
	{
		BigDecimal vatRate = BigDecimal.ZERO;
		if (affiliate != null)
		{
			vatRate = vatRate.add(affiliate.getVatRate());
		}
		return vatRate;
	}

	public boolean isMultiPurchase()
	{
		boolean isMultiPurchase = false;
		if (affiliateSubscriptionSettings != null)
		{
			isMultiPurchase = affiliateSubscriptionSettings.getMultiPurchaseEnabled();
		}
		return isMultiPurchase;
	}
	
	public String getSiteTitle()
	{
		String siteTitle = "";
		if (site != null)
		{
			siteTitle = site.getTitle();
		}
		return siteTitle;
	}
	
	public boolean enableAccountOnDetailsStep()
	{
		boolean enableAccountOnDetailsStep = false;
		if (affiliateSubscriptionSettings != null)
		{
			enableAccountOnDetailsStep = affiliateSubscriptionSettings.getEnableAccountOnDetailsStep();
		}
		return enableAccountOnDetailsStep;
	}
}