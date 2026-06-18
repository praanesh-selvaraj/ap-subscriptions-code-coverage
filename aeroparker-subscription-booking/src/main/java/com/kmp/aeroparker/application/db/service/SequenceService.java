package com.kmp.aeroparker.application.db.service;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.SequenceDao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class SequenceService
{
	private final SequenceDao dao;

	public int getNextId(final int siteId)
	{
		int sequence = 0;
		if (siteId < 1)
		{
			log.debug("Site Id not valid, sequence will not be fetched");
		}
		else
		{
			Integer seq = dao.getNextId(siteId);

			if (seq != null)
			{
				sequence = seq;
			}
		}
		return sequence;
	}
}