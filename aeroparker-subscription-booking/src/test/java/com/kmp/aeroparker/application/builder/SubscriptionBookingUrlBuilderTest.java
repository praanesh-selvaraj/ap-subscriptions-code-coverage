package com.kmp.aeroparker.application.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SubscriptionBookingUrlBuilderTest
{
	@Test
    public void testExtractBaseUrl_withHttpAndPort() {
        String result = SubscriptionBookingUrlBuilder.extractBaseUrl("http://example.com:8080/path/to/resource");
        assertEquals("http://example.com:8080", result);
    }

    @Test
    public void testExtractBaseUrl_withHttpsAndPort() {
        String result = SubscriptionBookingUrlBuilder.extractBaseUrl("https://api.example.com:443/users");
        assertEquals("https://api.example.com:443", result);
    }

    @Test
    public void testExtractBaseUrl_withoutPort() {
        String result = SubscriptionBookingUrlBuilder.extractBaseUrl("https://www.example.com/some/path");
        assertEquals("https://www.example.com", result);
    }

    @Test
    public void testExtractBaseUrl_withDefaultHttpPort() {
        String result = SubscriptionBookingUrlBuilder.extractBaseUrl("http://example.com/test");
        assertEquals("http://example.com", result);
    }

    @Test
    public void testExtractBaseUrl_withInvalidUrl() {
        String result = SubscriptionBookingUrlBuilder.extractBaseUrl("invalid-url");
        assertNull(result);
    }

    @Test
    public void testExtractBaseUrl_withNullUrl() {
        String result = SubscriptionBookingUrlBuilder.extractBaseUrl(null);
        assertNull(result);
    }

    @Test
    public void testExtractBaseUrl_withEmptyUrl() {
        String result = SubscriptionBookingUrlBuilder.extractBaseUrl("");
        assertNull(result);
    }
}
