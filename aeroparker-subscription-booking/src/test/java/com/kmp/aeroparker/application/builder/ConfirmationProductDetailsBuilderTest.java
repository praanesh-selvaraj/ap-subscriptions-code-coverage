package com.kmp.aeroparker.application.builder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.ConfirmationDetails;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

@ExtendWith(MockitoExtension.class)
class ConfirmationProductDetailsBuilderTest
{
	@Mock
	private Localise localise;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@InjectMocks
	private ConfirmationProductDetailsBuilder builder;

	@Test
	void testBuild()
	{
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn("18:31 Wed, 04 September 19");
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = mock(SubscriptionBookingItem.class);
		when(bookingItem.getPeriodType()).thenReturn("FIXED");
		BookingSeasonTicket bookingSeasonTicket = mock(BookingSeasonTicket.class);
		when(bookingSeasonTicket.getStartDate()).thenReturn(Date.valueOf(DateUtil.nowLocalDate("")));
		when(bookingSeasonTicket.getEndDate()).thenReturn(Date.valueOf(DateUtil.nowLocalDate("")
				.plusDays(3)));
		when(bookingItem.getProductDisplayName()).thenReturn("");
		bookingItemMap.put(bookingItem, bookingSeasonTicket);
		builder.build(bookingItemMap, new ConfirmationDetails());
	}

	@Test
	void testBuild_BookingRecurringTicket()
	{
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn("18:31 Wed, 04 September 19");
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = mock(SubscriptionBookingItem.class);
		when(bookingItem.getPeriodType()).thenReturn("RECURRING");
		BookingRecurringTicket bookingRecurringTicket = mock(BookingRecurringTicket.class);
		when(bookingRecurringTicket.getStartDate()).thenReturn(Date.valueOf(DateUtil.nowLocalDate("")));
		when(bookingItem.getProductDisplayName()).thenReturn("");
		bookingItemMap.put(bookingItem, bookingRecurringTicket);
		builder.build(bookingItemMap, new ConfirmationDetails());
	}
}