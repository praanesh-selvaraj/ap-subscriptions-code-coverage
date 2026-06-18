package com.kmp.aeroparker.application.db.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.kmp.aeroparker.application.db.dao.AffiliateDao;
import com.kmp.aeroparker.application.model.AffiliatesCustomFooterPages;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionMedia;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesContent;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesCrmOptIn;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesDisplay;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesMetadata;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class AffiliateService
{
	private final AffiliateDao dao;

	public Affiliates fetchAffiliateById(final int id)
	{
		Affiliates affiliate = null;
		if (id < 1)
		{
			log.info("ID is not greater than 0, affiliate will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate using ID {}", id);
			affiliate = dao.fetchAffiliateById(id);
			log.debug("Successfully fetched affiliate {}");
		}
		return affiliate;
	}

	public Affiliates fetchAffiliateByCode(final String affCode)
	{
		Affiliates affiliate = null;
		if (StringUtils.isEmpty(affCode))
		{
			log.info("Affiliate code is null or empty, affiliate will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate using code {}", affCode);
			affiliate = dao.fetchAffiliateByCode(affCode);
			log.debug("Successfully fetched affiliate {}");
		}
		return affiliate;
	}

	public AffiliatesDisplay fetchAffiliateDisplayByAffiliateIdAndLanguageId(final int affId, final int langId)
	{
		AffiliatesDisplay affiliatesDisplay = null;
		if (affId <= 0 || langId <= 0)
		{
			log.info("Affiliate ID/ LangID is not greater than 0, affiliate display will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate display using affilite ID {} and language ID {}", affId, langId);
			affiliatesDisplay = dao.fetchAffiliateDisplayByAffiliateIdAndLanguageId(affId, langId);
			log.debug("Successfully fetched affiliate display {}");
		}
		return affiliatesDisplay;
	}

	public AffiliatesMetadata fetchAffiliateMetadataByAffiliateId(final int affId)
	{
		AffiliatesMetadata affiliatesMetadata = null;
		if (affId <= 0)
		{
			log.info("Affiliate ID is not greater than 0, affiliate metadata will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate metadata using affilite ID {}", affId);
			affiliatesMetadata = dao.fetchAffiliateMetadataByAffiliateId(affId);
			log.debug("Successfully fetched affiliate metadata {}");
		}
		return affiliatesMetadata;
	}

	public List<AffiliatesCustomFooterPages> fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(final int affId, final int langId)
	{
		List<AffiliatesCustomFooterPages> footerPages = new ArrayList<>();
		if (affId <= 0 || langId <= 0)
		{
			log.info("Affiliate ID/language ID is not greater than 0, affiliate footer pages will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate footer pages using affilite ID {} and langugae ID {}", affId, langId);
			footerPages = dao.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(affId, langId);
			log.debug("Successfully fetched affiliate footer pages using affilite ID {} and langugae ID {}", affId, langId);
		}
		return footerPages;
	}

	public AffiliateSubscription fetchAffiliateSubscriptionByAffiliateId(final int affId, final int langId)
	{
		AffiliateSubscription affiliateSubscription = null;
		if (affId <= 0 || langId <= 0)
		{
			log.info("Affiliate ID/language ID is not greater than 0, affiliate subscription will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate subscription using affilite ID: {} and language ID: {}", affId, langId);
			affiliateSubscription = dao.fetchAffiliateSubscriptionByAffiliateId(affId, langId);
			log.debug("Successfully fetched affiliate subscription using affilite ID: {} and language ID: {}", affId, langId);
		}
		return unescapeFields(affiliateSubscription);
	}

	public AffiliateSubscriptionMedia fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(final int affSubId, int languageId)
	{
		AffiliateSubscriptionMedia subscriptionMedia = null;
		if (affSubId < 1)
		{
			log.info("Affiliate subscription ID is not greater than 0, affiliate subscription media will not be fetched");
		}
		else if (languageId < 1)
		{
			log.info("Affiliate subscription language id is not greater than 0, affiliate subscription media will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate subscription media using affilite subscription ID:{} and language Id: {}", affSubId, languageId);
			subscriptionMedia = dao.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(affSubId, languageId);
			log.debug("Successfully fetched affiliate subscription media using affilite subscription ID: {} and language Id: {}", affSubId, languageId);
		}
		return subscriptionMedia;
	}

	private AffiliateSubscription unescapeFields(final AffiliateSubscription affiliateSubscription)
	{
		if (affiliateSubscription != null)
		{
			log.debug("Attempting to unescape affiliate subscription fields");
			affiliateSubscription.setSubTitle(HtmlUtils.htmlUnescape(affiliateSubscription.getSubTitle()));
			affiliateSubscription.setMidPageText(HtmlUtils.htmlUnescape(affiliateSubscription.getMidPageText()));
			log.debug("Unescape affiliate subscription fields, sub title: {} , mid page text: {}", affiliateSubscription.getSubTitle(),
					affiliateSubscription.getMidPageText());
		}
		return affiliateSubscription;
	}

	public AffiliateConfig fetchAffiliateConfigValues(final int affId)
	{
		AffiliateConfig affiliateConfig = new AffiliateConfig();
		if (affId < 1)
		{
			log.info("Affiliate ID is not greater than 0, affiliate config values will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate config using affilite ID:{}", affId);
			affiliateConfig = dao.fetchAffiliateConfigValues(affId);
			// remove instances of null key
			log.debug("Removing instances of null key");
			affiliateConfig.remove(null);
			log.debug("Successfully fetched affiliate config using affilite ID: {}", affId);
		}
		return affiliateConfig;
	}

	public AffiliatesContent fetchAffiliateContentByAffIdAndLangId(final int affId, final int langId)
	{
		AffiliatesContent affiliateContent = null;
		if (affId <= 0 || langId <= 0)
		{
			log.info("Affiliate ID/language ID is not greater than 0, affiliate content will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate content using affilite ID:{} and language ID: {}", affId, langId);
			affiliateContent = dao.fetchAffiliateContentByAffIdAndLangId(affId, langId);
			log.debug("Successfully fetched affiliate content using affilite ID: {} and language ID: {}", affId, langId);
		}
		return affiliateContent;
	}

	public List<AffiliatesCrmOptIn> fetchAffiliateCrmOptInByAffiliateId(final int affId)
	{
		List<AffiliatesCrmOptIn> affiliatesCrmOptIns = new ArrayList<>();
		if (affId <= 0)
		{
			log.info("Affiliate ID/language ID is not greater than 0, affiliate will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate crm opt-ins using affilite ID {}", affId);
			affiliatesCrmOptIns = dao.fetchAffiliateCrmOptInByAffiliateId(affId);
			log.debug("Successfully fetched affiliate crm opt-ins using affilite ID {}", affId);
		}
		return affiliatesCrmOptIns;
	}

	public AffiliateSubscriptionSettings fetchSubscriptionSettingsByAffiliateId(final int affId)
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = null;
		if (affId < 1)
		{
			log.info("Affiliate id is not greater than 0, subscription settings will not be fetched");
		}
		else
		{
			affiliateSubscriptionSettings = dao.fetchSubscriptionSettingsByAffiliateId(affId);
			log.debug("Subscription settings fetched");
		}
		return affiliateSubscriptionSettings;
	}

	public AffiliatesCustomFooterPages fetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle(final int affiliateId, final int languageId,
			final String title)
	{
		AffiliatesCustomFooterPages footerPages = null;
		if (affiliateId <= 0 || languageId <= 0 || StringUtil.isEmpty(title))
		{
			log.info("Affiliate ID/language ID is not greater than 0 or title is empty, affiliate footer pages will not be fetched");
		}
		else
		{
			log.debug("Attempting to fetch affiliate footer pages using affilite ID {} , langugae ID {} and title {}", affiliateId, languageId,
					title);
			footerPages = dao.fetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle(affiliateId, languageId, title);
			log.debug("Successfully fetched affiliate footer pages using affilite ID {} , langugae ID {} and title {}", affiliateId, languageId,
					title);
		}
		return footerPages;
	}
	
	public String fetchSubscriptionBookingReferenceFormat(int affiliateId)
	{
		String format = "";
		if (affiliateId > 0)
		{
			try
			{
				format = dao.fetchSubscriptionBookingReferenceFormat(affiliateId);
			}
			catch (DataAccessException | SQLException e)
			{
				log.error("Error fetching subscription booking reference format using affiliateId " + affiliateId + e.getMessage(), e);
			}
		}
		else
		{
			log.debug("Could not fetch subscription booking reference format, affiliate Id was less than 0");
		}
		
		return format;
	}
	
	public String fetchAffiliateCodeById(int affiliateId)
	{
		String code = "";
		if (affiliateId > 0)
		{
			code = dao.fetchAffiliateCodeById(affiliateId);
		}
		else
		{
			log.debug("Could not fetch affiliate code as the affiliate Id was 0");
		}
		return code;
	}
}