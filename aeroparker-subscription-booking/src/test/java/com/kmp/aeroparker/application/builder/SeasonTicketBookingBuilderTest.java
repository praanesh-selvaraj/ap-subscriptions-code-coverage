package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SeasonTicketBookingBuilderTest
{
	@InjectMocks
	private SeasonTicketBookingBuilder builder;

	@Test
	void testGetPeriodType()
	{
		assertThat(builder.getPeriodType()).isEqualTo(SubscriptionPeriodType.FIXED);
	}

	@Test
	void testBuild()
	{
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequest.setPeriodType(SubscriptionPeriodType.FIXED);
		assertThat(builder.build(purchaseRequest)).isNotNull()
				.isInstanceOf(BookingSeasonTicket.class);
	}
}