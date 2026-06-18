package com.kmp.aeroparker.application.availability;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.kmp.aeroparker.subscription.string.utils.StringUtil;

// This can be extended to calculate commission and tax
// we can do so much stuff here
// Add whatever you want, obviously price related
public class PriceDetails
{
	private BigDecimal price;
	private String fractionPart;
	private String integerPart;
	private String priceIncludePennyPlaceholdersHtml = "";
	private BigDecimal discountedAmount = BigDecimal.ZERO;

	public BigDecimal getPrice()
	{
		return price.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String getFractionPart()
	{
		if (StringUtil.isEmpty(fractionPart))
		{
			String strValue = getPrice().toPlainString();
			int indexOfDecimalPoint = strValue.indexOf(".");
			fractionPart = strValue.substring(indexOfDecimalPoint);
		}
		return fractionPart;
	}

	public String getIntegerPart()
	{
		if (StringUtil.isEmpty(integerPart))
		{
			String strValue = getPrice().toPlainString();
			int indexOfDecimalPoint = strValue.indexOf(".");
			integerPart = strValue.substring(0, indexOfDecimalPoint);
		}
		return integerPart;
	}

	public void setPrice(final BigDecimal price)
	{
		this.price = price;
	}

	public void setFractionPart(final String fractionPart)
	{
		this.fractionPart = fractionPart;
	}

	public void setIntegerPart(final String integerPart)
	{
		this.integerPart = integerPart;
	}

	public String getPriceIncludePennyPlaceholdersHtml()
	{
		return priceIncludePennyPlaceholdersHtml;
	}

	public void setPriceIncludePennyPlaceholdersHtml(final String priceIncludePennyPlaceholdersHtml)
	{
		this.priceIncludePennyPlaceholdersHtml = priceIncludePennyPlaceholdersHtml;
	}

	public BigDecimal getDiscountedAmount()
	{
		return discountedAmount;
	}

	public void setDiscountedAmount(final BigDecimal discountedAmount)
	{
		this.discountedAmount = discountedAmount.setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal getGrandTotal()
	{
		return getPrice().subtract(getDiscountedAmount());
	}
}