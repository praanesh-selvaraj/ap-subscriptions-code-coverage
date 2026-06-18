package com.kmp.aeroparker.application.processor;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SubscriptionBookingMembershipProcessor implements IProcessor
{
	private final BookingService bookingService;
	
	@Override
	public ProcessorType getType()
	{
		return ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP;
	}

	public void process(int bookingId, String membershipId)
	{
		if (!bookingService.saveSubscriptionBookingMembership(bookingId, membershipId))
		{
			log.error("Unable to save membership id for booking with id {}", bookingId);
		}
	}
}
