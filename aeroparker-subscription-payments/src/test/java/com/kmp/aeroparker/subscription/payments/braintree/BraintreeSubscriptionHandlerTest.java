/**
 * 
 */
package com.kmp.aeroparker.subscription.payments.braintree;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionGateway;
import com.braintreegateway.SubscriptionRequest;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;

import io.github.benas.randombeans.api.EnhancedRandom;

/**
 * @author andrei.tuta
 *
 */

@ExtendWith(MockitoExtension.class)
class BraintreeSubscriptionHandlerTest
{
	@Mock
	private BraintreeObjectFactory braintreeObjectFactory;
	@Mock
	private GlobalProperties globalProperties;
	@InjectMocks
	private BraintreeSubscriptionHandler braintreeSubscriptionsHandler;

	@Test
	public void testProcessTransactionRequestSuccess()
	{
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
		BraintreeGateway braintreeGateway = mock(BraintreeGateway.class);
		CustomerGateway customerGateway = mock(CustomerGateway.class);
		Customer customer = EnhancedRandom.random(Customer.class);
		SubscriptionGateway subscriptionGateway = mock(SubscriptionGateway.class);
		BraintreeCredentials credentials = EnhancedRandom.random(BraintreeCredentials.class);
		Result<Subscription> subscriptionResult = EnhancedRandom.random(Result.class);
		String currency = "GBP";
		String customerId = "test";
		Result<Subscription> resultSubscriptionsTestCase1 = new Result();
		Result<Subscription> resultSubscriptionsTestCase2 = new Result();
		Result<Subscription> resultSubscriptionsTestCase3 = new Result();

		when(braintreeGateway.customer()).thenReturn(customerGateway);
		when(customerGateway.find(anyString())).thenReturn(customer);
		when(braintreeObjectFactory.createSubscriptionRequest()).thenReturn(subscriptionRequest);
		when(braintreeObjectFactory.createGateway(credentials)).thenReturn(braintreeGateway);
		when(braintreeGateway.customer()).thenReturn(customerGateway);
		when(braintreeGateway.subscription()).thenReturn(subscriptionGateway);
		when(subscriptionGateway.create(subscriptionRequest)).thenReturn(subscriptionResult);
		resultSubscriptionsTestCase1 =
				braintreeSubscriptionsHandler.processSubscriptionRequest(customerId, currency, credentials);
		assertNotNull(resultSubscriptionsTestCase1);
		assertEquals(subscriptionResult, resultSubscriptionsTestCase1);

		resultSubscriptionsTestCase2 =
				braintreeSubscriptionsHandler.processSubscriptionRequest(customerId, currency, credentials);
		assertNotNull(resultSubscriptionsTestCase2);
		assertEquals(subscriptionResult, resultSubscriptionsTestCase2);

		resultSubscriptionsTestCase3 =
				braintreeSubscriptionsHandler.processSubscriptionRequest(customerId, currency, credentials);
		assertNotNull(resultSubscriptionsTestCase3);
		assertEquals(subscriptionResult, resultSubscriptionsTestCase3);
	}
}
