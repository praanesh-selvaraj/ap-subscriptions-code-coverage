package com.kmp.aeroparker.application.model.external.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.kmp.aeroparker.application.model.external.api.datatypes.CustomerDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.Promotions;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonRootName("SubscriptionPromotionRQ")
public class SubscriptionPromotionRequest
{
	@JsonProperty("SubscriptionProductId")
	private Integer subscriptionProductId;
	@JsonProperty("Key")
	private String key;
	@JsonProperty("Promotions")
	private Promotions promotions;
	@JsonProperty("CustomerDetails")
	private CustomerDetails customerDetails;
}
