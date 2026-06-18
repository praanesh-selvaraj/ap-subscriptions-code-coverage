package com.kmp.aeroparker.application.availability;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class PriceDetailsTest
{
	private PriceDetails priceDetails = new PriceDetails();

	@Test
	void testGetPrice()
	{
		priceDetails.setPrice(BigDecimal.TEN);
		assertThat(priceDetails.getPrice()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_HALF_UP));
	}

	@Test
	void testGetFractionPart()
	{
		priceDetails.setPrice(BigDecimal.TEN);
		assertThat(priceDetails.getFractionPart()).isEqualTo(".00");
	}

	@Test
	void testGetFractionPart_Empty()
	{
		priceDetails.setPrice(BigDecimal.TEN);
		priceDetails.setFractionPart(".00");
		assertThat(priceDetails.getFractionPart()).isEqualTo(".00");
	}

	@Test
	void testGetIntegerPart()
	{
		priceDetails.setPrice(BigDecimal.TEN);
		assertThat(priceDetails.getIntegerPart()).isEqualTo("10");
	}

	@Test
	void testGetIntegerPart_Empty()
	{
		priceDetails.setPrice(BigDecimal.TEN);
		priceDetails.setIntegerPart("10");
		assertThat(priceDetails.getIntegerPart()).isEqualTo("10");
	}
}