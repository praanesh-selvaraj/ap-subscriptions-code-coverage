package com.kmp.aeroparker.application.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public final class CurrencyConverter
{
	private static final Map<String, BigDecimal> RATES = new HashMap<>();

	static
	{
		RATES.put("EUR_GBP", new BigDecimal("0.86"));
		RATES.put("GBP_EUR", new BigDecimal("1.16"));
		RATES.put("EUR_USD", new BigDecimal("1.08"));
		RATES.put("USD_EUR", new BigDecimal("0.93"));
	}

	private CurrencyConverter()
	{
	}

	public static BigDecimal convert(final BigDecimal amount, final String fromCurrency, final String toCurrency)
	{
		if (amount == null || fromCurrency == null || toCurrency == null)
		{
			return BigDecimal.ZERO;
		}
		if (fromCurrency.equalsIgnoreCase(toCurrency))
		{
			return amount.setScale(2, RoundingMode.HALF_UP);
		}
		String key = fromCurrency.toUpperCase() + "_" + toCurrency.toUpperCase();
		BigDecimal rate = RATES.get(key);
		if (rate == null)
		{
			throw new IllegalArgumentException("No conversion rate found for: " + key);
		}
		return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
	}

	public static boolean isSupported(final String fromCurrency, final String toCurrency)
	{
		if (fromCurrency == null || toCurrency == null)
		{
			return false;
		}
		String key = fromCurrency.toUpperCase() + "_" + toCurrency.toUpperCase();
		return RATES.containsKey(key);
	}
}
