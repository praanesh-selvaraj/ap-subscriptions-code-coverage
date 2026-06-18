package com.kmp.aeroparker.application.external.api;

import com.kmp.aeroparker.application.model.external.api.ExternalApiCredentials;
import com.kmp.aeroparker.application.model.external.api.datatypes.AvailabilityWindow;
import com.kmp.aeroparker.application.model.external.api.datatypes.CustomerDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.VehicleDetails;
import com.kmp.aeroparker.application.model.external.api.response.AmendSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.RenewSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionAvailabilityResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionPromotionResponse;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@AllArgsConstructor
public class SubscriptionBookingRequestHandler
{
	private final SubscriptionBookingRequestBuilder requestBuilder;
	private final SubscriptionBookingClient client;
	private final SubscriptionResponseHandler responseHandler;

	public SubscriptionAvailabilityResponse getRenewalAvailability(AffiliateConfig affiliateConfig, String reference)
	{
		String response = "";

		ExternalApiCredentials credentials = requestBuilder.buildCredentials(affiliateConfig);
		if (credentials != null)
		{
			String jsonRequestBody = requestBuilder.buildSubscriptionRenewalAvailabilityJsonRequestBody(reference);
			response = client.sendRequest(credentials.getEndpoint(), jsonRequestBody, credentials.getUsername(),
					credentials.getPassword());
		}
		else
		{
			log.info("No credentials found for affiliate, subscription renewal availability could not be retrieved.");
		}

		return responseHandler.getAvailabilityFromSubscriptionAvailabilityResponse(response);
	}

	public RenewSubscriptionResponse sendRenewalRequest(AffiliateConfig affiliateConfig, String reference,
			AvailabilityWindow availabilityWindow, String token, BigDecimal amount, String currency)
	{
		String response = "";

		ExternalApiCredentials credentials = requestBuilder.buildCredentials(affiliateConfig);
		if (credentials != null)
		{
			String jsonRequestBody = requestBuilder.buildSubscriptionRenewalJsonRequestBody(reference,
					availabilityWindow, token, amount, currency);
			response = client.sendRequest(credentials.getEndpoint(), jsonRequestBody, credentials.getUsername(),
					credentials.getPassword());
		}
		else
		{
			log.info("No credentials found for affiliate, subscription renewal availability could not be retrieved.");
		}

		return responseHandler.getRenewSubscriptionResponseFromResponseString(response);
	}
	
	public AmendSubscriptionResponse sendAmendRequest(String reference, CustomerDetails customerDetails,
			VehicleDetails vehicleDetails, AffiliateConfig affiliateConfig)
	{
		String response = "";

		ExternalApiCredentials credentials = requestBuilder.buildCredentials(affiliateConfig);
		if (credentials != null)
		{
			String jsonRequestBody =
					requestBuilder.buildSubscriptionAmendJsonRequestBody(reference, customerDetails, vehicleDetails);
			response = client.sendRequest(credentials.getEndpoint(), jsonRequestBody, credentials.getUsername(),
					credentials.getPassword());
		}
		else
		{
			log.info("No credentials found for affiliate, subscription amend will not be processed.");
		}

		return responseHandler.getAmendSubscriptionResponseFromResponseString(response);
	}

	public SubscriptionPromotionResponse validatePromotion(AffiliateConfig affiliateConfig,
			Integer subscriptionProductId, String promoCode, String emailAddress)
	{
		String response = "";

		ExternalApiCredentials credentials = requestBuilder.buildCredentials(affiliateConfig);
		if (credentials != null)
		{
			String jsonRequestBody = requestBuilder.buildSubscriptionPromotionJsonRequestBody(
					subscriptionProductId, promoCode, emailAddress);
			response = client.sendRequest(credentials.getEndpoint(), jsonRequestBody, credentials.getUsername(),
					credentials.getPassword());
		}
		else
		{
			log.info("No credentials found for affiliate, promotion validation could not be performed.");
		}

		return responseHandler.getPromotionResponseFromString(response);
	}
}
