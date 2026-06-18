package com.kmp.aeroparker.application.validator;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BookingTimesValidator
{
	public boolean validate(final SubscriptionBookingQuery bookingQuery)
	{
		log.debug("Validating booking time");
		boolean valid = false;
		String dateFormat = bookingQuery.getLocation()
				.getDateFormat();
		LocalDate startDate = DateUtil.strToLocalDate(bookingQuery.getStartDate(), dateFormat);
		LocalDate now = DateUtil.nowLocalDate(bookingQuery.getTimeZone());
		if (!(startDate.compareTo(now) < 0))
		{
			valid = true;
		}
		log.info("Finished validating Booking times, valid is : {}", valid);
		bookingQuery.setValid(valid);
		bookingQuery.setStartDate(DateUtil.localDateToString(startDate, dateFormat));
		return valid;
	}
}