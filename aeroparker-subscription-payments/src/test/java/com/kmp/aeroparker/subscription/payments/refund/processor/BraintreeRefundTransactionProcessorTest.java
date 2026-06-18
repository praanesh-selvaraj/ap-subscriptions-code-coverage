package com.kmp.aeroparker.subscription.payments.refund.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionGateway;
import com.braintreegateway.ValidationError;
import com.braintreegateway.ValidationErrorCode;
import com.braintreegateway.ValidationErrors;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

@ExtendWith(MockitoExtension.class)
class BraintreeRefundTransactionProcessorTest
{
	@InjectMocks
	private BraintreeRefundTransactionProcessor braintreeRefundTransactionProcessor;
	@Mock
	private Result<Transaction> result;
	@Mock
	private Payments payment;
	@Mock
	private BraintreeGateway gateway;

	@Test
	void testProcessFailedRefund()
	{
		when(payment.getReference()).thenReturn("reference");
		when(payment.getTransactionId()).thenReturn("transactionId");
		ValidationErrors errors = mock(ValidationErrors.class);
		when(result.getErrors()).thenReturn(errors);
		ValidationError error = mock(ValidationError.class);
		when(error.getCode()).thenReturn(ValidationErrorCode.TRANSACTION_CANNOT_REFUND_UNLESS_SETTLED);
		List<ValidationError> validationErrors = new ArrayList<>();
		validationErrors.add(error);
		when(errors.getAllDeepValidationErrors()).thenReturn(validationErrors);
		TransactionGateway transactionGateway = mock(TransactionGateway.class);
		when(gateway.transaction()).thenReturn(transactionGateway);
		Result<Transaction> resultVoid = new Result<>();
		when(transactionGateway.voidTransaction(anyString())).thenReturn(resultVoid);
		assertThat(braintreeRefundTransactionProcessor.processFailedRefund(result, payment, gateway)).isNotNull()
				.hasFieldOrPropertyWithValue("success", true);
	}

	@Test
	void testProcessFailedRefund_No_ValidationErrorCode()
	{
		when(payment.getReference()).thenReturn("reference");
		ValidationErrors errors = mock(ValidationErrors.class);
		when(result.getErrors()).thenReturn(errors);
		ValidationError error = mock(ValidationError.class);
		when(error.getCode()).thenReturn(null);
		List<ValidationError> validationErrors = new ArrayList<>();
		validationErrors.add(error);
		when(errors.getAllDeepValidationErrors()).thenReturn(validationErrors);
		assertThat(braintreeRefundTransactionProcessor.processFailedRefund(result, payment, gateway)).isNotNull()
				.hasFieldOrPropertyWithValue("success", false);
	}

	@Test
	void testProcessFailedRefund_No_ValidationErrors()
	{
		ValidationErrors errors = mock(ValidationErrors.class);
		when(result.getErrors()).thenReturn(errors);
		List<ValidationError> validationErrors = new ArrayList<>();
		when(errors.getAllDeepValidationErrors()).thenReturn(validationErrors);
		assertThat(braintreeRefundTransactionProcessor.processFailedRefund(result, payment, gateway)).isNotNull()
				.hasFieldOrPropertyWithValue("success", false);
	}

	@Test
	void testProcessRefund()
	{
		TransactionGateway transactionGateway = mock(TransactionGateway.class);
		when(gateway.transaction()).thenReturn(transactionGateway);
		Result<Transaction> resultVoid = new Result<>();
		when(transactionGateway.refund(anyString(), any(BigDecimal.class))).thenReturn(resultVoid);
		assertThat(braintreeRefundTransactionProcessor.processRefund(gateway, "transactionId", BigDecimal.TEN)).isNotNull()
				.hasFieldOrPropertyWithValue("success", true);
	}
}