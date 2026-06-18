package com.kmp.aeroparker.application.vies;

import org.springframework.stereotype.Controller;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.application.cognito.api.CognitoApiAuthTokenClient;
import com.kmp.aeroparker.application.model.api.vies.ViesVatValidationClient;
import com.kmp.aeroparker.application.model.api.vies.ViesVatValidationRequestBuilder;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.i18n.stringutil.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Controller
public class ViesVatValidator
{
	private static final String INVALID_VIES_VAT_MESSAGE = "Please enter a valid Company VAT Registration Number";

	private ViesVatValidationRequestBuilder requestBuilder;
	private ViesVatValidationClient client;
	private CognitoApiAuthTokenClient cognitoAuthTokenClient;

	public JsonObject validateCompanyVATRegistrationNumber(String fieldInput, int affiliateId,
			LanguageFieldsList languageFieldsList)
	{
		JsonObject response = new JsonObject();

		if (!StringUtil.isNullOrEmpty(fieldInput))
		{
			boolean isValid = false;
			if (fieldInput.length() > 2 && affiliateId > 0)
			{
				fieldInput = fieldInput.replaceAll("\\s+", "");
				String countryCode = fieldInput.substring(0, 2);
				String vatNumber = fieldInput.substring(2);
				isValid = client.sendViesVatValidationRequest(requestBuilder.buildRequest(countryCode, vatNumber,
						affiliateId, cognitoAuthTokenClient.getAccessToken()));
			}
			else
			{
				log.info("The value in the Company VAT Registration field " + fieldInput
						+ " was invalid, or affiliateId " + affiliateId
						+ " was invalid, and will not be validated by VIES");
			}
			response.addProperty("isValid", isValid);
			if (!isValid)
			{
				response.addProperty("invalidCompanyVatErrMessage",
						languageFieldsList.getTranslation(INVALID_VIES_VAT_MESSAGE));
			}
		}
		log.info("Returning JsonObject as response from ViesVATValidator: {}", response);
		return response;
	}
}
