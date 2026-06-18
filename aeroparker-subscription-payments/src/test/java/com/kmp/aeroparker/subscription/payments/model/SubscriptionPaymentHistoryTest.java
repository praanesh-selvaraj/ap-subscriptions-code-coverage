package com.kmp.aeroparker.subscription.payments.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionPaymentHistoryTest
{
	@Test
	void testSubscriptionPaymenthistory_ReverseOrder()
	{
		SubscriptionPaymentHistory paymentHistory = new SubscriptionPaymentHistory();
		paymentHistory.put(LocalDateTime.of(2021, 01, 01, 0, 0), BigDecimal.TEN);
		paymentHistory.put(LocalDateTime.of(2021, 02, 01, 0, 0), BigDecimal.ONE);
		paymentHistory.put(LocalDateTime.of(2021, 03, 01, 0, 0), BigDecimal.ZERO);
		
		assertEquals(paymentHistory.pollFirstEntry().getKey(), LocalDateTime.of(2021, 03, 01, 0, 0));
		assertEquals(paymentHistory.pollFirstEntry().getKey(), LocalDateTime.of(2021, 02, 01, 0, 0));
		assertEquals(paymentHistory.pollFirstEntry().getKey(), LocalDateTime.of(2021, 01, 01, 0, 0));
	}
	
	@Test
	void testSubscriptionPaymenthistory_GrandTotal()
	{
		SubscriptionPaymentHistory paymentHistory = new SubscriptionPaymentHistory();
		paymentHistory.put(LocalDateTime.of(2021, 01, 01, 0, 0), BigDecimal.TEN);
		paymentHistory.put(LocalDateTime.of(2021, 02, 01, 0, 0), BigDecimal.TEN);
		paymentHistory.put(LocalDateTime.of(2021, 03, 01, 0, 0), BigDecimal.TEN);
		
		// Grand Total field is zero here. So needs to do calculation.
		assertEquals(paymentHistory.getTotalPaymentAmount().intValue(), BigDecimal.valueOf(30).intValue());
		// Grand Total is saved to the field now, so can just return.
		assertEquals(paymentHistory.getTotalPaymentAmount().intValue(), BigDecimal.valueOf(30).intValue());
	}
}
