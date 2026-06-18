package com.kmp.aeroparker.application.builder;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.model.interfaces.IBuilder;
import com.kmp.utils.DateUtil;

@Component
public class PurchaseDataBuilder implements IBuilder
{
	public JsonArray buildPurchaseDataJson(int productId, LocalDate startDate, String dateFormat, boolean isFromThirdParty)
	{
		JsonArray jsonArray = new JsonArray();
		
		JsonObject json = new JsonObject();
		json.addProperty("productId", productId);
		json.addProperty("startDate", DateUtil.format(startDate, dateFormat));
		json.addProperty("isFromThirdParty", isFromThirdParty);
		
		jsonArray.add(json);
		
		return jsonArray;
	}

	@Override
	public BuilderType getType()
	{
		return BuilderType.PURCHASE_DATA;
	}
}
