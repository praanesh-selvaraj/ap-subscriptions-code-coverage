package com.kmp.aeroparker.application.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetails;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

@ExtendWith(MockitoExtension.class)
class RecurringTicketDisplayItemBuilderTest
{
	@Mock
	private Localise localise;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@InjectMocks
	private RecurringTicketDisplayItemBuilder builder;
	@Mock
	private SubscriptionProductAvailabilityDetails availabilityDetails;

	@Test
	void testBuild()
	{
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn(" Wed, 11 September 19");
		SubscriptionProductTerms productTerms = mock(SubscriptionProductTerms.class);
		when(availabilityDetails.getProductTerms()).thenReturn(productTerms);
		when(availabilityDetails.getStartDate()).thenReturn(DateUtil.nowLocalDate("Europe/London"));
		when(productTerms.getMinimumTerm()).thenReturn("ONE_MONTH");
		assertThat(builder.build(availabilityDetails)).isNotNull()
				.hasFieldOrPropertyWithValue("minimumTermDate", DateUtil.nowLocalDate("Europe/London")
						.plusMonths(1));
	}

	@Test
	void testBuild_SubscriptionProductTerms_Null()
	{
		when(availabilityDetails.getProductTerms()).thenReturn(null);
		when(availabilityDetails.getStartDate()).thenReturn(DateUtil.nowLocalDate("Europe/London"));
		assertThat(builder.build(availabilityDetails)).isNotNull()
				.hasFieldOrPropertyWithValue("minimumTermDate", DateUtil.nowLocalDate("Europe/London"));
	}
}
