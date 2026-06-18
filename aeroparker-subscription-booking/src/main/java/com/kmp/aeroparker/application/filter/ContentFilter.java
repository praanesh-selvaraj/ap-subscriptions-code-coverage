package com.kmp.aeroparker.application.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.exceptions.FilterException;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.html.utils.HtmlUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(2)
public class ContentFilter extends AbstractFilter
{
	private final UrlPathHelper pathHelper;
	private final AffiliateService affService;
	private final SiteService siteService;
	private final SubscriptionConfigBean requestBean;
	private final Localise localise;
	private static final Integer AFFILIATE_ENABLED = 1;
	private static final String AFFILIATE_NOT_FOUND = "Affiliate not found";

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
	{
		log.debug("Inside content filter");
		final HttpServletRequest req = (HttpServletRequest) request;
		try
		{
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
				Affiliates affiliate = affService.fetchAffiliateByCode(HtmlUtil.identifyAffiliateCodeFromRequest(req.getRequestURI()));

				if (affiliate != null && affiliate.getIsEnabled() == AFFILIATE_ENABLED)
				{
					requestBean.setAffiliate(affiliate);
					Sites site = setSiteToRequest(affiliate.getSiteid());
					requestBean.setSite(site);
					setLocalise(req, site.getLocationId());
				}
				else
				{
					throw new FilterException(AFFILIATE_NOT_FOUND);
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

	private void setLocalise(final HttpServletRequest req, final int locationId)
	{
		localise.setLocation(locationId);
		req.setAttribute("localise", localise);
	}

	private Sites setSiteToRequest(final int siteid) throws FilterException
	{
		Sites site = siteService.fetchSiteById(siteid);

		if (site == null)
		{
			throw new FilterException("Site not found");
		}
		return site;
	}
}