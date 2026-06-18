package com.kmp.aeroparker.application.db.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.AncillaryBarcodeDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteAncillaryBarcodeConfiguration;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class AncillaryBarcodeServiceTest
{
	@Mock
	private AncillaryBarcodeDao dao;
	@InjectMocks
	private AncillaryBarcodeService service;

	@Test
	void testFetchAncillaryBarcodeConfigurationBySubscriptionProductId()
	{
		when(dao.fetchAncillaryBarcodeConfigurationBySubscriptionProductId(anyInt()))
				.thenReturn(EnhancedRandom.randomListOf(1, SiteAncillaryBarcodeConfiguration.class));

		assertThat(service.fetchAncillaryBarcodeConfigurationBySubscriptionProductId(1)).isNotNull()
				.hasSize(1);
	}

	@Test
	void testFetchAncillaryBarcodeConfigurationBySubscriptionProductId_InvalidProductId()
	{
		assertThat(service.fetchAncillaryBarcodeConfigurationBySubscriptionProductId(0)).isNotNull()
				.hasSize(0);

		verifyNoInteractions(dao);
	}

}
