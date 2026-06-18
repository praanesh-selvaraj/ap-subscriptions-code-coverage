package com.kmp.aeroparker.application.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.SequenceService;

@ExtendWith(MockitoExtension.class)
class ReferenceDescriptorTest
{
	@Mock
	private SequenceService service;
	@InjectMocks
	private ReferenceDescriptor descriptor;

	@Test
	void testReferenceDescriptor()
	{
		when(service.getNextId(anyInt())).thenReturn(234234);
		assertThat(descriptor.generateReference(1, "{S}{R}{P}{I}", "code", SubscriptionReferenceGenerator.ROUTE_WEB, "PREF")).isNotEmpty();
		verify(service).getNextId(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testReferenceDescriptor_Empty_Format()
	{
		when(service.getNextId(anyInt())).thenReturn(234234);
		assertThat(descriptor.generateReference(1, "", "code", SubscriptionReferenceGenerator.ROUTE_WEB, "PREF")).isNotEmpty();
		verify(service).getNextId(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testReferenceDescriptor_Invalid_Format()
	{
		when(service.getNextId(anyInt())).thenReturn(234234);
		assertThat(descriptor.generateReference(1, "{P}{R}", "code", SubscriptionReferenceGenerator.ROUTE_WEB, "PREF")).isNotEmpty();
		verify(service).getNextId(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testReferenceDescriptor_MinimumRequiredFormat()
	{
		when(service.getNextId(anyInt())).thenReturn(234234);
		assertEquals("234234", descriptor.generateReference(1, "{I}", "code", SubscriptionReferenceGenerator.ROUTE_WEB, "PREF"));
		verify(service).getNextId(anyInt());
		verifyNoMoreInteractions(service);
	}
}