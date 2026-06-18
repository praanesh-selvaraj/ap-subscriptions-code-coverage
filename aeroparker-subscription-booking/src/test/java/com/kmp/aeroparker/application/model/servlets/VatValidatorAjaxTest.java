package com.kmp.aeroparker.application.model.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.application.vies.ViesVatValidator;
import com.kmp.aeroparker.i18n.LanguageFieldsList;

@RunWith(MockitoJUnitRunner.class)
public class VatValidatorAjaxTest
{
	@Mock
	private ViesVatValidator validator;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@InjectMocks
	private VatValidatorAjax validatorController;
	
	String cmd = "validateCompanyRegNumberVies";
	String vatNumber = "regNumber";
	int affiliateId = 1;

	@Test
	public void testValidateVat_ValidCmd()
	{
		JsonObject jsonResponse = new JsonObject();
		jsonResponse.addProperty("name", "value");

		when(validator.validateCompanyVATRegistrationNumber(anyString(), anyInt(), any())).thenReturn(jsonResponse);

		ResponseEntity<Map<String, Object>> response = validatorController.validateVat(cmd, vatNumber, affiliateId);

		Map<String, Object> responseBody = response.getBody();
		assertNotNull(responseBody);
		assertEquals("value", responseBody.get("name"));

		verify(validator).validateCompanyVATRegistrationNumber("regNumber", 1, languageFieldsList);
	}

	@Test
	public void testValidateVat_InvalidCmd()
	{
		String invalidCmd = "invalidCmd";
		ResponseEntity<Map<String, Object>> response = validatorController.validateVat(invalidCmd, vatNumber, affiliateId);

		assertNull(response.getBody());
		verifyNoInteractions(validator);
	}

	@Test
	public void testValidateVat_Exception()
	{
		when(validator.validateCompanyVATRegistrationNumber(anyString(), anyInt(), any()))
				.thenThrow(new RuntimeException("Test exception"));

		ResponseEntity<Map<String, Object>> response = validatorController.validateVat(cmd, vatNumber, affiliateId);

		assertNull(response.getBody());

		verify(validator).validateCompanyVATRegistrationNumber("regNumber", 1, languageFieldsList);
	}
}
