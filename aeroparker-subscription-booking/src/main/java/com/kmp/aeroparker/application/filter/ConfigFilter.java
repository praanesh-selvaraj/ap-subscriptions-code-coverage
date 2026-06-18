package com.kmp.aeroparker.application.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.enums.FontsEnum;
import com.kmp.aeroparker.exceptions.FilterException;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesMetadata;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Currencies;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ConfigFilter extends AbstractFilter
{
	private final UrlPathHelper pathHelper;
	private final AffiliateService affService;
	private final SiteService siteService;
	private final SubscriptionConfigBean requestBean;

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
	{
		log.debug("Inside config filter");
		final HttpServletRequest req = (HttpServletRequest) request;
		final String path = pathHelper.getPathWithinApplication(req);
		try
		{
			if (!checkIfServletNameAllowed(path))
			{
				log.debug("Filter -> Not required for: {}", path);
				chain.doFilter(request, response);
				return;
			}
			else
			{
				log.debug("Path received {}", path);
				// we should have the affiliate in req bean, content filter
				// should
				// have populated it
				Affiliates affiliate = requestBean.getAffiliate();
				Sites site = requestBean.getSite();

				if (affiliate != null && site != null)
				{
					setMetadataToRequest(req, affiliate.getId());
					setjsCssQueryStringToRequest(req);
					req.setAttribute("affCode", affiliate.getCode());
					setCurrency(site.getId());
					setLocationToRequest(site.getLocationId());
					setAffiliateConfig(affiliate.getId());
					setFontUrl(affiliate.getCode());
					req.setAttribute("requestBean", requestBean);
				}
				else
				{
					// something went wrong in the content filter, we should
					// stop
					throw new FilterException("Affiliate/Site not found");
				}
			}
		}
		catch (FilterException e)
		{
			req.setAttribute("errorMsg", e.getMessage());
			log.error("Content Filter Error", e.getMessage(), e);
		}
		chain.doFilter(request, response);
	}

	private void setFontUrl(final String affCode)
	{
		requestBean.setAffiliateFontUrl(FontsEnum.getUrlFromAffiliateCode(affCode));
	}

	private void setAffiliateConfig(final int affId)
	{
		AffiliateConfig affiliateConfig = affService.fetchAffiliateConfigValues(affId);

		if (affiliateConfig != null)
		{
			requestBean.setAffiliateConfig(affiliateConfig);
			requestBean.setSubscriptionStyleSheetUrl(affiliateConfig.getConfigValue_String(AffiliateConfigKeys.SUBSCRIPTION_CSS_SHEET_URL, ""));
		}
	}

	private void setCurrency(final int siteId)
	{
		Currencies currencies = siteService.fetchSiteCurrencyBySiteId(siteId);
		if (currencies != null)
		{
			requestBean.setCurrency(currencies.getCode());
			requestBean.setCurrencySymbol(currencies.getHtmlSymbol());
		}
	}

	private void setjsCssQueryStringToRequest(final HttpServletRequest req)
	{
		req.setAttribute("jsCssQueryString", LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
	}

	private void setMetadataToRequest(final HttpServletRequest req, final int affId)
	{
		AffiliatesMetadata affiliatesMetadata = affService.fetchAffiliateMetadataByAffiliateId(affId);
		if (affiliatesMetadata != null)
		{
			requestBean.setAffiliatesMetadata(affiliatesMetadata);
		}
	}

	private void setLocationToRequest(final int locationId)
	{
		requestBean.setLocation(siteService.fetchLocationById(locationId));
	}
}