package com.kmp.aeroparker.application.model.external.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.kmp.aeroparker.application.model.external.api.datatypes.AvailabilityWindow;
import com.kmp.aeroparker.application.model.external.api.datatypes.Error;
import com.kmp.aeroparker.application.model.external.api.datatypes.Language;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionQuotes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("SubscriptionAvailabilityRS")
public class SubscriptionAvailabilityResponse
{
	@JsonProperty("AvailabilityWindow")
	private AvailabilityWindow availabilityWindow;
	@JsonProperty("SubscriptionQuotes")
	private SubscriptionQuotes subscriptionQuotes;
	@JsonProperty("Language")
	private Language language;
	@JsonProperty("Error")
	private Error error;
	private String jsonString;
}
