package com.kmp.aeroparker.application.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;

@ExtendWith(MockitoExtension.class)
class SubscriptionBookingMembershipProcessorTest
{
	@InjectMocks
	private SubscriptionBookingMembershipProcessor processor;

	@Mock
	private BookingService bookingService;

	@Test
	void getType()
	{
		assertEquals(ProcessorType.SUBSCRIPTION_BOOKING_MEMBERSHIP, processor.getType());
	}

	@Test
	void process()
	{
		when(bookingService.saveSubscriptionBookingMembership(anyInt(), anyString())).thenReturn(true);

		processor.process(1, "123");

		verify(bookingService).saveSubscriptionBookingMembership(anyInt(), anyString());
	}
}