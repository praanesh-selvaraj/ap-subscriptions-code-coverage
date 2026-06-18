package com.kmp.aeroparker.application.model.external.api.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionRequest
{
	@JsonProperty("PromoCode")
	private String promoCode;
	@JsonProperty("Name")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;
}
