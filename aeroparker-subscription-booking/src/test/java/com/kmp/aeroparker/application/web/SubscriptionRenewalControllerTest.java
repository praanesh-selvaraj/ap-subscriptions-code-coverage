package com.kmp.aeroparker.application.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

@ExtendWith(MockitoExtension.class)
class SubscriptionRenewalControllerTest
{
	@InjectMocks
	private SubscriptionRenewalController subscriptionRenewalController;
	@Mock
	private SubscriptionRenewalService subscriptionRenewalService;

	@Test
	void testValidateAndRenewSubscription_Success()
	{
		Map<String, String> requestData = new HashMap<>();
		requestData.put("email", "test@example.com");
		requestData.put("timeZone", "Europe/London");
		requestData.put("customerGuid", "guid123");

		Map<String, Object> mockResponse = new HashMap<>();
		mockResponse.put("newStartDate", "10.10.2024");
		mockResponse.put("grandTotal", "100.0");

		when(subscriptionRenewalService.validateSubscription(any(SubscriptionBookingData.class), anyString()))
				.thenReturn(mockResponse);

		ResponseEntity<Map<String, Object>> response =
				subscriptionRenewalController.validateAndRenewSubscription("affCode", requestData);

		verify(subscriptionRenewalService, times(1)).validateSubscription(any(SubscriptionBookingData.class),
				anyString());

		assertNotNull(response);
		assertEquals(mockResponse, response.getBody());
	}

	@Test
	void testValidateAndRenewSubscription_NoBookingDetails()
	{
		Map<String, String> requestData = new HashMap<>();
		requestData.put("email", "test@example.com");
		requestData.put("timeZone", "Europe/London");
		requestData.put("customerGuid", "guid123");

		Map<String, Object> mockErrorResponse = new HashMap<>();
		mockErrorResponse.put("error", "No booking details found for this customer.");
		when(subscriptionRenewalService.validateSubscription(any(SubscriptionBookingData.class), anyString()))
				.thenReturn(mockErrorResponse);

		ResponseEntity<Map<String, Object>> response =
				subscriptionRenewalController.validateAndRenewSubscription("affCode", requestData);

		verify(subscriptionRenewalService, times(1)).validateSubscription(any(SubscriptionBookingData.class),
				anyString());

		assertNotNull(response);
		assertEquals(mockErrorResponse, response.getBody());
	}

	@Test
	void testValidateAndRenewSubscription_InvalidTimeZone()
	{
		Map<String, String> requestData = new HashMap<>();
		requestData.put("email", "test@example.com");
		requestData.put("timeZone", "Invalid/TimeZone");
		requestData.put("customerGuid", "guid123");

		subscriptionRenewalController.validateAndRenewSubscription("affCode", requestData);
	}

	@Test
	void testValidateAndRenewSubscription_MissingEmail()
	{
		Map<String, String> requestData = new HashMap<>();
		requestData.put("email", null); // Missing email
		requestData.put("timeZone", "Europe/London");
		requestData.put("customerGuid", "guid123");

		subscriptionRenewalController.validateAndRenewSubscription("affCode", requestData);
	}
}