package com.kmp.aeroparker.application.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subscriptions/{affCode}")
@RequiredArgsConstructor
public class SubscriptionRenewalController
{
	private final SubscriptionRenewalService subscriptionRenewalService;

	@PostMapping("/validate-subscription-renewal")
	public ResponseEntity<Map<String, Object>> validateAndRenewSubscription(@PathVariable String affCode,
			@RequestBody Map<String, String> requestData)
	{
		String email = requestData.get("email");

		SubscriptionBookingData bookingData = new SubscriptionBookingData();
		bookingData.setEmail(email);
		bookingData.setCustomerGuid(requestData.get("customerGuid"));

		Map<String, Object> response = subscriptionRenewalService.validateSubscription(bookingData, affCode);

		return ResponseEntity.ok(response);
	}
}