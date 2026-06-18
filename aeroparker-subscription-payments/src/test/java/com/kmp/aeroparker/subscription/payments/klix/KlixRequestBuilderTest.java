package com.kmp.aeroparker.subscription.payments.klix;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

@ExtendWith(MockitoExtension.class)
class KlixRequestBuilderTest
{
	@InjectMocks
	private KlixRequestBuilder builder;
	private KlixRequest request;
	@Mock
	private KlixCredentials credentials;
	@Mock
	private Affiliates affiliates;
	@Mock
	private SubscriptionBookingData bookingData;
	@Mock
	private KlixUrlBuilder urlBuilder;

	@BeforeEach
	public void init()
	{
		request = new KlixRequest("successRedirect", "failureRedirect", "successCallback", "language", "productName",
				"10", "email", affiliates, "firstName", "originalBookingReference", "city", "zipCode", "streetAddress",
				credentials, "currency", "phoneNumber", "country", "lastName", "guid", "bookingReference", 10,
				"savedCardToken", true, true, "");
	}

	@Test
	public void testGenerateJsonForInitialRequest()
	{
		assertNotNull(builder.generateJsonForInitialRequest(request));
	}

	@Test
	public void testGenerateJsonForRefund()
	{
		assertEquals("{\"amount\":1000}", builder.generateJsonForRefund(BigDecimal.TEN)
				.toString());
	}

	@Test
	public void testGetRequestParameters()
	{
		when(bookingData.getCustomerGuid()).thenReturn("GUID");
		when(bookingData.getEmail()).thenReturn("email");
		when(affiliates.getName()).thenReturn("affiliate");

		KlixRequest request =
				builder.getRequestParameters(bookingData, affiliates, "abc.com/", "en", credentials, BigDecimal.TEN);

		assertNotNull(request);
	}
}
