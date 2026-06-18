package com.kmp.aeroparker.application.model.external.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.kmp.aeroparker.application.model.external.api.datatypes.AvailabilityWindow;
import com.kmp.aeroparker.application.model.external.api.datatypes.Language;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionBooking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("SubscriptionAvailabilityRQ")
public class SubscriptionAvailabilityRequest
{
	@JsonProperty("AvailabilityWindow")
	private AvailabilityWindow availabilityWindow;
	@JsonProperty("SubscriptionBooking")
	private SubscriptionBooking subscriptionBooking;
	@JsonProperty("Language")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Language language;
}
