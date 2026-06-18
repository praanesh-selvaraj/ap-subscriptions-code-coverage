package com.kmp.aeroparker.application.external.api;

import com.kmp.aeroparker.application.model.external.api.ExternalApiCredentials;
import com.kmp.aeroparker.application.model.external.api.datatypes.AvailabilityWindow;
import com.kmp.aeroparker.application.model.external.api.datatypes.CustomerDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.PaymentDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.PromotionRequest;
import com.kmp.aeroparker.application.model.external.api.datatypes.Promotions;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionBooking;
import com.kmp.aeroparker.application.model.external.api.datatypes.Total;
import com.kmp.aeroparker.application.model.external.api.datatypes.VehicleDetails;
import com.kmp.aeroparker.application.model.external.api.request.AmendSubscriptionRequest;
import com.kmp.aeroparker.application.model.external.api.request.RenewSubscriptionRequest;
import com.kmp.aeroparker.application.model.external.api.request.SubscriptionAvailabilityRequest;
import com.kmp.aeroparker.application.model.external.api.request.SubscriptionPromotionRequest;
import com.kmp.aeroparker.application.utils.JsonUtil;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.Collections;

@Component
@AllArgsConstructor
public class SubscriptionBookingRequestBuilder
{
	private JsonUtil jsonUtil;

	public ExternalApiCredentials buildCredentials(AffiliateConfig affiliateConfig)
	{
		ExternalApiCredentials credentials = null;

		if (affiliateConfig != null)
		{
			String endpoint = affiliateConfig.getConfigValue_String(AffiliateConfigKeys.EXTERNAL_API_ENDPOINT, "");
			String username = affiliateConfig.getConfigValue_String(AffiliateConfigKeys.EXTERNAL_API_USERNAME, "");
			String password = affiliateConfig.getConfigValue_String(AffiliateConfigKeys.EXTERNAL_API_PASSWORD, "");

			if (!StringUtil.isNullOrEmpty(endpoint) && !StringUtil.isNullOrEmpty(username)
					&& !StringUtil.isNullOrEmpty(password))
			{
				credentials = new ExternalApiCredentials();
				credentials.setEndpoint(endpoint);
				credentials.setUsername(username);
				credentials.setPassword(password);
			}
		}
		return credentials;
	}

	public HttpPost buildSubscriptionHttpRequest(String endpoint, String requestBody, String username, String password)
	{
		HttpPost request = new HttpPost(endpoint);
		String authHeader = Base64.getEncoder()
				.encodeToString((username + ":" + password).getBytes());
		request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + authHeader);
		request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
		request.setEntity(entity);

		return request;
	}

	public String buildSubscriptionRenewalAvailabilityJsonRequestBody(String reference)
	{
		SubscriptionBooking booking = new SubscriptionBooking();
		booking.setReference(reference);

		SubscriptionAvailabilityRequest availabilityRequest = new SubscriptionAvailabilityRequest();
		availabilityRequest.setSubscriptionBooking(booking);

		return jsonUtil.jsonObjectToStringWithRootElement(availabilityRequest);
	}

	public String buildSubscriptionRenewalJsonRequestBody(String reference, AvailabilityWindow availabilityWindow,
			String token, BigDecimal amount, String currency)
	{
		Total total = new Total();
		total.setCurrency(currency);
		total.setValue(amount.setScale(2, RoundingMode.HALF_UP));

		PaymentDetails paymentDetails = new PaymentDetails();
		paymentDetails.setToken(token);
		paymentDetails.setTotal(total);

		SubscriptionBooking booking = new SubscriptionBooking();
		booking.setReference(reference);
		booking.setAvailabilityWindow(availabilityWindow);
		booking.setPaymentDetails(paymentDetails);

		RenewSubscriptionRequest renewRequest = new RenewSubscriptionRequest();
		renewRequest.setSubscriptionBooking(booking);

		return jsonUtil.jsonObjectToStringWithRootElement(renewRequest);
	}
	
	public String buildSubscriptionAmendJsonRequestBody(String reference, CustomerDetails customerDetails,
			VehicleDetails vehicleDetails)
	{
		SubscriptionBooking booking = new SubscriptionBooking();
		booking.setReference(reference);
		booking.setCustomerDetails(customerDetails);
		booking.setVehicleDetails(vehicleDetails);

		AmendSubscriptionRequest amendRequest = new AmendSubscriptionRequest();
		amendRequest.setSubscriptionBooking(booking);

		return jsonUtil.jsonObjectToStringWithRootElement(amendRequest);
	}

	public String buildSubscriptionPromotionJsonRequestBody(Integer subscriptionProductId, String promoCode, String emailAddress)
	{
		PromotionRequest promotionRequest = new PromotionRequest();
		Promotions promotions = new Promotions();
		SubscriptionPromotionRequest request = new SubscriptionPromotionRequest();
		CustomerDetails customerDetails = new CustomerDetails();

		promotionRequest.setPromoCode(promoCode);
		promotions.setPromotion(Collections.singletonList(promotionRequest));
		customerDetails.setEmailAddress(emailAddress);
		request.setSubscriptionProductId(subscriptionProductId);
		request.setPromotions(promotions);
		request.setCustomerDetails(customerDetails);

		return jsonUtil.jsonObjectToStringWithRootElement(request);
	}
}
