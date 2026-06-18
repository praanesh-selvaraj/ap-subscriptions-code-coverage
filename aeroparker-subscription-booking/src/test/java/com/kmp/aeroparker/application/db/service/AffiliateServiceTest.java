package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.AffiliateDao;
import com.kmp.aeroparker.application.model.AffiliatesCustomFooterPages;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionMedia;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesContent;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesCrmOptIn;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesDisplay;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesMetadata;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class AffiliateServiceTest
{
	@Mock
	private AffiliateDao dao;
	@InjectMocks
	private AffiliateService service;
	@Mock
	private Affiliates affiliate;
	@Mock
	private AffiliatesDisplay affDisplay;

	@Test
	void testFetchAffiliateById()
	{
		when(dao.fetchAffiliateById(anyInt())).thenReturn(affiliate);
		assertThat(service.fetchAffiliateById(1)).isNotNull()
				.isInstanceOf(Affiliates.class);
	}

	@Test
	void testFetchAffiliateById_Zero()
	{
		assertThat(service.fetchAffiliateById(0)).isNull();
	}

	@Test
	void testFetchAffiliateByCode()
	{
		when(dao.fetchAffiliateByCode(anyString())).thenReturn(affiliate);
		assertThat(service.fetchAffiliateByCode("aff_code")).isNotNull()
				.isInstanceOf(Affiliates.class);
	}

	@Test
	void testFetchAffiliateByCode_Empty_Code()
	{
		assertThat(service.fetchAffiliateByCode("")).isNull();
	}

	@Test
	void testFetchAffiliateDisplayByAffiliateIdAndLanguageId()
	{
		when(dao.fetchAffiliateDisplayByAffiliateIdAndLanguageId(anyInt(), anyInt())).thenReturn(affDisplay);
		assertThat(service.fetchAffiliateDisplayByAffiliateIdAndLanguageId(1, 1)).isNotNull()
				.isInstanceOf(AffiliatesDisplay.class);
	}

	@Test
	void testFetchAffiliateDisplayByAffiliateIdAndLanguageId_Incorrect_AffiliateId()
	{
		assertThat(service.fetchAffiliateDisplayByAffiliateIdAndLanguageId(0, 1)).isNull();
		assertThat(service.fetchAffiliateDisplayByAffiliateIdAndLanguageId(1, 0)).isNull();
		assertThat(service.fetchAffiliateDisplayByAffiliateIdAndLanguageId(0, 0)).isNull();
	}

	@Test
	void testFetchAffiliateMetadataByAffiliateId()
	{
		AffiliatesMetadata affiliatesMetadata = mock(AffiliatesMetadata.class);
		when(dao.fetchAffiliateMetadataByAffiliateId(anyInt())).thenReturn(affiliatesMetadata);
		assertThat(service.fetchAffiliateMetadataByAffiliateId(1)).isNotNull()
				.isInstanceOf(AffiliatesMetadata.class);
	}

	@Test
	void testFetchAffiliateMetadataByAffiliateId_Incorrect_AffiliateId()
	{
		assertThat(service.fetchAffiliateMetadataByAffiliateId(0)).isNull();
	}

	@Test
	void testFetchAffiliatesFooterPagesByAffiliateId()
	{
		when(dao.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(anyInt(), anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCustomFooterPages.class));
		assertThat(service.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(1, 1)).isNotNull()
				.extracting("class")
				.containsOnlyOnce(AffiliatesCustomFooterPages.class);
	}

	@Test
	void testFetchAffiliatesFooterPagesByAffiliateId_Incorrect_AffiliateId()
	{
		assertThat(service.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(0, 0)).isEmpty();
		assertThat(service.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(0, 1)).isEmpty();
		assertThat(service.fetchAffiliatesFooterPagesByAffiliateIdAndLanguageId(1, 0)).isEmpty();
	}

	@Test
	void testFetchAffiliateSubscriptionByAffiliateId()
	{
		when(dao.fetchAffiliateSubscriptionByAffiliateId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscription.class));
		assertThat(service.fetchAffiliateSubscriptionByAffiliateId(1, 1)).isNotNull()
				.isInstanceOf(AffiliateSubscription.class);
	}

	@Test
	void testFetchAffiliateSubscriptionByAffiliateId_Invalid_AffiliateIdAndLanguageId()
	{
		assertThat(service.fetchAffiliateSubscriptionByAffiliateId(1, 0)).isNull();
		assertThat(service.fetchAffiliateSubscriptionByAffiliateId(0, 0)).isNull();
		assertThat(service.fetchAffiliateSubscriptionByAffiliateId(0, 1)).isNull();
	}

	@Test
	void testFetchAffiliateSubscriptionMediaByAffSubId()
	{
		when(dao.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(anyInt(), anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscriptionMedia.class));
		assertThat(service.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(1, 1)).isNotNull()
				.isInstanceOf(AffiliateSubscriptionMedia.class);
	}

	@Test
	void testFetchAffiliateSubscriptionMediaByAffSubId_Invalid_AffiliateId()
	{
		assertThat(service.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(0, 1)).isNull();
		assertThat(service.fetchAffiliateSubscriptionMediaByAffSubIdAndLanguageId(1, 0)).isNull();
	}

	@Test
	void testFetchAffiliateConfigValues()
	{
		AffiliateConfig configValues = new AffiliateConfig();
		configValues.put(AffiliateConfigKeys.ENABLE_ACCOUNT_CREATED_EMAIL, "1");
		when(dao.fetchAffiliateConfigValues(anyInt())).thenReturn(configValues);
		assertThat(service.fetchAffiliateConfigValues(1)).isNotEmpty()
				.containsEntry(AffiliateConfigKeys.ENABLE_ACCOUNT_CREATED_EMAIL, "1");
	}

	@Test
	void testFetchAffiliateConfigValues_Param_Zero()
	{
		assertThat(service.fetchAffiliateConfigValues(0)).isEmpty();
	}

	@Test
	void testFetchAffiliateContentByAffIdAndLangId()
	{
		AffiliatesContent AffiliatesContent = mock(AffiliatesContent.class);
		when(dao.fetchAffiliateContentByAffIdAndLangId(anyInt(), anyInt())).thenReturn(AffiliatesContent);
		assertThat(service.fetchAffiliateContentByAffIdAndLangId(1, 1)).isNotNull()
				.isInstanceOf(AffiliatesContent.class);
	}

	@Test
	void testFetchAffiliateContentByAffIdAndLangId_Params_Invalid()
	{
		assertThat(service.fetchAffiliateContentByAffIdAndLangId(1, 0)).isNull();
		assertThat(service.fetchAffiliateContentByAffIdAndLangId(0, 1)).isNull();
		assertThat(service.fetchAffiliateContentByAffIdAndLangId(0, 0)).isNull();
	}

	@Test
	void testFetchAffiliateCrmOptInByAffiliateId()
	{
		when(dao.fetchAffiliateCrmOptInByAffiliateId(anyInt())).thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCrmOptIn.class));
		assertThat(service.fetchAffiliateCrmOptInByAffiliateId(1)).isNotNull()
				.extracting("class")
				.containsOnlyOnce(AffiliatesCrmOptIn.class);
	}

	@Test
	void testFetchAffiliateCrmOptInByAffiliateId_Incorrect_AffiliateId()
	{
		assertThat(service.fetchAffiliateCrmOptInByAffiliateId(0)).isEmpty();
	}

	@Test
	void testFetchSubscriptionSettingsByAffiliateId()
	{
		when(dao.fetchSubscriptionSettingsByAffiliateId(anyInt())).thenReturn(EnhancedRandom.random(AffiliateSubscriptionSettings.class));
		assertThat(service.fetchSubscriptionSettingsByAffiliateId(2)).isNotNull()
				.isInstanceOf(AffiliateSubscriptionSettings.class);
	}

	@Test
	void testFetchSubscriptionSettingsByAffiliateId_Invalid_affId()
	{
		assertThat(service.fetchSubscriptionSettingsByAffiliateId(0)).isNull();
		verifyNoInteractions(dao);
	}
	
	@Test
	void testFetchSubscriptionBookingReferenceFormat() throws DataAccessException, SQLException
	{
		when(dao.fetchSubscriptionBookingReferenceFormat(anyInt())).thenReturn("test");
		assertEquals(service.fetchSubscriptionBookingReferenceFormat(1), "test");
	}
	
	@Test
	void testFetchSubscriptionBookingReferenceFormat_InvalidId() throws DataAccessException, SQLException
	{
		assertTrue(service.fetchSubscriptionBookingReferenceFormat(0).isEmpty());
	}
	
 	@Test
	void testFetchSubscriptionBookingReferenceFormat_DataAccessException() throws DataAccessException, SQLException
	{
		when(dao.fetchSubscriptionBookingReferenceFormat(anyInt())).thenThrow(DataAccessException.class);
		assertTrue(service.fetchSubscriptionBookingReferenceFormat(1).isEmpty());
	}
 	
 	@Test
	void testFetchSubscriptionBookingReferenceFormat_SQLException() throws DataAccessException, SQLException
	{
		when(dao.fetchSubscriptionBookingReferenceFormat(anyInt())).thenThrow(SQLException.class);
		assertTrue(service.fetchSubscriptionBookingReferenceFormat(1).isEmpty());
	}

	@Test
	void testFetchAffiliateCodeById() throws DataAccessException, SQLException
	{
		when(dao.fetchAffiliateCodeById(anyInt())).thenReturn("test");
		assertEquals(service.fetchAffiliateCodeById(1), "test");
	}

	@Test
	void testFetchAffiliateCodeById_InvalidId() throws DataAccessException, SQLException
	{
		assertEquals(service.fetchAffiliateCodeById(0), "");
	}
}