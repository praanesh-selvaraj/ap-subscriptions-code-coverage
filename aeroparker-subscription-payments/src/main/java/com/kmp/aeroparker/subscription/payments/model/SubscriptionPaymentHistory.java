package com.kmp.aeroparker.subscription.payments.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

@Component
public class SubscriptionPaymentHistory extends TreeMap<LocalDateTime, BigDecimal>
{
	private static final long serialVersionUID = 1L;

	private BigDecimal totalPaymentAmount = BigDecimal.ZERO;
	
	public SubscriptionPaymentHistory()
	{
		super(Collections.reverseOrder());
	}
	
	public BigDecimal getTotalPaymentAmount()
	{
		if (totalPaymentAmount.equals(BigDecimal.ZERO))
		{
			for (Entry<LocalDateTime, BigDecimal> payment : this.entrySet())
			{
				totalPaymentAmount = totalPaymentAmount.add(payment.getValue());
			}
		}
		return totalPaymentAmount;
	}
}
