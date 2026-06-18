package com.kmp.aeroparker.application.model.external.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.kmp.aeroparker.application.model.external.api.datatypes.Error;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionBooking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("SubscriptionBookingAmendRS")
public class AmendSubscriptionResponse
{
	@JsonProperty("SubscriptionBooking")
	private SubscriptionBooking subscriptionBooking;
	private String jsonString;
	@JsonProperty("Error")
	private Error error;
}
