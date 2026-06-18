package com.kmp.aeroparker.application.processor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kmp.aeroparker.application.db.service.ReceiptDetailsService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionBookingReceiptDetailsProcessorTest
{
	@Mock
	ReceiptDetailsService service;
	@InjectMocks
	private SubscriptionBookingReceiptDetailsProcessor processor;

	@Test
	void testProcess()
	{
		SubscriptionBookingReceiptDetails receiptDetails =
				EnhancedRandom.random(SubscriptionBookingReceiptDetails.class);
		when(service.saveReceiptDetais(receiptDetails)).thenReturn(true);
		assertThat(processor.process(1, receiptDetails)).isTrue();
		verify(service).saveReceiptDetais(receiptDetails);
		verifyNoMoreInteractions(service);
	}

	@Test
	void testProcess_Null()
	{
		NullPointerException thrown = assertThrows(NullPointerException.class, () -> processor.process(1, null));
		assertThat(thrown.getMessage()).isNotNull();
		verifyNoMoreInteractions(service);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(ProcessorType.RECEIPT_DETAILS);
	}
}