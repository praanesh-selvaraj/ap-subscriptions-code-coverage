package com.kmp.aeroparker.application.model.external.api.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinimumTerm
{
	@JsonProperty("Term")
	private String term;
	@JsonProperty("Cancellation")
	private Boolean cancellation;
}
