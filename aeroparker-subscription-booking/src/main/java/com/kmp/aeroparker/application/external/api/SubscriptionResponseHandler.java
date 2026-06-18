package com.kmp.aeroparker.application.external.api;

import com.kmp.aeroparker.application.model.external.api.response.AmendSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.RenewSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionAvailabilityResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionPromotionResponse;
import com.kmp.aeroparker.application.utils.JsonUtil;
import com.kmp.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SubscriptionResponseHandler
{
	private final JsonUtil jsonUtil;

	public SubscriptionAvailabilityResponse getAvailabilityFromSubscriptionAvailabilityResponse(String responseString)
	{
		SubscriptionAvailabilityResponse availabilityResponse = null;

		if (!StringUtil.isNullOrEmpty(responseString))
		{
			availabilityResponse =
					jsonUtil.jsonToObjectWithRootElement(responseString, SubscriptionAvailabilityResponse.class);
			if (availabilityResponse != null)
			{
				availabilityResponse.setJsonString(responseString);
			}
		}

		return availabilityResponse;
	}

	public RenewSubscriptionResponse getRenewSubscriptionResponseFromResponseString(String responseString)
	{
		RenewSubscriptionResponse renewSubscriptionResponse = null;

		if (!StringUtil.isNullOrEmpty(responseString))
		{
			renewSubscriptionResponse =
					jsonUtil.jsonToObjectWithRootElement(responseString, RenewSubscriptionResponse.class);
			if (renewSubscriptionResponse != null)
			{
				renewSubscriptionResponse.setJsonString(responseString);
			}
		}

		return renewSubscriptionResponse;
	}
	
	public AmendSubscriptionResponse getAmendSubscriptionResponseFromResponseString(String responseString)
	{
		AmendSubscriptionResponse amendSubscriptionResponse = null;

		if (!StringUtil.isNullOrEmpty(responseString))
		{
			amendSubscriptionResponse =
					jsonUtil.jsonToObjectWithRootElement(responseString, AmendSubscriptionResponse.class);
			if (amendSubscriptionResponse != null)
			{
				amendSubscriptionResponse.setJsonString(responseString);
			}
		}

		return amendSubscriptionResponse;
	}

	public SubscriptionPromotionResponse getPromotionResponseFromString(String responseString)
	{
		SubscriptionPromotionResponse promotionResponse = null;

		if (!StringUtil.isNullOrEmpty(responseString))
		{
			promotionResponse =
					jsonUtil.jsonToObjectWithRootElement(responseString, SubscriptionPromotionResponse.class);
			if (promotionResponse != null)
			{
				promotionResponse.setJsonString(responseString);
			}
		}

		return promotionResponse;
	}
}
