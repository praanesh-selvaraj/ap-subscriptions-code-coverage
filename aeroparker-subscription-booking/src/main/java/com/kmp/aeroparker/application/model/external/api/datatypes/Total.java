package com.kmp.aeroparker.application.model.external.api.datatypes;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Total
{
	@JsonProperty("Currency")
	private String currency;
	@JsonProperty("Value")
	private BigDecimal value;
}
