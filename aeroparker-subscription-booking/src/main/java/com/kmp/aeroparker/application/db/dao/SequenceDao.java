package com.kmp.aeroparker.application.db.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class SequenceDao
{
	private final DSLContext dsl;

	/**
	 * If the table subscription_reference_sequence has a record then we are building the reference with this record
	 * rather than building with the parking and ancillary sequence. If not then we return parking and ancillary
	 * sequence as usual
	 * 
	 * @param siteId
	 * @return value for building reference
	 */
	public Integer getNextId(final int siteId)
	{
		Integer sequence = dsl.select(Tables.SUBSCRIPTION_REFERENCE_SEQUENCE.VALUE)
				.from(Tables.SUBSCRIPTION_REFERENCE_SEQUENCE)
				.where(Tables.SUBSCRIPTION_REFERENCE_SEQUENCE.SITE_ID.eq(siteId))
				.limit(1)
				.forUpdate()
				.fetchOneInto(Integer.class);

		if (sequence != null)
		{
			dsl.update(Tables.SUBSCRIPTION_REFERENCE_SEQUENCE)
					.set(Tables.SUBSCRIPTION_REFERENCE_SEQUENCE.VALUE,
							Tables.SUBSCRIPTION_REFERENCE_SEQUENCE.VALUE.plus(1))
					.where(Tables.SUBSCRIPTION_REFERENCE_SEQUENCE.SITE_ID.eq(siteId))
					.execute();
		}
		else
		{
			sequence = dsl.select(Tables.AB_SEQUENCE.VALUE)
					.from(Tables.AB_SEQUENCE)
					.where(Tables.AB_SEQUENCE.SITEID.eq(siteId))
					.limit(1)
					.forUpdate()
					.fetchOneInto(Integer.class);
			dsl.update(Tables.AB_SEQUENCE)
					.set(Tables.AB_SEQUENCE.VALUE,
							Tables.AB_SEQUENCE.VALUE.plus(1))
					.where(Tables.AB_SEQUENCE.SITEID.eq(siteId))
					.execute();
		}

		return sequence;
	}
}