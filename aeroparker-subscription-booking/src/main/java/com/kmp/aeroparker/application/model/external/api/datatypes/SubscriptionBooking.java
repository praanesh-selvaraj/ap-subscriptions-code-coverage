package com.kmp.aeroparker.application.model.external.api.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionBooking
{
	@JsonProperty("ID")
	private Integer id;
	@JsonProperty("Reference")
	private String reference;
	@JsonProperty("EmailAddress")
	private String emailAddress;
	@JsonProperty("Created")
	private String created;
	@JsonProperty("CustomerDetails")
	private CustomerDetails customerDetails;
	@JsonProperty("VehicleDetails")
	private VehicleDetails vehicleDetails;
	@JsonProperty("SubscriptionQuote")
	private SubscriptionQuote subscriptionQuote;
	@JsonProperty("AvailabilityWindow")
	private AvailabilityWindow availabilityWindow;
	@JsonProperty("PaymentDetails")
	private PaymentDetails paymentDetails;
	@JsonProperty("Error")
	private Error error;
	@JsonProperty("Amount")
	private String amount;
	@JsonProperty("Cancelled")
	private String cancelled;
}
