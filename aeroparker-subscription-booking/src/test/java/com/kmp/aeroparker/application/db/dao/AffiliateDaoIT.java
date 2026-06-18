package com.kmp.aeroparker.application.db.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;

@Testcontainers
@JooqTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AffiliateDao.class })
class AffiliateDaoIT
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@Autowired
	private AffiliateDao dao;

	@Test
	void testFetchById()
	{
		assertThat(dao.fetchAffiliateById(72)).hasFieldOrPropertyWithValue("id", 72)
				.hasFieldOrPropertyWithValue("code", "SNN");
	}

	@Test
	void testFetchByCode()
	{
		assertThat(dao.fetchAffiliateByCode("NLDBS")).hasFieldOrPropertyWithValue("id", 169)
				.hasFieldOrPropertyWithValue("code", "NLDBS");
	}

	@Test
	void testFetchAffiliateDisplayByAffiliateIdAndLanguageId()
	{
		assertThat(dao.fetchAffiliateDisplayByAffiliateIdAndLanguageId(1, 1)).isNotNull()
				.hasFieldOrPropertyWithValue("id", 2)
				.hasFieldOrPropertyWithValue("homeUrl", "https://aeroparker.kmp.co.uk/");
	}

	@Test
	void testFetchAffiliateMetadataByAffiliateId()
	{
		assertThat(dao.fetchAffiliateMetadataByAffiliateId(72)).hasFieldOrPropertyWithValue("id", 17)
				.hasFieldOrPropertyWithValue("title", "Shannon Airport Parking");
	}

	@Test
	void testFetchAffiliatesFooterPagesByAffiliateIdAndLanguageId()
	{
		assertThat(dao.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(64, 11)).hasSize(1);
	}

	@Test
	void testFetchAffiliateSubscriptionByAffiliateId()
	{
		assertThat(dao.fetchAffiliateSubscriptionByAffiliateId(41, 1)).hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("title", "Title Test");
	}

	@Test
	void testFetchAffiliateSubscriptionMediaByAffSubId()
	{
		assertThat(dao.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(1, 1)).hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("imagePath", "/subMedia/abz.jpeg");
	}

	@Test
	void testFetchAffiliateConfigValues()
	{
		assertThat(dao.fetchAffiliateConfigValues(18)).contains(entry(AffiliateConfigKeys.ENABLE_OPT_INS, "0"),
				entry(AffiliateConfigKeys.SHOWVEHICLELOOKUP, "0"));
	}

	@Test
	void testFetchAffiliateCrmOptInByAffiliateId()
	{
		assertThat(dao.fetchAffiliateCrmOptInByAffiliateId(72)).hasSize(2);
	}

	@Test
	void testFetchSubscriptionSettingsByAffiliateId()
	{
		assertThat(dao.fetchSubscriptionSettingsByAffiliateId(72)).isNotNull()
				.hasFieldOrPropertyWithValue("leadTimeType", "DAY");
	}

	@Test
	void testFetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle()
	{
		assertThat(dao.fetchAffiliatesFooterPagesByAffiliateIdLanguageIdAndTitle(64, 11, "Italy page")).isNotNull()
				.hasFieldOrPropertyWithValue("enabled", (byte) 0);
	}
	
	@Test
	void testFetchSubscriptionBookingReferenceFormat() throws DataAccessException, SQLException
	{
		assertEquals(dao.fetchSubscriptionBookingReferenceFormat(24), "test");
	}
}