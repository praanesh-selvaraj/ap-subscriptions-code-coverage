package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class CustomerDetailsProcessorTest
{
	@Mock
	private BookingService service;
	@InjectMocks
	private CustomerDetailsProcessor processor;

	@Test
	void testProcess()
	{
		SubscriptionBookingCustomerDetails customerDetails = EnhancedRandom.random(SubscriptionBookingCustomerDetails.class);
		when(service.saveSubscriptionBookingCustomerDetails(any())).thenReturn(true);
		assertThat(processor.process(1, customerDetails)).isTrue();
		verify(service).saveSubscriptionBookingCustomerDetails(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testProcess_Null()
	{
		NullPointerException thrown = assertThrows(NullPointerException.class, () -> processor.process(1, null));
		assertThat(thrown.getMessage()).containsIgnoringCase("customerDetails is marked")
				.containsIgnoringCase("but is null");
		verifyNoMoreInteractions(service);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(ProcessorType.CUSTOMER_DETAILS);
	}
}