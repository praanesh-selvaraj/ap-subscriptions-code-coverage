package com.kmp.aeroparker.application.processor;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionBookingLanguageProcessor implements IProcessor
{
	private final BookingService service;

	public boolean process(final int bookingId, final SubscriptionBookingLanguage subscriptionBookingLanguage)
	{
		if (subscriptionBookingLanguage != null)
		{
			subscriptionBookingLanguage.setSubscriptionBookingId(bookingId);
			return service.saveLanguage(subscriptionBookingLanguage);
		}
		else
		{
			log.error("subscription booking language was null and could not be saved for booking with id: {}", bookingId);
			return false;
		}
	}

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.LANGUAGE;
	}
}