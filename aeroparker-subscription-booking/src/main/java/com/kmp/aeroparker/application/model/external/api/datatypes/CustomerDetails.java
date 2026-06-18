package com.kmp.aeroparker.application.model.external.api.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDetails
{
	@JsonProperty("Title")
	private String title;
	@JsonProperty("FirstName")
	private String firstName;
	@JsonProperty("LastName")
	private String lastName;
	@JsonProperty("EmailAddress")
	private String emailAddress;
	@JsonProperty("PhoneNumber")
	private String phoneNumber;
	@JsonProperty("AddressLine1")
	private String addressLine1;
	@JsonProperty("AddressLine2")
	private String addressLine2;
	@JsonProperty("Town")
	private String town;
	@JsonProperty("County")
	private String county;
	@JsonProperty("Country")
	private String country;
	@JsonProperty("Postcode")
	private String postCode;
}
