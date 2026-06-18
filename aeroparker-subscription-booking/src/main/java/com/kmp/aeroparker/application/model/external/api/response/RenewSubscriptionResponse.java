package com.kmp.aeroparker.application.model.external.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionBooking;
import com.kmp.aeroparker.application.model.external.api.datatypes.Error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("RenewSubscriptionRS")
public class RenewSubscriptionResponse
{
	@JsonProperty("SubscriptionBooking")
	private SubscriptionBooking subscriptionBooking;
	private String jsonString;
	@JsonProperty("Error")
	private Error error;
}
