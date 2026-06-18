package com.kmp.aeroparker.application.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UrlPathHelper;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.html.utils.HtmlUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController
{
	private static final int SECTION_ID = 9;
	private final UrlPathHelper pathHelper;
	private final LanguageFieldsList languageFieldsList;
	private final AffiliateService affService;
	private final SiteService siteService;

	private final static String PAGE_NOT_FOUND = "An error occurred during the booking process. We apologise for the inconvenience.";
	private final static String ERROR_MSG =
			"Either something went wrong or the page doesn't exist. Please check the url or go back to the {link} home page by clicking here.{end-link}";
	private final static String LINK_START = "<a href=./dates>";
	private final static String LINK_END = "</a>";
	private final static String AFFILIATE_NOT_FOUND = "Affiliate not found";

	@GetMapping("/error")
	public String handleError(final Model model, final HttpServletRequest httpRequest,
			@RequestParam(name = "errorTitle", defaultValue = PAGE_NOT_FOUND) String errTitle)
	{
		log.debug("Inside error controller");
		// we can be more specific with the error, by passing the
		// HttpServletRequest as a param and getting the error status from it
		final String path = pathHelper.getOriginatingRequestUri(httpRequest);
		String errMsg = "";
		if (!StringUtil.isEmpty(path))
		{
			String affCode = HtmlUtil.identifyAffiliateCodeFromRequest(path);

			if (!StringUtil.isEmpty(affCode))
			{
				Affiliates affiliate = affService.fetchAffiliateByCode(affCode);

				if (affiliate != null)
				{
					Sites site = siteService.fetchSiteById(affiliate.getSiteid());

					if (site != null)
					{
						languageFieldsList.populateTranslations(site.getDefaultLanguageId(), SECTION_ID);
						errMsg = languageFieldsList.getTranslation(ERROR_MSG, LINK_START, LINK_END);
						errTitle = languageFieldsList.getTranslation(errTitle);
					}
					else
					{
						errMsg = AFFILIATE_NOT_FOUND;
					}
				}
				else
				{
					errMsg = AFFILIATE_NOT_FOUND;
				}
			}
			else
			{
				errMsg = AFFILIATE_NOT_FOUND;
			}
		}

		model.addAttribute("errMsg", StringUtil.isEmpty(errMsg) ? ERROR_MSG.replace("{link}", LINK_START)
				.replace("{end-link}", LINK_END) : errMsg);
		model.addAttribute("errTitle", errTitle);
		log.debug("Error title: \"{}\", error message: \"{}\".", errTitle, errMsg);
		return "subscription-error";
	}
}