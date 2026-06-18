package com.kmp.aeroparker.application.model.external.api.datatypes;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Price
{
	@JsonProperty("Value")
	private BigDecimal value;
}
