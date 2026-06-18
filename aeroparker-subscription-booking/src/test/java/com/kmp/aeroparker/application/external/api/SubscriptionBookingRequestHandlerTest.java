package com.kmp.aeroparker.application.external.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.ServiceFactory;

import com.kmp.aeroparker.application.model.external.api.ExternalApiCredentials;
import com.kmp.aeroparker.application.model.external.api.datatypes.AvailabilityWindow;
import com.kmp.aeroparker.application.model.external.api.datatypes.CustomerDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.VehicleDetails;
import com.kmp.aeroparker.application.model.external.api.response.AmendSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.RenewSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionAvailabilityResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionPromotionResponse;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;

@ExtendWith(MockitoExtension.class)
class SubscriptionBookingRequestHandlerTest
{
	@InjectMocks
	private SubscriptionBookingRequestHandler requestHandler;
	@Mock
	private SubscriptionBookingRequestBuilder requestBuilder;
	@Mock
	private SubscriptionBookingClient client;
	@Mock
	private ServiceFactory serviceFactory;
	@Mock
	private SubscriptionResponseHandler responseHandler;
	@Mock
	private AffiliateConfig affiliateConfig;
	@Mock
	private ExternalApiCredentials credentials;
	@Mock
	private CustomerDetails customerDetails;

	@Test
	public void testGetRenewalAvailability()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionRenewalAvailabilityJsonRequestBody(anyString())).thenReturn("request");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getAvailabilityFromSubscriptionAvailabilityResponse(anyString()))
				.thenReturn(mock(SubscriptionAvailabilityResponse.class));

		assertNotNull(requestHandler.getRenewalAvailability(affiliateConfig, "ref"));

		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getAvailabilityFromSubscriptionAvailabilityResponse(anyString());
	}

	@Test
	public void testGetRenewalAvailability_NullResponse()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionRenewalAvailabilityJsonRequestBody(anyString())).thenReturn("request");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getAvailabilityFromSubscriptionAvailabilityResponse(anyString())).thenReturn(null);

		assertNull(requestHandler.getRenewalAvailability(affiliateConfig, "ref"));

		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getAvailabilityFromSubscriptionAvailabilityResponse(anyString());
	}

	@Test
	public void testGetRenewalAvailability_NoCredentials()
	{
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(null);

		assertNull(requestHandler.getRenewalAvailability(affiliateConfig, "ref"));

		verifyNoInteractions(client);
	}

	@Test
	public void testSendRenewalRequest()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionRenewalJsonRequestBody(anyString(), any(), anyString(), any(),
				anyString())).thenReturn("request");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getRenewSubscriptionResponseFromResponseString(anyString()))
				.thenReturn(mock(RenewSubscriptionResponse.class));

		assertNotNull(requestHandler.sendRenewalRequest(affiliateConfig, "ref", mock(AvailabilityWindow.class), "token",
				BigDecimal.ONE, "GBP"));

		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getRenewSubscriptionResponseFromResponseString(anyString());
	}

	@Test
	public void testSendRenewalRequest_NullResponse()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionRenewalJsonRequestBody(anyString(), any(), anyString(), any(),
				anyString())).thenReturn("");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getRenewSubscriptionResponseFromResponseString(anyString())).thenReturn(null);

		assertNull(requestHandler.sendRenewalRequest(affiliateConfig, "ref", mock(AvailabilityWindow.class), "token",
				BigDecimal.ONE, "GBP"));

		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getRenewSubscriptionResponseFromResponseString(anyString());
	}

	@Test
	public void testSendRenewalRequest_NoCredentials()
	{
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(null);

		assertNull(requestHandler.sendRenewalRequest(affiliateConfig, "ref", mock(AvailabilityWindow.class), "token",
				BigDecimal.ONE, "GBP"));

		verifyNoInteractions(client);
	}

	@Test
	public void testSendAmendRequest()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionAmendJsonRequestBody(anyString(), any(), any())).thenReturn("request");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getAmendSubscriptionResponseFromResponseString(anyString()))
				.thenReturn(mock(AmendSubscriptionResponse.class));

		assertNotNull(
				requestHandler.sendAmendRequest("ref", customerDetails, mock(VehicleDetails.class), affiliateConfig));
		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getAmendSubscriptionResponseFromResponseString(anyString());
	}

	@Test
	public void testSendAmendRequest_NullResponse()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionAmendJsonRequestBody(anyString(), any(), any())).thenReturn("request");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getAmendSubscriptionResponseFromResponseString(anyString())).thenReturn(null);

		assertNull(
				requestHandler.sendAmendRequest("ref", customerDetails, mock(VehicleDetails.class), affiliateConfig));
		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getAmendSubscriptionResponseFromResponseString(anyString());
	}

	@Test
	public void testSendAmendRequest_NoCredentials()
	{
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(null);

		assertNull(
				requestHandler.sendAmendRequest("ref", customerDetails, mock(VehicleDetails.class), affiliateConfig));
		verifyNoInteractions(client);
	}

	@Test
	public void testValidatePromotion()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionPromotionJsonRequestBody(any(Integer.class), anyString(), anyString()))
				.thenReturn("request");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getPromotionResponseFromString(anyString()))
				.thenReturn(mock(SubscriptionPromotionResponse.class));

		assertNotNull(requestHandler.validatePromotion(affiliateConfig, 123, "SAVE10", "user@test.com"));

		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getPromotionResponseFromString(anyString());
	}

	@Test
	public void testValidatePromotion_NullResponse()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionPromotionJsonRequestBody(any(Integer.class), anyString(), anyString()))
				.thenReturn("request");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getPromotionResponseFromString(anyString())).thenReturn(null);

		assertNull(requestHandler.validatePromotion(affiliateConfig, 123, "SAVE10", "user@test.com"));

		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getPromotionResponseFromString(anyString());
	}

	@Test
	public void testValidatePromotion_NoCredentials()
	{
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(null);

		assertNull(requestHandler.validatePromotion(affiliateConfig, 123, "SAVE10", "user@test.com"));

		verifyNoInteractions(client);
	}

	@Test
	public void testValidatePromotion_NullPromoCode()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionPromotionJsonRequestBody(any(Integer.class), isNull(), anyString()))
				.thenReturn("request");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getPromotionResponseFromString(anyString()))
				.thenReturn(mock(SubscriptionPromotionResponse.class));

		assertNotNull(requestHandler.validatePromotion(affiliateConfig, 123, null, "user@test.com"));

		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getPromotionResponseFromString(anyString());
	}

	@Test
	public void testValidatePromotion_NullSubscriptionProductId()
	{
		when(credentials.getEndpoint()).thenReturn("endpoint");
		when(credentials.getUsername()).thenReturn("username");
		when(credentials.getPassword()).thenReturn("password");
		when(requestBuilder.buildCredentials(affiliateConfig)).thenReturn(credentials);
		when(requestBuilder.buildSubscriptionPromotionJsonRequestBody(any(), anyString(), anyString())).thenReturn("request");
		when(client.sendRequest(anyString(), anyString(), anyString(), anyString())).thenReturn("");
		when(responseHandler.getPromotionResponseFromString(anyString()))
				.thenReturn(mock(SubscriptionPromotionResponse.class));

		assertNotNull(requestHandler.validatePromotion(affiliateConfig, null, "SAVE10", "user@test.com"));

		verify(client).sendRequest(anyString(), anyString(), anyString(), anyString());
		verify(responseHandler).getPromotionResponseFromString(anyString());
	}
}
