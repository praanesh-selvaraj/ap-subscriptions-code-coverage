package com.kmp.aeroparker.application.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionReferenceGeneratorTest
{
	@Mock
	private ReferenceDescriptor descriptor;
	@Mock
	private BookingService service;
	@Mock
	private AffiliateService affiliateService;
	@InjectMocks
	private SubscriptionReferenceGenerator generator;

	@Test
	void testGenerate()
	{
		when(descriptor.generateReference(anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn("reference");
		when(service.fetchBookingByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(mock(SubscriptionBooking.class))
				.thenReturn(null);
		Affiliates affiliate = EnhancedRandom.random(Affiliates.class);
		assertThat(generator.generate(affiliate)).isNotBlank();
	}
	
	@Test
	void testGenerate_SubscriptionReferenceFormat()
	{
		Affiliates affiliate = mock(Affiliates.class);
		when(descriptor.generateReference(anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn("reference");
		when(service.fetchBookingByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(mock(SubscriptionBooking.class))
				.thenReturn(null);
		when(affiliate.getId()).thenReturn(1);
		when(affiliate.getBookingReferencePrefix()).thenReturn("prefix");
		when(affiliateService.fetchSubscriptionBookingReferenceFormat(anyInt())).thenReturn("format");
		assertThat(generator.generate(affiliate)).isNotBlank();
	}

	@Test
	void testGetType()
	{
		assertThat(generator.getType()).isEqualTo(BuilderType.REFERENCE);
	}
}