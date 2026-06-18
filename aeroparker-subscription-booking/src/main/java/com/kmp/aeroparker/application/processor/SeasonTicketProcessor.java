package com.kmp.aeroparker.application.processor;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class SeasonTicketProcessor
{
	private final BookingService service;

	public boolean process(final int itemId, final BookingSeasonTicket bookingTicket)
	{
		bookingTicket.setItemId(itemId);
		return service.saveBookingSeasonTicket(bookingTicket);
	}
}