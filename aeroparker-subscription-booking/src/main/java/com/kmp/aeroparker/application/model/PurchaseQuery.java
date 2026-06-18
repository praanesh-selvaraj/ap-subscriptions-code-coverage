package com.kmp.aeroparker.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PurchaseQuery
{
	@JsonProperty("productId")
	private int productId;
	@JsonProperty("startDate")
	private String startDate;
}