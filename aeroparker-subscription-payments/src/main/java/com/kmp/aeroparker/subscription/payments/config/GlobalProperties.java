package com.kmp.aeroparker.subscription.payments.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.string.utils.StringUtil;

@Component
public class GlobalProperties
{
	@Value("${global.properties.system.installationType:live}")
	private String installationType;

	public boolean isDev()
	{
		return StringUtil.isEqual("dev", installationType);
	}

	public boolean isStaging()
	{
		return StringUtil.isEqual("staging", installationType);
	}
}