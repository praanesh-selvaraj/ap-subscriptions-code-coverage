package com.kmp.aeroparker.application.model.external.api.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error
{
	@JsonProperty("Code")
	private String code;
	@JsonProperty("Message")
	private String message;
}
