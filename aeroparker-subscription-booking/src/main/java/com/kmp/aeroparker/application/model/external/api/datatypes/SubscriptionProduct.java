package com.kmp.aeroparker.application.model.external.api.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionProduct
{
	@JsonProperty("ID")
	private Integer id;
	@JsonProperty("Name")
	private String name;
}
