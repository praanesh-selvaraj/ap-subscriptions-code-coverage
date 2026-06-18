package com.kmp.aeroparker.application.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

class SubscriptionConfigBeanTest
{
	SubscriptionConfigBean configBean = new SubscriptionConfigBean();

	@Test
	void testGetTimeZone()
	{
		Sites site = new Sites();
		site.setTimezone("Europe/London");
		configBean.setSite(site);
		assertThat(configBean.getTimeZone()).isEqualTo("Europe/London");
	}

	@Test
	void testGetTimeZone_Null()
	{
		configBean.setSite(null);
		assertThat(configBean.getTimeZone()).isEqualTo("");
	}

	@Test
	void testGetAffiliateId()
	{
		Affiliates affiliate = new Affiliates();
		affiliate.setId(1);
		configBean.setAffiliate(affiliate);
		assertThat(configBean.getAffiliateId()).isEqualTo(1);
	}

	@Test
	void testGetAffiliateId_Null()
	{
		configBean.setAffiliate(null);
		assertThat(configBean.getAffiliateId()).isEqualTo(0);
	}

	@Test
	void testGetSiteId()
	{
		Sites site = new Sites();
		site.setId(1);
		configBean.setSite(site);
		assertThat(configBean.getSiteId()).isEqualTo(1);
	}

	@Test
	void testGetSiteId_Null()
	{
		configBean.setSite(null);
		assertThat(configBean.getSiteId()).isEqualTo(0);
	}

	@Test
	void testGetCurrentLanguageId()
	{
		Languages language = new Languages();
		language.setId(1);
		configBean.setCurrentLanguage(language);
		assertThat(configBean.getCurrentLanguageId()).isEqualTo(1);
	}

	@Test
	void testGetCurrentLanguageId_Null()
	{
		configBean.setCurrentLanguage(null);
		assertThat(configBean.getCurrentLanguageId()).isEqualTo(0);
	}

	@Test
	void testGetDateFormat()
	{
		Locations location = new Locations();
		location.setDateFormat("dd/MM/yyyy");
		configBean.setLocation(location);
		assertThat(configBean.getDateFormat()).isEqualTo("dd/MM/yyyy");
	}

	@Test
	void testGetDateFormat_Null()
	{
		configBean.setLocation(null);
		assertThat(configBean.getDateFormat()).isEqualTo("dd/MM/yyyy");
	}

	@Test
	void testGetAffiliateVatRate()
	{
		Affiliates affiliate = new Affiliates();
		affiliate.setVatRate(BigDecimal.TEN);
		configBean.setAffiliate(affiliate);
		assertThat(configBean.getAffiliateVatRate()).isEqualTo(BigDecimal.TEN);
	}

	@Test
	void testGetAffiliateVatRate_Null()
	{
		configBean.setAffiliate(null);
		assertThat(configBean.getAffiliateVatRate()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	void testIsMultiPurchase()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = new AffiliateSubscriptionSettings();
		affiliateSubscriptionSettings.setMultiPurchaseEnabled(true);
		configBean.setAffiliateSubscriptionSettings(affiliateSubscriptionSettings);
		assertThat(configBean.isMultiPurchase()).isTrue();
	}

	@Test
	void testIsMultiPurchase_AffiliateSubscriptionSettings_Null()
	{
		configBean.setAffiliateSubscriptionSettings(null);
		assertThat(configBean.isMultiPurchase()).isFalse();
	}

	@Test
	void testGetDefaultLanguageId()
	{
		Languages language = new Languages();
		language.setId(1);
		configBean.setDefaultLanguage(language);
		assertThat(configBean.getDefaultLanguageId()).isEqualTo(1);
	}

	@Test
	void testGetDefaultLanguageId_Null()
	{
		configBean.setDefaultLanguage(null);
		assertThat(configBean.getDefaultLanguageId()).isEqualTo(0);
	}
	
	@Test
	void testGetSiteTitle()
	{
		Sites site = new Sites();
		site.setTitle("Test");
		configBean.setSite(site);
		assertThat(configBean.getSiteTitle()).isEqualTo("Test");
	}
	
	@Test
	void testGetSiteTitle_Null()
	{
		configBean.setSite(null);
		assertThat(configBean.getSiteTitle()).isEqualTo("");
	}
	
	@Test
	void testEnableAccountOnDetailsStep()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = new AffiliateSubscriptionSettings();
		affiliateSubscriptionSettings.setEnableAccountOnDetailsStep(true);
		configBean.setAffiliateSubscriptionSettings(affiliateSubscriptionSettings);
		assertThat(configBean.enableAccountOnDetailsStep()).isTrue();
	}

	@Test
	void testEnableAccountOnDetailsStep_False()
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = new AffiliateSubscriptionSettings();
		affiliateSubscriptionSettings.setEnableAccountOnDetailsStep(false);
		configBean.setAffiliateSubscriptionSettings(null);
		assertThat(configBean.enableAccountOnDetailsStep()).isFalse();
	}
	
	@Test
	void testGetAncillaryBarcodeQueueUrl()
	{
		configBean.setAncillaryBarcodeQueueUrl("Test");
		assertThat(configBean.getAncillaryBarcodeQueueUrl()).isEqualTo("Test");
	}
	
	@Test
	void testGetAncillaryBarcodeQueueUrl_Null()
	{
		configBean.setSite(null);
		assertThat(configBean.getAncillaryBarcodeQueueUrl()).isNull();
	}
}