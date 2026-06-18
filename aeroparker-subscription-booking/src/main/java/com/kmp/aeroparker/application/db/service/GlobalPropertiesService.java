package com.kmp.aeroparker.application.db.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.dao.GlobalPropertiesDao;
import com.kmp.aeroparker.application.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class GlobalPropertiesService
{
	private static final String TENANTS = "tenants";
	private final GlobalPropertiesDao dao;
	private final CustomConnectionProvider provider;

	@Cacheable(value = "globalProperties", key = "#key")
	public String fetchProperty(final String key)
	{
		String value = null;

		if (StringUtil.isNullOrEmpty(key))
		{
			log.info("Key is null or empty, property can't be fetched.");
			return null;
		}

		try
		{
			provider.setSchema(TENANTS);
			value = dao.fetchProperty(key);

			if (StringUtil.isNullOrEmpty(value))
			{
				log.info("No value found for key: {}", key);
			}
		}
		catch (Exception e)
		{
			log.error("Error occurred when fetching property with key {}", key, e);
		}

		return value;
	}

	@Scheduled(fixedRate = 60000) // 1 minute
	public void evictAllPropertiesScheduled()
	{
		log.info("Scheduled eviction of all globalProperties cache entries.");
		evictAllProperties();
	}

	@CacheEvict(value = "globalProperties", allEntries = true)
	public void evictAllProperties()
	{
		log.info("Evicting all cache entries for globalProperties");
	}
}
