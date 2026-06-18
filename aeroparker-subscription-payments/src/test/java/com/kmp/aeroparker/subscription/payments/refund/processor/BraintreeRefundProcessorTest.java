package com.kmp.aeroparker.subscription.payments.refund.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.ValidationErrors;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

@ExtendWith(MockitoExtension.class)
class BraintreeRefundProcessorTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private BraintreeObjectFactory braintreeObjectFactory;
	@Mock
	private BraintreeRefundTransactionProcessor refundTransactionProcessor;
	@InjectMocks
	private BraintreeRefundProcessor processor;
	@Mock
	private Payments payment;

	@Test
	void testProcess()
	{
		when(payment.getId()).thenReturn(1);
		when(payment.getTransactionId()).thenReturn("transactionId");
		when(payment.getReference()).thenReturn("reference");
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(BraintreeCredentials.class));
		BraintreeGateway gateway = mock(BraintreeGateway.class);
		when(braintreeObjectFactory.createGateway(any())).thenReturn(gateway);
		Result<Transaction> result = new Result<Transaction>();
		when(refundTransactionProcessor.processRefund(any(), anyString(), any(BigDecimal.class))).thenReturn(result);
		when(paymentService.insertRefund(any())).thenReturn(true);
		when(paymentService.savePayment(any())).thenReturn(true);
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isTrue();
		verify(paymentService).insertRefund(any());
		verify(paymentService).savePayment(any());
		verify(braintreeObjectFactory).createGateway(any());
		verify(refundTransactionProcessor).processRefund(any(), anyString(), any(BigDecimal.class));
		verifyNoMoreInteractions(paymentService, braintreeObjectFactory, refundTransactionProcessor);
	}

	@Test
	void testProcess_Amount_Negative()
	{
		assertThat(processor.process(1, payment, new BigDecimal(-10), "Europe/London")).isFalse();
		verifyNoInteractions(paymentService, braintreeObjectFactory, refundTransactionProcessor);
	}

	@Test
	void testProcess_Payment_Null()
	{
		assertThat(processor.process(1, null, BigDecimal.TEN, "Europe/London")).isFalse();
		verifyNoInteractions(paymentService, braintreeObjectFactory, refundTransactionProcessor);
	}

	@Test
	void testProcess_Refund_Not_Saved()
	{
		when(payment.getId()).thenReturn(1);
		when(payment.getTransactionId()).thenReturn("transactionId");
		when(payment.getReference()).thenReturn("reference");
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(BraintreeCredentials.class));
		BraintreeGateway gateway = mock(BraintreeGateway.class);
		when(braintreeObjectFactory.createGateway(any())).thenReturn(gateway);
		Result<Transaction> result = new Result<Transaction>();
		when(refundTransactionProcessor.processRefund(any(), anyString(), any(BigDecimal.class))).thenReturn(result);
		when(paymentService.insertRefund(any())).thenReturn(false);
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isFalse();
		verify(paymentService).insertRefund(any());
		verify(paymentService, times(0)).savePayment(any());
		verify(braintreeObjectFactory).createGateway(any());
		verify(refundTransactionProcessor).processRefund(any(), anyString(), any(BigDecimal.class));
		verifyNoMoreInteractions(paymentService, braintreeObjectFactory, refundTransactionProcessor);
	}

	@Test
	void testProces_Refund_Voided_And_SuccessFul()
	{
		when(payment.getId()).thenReturn(1);
		when(payment.getTransactionId()).thenReturn("transactionId");
		when(payment.getReference()).thenReturn("reference");
		when(payment.getAmount()).thenReturn(BigDecimal.TEN.toPlainString());
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(BraintreeCredentials.class));
		BraintreeGateway gateway = mock(BraintreeGateway.class);
		when(braintreeObjectFactory.createGateway(any())).thenReturn(gateway);
		Result<Transaction> resultFailed = new Result(new ValidationErrors());
		when(refundTransactionProcessor.processRefund(any(), anyString(), any(BigDecimal.class))).thenReturn(resultFailed);
		Result<Transaction> resultPassed = new Result<Transaction>();
		when(refundTransactionProcessor.processFailedRefund(any(), any(), any())).thenReturn(resultPassed);
		when(paymentService.insertRefund(any())).thenReturn(true);
		when(paymentService.savePayment(any())).thenReturn(true);
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isTrue();
		verify(paymentService).insertRefund(any());
		verify(paymentService).savePayment(any());
		verify(braintreeObjectFactory).createGateway(any());
		verify(refundTransactionProcessor).processRefund(any(), anyString(), any(BigDecimal.class));
		verifyNoMoreInteractions(paymentService, braintreeObjectFactory, refundTransactionProcessor);
	}

	@Test
	void testProces_Refund_Voided_And_Failed()
	{
		when(payment.getTransactionId()).thenReturn("transactionId");
		when(payment.getReference()).thenReturn("reference");
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(BraintreeCredentials.class));
		BraintreeGateway gateway = mock(BraintreeGateway.class);
		when(braintreeObjectFactory.createGateway(any())).thenReturn(gateway);
		Result<Transaction> resultFailed = new Result<Transaction>(new ValidationErrors());
		when(refundTransactionProcessor.processRefund(any(), anyString(), any(BigDecimal.class))).thenReturn(resultFailed);
		when(refundTransactionProcessor.processFailedRefund(any(), any(), any())).thenReturn(resultFailed);
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isFalse();
		verify(paymentService, times(0)).insertRefund(any());
		verify(paymentService, times(0)).savePayment(any());
		verify(braintreeObjectFactory).createGateway(any());
		verify(refundTransactionProcessor).processRefund(any(), anyString(), any(BigDecimal.class));
		verifyNoMoreInteractions(paymentService, braintreeObjectFactory, refundTransactionProcessor);
	}

	@Test
	void testProcess_Gateway_Null()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(BraintreeCredentials.class));
		when(braintreeObjectFactory.createGateway(any())).thenReturn(null);
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isFalse();
		verify(braintreeObjectFactory).createGateway(any());
		verifyNoMoreInteractions(braintreeObjectFactory);
	}

	@Test
	void testProcess_Credentials_Null()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(null);
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isFalse();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verifyNoInteractions(refundTransactionProcessor, braintreeObjectFactory);
	}

	@Test
	void testProcess_Amount_Zero()
	{
		assertThat(processor.process(1, payment, BigDecimal.ZERO, "Europe/London")).isTrue();
		verifyNoInteractions(paymentService, refundTransactionProcessor, braintreeObjectFactory);
	}
}