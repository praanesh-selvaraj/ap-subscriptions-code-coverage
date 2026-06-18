package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingLanguage;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
public class SubscriptionBookingLanguageProcessorTest
{
	@Mock
	BookingService service;
	@InjectMocks
	private SubscriptionBookingLanguageProcessor processor;

	@Test
	void testProcess()
	{
		SubscriptionBookingLanguage language =
				EnhancedRandom.random(SubscriptionBookingLanguage.class);
		when(service.saveLanguage(language)).thenReturn(true);
		assertThat(processor.process(1, language)).isTrue();
		verify(service).saveLanguage(language);
		verifyNoMoreInteractions(service);
	}

	@Test
	void testProcess_Null()
	{
		assertThat(processor.process(1, null)).isFalse();
		verify(service, never()).saveLanguage(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(ProcessorType.LANGUAGE);
	}
}
