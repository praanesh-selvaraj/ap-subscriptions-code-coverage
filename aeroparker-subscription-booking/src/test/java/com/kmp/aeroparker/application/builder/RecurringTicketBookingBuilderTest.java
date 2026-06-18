package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class RecurringTicketBookingBuilderTest
{
	@InjectMocks
	private RecurringTicketBookingBuilder builder;

	@Test
	void testGetPeriodType()
	{
		assertThat(builder.getPeriodType()).isEqualTo(SubscriptionPeriodType.RECURRING);
	}

	@Test
	void testBuild()
	{
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequest.setPeriodType(SubscriptionPeriodType.FIXED);
		assertThat(builder.build(purchaseRequest)).isNotNull()
				.isInstanceOf(BookingRecurringTicket.class);
	}
}