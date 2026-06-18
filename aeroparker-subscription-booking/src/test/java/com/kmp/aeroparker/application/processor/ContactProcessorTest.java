package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.builder.ContactBuilder;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class ContactProcessorTest
{
	@Mock
	private ContactBuilder builder;
	@Mock
	private ContactService service;
	@InjectMocks
	private ContactProcessor processor;

	@Test
	void testProcess()
	{
		Contacts contacts = EnhancedRandom.random(Contacts.class);
		when(service.fetchContactByEmailAndSiteId(anyString(), anyInt())).thenReturn(contacts);
		when(service.saveContact(any())).thenReturn(true);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getEmail()).thenReturn("test@aeroparker.com");
		assertThat(processor.process(bookingData)).isEqualTo(contacts.getId());
		verify(service).saveContact(any());
		verify(builder).build(any(), any());
		verify(service).fetchContactByEmailAndSiteId(anyString(), anyInt());
		verifyNoMoreInteractions(service, builder);
	}

	@Test
	void testProcess_save_False()
	{
		when(service.saveContact(any())).thenReturn(false);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getEmail()).thenReturn("test@aeroparker.com");
		assertThat(processor.process(bookingData)).isZero();
		verify(service).saveContact(any());
		verify(builder).build(any(), any());
		verify(service).fetchContactByEmailAndSiteId(anyString(), anyInt());
		verifyNoMoreInteractions(service, builder);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(ProcessorType.CONTACT);
	}
}