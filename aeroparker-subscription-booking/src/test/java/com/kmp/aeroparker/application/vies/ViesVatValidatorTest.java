package com.kmp.aeroparker.application.vies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.application.cognito.api.CognitoApiAuthTokenClient;
import com.kmp.aeroparker.application.model.api.vies.ViesVatValidationClient;
import com.kmp.aeroparker.application.model.api.vies.ViesVatValidationRequestBuilder;
import com.kmp.aeroparker.i18n.LanguageFieldsList;

@RunWith(MockitoJUnitRunner.class)
public class ViesVatValidatorTest
{
	@Mock
	private ViesVatValidationRequestBuilder requestBuilder;
	@Mock
	private ViesVatValidationClient client;
	@Mock
	private CognitoApiAuthTokenClient cognitoClient;
	@InjectMocks
	private ViesVatValidator validator;

	@Test
	public void testValidateCompanyVATRegistrationNumber_InvalidParameters()
	{
		JsonObject responseShortInput =
				validator.validateCompanyVATRegistrationNumber("a", 1, mock(LanguageFieldsList.class));
		JsonObject responseInvalidAffiliateId =
				validator.validateCompanyVATRegistrationNumber("fr13513513", 0, mock(LanguageFieldsList.class));

		assertFalse(responseShortInput.get("isValid")
				.getAsBoolean());
		assertFalse(responseInvalidAffiliateId.get("isValid")
				.getAsBoolean());
		verifyNoInteractions(requestBuilder);
		verifyNoInteractions(client);
	}

	@Test
	public void testValidateCompanyVATRegistrationNumber_InvalidResponse()
	{
		LanguageFieldsList languageFieldsList = mock(LanguageFieldsList.class);
		when(cognitoClient.getAccessToken()).thenReturn("accessToken");
		when(requestBuilder.buildRequest(anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(mock(HttpRequestBase.class));
		when(client.sendViesVatValidationRequest(any())).thenReturn(false);
		when(languageFieldsList.getTranslation(anyString()))
				.thenReturn("Please enter a valid Company VAT Registration Number");

		JsonObject response = validator.validateCompanyVATRegistrationNumber("fr13515135", 1, languageFieldsList);

		assertFalse(response.get("isValid")
				.getAsBoolean());
		assertEquals(response.get("invalidCompanyVatErrMessage")
				.getAsString(), "Please enter a valid Company VAT Registration Number");
		verify(requestBuilder).buildRequest("fr", "13515135", 1, "accessToken");
		verify(client).sendViesVatValidationRequest(any());
	}

	@Test
	public void testValidateCompanyVATRegistrationNumber_ValidResponse()
	{
		when(cognitoClient.getAccessToken()).thenReturn("accessToken");
		when(requestBuilder.buildRequest(anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(mock(HttpRequestBase.class));
		when(client.sendViesVatValidationRequest(any())).thenReturn(true);

		JsonObject response =
				validator.validateCompanyVATRegistrationNumber("fr13515135", 1, mock(LanguageFieldsList.class));

		assertTrue(response.get("isValid")
				.getAsBoolean());
		verify(requestBuilder).buildRequest("fr", "13515135", 1, "accessToken");
		verify(client).sendViesVatValidationRequest(any());
	}
}
