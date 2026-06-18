package com.kmp.aeroparker.application.db.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.dao.ReceiptDetailsDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class ReceiptDetailsServiceTest
{
	@Mock
	private ReceiptDetailsDao dao;
	@InjectMocks
	private ReceiptDetailsService service;

	@Test
	void testSaveSubscriptionReceiptDetails()
	{
		when(dao.saveReceiptDetails(any())).thenReturn(true);
		assertThat(service.saveReceiptDetais(mock(SubscriptionBookingReceiptDetails.class))).isTrue();
		verify(dao).saveReceiptDetails(any());
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testSaveSubscriptionReceiptDetails_when_null()
	{
		assertThat(service.saveReceiptDetais(null));
	}

	@Test
	void testFetchSubscriptionBookingReceiptDetailsByBookingId()
	{
		when(dao.fetchSubscriptionBookingReceiptDetailsByBookingId(anyInt()))
				.thenReturn(EnhancedRandom.random(SubscriptionBookingReceiptDetails.class));
		assertThat(service.fetchSubscriptionBookingReceiptDetailsByBookingId(1)).isNotNull()
				.isInstanceOf(SubscriptionBookingReceiptDetails.class);
		verify(dao).fetchSubscriptionBookingReceiptDetailsByBookingId(anyInt());
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testFetchSubscriptionBookingReceiptDetailsByBookingId_Id_Zero()
	{
		assertThat(service.fetchSubscriptionBookingReceiptDetailsByBookingId(0)).isNull();
		verifyNoInteractions(dao);
	}
}