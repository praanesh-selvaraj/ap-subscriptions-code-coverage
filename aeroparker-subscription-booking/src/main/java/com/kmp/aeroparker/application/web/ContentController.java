package com.kmp.aeroparker.application.web;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.model.AffiliatesCustomFooterPages;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.subscription.html.utils.HtmlUtil;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class ContentController
{
	private final SubscriptionConfigBean requestBean;
	private final AffiliateService service;

	@GetMapping(value = "/content")
	public String getContent(final Model model, @RequestParam(value = "title") final String title)
	{
		log.debug("Redirecting to content page");
		AffiliatesCustomFooterPages affiliatesCustomFooterPages = Optional
				.ofNullable(service.fetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle(requestBean.getAffiliateId(),
						requestBean.getCurrentLanguageId(), title))
				.orElseGet(() -> new AffiliatesCustomFooterPages());
		model.addAttribute("contentTitle", affiliatesCustomFooterPages.getLinkTitle());
		model.addAttribute("contentData",
				HtmlUtil.htmlUnescape(StringUtil.isEmpty(affiliatesCustomFooterPages.getContent()) ? "" : affiliatesCustomFooterPages.getContent()));
		return "subscription-content";
	}
}