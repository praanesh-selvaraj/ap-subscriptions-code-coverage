package com.kmp.aeroparker.application.model.servlets;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kmp.aeroparker.application.utils.StringUtil;
import com.kmp.aeroparker.application.vies.ViesVatValidator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(value = "/subscriptions/{affCode}")
public class VatValidatorAjax
{
	private static final String VIES_VALIDATION_CMD = "validateCompanyRegNumberVies";
	private final LanguageFieldsList languageFieldsList;

	private ViesVatValidator validator;

	@PostMapping("/VatValidatorAjax")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> validateVat(@RequestParam("cmd") String cmd,
			@RequestParam("vatNumber") String vatNumber,
			@RequestParam(value = "affiliateId", required = false, defaultValue = "0") int affiliateId)
	{
		try
		{
			if (StringUtil.isEqual(VIES_VALIDATION_CMD, cmd))
			{
				JsonObject gsonObject =
						validator.validateCompanyVATRegistrationNumber(vatNumber, affiliateId, languageFieldsList);
				Map<String, Object> response = convertJsonObjectToMap(gsonObject);
				return ResponseEntity.ok(response);
			}
			else
			{
				log.info("Unknown command: {}", cmd);
				return ResponseEntity.badRequest()
						.build();
			}
		}
		catch (Exception e)
		{
			log.error("Error processing VAT validation request: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
	}

	private Map<String, Object> convertJsonObjectToMap(JsonObject gsonObject)
	{
		Map<String, Object> map = new HashMap<>();

		for (Map.Entry<String, JsonElement> entry : gsonObject.entrySet())
		{
			JsonElement value = entry.getValue();

			if (value.isJsonPrimitive())
			{
				JsonPrimitive primitive = value.getAsJsonPrimitive();
				if (primitive.isBoolean())
				{
					map.put(entry.getKey(), primitive.getAsBoolean());
				}
				else if (primitive.isNumber())
				{
					map.put(entry.getKey(), primitive.getAsNumber());
				}
				else if (primitive.isString())
				{
					map.put(entry.getKey(), primitive.getAsString());
				}
			}
			else if (value.isJsonNull())
			{
				map.put(entry.getKey(), null);
			}
			else if (value.isJsonObject())
			{
				map.put(entry.getKey(), convertJsonObjectToMap(value.getAsJsonObject()));
			}
			else if (value.isJsonArray())
			{
				map.put(entry.getKey(), value.getAsJsonArray());
			}
		}

		return map;
	}
}
