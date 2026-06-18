package com.kmp.aeroparker.application.external.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.model.external.api.ExternalApiCredentials;
import com.kmp.aeroparker.application.model.external.api.datatypes.AvailabilityWindow;
import com.kmp.aeroparker.application.model.external.api.datatypes.CustomerDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.VehicleDetails;
import com.kmp.aeroparker.application.utils.JsonUtil;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;

@ExtendWith(MockitoExtension.class)
class SubscriptionBookingRequestBuilderTest
{
	@Mock
	private JsonUtil jsonUtil;
	@InjectMocks
	private SubscriptionBookingRequestBuilder builder;
	@Mock
	private AffiliateConfig affiliateConfig;

	@Test
	public void testBuildCredentials()
	{
		when(affiliateConfig.getConfigValue_String(eq(AffiliateConfigKeys.EXTERNAL_API_ENDPOINT), anyString()))
				.thenReturn("endpoint");
		when(affiliateConfig.getConfigValue_String(eq(AffiliateConfigKeys.EXTERNAL_API_USERNAME), anyString()))
				.thenReturn("username");
		when(affiliateConfig.getConfigValue_String(eq(AffiliateConfigKeys.EXTERNAL_API_PASSWORD), anyString()))
				.thenReturn("password");

		ExternalApiCredentials credentials = builder.buildCredentials(affiliateConfig);

		assertNotNull(credentials);
		assertEquals("endpoint", credentials.getEndpoint());
		assertEquals("username", credentials.getUsername());
		assertEquals("password", credentials.getPassword());
	}

	@Test
	public void testBuildCredentials_NoAffiliate()
	{
		assertNull(builder.buildCredentials(null));
	}

	@Test
	public void testBuildCredentials_NoConfigValue()
	{
		assertNull(builder.buildCredentials(affiliateConfig));
	}

	@Test
	public void testBuildCredentials_NoEndpoint()
	{
		when(affiliateConfig.getConfigValue_String(eq(AffiliateConfigKeys.EXTERNAL_API_ENDPOINT), anyString()))
				.thenReturn("");

		assertNull(builder.buildCredentials(affiliateConfig));
	}

	@Test
	public void testBuildCredentials_NoUsername()
	{
		when(affiliateConfig.getConfigValue_String(eq(AffiliateConfigKeys.EXTERNAL_API_ENDPOINT), anyString()))
				.thenReturn("endpoint");
		when(affiliateConfig.getConfigValue_String(eq(AffiliateConfigKeys.EXTERNAL_API_USERNAME), anyString()))
				.thenReturn("");

		assertNull(builder.buildCredentials(affiliateConfig));
	}

	@Test
	public void testBuildCredentials_NoPassword()
	{
		when(affiliateConfig.getConfigValue_String(eq(AffiliateConfigKeys.EXTERNAL_API_ENDPOINT), anyString()))
				.thenReturn("endpoint");
		when(affiliateConfig.getConfigValue_String(eq(AffiliateConfigKeys.EXTERNAL_API_USERNAME), anyString()))
				.thenReturn("username");
		when(affiliateConfig.getConfigValue_String(eq(AffiliateConfigKeys.EXTERNAL_API_PASSWORD), anyString()))
				.thenReturn("");

		assertNull(builder.buildCredentials(affiliateConfig));
	}

	@Test
	public void testBuildSubscriptionHttpRequest()
	{
		HttpPost request = builder.buildSubscriptionHttpRequest("endpoint", "requestBody", "username", "password");

		assertTrue(request.containsHeader(HttpHeaders.AUTHORIZATION));
		assertTrue(request.containsHeader(HttpHeaders.CONTENT_TYPE));
	}

	@Test
	public void testBuildSubscriptionRenewalAvailabilityJsonRequestBody()
	{
		String expectedRequestJson = "SubscriptionAvailabilityRequestJson";

		when(jsonUtil.jsonObjectToStringWithRootElement(any())).thenReturn(expectedRequestJson);

		assertEquals(expectedRequestJson, builder.buildSubscriptionRenewalAvailabilityJsonRequestBody("ref"));
	}

	@Test
	public void testBuildSubscriptionRenewalJsonRequestBody()
	{
		String expectedRequestJson = "RenewSubscriptionRequestJson";
		AvailabilityWindow availabilityWindow = mock(AvailabilityWindow.class);

		when(jsonUtil.jsonObjectToStringWithRootElement(any())).thenReturn(expectedRequestJson);

		assertEquals(expectedRequestJson, builder.buildSubscriptionRenewalJsonRequestBody("ref", availabilityWindow,
				"token", BigDecimal.ONE, "GBP"));
	}

	@Test
	public void testBuildSubscriptionAmendJsonRequestBody()
	{
		String expectedRequestJson = "AmendSubscriptionRequestJson";
		CustomerDetails customerDetails = mock(CustomerDetails.class);
		VehicleDetails vehicleDetails = mock(VehicleDetails.class);

		when(jsonUtil.jsonObjectToStringWithRootElement(any())).thenReturn(expectedRequestJson);

		assertEquals(expectedRequestJson,
				builder.buildSubscriptionAmendJsonRequestBody("ref", customerDetails, vehicleDetails));
	}

	@Test
	public void testBuildSubscriptionAmendJsonRequestBody_NullDetails()
	{
		String expectedRequestJson = "AmendSubscriptionRequestJson";

		when(jsonUtil.jsonObjectToStringWithRootElement(any())).thenReturn(expectedRequestJson);

		assertEquals(expectedRequestJson, builder.buildSubscriptionAmendJsonRequestBody("ref", null, null));
	}

	@Test
	public void testBuildSubscriptionPromotionJsonRequestBody()
	{
		String expectedRequestJson = "SubscriptionPromotionRequestJson";

		when(jsonUtil.jsonObjectToStringWithRootElement(any())).thenReturn(expectedRequestJson);

		assertEquals(expectedRequestJson, builder.buildSubscriptionPromotionJsonRequestBody(123, "SAVE10", "user@test.com"));
	}

	@Test
	public void testBuildSubscriptionPromotionJsonRequestBody_NullPromoCode()
	{
		String expectedRequestJson = "SubscriptionPromotionRequestJson";

		when(jsonUtil.jsonObjectToStringWithRootElement(any())).thenReturn(expectedRequestJson);

		assertEquals(expectedRequestJson, builder.buildSubscriptionPromotionJsonRequestBody(123, null, "user@test.com"));
	}

	@Test
	public void testBuildSubscriptionPromotionJsonRequestBody_NullProductId()
	{
		String expectedRequestJson = "SubscriptionPromotionRequestJson";

		when(jsonUtil.jsonObjectToStringWithRootElement(any())).thenReturn(expectedRequestJson);

		assertEquals(expectedRequestJson, builder.buildSubscriptionPromotionJsonRequestBody(null, "SAVE10", "user@test.com"));
	}

	@Test
	public void testBuildSubscriptionPromotionJsonRequestBody_EmptyPromoCode()
	{
		String expectedRequestJson = "SubscriptionPromotionRequestJson";

		when(jsonUtil.jsonObjectToStringWithRootElement(any())).thenReturn(expectedRequestJson);

		assertEquals(expectedRequestJson, builder.buildSubscriptionPromotionJsonRequestBody(123, "", "user@test.com"));
	}
}
