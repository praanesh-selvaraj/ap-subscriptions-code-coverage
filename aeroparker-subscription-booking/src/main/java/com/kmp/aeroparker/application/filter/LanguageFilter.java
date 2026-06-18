package com.kmp.aeroparker.application.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import com.kmp.aeroparker.application.db.service.LanguageService;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.exceptions.FilterException;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(3)
public class LanguageFilter extends AbstractFilter
{
	private static final int SECTION_ID = 9;
	private final UrlPathHelper pathHelper;
	private final LanguageService langService;
	private final SubscriptionConfigBean requestBean;
	private final LanguageFieldsList languageFieldsList;
	@Value("${cookie.properties.secure:true}")
	private boolean secure;

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
	{
		log.debug("Inside language filter");
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpServletResponse resp = (HttpServletResponse) response;
		final String path = pathHelper.getPathWithinApplication(req);
		log.debug("Path received {}", path);
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
				Affiliates affiliate = requestBean.getAffiliate();
				Sites site = requestBean.getSite();
				if (affiliate != null && site != null)
				{
					int affId = affiliate.getId();
					Languages defaultLanguage = setDefaultLanguge(affId, site.getDefaultLanguageId());
					Languages currentLanguage = setLanguages(req, resp, affId, defaultLanguage);
					// throw exception when language is null
					int currentLanguageId = currentLanguage.getId();
					setLanguageFieldsList(req, currentLanguageId);
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

	private Languages setDefaultLanguge(final int affId, final int siteDefaultLanguageId)
	{
		// fetch the default language
		Languages defaultLanguage = Optional.ofNullable(langService.fetchAffiliateDefaultLanguage(affId))
				.orElseGet(() -> langService.fetchLanguageById(siteDefaultLanguageId));
		requestBean.setDefaultLanguage(defaultLanguage);
		return defaultLanguage;
	}

	private Languages setLanguages(final HttpServletRequest req, final HttpServletResponse resp, final int affId, final Languages defaultLanguage)
			throws FilterException
	{
		// Fetch all the languages by affiliate
		List<Languages> languageList = langService.fetchLanguagesByAffiliateId(affId);
		// Try and get the language code from the request
		String languageDisplayCode = getRequestString(req, "lang", "");
		// For more security we check if the lang selected in the dropdown
		// exists, if it doesn't exist, set it to empty
		boolean selectedLangExist = false;
		if (!StringUtil.isEmpty(languageDisplayCode))
		{
			for (Languages language : languageList)
			{
				if (language.getDisplayCode()
						.equalsIgnoreCase(languageDisplayCode))
				{
					selectedLangExist = true;
				}
			}
			if (!selectedLangExist)
			{
				languageDisplayCode = "";
			}
		}

		// Attempt to fetch the langcode from the cookie
		String strCookieLanguageDisplayCode = "";
		Cookie cookieLanguageDisplayCode = WebUtils.getCookie(req, "languageDisplayCode");
		if (cookieLanguageDisplayCode != null)
		{
			strCookieLanguageDisplayCode = cookieLanguageDisplayCode.getValue();
		}

		// if langcode is empty check if we have any thing stored in the
		// cookie, if we do use it
		if (StringUtil.isEmpty(languageDisplayCode) && !StringUtil.isEmpty(strCookieLanguageDisplayCode))
		{
			languageDisplayCode = strCookieLanguageDisplayCode;
		}

		// If the user hasn't change the language dropdown , keep the same lang
		// in the cookie, if not set the new one or override the existing one
		if (!StringUtil.isEmpty(languageDisplayCode) && !StringUtil.isEqual(languageDisplayCode, strCookieLanguageDisplayCode))
		{
			Cookie cookie = new Cookie("languageDisplayCode", languageDisplayCode);
			cookie.setHttpOnly(true);
			cookie.setSecure(secure);
			// A zero value causes the cookie to be deleted.
			// A negative value will be deleted when the Web browser exits
			cookie.setMaxAge(-3);
			resp.addCookie(cookie);
		}
		// Fetch by code if null fetch the default language
		Languages currentLanguage = Optional.ofNullable(langService.fetchLanguageByDisplayCode(languageDisplayCode))
				.orElseGet(() -> defaultLanguage);
		// If still at this point it is null, just get the first one from the
		// list
		currentLanguage = Optional.ofNullable(currentLanguage)
				.orElseGet(() -> languageList.isEmpty() ? null : languageList.get(0));
		// If still null throw an exception
		if (currentLanguage == null)
		{
			throw new FilterException("Language not found");
		}
		else
		{
			// set he languages in the request to be used for the language drop
			// down
			int currentLanguageId = currentLanguage.getId();
			// Remove the current language from the language drop down
			languageList.removeIf(language -> language.getId() == currentLanguageId);
			req.setAttribute("languageList", languageList);
			requestBean.setCurrentLanguage(currentLanguage);
		}
		return currentLanguage;
	}

	private void setLanguageFieldsList(final HttpServletRequest req, final int languageId)
	{
		languageFieldsList.populateTranslations(languageId, SECTION_ID);
		req.setAttribute("languageFieldsList", languageFieldsList);
	}
}