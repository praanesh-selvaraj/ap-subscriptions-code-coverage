package com.kmp.aeroparker.application.filter;

import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import com.kmp.aeroparker.subscription.html.utils.HtmlUtil;

public abstract class AbstractFilter implements Filter
{
	final static String[] SUPPORTED_SERVLET_NAMES = { "booking", "products", "dates", "details", "confirmation",
			"basketupdate", "error", "content", "view-booking", "resend-confirmation", "VatValidatorAjax",
			"SubscriptionInvoice", "validate-subscription-renewal", "validate-promotion", "update-payment-intent", "products-thirdparty", "setup-booking-renewal",
			"display-renewal-payment", "send-renewal", "renewal-confirmation", "vehicle-lookup", "edit-details" };

	protected String getServletNameFromUrl(final String url)
	{
		String serlvetName = "";
		int slashPos = url.lastIndexOf('/');
		if (slashPos > -1 && slashPos < url.length() - 1)
		{
			serlvetName = url.substring(slashPos + 1);
		}
		return serlvetName;
	}

	protected boolean checkIfServletNameAllowed(final String path)
	{
		return Arrays.asList(SUPPORTED_SERVLET_NAMES)
				.contains(getServletNameFromUrl(path));
	}

	protected String getRequestString(final HttpServletRequest req, final String key, final String defaultVal)
	{
		String val = req.getParameter(key);
		val = HtmlUtil.removeXSS(val);
		return HtmlUtil.escapeHtml(val, defaultVal);
	}
}