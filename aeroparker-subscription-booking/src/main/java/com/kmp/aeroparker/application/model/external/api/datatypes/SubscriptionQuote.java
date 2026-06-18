package com.kmp.aeroparker.application.model.external.api.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionQuote
{
	@JsonProperty("SubscriptionProduct")
	private SubscriptionProduct subscriptionProduct;
	@JsonProperty("Price")
	private Price price;
	@JsonProperty("StartDate")
	private String startDate;
	@JsonProperty("EndDate")
	private String endDate;
	@JsonProperty("MinimumTerm")
	private MinimumTerm minimumTerm;
	@JsonProperty("MembershipId")
	private String membershipId;
	@JsonProperty("CarParks")
	private CarParks carParks;
}
