package com.kmp.aeroparker.application.model.external.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionBooking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("SubscriptionBookingAmendRQ")
public class AmendSubscriptionRequest
{
	@JsonProperty("SubscriptionBooking")
	private SubscriptionBooking subscriptionBooking;
}
