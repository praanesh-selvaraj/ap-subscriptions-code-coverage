package com.kmp.aeroparker.subscription.payments.handler;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionGateway;
import com.braintreegateway.TransactionOptionsRequest;
import com.braintreegateway.TransactionRequest;
import com.kmp.aeroparker.subscription.payments.braintree.BraintreeTransactionHandler;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class BraintreeTransactionHandlerTest
{

	@Mock
	private BraintreeObjectFactory braintreeObjectFactory;
	@Mock
	private GlobalProperties globalProperties;
	@InjectMocks
	BraintreeTransactionHandler braintreeTransactionHandler;

	BraintreeCredentials credentials = EnhancedRandom.random(BraintreeCredentials.class);
	BraintreeGateway braintreeGateway = mock(BraintreeGateway.class);
	TransactionGateway transactionGateway = mock(TransactionGateway.class);

	@Test
	public void testProcessTransactionRequestSuccess()
	{
		TransactionRequest transactionRequest = mock(TransactionRequest.class);
		TransactionOptionsRequest transactionOptionsRequest = mock(TransactionOptionsRequest.class);
		CustomerGateway customerGateway = mock(CustomerGateway.class);
		Result<Transaction> transactionResult = EnhancedRandom.random(Result.class);
		String token = "testToken";
		BigDecimal amount = new BigDecimal("10.00");
		String currency = "GBP";
		String reference = "testReference";
		Result<Customer> customerResult = mock(Result.class);
		Result<Transaction> resultTransactionTestCase1 = new Result();
		Result<Transaction> resultTransactionTestCase2 = new Result();
		Result<Transaction> resultTransactionTestCase3 = new Result();
		Result<Transaction> resultTransactionTestCase4 = new Result();

		when(braintreeObjectFactory.createTransactionRequest()).thenReturn(transactionRequest);
		when(transactionRequest.amount(any())).thenReturn(transactionRequest);
		when(transactionRequest.orderId(anyString())).thenReturn(transactionRequest);
		when(transactionRequest.options()).thenReturn(transactionOptionsRequest);
		when(transactionRequest.paymentMethodNonce(token)).thenReturn(transactionRequest);
		when(transactionRequest.transactionSource(anyString())).thenReturn(transactionRequest);
		when(transactionRequest.merchantAccountId(anyString())).thenReturn(transactionRequest);
		when(transactionRequest.customerId(anyString())).thenReturn(transactionRequest);
		when(transactionOptionsRequest.submitForSettlement(any())).thenReturn(transactionOptionsRequest);
		when(transactionOptionsRequest.done()).thenReturn(transactionRequest);
		when(globalProperties.isDev()).thenReturn(true)
				.thenReturn(false)
				.thenReturn(false);
		when(globalProperties.isStaging()).thenReturn(false)
				.thenReturn(true)
				.thenReturn(false);
		when(braintreeObjectFactory.createGateway(credentials)).thenReturn(braintreeGateway);
		when(braintreeGateway.transaction()).thenReturn(transactionGateway);
		when(transactionGateway.sale(any())).thenReturn(transactionResult);

		when(braintreeGateway.customer()).thenReturn(customerGateway);
		when(customerGateway.create(any())).thenReturn(customerResult);
		when(customerResult.isSuccess()).thenReturn(true)
				.thenReturn(true)
				.thenReturn(true)
				.thenReturn(false);
		when(customerResult.getTarget()).thenReturn(EnhancedRandom.random(Customer.class));

		resultTransactionTestCase1 =
				braintreeTransactionHandler.processTransactionRequest(reference, token, amount, currency, credentials, true);
		assertNotNull(resultTransactionTestCase1);
		assertEquals(transactionResult, resultTransactionTestCase1);

		when(transactionRequest.merchantAccountId(null)).thenCallRealMethod();
		resultTransactionTestCase2 =
				braintreeTransactionHandler.processTransactionRequest(reference, token, amount, null, credentials, true);
		assertNotNull(resultTransactionTestCase2);
		assertEquals(transactionResult, resultTransactionTestCase2);

		resultTransactionTestCase3 =
				braintreeTransactionHandler.processTransactionRequest(reference, token, amount, currency, credentials, true);
		assertNotNull(resultTransactionTestCase3);
		assertEquals(transactionResult, resultTransactionTestCase3);
		// failed customer fetch
		resultTransactionTestCase4 =
				braintreeTransactionHandler.processTransactionRequest(reference, token, amount, currency, credentials, true);
		assertNull(resultTransactionTestCase4);
	}
	
	@Test
	public void testProcessTransactionRequestSuccess_Not_Recurring()
	{
		TransactionRequest transactionRequest = mock(TransactionRequest.class);
		TransactionOptionsRequest transactionOptionsRequest = mock(TransactionOptionsRequest.class);
		Result<Transaction> transactionResult = EnhancedRandom.random(Result.class);
		String token = "testToken";
		BigDecimal amount = new BigDecimal("10.00");
		String currency = "GBP";
		String reference = "testReference";

		when(braintreeObjectFactory.createTransactionRequest()).thenReturn(transactionRequest);
		when(transactionRequest.amount(any())).thenReturn(transactionRequest);
		when(transactionRequest.orderId(anyString())).thenReturn(transactionRequest);
		when(transactionRequest.options()).thenReturn(transactionOptionsRequest);
		when(transactionRequest.paymentMethodNonce(token)).thenReturn(transactionRequest);
		when(transactionRequest.transactionSource(anyString())).thenReturn(transactionRequest);
		when(transactionRequest.merchantAccountId(anyString())).thenReturn(transactionRequest);
		when(transactionOptionsRequest.submitForSettlement(any())).thenReturn(transactionOptionsRequest);
		when(transactionOptionsRequest.done()).thenReturn(transactionRequest);
		when(globalProperties.isDev()).thenReturn(true)
				.thenReturn(false)
				.thenReturn(false);
		when(globalProperties.isStaging()).thenReturn(false)
				.thenReturn(true)
				.thenReturn(false);
		when(braintreeObjectFactory.createGateway(credentials)).thenReturn(braintreeGateway);
		when(braintreeGateway.transaction()).thenReturn(transactionGateway);
		when(transactionGateway.sale(any())).thenReturn(transactionResult);

		Result<Transaction> resultTransactionTestCase =
				braintreeTransactionHandler.processTransactionRequest(reference, token, amount, currency, credentials, false);
		assertNotNull(resultTransactionTestCase);
	}

	@Test
	public void testFetchTransactionById()
	{
		when(braintreeObjectFactory.createGateway(credentials)).thenReturn(braintreeGateway);
		when(braintreeGateway.transaction()).thenReturn(transactionGateway);
		when(transactionGateway.find(anyString())).thenReturn(mock(Transaction.class));

		assertNotNull(braintreeTransactionHandler.fetchTransactionById(credentials, "TEST"));
	}
}
