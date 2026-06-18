package com.kmp.aeroparker.application.external.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.model.external.api.response.AmendSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.RenewSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionAvailabilityResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionPromotionResponse;
import com.kmp.aeroparker.application.utils.JsonUtil;

@ExtendWith(MockitoExtension.class)
class SubscriptionResponseHandlerTest
{
	@Mock
	private JsonUtil jsonUtil;
	@InjectMocks
	private SubscriptionResponseHandler responseHandler;

	@Test
	public void testGetAvailabilityFromSubscriptionAvailabilityResponse()
	{
		SubscriptionAvailabilityResponse availabilityResponse = mock(SubscriptionAvailabilityResponse.class);
		
		when(jsonUtil.jsonToObjectWithRootElement(anyString(), any())).thenReturn(availabilityResponse);
		
		assertEquals(availabilityResponse, responseHandler.getAvailabilityFromSubscriptionAvailabilityResponse("response"));
		
		verify(jsonUtil).jsonToObjectWithRootElement("response", SubscriptionAvailabilityResponse.class);
	}
	
	@Test
	public void testGetAvailabilityFromSubscriptionAvailabilityResponse_JsonMappingFailure()
	{
		when(jsonUtil.jsonToObjectWithRootElement(anyString(), any())).thenReturn(null);
		
		assertNull(responseHandler.getAvailabilityFromSubscriptionAvailabilityResponse("response"));
		
		verify(jsonUtil).jsonToObjectWithRootElement("response", SubscriptionAvailabilityResponse.class);
	}
	
	@Test
	public void testGetAvailabilityFromSubscriptionAvailabilityResponse_NullResponse()
	{
		assertNull(responseHandler.getAvailabilityFromSubscriptionAvailabilityResponse(null));
		
		verifyNoInteractions(jsonUtil);
	}

	@Test
	public void testGetRenewSubscriptionResponseFromResponseString()
	{
		RenewSubscriptionResponse renewSubscriptionResponse = mock(RenewSubscriptionResponse.class);

		when(jsonUtil.jsonToObjectWithRootElement(anyString(), any())).thenReturn(renewSubscriptionResponse);

		assertEquals(renewSubscriptionResponse,
				responseHandler.getRenewSubscriptionResponseFromResponseString("response"));

		verify(jsonUtil).jsonToObjectWithRootElement("response", RenewSubscriptionResponse.class);
		verify(renewSubscriptionResponse).setJsonString(any());
	}

	@Test
	public void testGetRenewSubscriptionResponseFromResponseString_JsonMappingFailure()
	{
		when(jsonUtil.jsonToObjectWithRootElement(anyString(), any())).thenReturn(null);

		assertNull(responseHandler.getRenewSubscriptionResponseFromResponseString("response"));

		verify(jsonUtil).jsonToObjectWithRootElement("response", RenewSubscriptionResponse.class);
	}

	@Test
	public void testGetRenewSubscriptionResponseFromResponseString_NullResponse()
	{
		assertNull(responseHandler.getRenewSubscriptionResponseFromResponseString(null));

		verifyNoInteractions(jsonUtil);
	}

	@Test
	public void testGetAmendSubscriptionResponseFromResponseString()
	{
		AmendSubscriptionResponse amendSubscriptionResponse = mock(AmendSubscriptionResponse.class);
		when(jsonUtil.jsonToObjectWithRootElement(anyString(), any())).thenReturn(amendSubscriptionResponse);
		assertEquals(amendSubscriptionResponse,
				responseHandler.getAmendSubscriptionResponseFromResponseString("response"));
		verify(jsonUtil).jsonToObjectWithRootElement("response", AmendSubscriptionResponse.class);
		verify(amendSubscriptionResponse).setJsonString(any());
	}

	@Test
	public void testGetAmendSubscriptionResponseFromResponseString_JsonMappingFailure()
	{
		when(jsonUtil.jsonToObjectWithRootElement(anyString(), any())).thenReturn(null);
		assertNull(responseHandler.getAmendSubscriptionResponseFromResponseString("response"));
		verify(jsonUtil).jsonToObjectWithRootElement("response", AmendSubscriptionResponse.class);
	}

	@Test
	public void testGetAmendSubscriptionResponseFromResponseString_NullResponse()
	{
		assertNull(responseHandler.getAmendSubscriptionResponseFromResponseString(null));
		verifyNoInteractions(jsonUtil);
	}

	@Test
	public void testGetPromotionResponseFromString()
	{
		SubscriptionPromotionResponse promotionResponse = mock(SubscriptionPromotionResponse.class);

		when(jsonUtil.jsonToObjectWithRootElement(anyString(), any())).thenReturn(promotionResponse);

		assertEquals(promotionResponse, responseHandler.getPromotionResponseFromString("response"));

		verify(jsonUtil).jsonToObjectWithRootElement("response", SubscriptionPromotionResponse.class);
		verify(promotionResponse).setJsonString(any());
	}

	@Test
	public void testGetPromotionResponseFromString_JsonMappingFailure()
	{
		when(jsonUtil.jsonToObjectWithRootElement(anyString(), any())).thenReturn(null);

		assertNull(responseHandler.getPromotionResponseFromString("response"));

		verify(jsonUtil).jsonToObjectWithRootElement("response", SubscriptionPromotionResponse.class);
	}

	@Test
	public void testGetPromotionResponseFromString_NullResponse()
	{
		assertNull(responseHandler.getPromotionResponseFromString(null));

		verifyNoInteractions(jsonUtil);
	}

	@Test
	public void testGetPromotionResponseFromString_EmptyResponse()
	{
		assertNull(responseHandler.getPromotionResponseFromString(""));

		verifyNoInteractions(jsonUtil);
	}
}
