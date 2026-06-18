package com.kmp.aeroparker.application.engine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Carparks;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionPurchaseRequest
{
	private int productId;
	private int carParkId;
	private String displayName;
	private SubscriptionProduct product;
	private SubscriptionPurchaseAgreement purchaseAgreement;
	private Carparks carPark;
	private LocalDate startDate;
	private LocalDate endDate;
	private String localisedStartDate;
	private String localisedEndDate;
	private SubscriptionPeriodType periodType;
	private SubscriptionMinimumTerm minimumTerm;
	private List<Carparks> subscriptionCarparks = new ArrayList<>();

	public BigDecimal getGrandTotal()
	{
		return purchaseAgreement == null ? BigDecimal.ZERO : purchaseAgreement.getGrandTotal();
	}

	public BigDecimal getOriginalGrandTotal()
	{
		return purchaseAgreement == null ? BigDecimal.ZERO : purchaseAgreement.getOriginalGrandTotal();
	}

	public BigDecimal getOriginalProductPrice()
	{
		return purchaseAgreement == null ? BigDecimal.ZERO : purchaseAgreement.getOriginalProductPrice();
	}

	public BigDecimal getProductPrice()
	{
		return purchaseAgreement == null ? BigDecimal.ZERO : purchaseAgreement.getProductPrice();
	}

	public boolean isRecurringTicket()
	{
		boolean isRecurring = false;
		if (periodType != null)
		{
			isRecurring = SubscriptionPeriodType.RECURRING.equals(periodType);
		}
		return isRecurring;
	}

	public boolean isSeasonTicket()
	{
		boolean isSeason = false;
		if (periodType != null)
		{
			isSeason = SubscriptionPeriodType.FIXED.equals(periodType);
		}
		return isSeason;
	}

	public BigDecimal getBookingFee()
	{
		return purchaseAgreement == null ? BigDecimal.ZERO
				: purchaseAgreement.getBookingFee()
						.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
}