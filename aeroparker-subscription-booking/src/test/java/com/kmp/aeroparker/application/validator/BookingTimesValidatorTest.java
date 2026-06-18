package com.kmp.aeroparker.application.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;

@ExtendWith(MockitoExtension.class)
class BookingTimesValidatorTest
{
	@InjectMocks
	private BookingTimesValidator validator;

	@Test
	void testValidate()
	{
		SubscriptionBookingQuery bookingQuery = mock(SubscriptionBookingQuery.class);
		when(bookingQuery.getTimeZone()).thenReturn("Europe/London");
		when(bookingQuery.getStartDate()).thenReturn(LocalDate.now()
				.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
				.toString());
		Locations locations = mock(Locations.class);
		when(locations.getDateFormat()).thenReturn("MM-dd-yyyy");
		when(bookingQuery.getLocation()).thenReturn(locations);
		assertThat(validator.validate(bookingQuery)).isTrue();
	}

	@Test
	void testValidate_Invalid_Date()
	{
		SubscriptionBookingQuery bookingQuery = mock(SubscriptionBookingQuery.class);
		when(bookingQuery.getTimeZone()).thenReturn("Europe/London");
		when(bookingQuery.getStartDate()).thenReturn(LocalDate.now()
				.minusDays(3)
				.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
				.toString());
		Locations locations = mock(Locations.class);
		when(locations.getDateFormat()).thenReturn("MM-dd-yyyy");
		when(bookingQuery.getLocation()).thenReturn(locations);
		assertThat(validator.validate(bookingQuery)).isFalse();
	}
}