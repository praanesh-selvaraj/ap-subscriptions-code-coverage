package com.kmp.aeroparker.subscription.payments.klix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KlixUrlBuilderTest
{
	@InjectMocks
	private KlixUrlBuilder builder;

	private static final String SUCCESS_URL =
			"test.com/processPaymentCmd?cmd=/success&affiliateId=1&redirectUrl=abc&customerGuid=guid&bookingReference=ref&startDate=25/07/2024&firstName=firstn&lastName=lastn&siteId=2&originalBookingReference=ogref&carParkId=0&callback=false";
	private static final String REDIRECT_URL =
			"test.com/booking/processPaymentCmd?cmd=/Success&affiliateId=1&redirectUrl=redirect.com&customerGuid=guid&bookingReference=ref&startDate=25/07/2024&firstName=firstn&lastName=lastn&siteId=1";
	private static final String SUCCESS_URL_SPECIAL_CHARS =
			"test.com/processPaymentCmd?cmd=/success&affiliateId=1&redirectUrl=abc&customerGuid=guid&bookingReference=ref&startDate=25/07/2024&firstName=first+name&lastName=O%27+Malley&siteId=2&originalBookingReference=ogref&carParkId=0&callback=false";
	private static final String REDIRECT_URL_SPECIAL_CHARS =
			"test.com/booking/processPaymentCmd?cmd=/Success&affiliateId=1&redirectUrl=redirect.com&customerGuid=guid&bookingReference=ref&startDate=25/07/2024&firstName=first+name&lastName=O%27+Malley&siteId=1";
	@Test
	public void testGetSuccessRedirectUrl()
	{
		assertEquals(SUCCESS_URL, builder.getSuccessRedirectUrl("test.com", "/success", 1, "abc", "guid", "ref",
				"ogref", 0, false, "25/07/2024", "firstn", "lastn", 2));
	}

	@Test
	public void testBuildRedirectUrl()
	{
		assertEquals(REDIRECT_URL, builder.buildRedirectUrl("/Success", "test.com/booking", 1, "redirect.com", "guid",
				"ref", "25/07/2024", "firstn", "lastn", 1));
	}
	
	@Test
	public void testGetSuccessRedirectUrl_SpecialChars()
	{
		assertEquals(SUCCESS_URL_SPECIAL_CHARS, builder.getSuccessRedirectUrl("test.com", "/success", 1, "abc", "guid", "ref",
				"ogref", 0, false, "25/07/2024", "first name", "O' Malley", 2));
	}

	@Test
	public void testBuildRedirectUrl_SpecialChars()
	{
		assertEquals(REDIRECT_URL_SPECIAL_CHARS, builder.buildRedirectUrl("/Success", "test.com/booking", 1, "redirect.com", "guid",
				"ref", "25/07/2024", "first name", "O' Malley", 1));
	}
}