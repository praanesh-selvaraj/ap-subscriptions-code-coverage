package com.kmp.aeroparker.application.model;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.SequenceService;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ReferenceDescriptor
{
	private final SequenceService service;

	public static final String DEFAULT_FORMAT = "{S}{R}{P}{I}";
	public static final String ROUTE_WEB = "W";
	public static final String ROUTE_WHITELABEL = "L";

	public String generateReference(final int siteId, final String format, final String productCode, final String route,
			final String parentSiteIndicator)
	{
		String ref = StringUtil.parse(format);
		ref = ref.trim();
		if (StringUtil.isEmpty(ref) || getFormatErrors(ref))
		{
			ref = DEFAULT_FORMAT;
		}

		int sequenceIndex = service.getNextId(siteId);
		ref = StringUtil.replace(ref, "{S}", parentSiteIndicator);
		ref = StringUtil.replace(ref, "{R}", route);
		ref = StringUtil.replace(ref, "{P}", productCode);
		ref = StringUtil.replace(ref, "{I}", "" + sequenceIndex);
		return ref;
	}

	private boolean getFormatErrors(final String format)
	{
		boolean error = false;

		if (format.indexOf("{I}") == -1)
		{
			error = true;
		}
		return error;
	}
}