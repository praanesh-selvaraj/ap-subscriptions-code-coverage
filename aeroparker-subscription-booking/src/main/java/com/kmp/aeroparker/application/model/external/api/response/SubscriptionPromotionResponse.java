package com.kmp.aeroparker.application.model.external.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.kmp.aeroparker.application.model.external.api.datatypes.Promotion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("SubscriptionPromotionRS")
public class SubscriptionPromotionResponse
{
	@JsonProperty("Promotion")
	private Promotion promotion;
	@JsonProperty("Timestamp")
	private String timestamp;
	private String jsonString;
}
