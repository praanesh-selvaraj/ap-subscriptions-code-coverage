package com.kmp.aeroparker.application.db.dao;

import java.sql.SQLException;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.application.model.AffiliatesCustomFooterPages;
import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.AffiliateSubscriptionSettingsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.AffiliatesCrmOptInDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.AffiliatesMetadataDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionMedia;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesContent;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesCrmOptIn;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesDisplay;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesMetadata;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.payments.tables.daos.AffiliateConfigValuesDao;
import com.kmp.aeroparker.subscription.payments.tables.daos.AffiliatesDao;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class AffiliateDao
{
	private final DSLContext dsl;

	public Affiliates fetchAffiliateById(final int id)
	{
		return new AffiliatesDao(dsl.configuration()).fetchOneById(id);
	}

	public Affiliates fetchAffiliateByCode(final String affCode)
	{
		return new AffiliatesDao(dsl.configuration()).fetchOneByCode(affCode);
	}

	public AffiliatesDisplay fetchAffiliateDisplayByAffiliateIdAndLanguageId(final int affId, final int langId)
	{
		return dsl.selectFrom(Tables.AB_AFFILIATES_DISPLAY)
				.where(Tables.AB_AFFILIATES_DISPLAY.AFFILIATEID.eq(affId))
				.and(Tables.AB_AFFILIATES_DISPLAY.LANGUAGEID.eq(langId))
				.fetchOneInto(AffiliatesDisplay.class);
	}

	public AffiliatesMetadata fetchAffiliateMetadataByAffiliateId(final int affId)
	{
		return new AffiliatesMetadataDao(dsl.configuration()).fetchByAffiliateid(affId)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public List<AffiliatesCustomFooterPages> fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(final int affId, final int langId)
	{
		return dsl.selectFrom(Tables.AB_AFFILIATES_FOOTER_PAGES)
				.where(Tables.AB_AFFILIATES_FOOTER_PAGES.AFFILIATEID.eq(affId))
				.and(Tables.AB_AFFILIATES_FOOTER_PAGES.LANGUAGEID.eq(langId))
				.fetchInto(AffiliatesCustomFooterPages.class);
	}

	public AffiliateSubscription fetchAffiliateSubscriptionByAffiliateId(final int affId, final int langId)
	{
		return dsl.selectFrom(Tables.AFFILIATE_SUBSCRIPTION)
				.where(Tables.AFFILIATE_SUBSCRIPTION.AFFILIATE_ID.eq(affId))
				.and(Tables.AFFILIATE_SUBSCRIPTION.LANGUAGE_ID.eq(langId))
				.fetchOneInto(AffiliateSubscription.class);
	}

	public AffiliateSubscriptionMedia fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(final int affSubId, int languageId)
	{
		return dsl.selectFrom(Tables.AFFILIATE_SUBSCRIPTION_MEDIA)
				.where(Tables.AFFILIATE_SUBSCRIPTION_MEDIA.AFFILIATE_SUBSCRIPTION_ID.eq(affSubId))
				.and(Tables.AFFILIATE_SUBSCRIPTION_MEDIA.LANGUAGE_ID.eq(languageId))
				.fetchOneInto(AffiliateSubscriptionMedia.class);
	}

	public AffiliateConfig fetchAffiliateConfigValues(final int affId)
	{
		AffiliateConfig affiliateConfigs = new AffiliateConfig();
		new AffiliateConfigValuesDao(dsl.configuration()).fetchByAffiliateid(affId)
				.forEach(affiliateConfig -> affiliateConfigs.put(AffiliateConfigKeys.customValueOf(affiliateConfig.getAffiliateConfigKey()),
						affiliateConfig.getAbAffiliateConfigValues()));
		return affiliateConfigs;
	}

	public AffiliatesContent fetchAffiliateContentByAffIdAndLangId(final int affId, final int langId)
	{
		return dsl.selectFrom(Tables.AB_AFFILIATES_CONTENT)
				.where(Tables.AB_AFFILIATES_CONTENT.AFFILIATEID.eq(affId))
				.and(Tables.AB_AFFILIATES_CONTENT.LANGUAGEID.eq(langId))
				.fetchOneInto(AffiliatesContent.class);
	}

	public List<AffiliatesCrmOptIn> fetchAffiliateCrmOptInByAffiliateId(final int affId)
	{
		return new AffiliatesCrmOptInDao(dsl.configuration()).fetchByAffiliateid(affId);
	}

	public AffiliateSubscriptionSettings fetchSubscriptionSettingsByAffiliateId(final int affId)
	{
		return new AffiliateSubscriptionSettingsDao(dsl.configuration()).fetchByAffiliateId(affId)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public AffiliatesCustomFooterPages fetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle(final int affiliateId, final int languageId,
			final String title)
	{
		return dsl.selectFrom(Tables.AB_AFFILIATES_FOOTER_PAGES)
				.where(Tables.AB_AFFILIATES_FOOTER_PAGES.AFFILIATEID.eq(affiliateId))
				.and(Tables.AB_AFFILIATES_FOOTER_PAGES.LANGUAGEID.eq(languageId))
				.and(Tables.AB_AFFILIATES_FOOTER_PAGES.LINKTITLE.eq(title))
				.fetchOneInto(AffiliatesCustomFooterPages.class);
	}
	
	public String fetchSubscriptionBookingReferenceFormat(int affiliateId) throws DataAccessException, SQLException
	{
		return dsl.select(Tables.AFFILIATE_SUBSCRIPTION_SETTINGS.BOOKING_REFERENCE_FORMAT)
				.from(Tables.AFFILIATE_SUBSCRIPTION_SETTINGS)
				.where(Tables.AFFILIATE_SUBSCRIPTION_SETTINGS.AFFILIATE_ID.eq(affiliateId))
				.fetchOneInto(String.class);
	}
	
	public String fetchAffiliateCodeById(int affiliateId)
	{
		return dsl.select(Tables.AB_AFFILIATES.CODE)
				.from(Tables.AB_AFFILIATES)
				.where(Tables.AB_AFFILIATES.ID.eq(affiliateId))
				.fetchOne(Tables.AB_AFFILIATES.CODE);
	}
}