package com.kmp.aeroparker.application.engine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kmp.aeroparker.l10n.Localise;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Basket
{
	private PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
	private String priceIncludePennyPlaceholdersHtml = "";
	private String originalPriceIncludePennyPlaceholdersHtml = "";
	private BigDecimal promoDiscount = BigDecimal.ZERO;
	private String promoCode;

	public BigDecimal getOriginalGrandTotal()
	{
		BigDecimal totalPrice = BigDecimal.ZERO;

		for (SubscriptionPurchaseRequest purchaseRequest : getPurchaseRequestList())
		{
			totalPrice = totalPrice.add(purchaseRequest.getOriginalGrandTotal());
		}


		if (totalPrice.compareTo(BigDecimal.ZERO) < 0)
		{
			totalPrice = BigDecimal.ZERO;
		}
		return totalPrice;
	}
	public BigDecimal getGrandTotal()
	{
		BigDecimal totalPrice = BigDecimal.ZERO;

		for (SubscriptionPurchaseRequest purchaseRequest : getPurchaseRequestList())
		{
			totalPrice = totalPrice.add(purchaseRequest.getGrandTotal());
		}

		// Check if the current total is less than 0 if it is set it to 0.
		if (totalPrice.compareTo(BigDecimal.ZERO) < 0)
		{
			totalPrice = BigDecimal.ZERO;
		}
		return totalPrice;
	}

	public BigDecimal getGrandTotalWithoutBookingFee()
	{
		BigDecimal totalPrice = BigDecimal.ZERO;

		for (SubscriptionPurchaseRequest purchaseRequest : getPurchaseRequestList())
		{
			totalPrice = totalPrice.add(purchaseRequest.getProductPrice());
		}

		// Check if the current total is less than 0 if it is set it to 0.
		if (totalPrice.compareTo(BigDecimal.ZERO) < 0)
		{
			totalPrice = BigDecimal.ZERO;
		}
		return totalPrice;
	}

	public BigDecimal getTotalBookingFee()
	{
		BigDecimal totalBookingFee = BigDecimal.ZERO;

		for (SubscriptionPurchaseRequest purchaseRequest : getPurchaseRequestList())
		{
			totalBookingFee = totalBookingFee.add(purchaseRequest.getBookingFee());
		}
		return totalBookingFee;
	}

	public List<Integer> getAllProductIds()
	{
		List<Integer> ids = new ArrayList<>();
		ids = getPurchaseRequestList().stream()
				.map(x -> x.getProductId())
				.collect(Collectors.toList());
		return ids;
	}

	public BigDecimal getGrandTotalVatAmount()
	{
		BigDecimal totalVatAmount = BigDecimal.ZERO;

		for (SubscriptionPurchaseRequest purchaseRequest : getPurchaseRequestList())
		{
			totalVatAmount = totalVatAmount.add(purchaseRequest.getPurchaseAgreement()
					.getVatAmount());
		}
		return totalVatAmount;
	}

	public boolean isRecurring()
	{
		return getPurchaseRequestList().stream()
				.filter(x -> x.isRecurringTicket())
				.findAny()
				.isPresent();
	}

	public void initialize(final Localise localise)
	{
		// Store original price HTML (without discount applied)
		originalPriceIncludePennyPlaceholdersHtml = localise.priceIncludePennyPlaceholders(getOriginalGrandTotal().floatValue(), true)
				.replace("{pennies}", "<span class='booking-summary__item__val--total__pence'>")
				.replace("{/pennies}", "</span>");

		// Store current price HTML (with promo discount applied at purchase agreement level if present)
		priceIncludePennyPlaceholdersHtml = localise.priceIncludePennyPlaceholders(getGrandTotal().floatValue(), true)
				.replace("{pennies}", "<span class='booking-summary__item__val--total__pence'>")
				.replace("{/pennies}", "</span>");
	}
}