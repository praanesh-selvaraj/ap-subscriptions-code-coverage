package com.kmp.aeroparker.application.model.external.api.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityWindow
{
	@JsonProperty("ArrivalDateTime")
	private String arrivalDateTime;
	@JsonProperty("ReturnDateTime")
	private String returnDateTime;
	@JsonProperty("Key")
	private String key;
}
