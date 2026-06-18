package com.kmp.aeroparker.application.model.external.api.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Promotion
{
	private int promotionId;
	private String promotionType;
	@JsonProperty("PromoCode")
	private String promoCode;
	@JsonProperty("Name")
	private String name;
	@JsonProperty("Valid")
	private Boolean valid;
	@JsonProperty("Discount")
	private BigDecimal discount;
	@JsonProperty("DiscountedPrice")
	private BigDecimal discountedPrice;
}
