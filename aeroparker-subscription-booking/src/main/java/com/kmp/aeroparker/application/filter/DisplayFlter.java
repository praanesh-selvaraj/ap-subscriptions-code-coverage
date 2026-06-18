package com.kmp.aeroparker.application.filter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.model.AffiliatesCustomFooterPages;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionMedia;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesContent;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesDisplay;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(4)
public class DisplayFlter extends AbstractFilter
{
	private final UrlPathHelper pathHelper;
	private final AffiliateService affService;
	private final SubscriptionConfigBean requestBean;

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
	{
		log.debug("Inside display filter");
		final HttpServletRequest req = (HttpServletRequest) request;
		final String path = pathHelper.getPathWithinApplication(req);
		log.debug("Path received {}", path);
		if (!checkIfServletNameAllowed(path))
		{
			log.debug("Filter -> Not required for: {}", path);
			chain.doFilter(request, response);
			return;
		}
		else
		{
			Affiliates affiliate = requestBean.getAffiliate();
			Sites site = requestBean.getSite();
			if (affiliate != null && site != null)
			{
				int affId = affiliate.getId();
				Languages defaultLanguage = requestBean.getDefaultLanguage();
				int defaultLanguageId = defaultLanguage == null ? 0 : defaultLanguage.getId();
				Languages currentLanguage = requestBean.getCurrentLanguage();
				int currentLanguageId = currentLanguage.getId();
				setAffiliateDisplayInRequest(affId, currentLanguageId, defaultLanguageId);
				setFooterLinksToRequest(affId, currentLanguageId, defaultLanguageId);
				setStep1DisplaySettingsToRequest(affId, currentLanguageId, defaultLanguageId);
				setAffiliateContent(currentLanguageId, affId, defaultLanguageId);
				setCurrentYearToRequest();
			}
		}
		chain.doFilter(request, response);
	}

	private void setAffiliateDisplayInRequest(final int affId, final int currentLanguageId, final int defaultLanguageId)
	{
		AffiliatesDisplay affiliatesDisplay =
				Optional.ofNullable(affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(affId, currentLanguageId))
						.orElseGet(() -> affService.fetchAffiliateDisplayByAffiliateIdAndLanguageId(affId, defaultLanguageId));
		requestBean.setAffiliatesDisplay(affiliatesDisplay);
	}

	private void setCurrentYearToRequest()
	{
		// year for @ all rights reserved
		requestBean.setCurrentYear(LocalDate.now()
				.getYear());
	}

	private void setFooterLinksToRequest(final int affId, final int currentLanguageId, final int defaultLanguageId)
	{
		// Fetch with the current language if null, attempt fetching with the
		// default language
		List<AffiliatesCustomFooterPages> affiliatesFooterPages =
				affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(affId, currentLanguageId);
		if (affiliatesFooterPages.isEmpty())
		{
			affiliatesFooterPages.addAll(affService.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(affId, defaultLanguageId));
		}
		requestBean.setAffiliatesFooterPages(affiliatesFooterPages);
	}

	private void setStep1DisplaySettingsToRequest(final int affId, final int currentLanguageId, final int defaultLanguageId)
	{
		// Fetch with the current language if null, attempt fetching with the
		// default language
		AffiliateSubscription affiliateSubscription =
				Optional.ofNullable(affService.fetchAffiliateSubscriptionByAffiliateId(affId, currentLanguageId))
						.orElseGet(() -> affService.fetchAffiliateSubscriptionByAffiliateId(affId, defaultLanguageId));
		AffiliateSubscriptionMedia affiliateSubscriptionMedia = null;

		if (affiliateSubscription != null)
		{
			int id = affiliateSubscription.getId();
			affiliateSubscriptionMedia = Optional.ofNullable(affService.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(id, currentLanguageId))
					.orElseGet(() -> affService.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(id, defaultLanguageId));
		}
		else
		{
			affiliateSubscription = new AffiliateSubscription();
		}
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = Optional.ofNullable(affService.fetchSubscriptionSettingsByAffiliateId((affId)))
				.orElseGet(() -> new AffiliateSubscriptionSettings());
		requestBean.setAffiliateSubscription(affiliateSubscription);
		requestBean.setAffiliateSubscriptionMedia(affiliateSubscriptionMedia == null ? new AffiliateSubscriptionMedia() : affiliateSubscriptionMedia);
		requestBean.setAffiliateSubscriptionSettings(affiliateSubscriptionSettings);
	}

	private void setAffiliateContent(final int currentLanguageId, final int affId, final int defaultLanguageId)
	{
		// Fetch with the current language if null, attempt fetching with the
		// default language
		AffiliatesContent affiliateContent = Optional.ofNullable(affService.fetchAffiliateContentByAffIdAndLangId(affId, currentLanguageId))
				.orElseGet(() -> affService.fetchAffiliateContentByAffIdAndLangId(affId, defaultLanguageId));
		requestBean.setAffiliateContent(affiliateContent);
	}
}