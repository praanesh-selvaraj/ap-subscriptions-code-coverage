package com.kmp.aeroparker.application.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;

@ExtendWith(MockitoExtension.class)
class SubscriptionDetailsUtilTest
{
	@Mock
	private SubscriptionAllBookingData bookingData;

	@InjectMocks
	private SubscriptionDetailsUtil detailsUtil;

	@Test
	void testShowVehicleDetails()
	{
		assertThat(detailsUtil.showVehicleDetails(bookingData)).isFalse();
		when(bookingData.getCustomerVehicleColour()).thenReturn("Colour");
		assertThat(detailsUtil.showVehicleDetails(bookingData)).isTrue();
		when(bookingData.getCustomerVehicleModel()).thenReturn("Model");
		assertThat(detailsUtil.showVehicleDetails(bookingData)).isTrue();
		when(bookingData.getCustomerVehicleMake()).thenReturn("Make");
		assertThat(detailsUtil.showVehicleDetails(bookingData)).isTrue();
		when(bookingData.getCustomerVehicleReg()).thenReturn("Reg");
		assertThat(detailsUtil.showVehicleDetails(bookingData)).isTrue();

	}

	@Test
	void testGetCustomerAddress()
	{
		assertThat(detailsUtil.getCustomerAddress(bookingData)).isEmpty();
		when(bookingData.getCustomerAddress_2()).thenReturn("Line 2");
		assertEquals(detailsUtil.getCustomerAddress(bookingData), "Line 2");
		when(bookingData.getCustomerAddress_1()).thenReturn("Line 1");
		assertEquals(detailsUtil.getCustomerAddress(bookingData), "Line 1, Line 2");
	}

	@Test
	void testFormatCardExpiryDate()
	{
		SubscriptionAllBookingData testBookingData = new SubscriptionAllBookingData();
		testBookingData.setCardExpiryDate("1221");
		detailsUtil.formatCardExpiryDate(testBookingData);
		assertEquals(testBookingData.getCardExpiryDate(), "12/21");
	}

	@Test
	void testFormatCardExpiryDate_No_Formatting()
	{
		when(bookingData.getCardExpiryDate()).thenReturn("");
		detailsUtil.formatCardExpiryDate(bookingData);
		verify(bookingData, times(0)).setCardExpiryDate(anyString());
		when(bookingData.getCardExpiryDate()).thenReturn("12/21");
		detailsUtil.formatCardExpiryDate(bookingData);
		verify(bookingData, times(0)).setCardExpiryDate(anyString());
	}
	
	@Test
	void testFormatCardExpiryDate_Five_Digit_Format()
	{
		SubscriptionAllBookingData testBookingData = new SubscriptionAllBookingData();
		testBookingData.setCardExpiryDate("62025");
		
		detailsUtil.formatCardExpiryDate(testBookingData);
		
		assertEquals(testBookingData.getCardExpiryDate(), "06/25");
	}

	@Test
	void testFormatCardExpiryDate_Six_Digit_Format()
	{
		SubscriptionAllBookingData testBookingData = new SubscriptionAllBookingData();
		testBookingData.setCardExpiryDate("122025");
		
		detailsUtil.formatCardExpiryDate(testBookingData);
		
		assertEquals(testBookingData.getCardExpiryDate(), "12/25");
	}
	
	@Test
	void testFormatMinimumTerm()
	{
		List<SubscriptionAllBookingData> bookingDataList = new ArrayList<>();
		SubscriptionAllBookingData testBookingData = new SubscriptionAllBookingData();
		testBookingData.setMinimumTermLength("ONE_MONTH");
		bookingDataList.add(testBookingData);

		detailsUtil.formatMinimumTerm(bookingDataList);
		assertEquals(testBookingData.getMinimumTermLength(), "1");
	}

	@Test
	public void testFormatMinimumTerm_StartEndDateFallback()
	{
		List<SubscriptionAllBookingData> bookingDataList = new ArrayList<>();
		SubscriptionAllBookingData testBookingData = new SubscriptionAllBookingData();
		testBookingData.setStartDate(Date.valueOf("2025-01-01"));
		testBookingData.setEndDate(Date.valueOf("2025-03-01"));
		bookingDataList.add(testBookingData);

		detailsUtil.formatMinimumTerm(bookingDataList);
		assertEquals("2", testBookingData.getMinimumTermLength());
	}
}
