package com.kmp.aeroparker.application.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class VatFormatter
{
	private VatFormatter()
	{
	}

	public static BigDecimal calculateVat(final BigDecimal amount, final BigDecimal vatRate)
	{
		if (amount == null || vatRate == null)
		{
			return BigDecimal.ZERO;
		}
		return amount.multiply(vatRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	}

	public static BigDecimal calculateGross(final BigDecimal net, final BigDecimal vatRate)
	{
		if (net == null || vatRate == null)
		{
			return BigDecimal.ZERO;
		}
		return net.add(calculateVat(net, vatRate));
	}

	public static BigDecimal extractNet(final BigDecimal gross, final BigDecimal vatRate)
	{
		if (gross == null || vatRate == null)
		{
			return BigDecimal.ZERO;
		}
		BigDecimal divisor = BigDecimal.ONE.add(vatRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
		return gross.divide(divisor, 2, RoundingMode.HALF_UP);
	}

	public static String format(final BigDecimal amount)
	{
		if (amount == null)
		{
			return "0.00";
		}
		return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}
}
