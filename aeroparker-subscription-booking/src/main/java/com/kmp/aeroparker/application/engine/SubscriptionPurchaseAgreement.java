package com.kmp.aeroparker.application.engine;

import com.kmp.aeroparker.application.availability.PriceDetails;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class SubscriptionPurchaseAgreement
{
	private PriceDetails priceDetails;
	private BigDecimal vatRate;
	private BigDecimal stateTax;
	private BigDecimal bookingFee;
	private BigDecimal recurringProductPrice = BigDecimal.ZERO;
	private BigDecimal promoDiscount = BigDecimal.ZERO;
	private String promoCode;

	public BigDecimal getOriginalProductPrice()
	{
		BigDecimal price = getPriceDetails().getPrice();
		return price.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : price;
	}

	public BigDecimal getProductPrice()
	{
		BigDecimal price = getPriceDetails().getPrice();

		// Apply promo discount if present
		if (promoDiscount != null && promoDiscount.compareTo(BigDecimal.ZERO) > 0)
		{
			price = price.subtract(promoDiscount);
		}

		return price.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : price;
	}

	public BigDecimal getGrandTotal()
	{
		BigDecimal total = getPriceDetails().getGrandTotal();

		total = total.subtract(recurringProductPrice.setScale(2, BigDecimal.ROUND_HALF_UP));

		if (bookingFee != null && BigDecimal.ZERO.compareTo(bookingFee) < 0)
		{
			total = total.add(bookingFee.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		// Apply promo discount if present
		if (promoDiscount != null && promoDiscount.compareTo(BigDecimal.ZERO) > 0)
		{
			total = total.subtract(promoDiscount);
		}

		return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getOriginalGrandTotal()
	{
		BigDecimal total = getPriceDetails().getPrice()
				.subtract(recurringProductPrice);

		if (bookingFee != null && BigDecimal.ZERO.compareTo(bookingFee) == -1)
		{
			total = total.add(bookingFee.setScale(2, BigDecimal.ROUND_HALF_UP));
		}
		return total;
	}

	public BigDecimal getVatAmount()
	{
		BigDecimal taxDivisor = (BigDecimal.valueOf(100)
				.add(vatRate)
				.add(stateTax)).divide(BigDecimal.valueOf(100));
		BigDecimal grandTotalBeforeTax = getGrandTotal().divide(taxDivisor, 2, BigDecimal.ROUND_HALF_UP);
		return getGrandTotal().subtract(grandTotalBeforeTax);
	}

	public void setRecurringProductPrice(BigDecimal recurringProductPrice)
	{
		this.recurringProductPrice = recurringProductPrice.setScale(2, RoundingMode.HALF_UP);
	}
}