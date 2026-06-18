package com.kmp.aeroparker.subscription.payments.processor.history;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionGateway;
import com.braintreegateway.Transaction;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;


@ExtendWith(MockitoExtension.class)
class BraintreePaymentHistoryProcessorTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private BraintreeObjectFactory braintreeObjectFactory;
	
	@InjectMocks
	private BraintreePaymentHistoryProcessor processor;
	
	@Test
	void testLoadHistory()
	{
		BraintreeGateway mockGateway = mock(BraintreeGateway.class);
		SubscriptionGateway mockSubGateway = mock(SubscriptionGateway.class);
		Subscription mockSubscription = mock(Subscription.class);
		Payments mockPayment = mock(Payments.class);
		Map<String, String> customValueMap = new HashMap<>();
		customValueMap.put("braintreeSubId", "1");
		List<Transaction> transactionList = new ArrayList<>();
		Transaction mockTransaction = mock(Transaction.class);
		transactionList.add(mockTransaction);
		Calendar createdAtCalendar = Calendar.getInstance();
		
		when(braintreeObjectFactory.createGateway(any())).thenReturn(mockGateway);
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(mockPayment);
		when(mockPayment.getId()).thenReturn(1);
		when(paymentService.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(customValueMap);
		when(mockGateway.subscription()).thenReturn(mockSubGateway);
		when(mockSubGateway.find(anyString())).thenReturn(mockSubscription);
		when(mockSubscription.getTransactions()).thenReturn(transactionList);
		when(mockTransaction.getCreatedAt()).thenReturn(createdAtCalendar);
		when(mockTransaction.getAmount()).thenReturn(mock(BigDecimal.class));
		
		assertThat(processor.loadHistory(1, 1, "Reference")).isNotEmpty();
		verify(paymentService).fetchPaymentCustomValueByPaymentId(anyInt());
		verify(mockSubGateway).find(anyString());
		verify(mockSubscription).getTransactions();
		verify(mockTransaction).getCreatedAt();
		verify(mockTransaction).getAmount();
	}
	
	@Test
	void testLoadHistory_No_SubscriptionId()
	{
		BraintreeGateway mockGateway = mock(BraintreeGateway.class);
		Payments mockPayment = mock(Payments.class);
		Map<String, String> customValueMap = new HashMap<>();
		
		when(braintreeObjectFactory.createGateway(any())).thenReturn(mockGateway);
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(mockPayment);
		when(mockPayment.getId()).thenReturn(1);
		when(paymentService.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(customValueMap);
		
		assertThat(processor.loadHistory(1, 1, "Reference")).isEmpty();
		verify(paymentService).fetchPaymentCustomValueByPaymentId(anyInt());
	}
	
	@Test
	void testLoadHistory_No_Subscription()
	{
		BraintreeGateway mockGateway = mock(BraintreeGateway.class);
		SubscriptionGateway mockSubGateway = mock(SubscriptionGateway.class);
		Payments mockPayment = mock(Payments.class);
		Map<String, String> customValueMap = new HashMap<>();
		customValueMap.put("braintreeSubId", "1");
		
		when(braintreeObjectFactory.createGateway(any())).thenReturn(mockGateway);
		when(paymentService.fetchPaymentByReference(anyString())).thenReturn(mockPayment);
		when(mockPayment.getId()).thenReturn(1);
		when(paymentService.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(customValueMap);
		when(mockGateway.subscription()).thenReturn(mockSubGateway);
		when(mockSubGateway.find(anyString())).thenReturn(null);
		
		assertThat(processor.loadHistory(1, 1, "Reference")).isEmpty();
		verify(paymentService).fetchPaymentCustomValueByPaymentId(anyInt());
		verify(mockSubGateway).find(anyString());
	}
}
