package com.kmp.aeroparker.application.db.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.dao.ApiUserDao;
import com.kmp.aeroparker.subscription.booking.api.tables.pojos.ApiUserSettings;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ApiUserService
{
	private final ApiUserDao dao;
	private final CustomConnectionProvider provider;

	private static final String API = "api";

	public ApiUserSettings fetchAeroparkerApiUserBySchemaAndSiteId(String schema, int siteId)
	{
		ApiUserSettings apiUser = null;
		if (!StringUtils.isEmpty(schema) && siteId > 0)
		{
			provider.setSchema(API);
			apiUser = dao.fetchAeroparkerApiUserBySchemaAndSiteId(schema, siteId);
		}
		else
		{
			log.debug("Unable to fetch api user as schema was null or site was less than 1");
		}
		return apiUser;
	}
}
