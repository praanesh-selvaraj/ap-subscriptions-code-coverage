package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;

@ExtendWith(MockitoExtension.class)
class SeasonTicketProcessorTest
{
	@Mock
	private BookingService service;
	@InjectMocks
	private SeasonTicketProcessor processor;

	@Test
	void testProcess_BookingSeasonTicket()
	{
		BookingSeasonTicket bookingSeasonTicket = mock(BookingSeasonTicket.class);
		when(service.saveBookingSeasonTicket(any())).thenReturn(true);
		assertThat(processor.process(1, bookingSeasonTicket)).isTrue();
	}
}