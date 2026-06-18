package com.kmp.aeroparker.application.model.api.vies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.kmp.aeroparker.application.db.service.GlobalPropertiesService;

@RunWith(MockitoJUnitRunner.class)
public class ViesVatValidationRequestBuilderTest
{
	private static final String VIES_VALIDATION_API_ENDPOINT = "aeroparker.api.viesvalidation.endpoint";

	@InjectMocks
	private ViesVatValidationRequestBuilder requestBuilder;

	@Mock
	private GlobalPropertiesService globalProperties;

	@Test
	public void testBuildRequest()
	{
		when(globalProperties.fetchProperty(VIES_VALIDATION_API_ENDPOINT)).thenReturn("http://example.com");
		HttpRequestBase request = requestBuilder.buildRequest("countryCode", "vatNumber", 1, "accessToken");

		assertThat(request).isNotNull()
				.isInstanceOf(HttpRequestBase.class);
	}
}