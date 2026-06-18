package com.kmp.aeroparker.application.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kmp.aeroparker.application.model.enums.BuilderType;

@ExtendWith(MockitoExtension.class)
public class PurchaseDataBuilderTest
{
	@InjectMocks
	private PurchaseDataBuilder purchaseDataBuilder;
	
	private static final int TEST_PRODUCT_ID = 1;
	private static final LocalDate TEST_DATE = LocalDate.of(2020, 10, 20);
	private static final String TEST_DATE_FORMAT = "dd/MM/yyyy";
	
	@Test
	public void testBuildPurchaseDataJson()
	{
		JsonArray purchaseData = purchaseDataBuilder.buildPurchaseDataJson(TEST_PRODUCT_ID, TEST_DATE, TEST_DATE_FORMAT, true);
		
		JsonObject dataObject = (JsonObject) purchaseData.get(0);
		assertEquals(TEST_PRODUCT_ID, dataObject.get("productId").getAsInt());
		assertEquals("20/10/2020", dataObject.get("startDate").getAsString());
		assertTrue(dataObject.get("isFromThirdParty").getAsBoolean());
	}
	
	@Test
	public void testGetType()
	{
		assertEquals(BuilderType.PURCHASE_DATA, purchaseDataBuilder.getType());
	}
}
