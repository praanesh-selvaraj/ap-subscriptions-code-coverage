package com.kmp.aeroparker.subscription.payments.processor.history;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;


@ExtendWith(MockitoExtension.class)
public class WirecardPaymentHistoryProcessorTest
{
	@Mock
	private PaymentService paymentService;
	
	@InjectMocks
	private WirecardPaymentHistoryProcessor processor;
	
	@Test
	void testLoadHistory()
	{
		List<Payments> paymentList = new ArrayList<>();
		Payments mockPayment = mock(Payments.class);
		paymentList.add(mockPayment);
		Timestamp createdAt = new Timestamp(1000000L);
		
		when(paymentService.fetchRecurringPaymentSentByBookingReference("reference")).thenReturn(paymentList);
		when(mockPayment.getCreated()).thenReturn(createdAt);
		when(mockPayment.getAmount()).thenReturn("10.00");
		
		assertThat(processor.loadHistory(1, 1, "reference")).isNotEmpty();
	}
}
