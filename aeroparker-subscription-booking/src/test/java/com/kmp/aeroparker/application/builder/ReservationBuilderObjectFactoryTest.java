package com.kmp.aeroparker.application.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReservationData;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

@ExtendWith(MockitoExtension.class)
class ReservationBuilderObjectFactoryTest
{
	@InjectMocks
	private ReservationBuilderObjectFactory factory;
	@Mock
	private SubscriptionBookingReservationData reservationData;
	@Mock
	private SubscriptionBookingData bookingData;

	@Test
	public void testBuildReservationData()
	{
		when(bookingData.getAddr1()).thenReturn("address1");
		when(bookingData.getCountryCodeReceipt()).thenReturn("AZ");
		when(bookingData.getReferrerMembershipId()).thenReturn("MebershipID1234");
		when(bookingData.isEmailOptIn()).thenReturn(true);

		SubscriptionBookingReservationData reservationData = factory.buildReservationData(bookingData);

		assertEquals("address1", reservationData.getAddress1());
		assertEquals("AZ", reservationData.getCountryCodeReceipt());
		assertEquals("MebershipID1234", reservationData.getReferrerMembershipId());
		assertTrue(reservationData.getEmailOptIn());
	}

	@Test
	public void testBuildReservationData_Null()
	{
		SubscriptionBookingReservationData reservationData = factory.buildReservationData(null);

		assertNull(reservationData.getAddress1());
	}

	@Test
	public void testBuildBookingData()
	{
		SubscriptionBookingReservationData reservationData = mock(SubscriptionBookingReservationData.class);
		when(reservationData.getReferrerMembershipId()).thenReturn("MebershipID1234");
		when(reservationData.getEmailOptIn()).thenReturn(true);

		factory.buildBookingData(reservationData, bookingData);

		verify(bookingData).setReferrerMembershipId("MebershipID1234");
		verify(bookingData).setEmailOptIn(true);
	}
}
