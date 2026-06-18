package com.kmp.aeroparker.application.email.dispatcher;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmailAppearance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailAppearanceValidator
{
	public boolean validateAppearance(SubscriptionEmailAppearance appearance)
	{
		boolean validated = true;

		log.debug("Validating appearance fields.");
		if (appearance == null)
		{
			log.debug("Appearance fields are null or empty.");
			return false;
		}
		if (StringUtils.isEmpty(appearance.getSenderName()))
		{
			log.debug("Email sender name is null or empty.");
			validated = false;
		}
		if (StringUtils.isEmpty(appearance.getSenderAddress()))
		{
			log.debug("Email sender address is null or empty.");
			validated = false;
		}
		if (StringUtils.isEmpty(appearance.getSubject()))
		{
			log.debug("Email subject is null or empty.");
			validated = false;
		}
		if (StringUtils.isEmpty(appearance.getBody()))
		{
			log.debug("Email body is null or empty.");
			validated = false;
		}
		return validated;
	}
}
