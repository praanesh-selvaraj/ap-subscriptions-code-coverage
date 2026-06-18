package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuid;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionGuidBooking;

@ExtendWith(MockitoExtension.class)
class SubscriptionGuidBuilderTest
{
	@InjectMocks
	private SubscriptionGuidBuilder builder;

	@Test
	void testBuildGuidBooking()
	{
		assertThat(builder.buildGuidBooking(1, 1, 1)).isNotNull()
				.isInstanceOf(SubscriptionGuidBooking.class);
	}

	@Test
	void testBuild()
	{
		assertThat(builder.build("GUID")).isNotNull()
				.isInstanceOf(SubscriptionGuid.class);
	}
}