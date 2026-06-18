package com.kmp.aeroparker.application.builder;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.ConfirmationDetails;
import com.kmp.aeroparker.application.model.ConfirmationProductDetails;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ConfirmationProductDetailsBuilder
{
	private final Localise localise;
	private final LanguageFieldsList languageFieldsList;

	public void build(final Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap, final ConfirmationDetails confirmationDetails)
	{
		for (Map.Entry<SubscriptionBookingItem, IBookingTicket> bookingItem : bookingItemMap.entrySet())
		{
			SubscriptionBookingItem item = bookingItem.getKey();
			IBookingTicket bookingTicket = bookingItem.getValue();
			ConfirmationProductDetails confirmationProductDetails = new ConfirmationProductDetails();
			boolean isSeasonTicket = SubscriptionPeriodType.isFixedTicket(item.getPeriodType());
			if (isSeasonTicket)
			{
				BookingSeasonTicket bookingSeasonTicket = (BookingSeasonTicket) bookingTicket;
				confirmationProductDetails.setStartDate(localise.longDateTranslated(DateUtil.localDateToCalendar(bookingSeasonTicket.getStartDate()
						.toLocalDate()), languageFieldsList));
				confirmationProductDetails.setEndDate(localise.longDateTranslated(DateUtil.localDateToCalendar(bookingSeasonTicket.getEndDate()
						.toLocalDate()), languageFieldsList));
			}
			else
			{
				BookingRecurringTicket bookingRecurringTicket = (BookingRecurringTicket) bookingTicket;
				confirmationProductDetails.setStartDate(localise.longDateTranslated(DateUtil.localDateToCalendar(bookingRecurringTicket.getStartDate()
						.toLocalDate()), languageFieldsList));
			}
			confirmationProductDetails.setSeasonTicket(isSeasonTicket);
			confirmationProductDetails.setDisplayName(item.getProductDisplayName());
			confirmationDetails.getConfirmationProductDetailList()
					.add(confirmationProductDetails);
		}
	}
}