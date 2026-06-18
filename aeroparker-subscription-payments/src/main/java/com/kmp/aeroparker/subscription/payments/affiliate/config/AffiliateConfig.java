package com.kmp.aeroparker.subscription.payments.affiliate.config;

import java.util.HashMap;

public class AffiliateConfig extends HashMap<AffiliateConfigKeys, String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getConfigValue_String(final AffiliateConfigKeys key, final String defaultValue)
	{
		String configValue = get(key);
		if (configValue != null)
		{
			return configValue;
		}
		return defaultValue;
	}

	/**
	 * Get a config value as a boolean
	 * 
	 * @param key
	 * @return
	 */
	public boolean getConfigValue_Boolean(final AffiliateConfigKeys key)
	{
		String configString = getConfigValue_String(key, "");
		return "1".equals(configString) || "true".equals(configString);
	}
}