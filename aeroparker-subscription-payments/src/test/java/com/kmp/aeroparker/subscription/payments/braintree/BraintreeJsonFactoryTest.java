/**
 * 
 */
package com.kmp.aeroparker.subscription.payments.braintree;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.mock;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.braintreegateway.Subscription;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

import io.github.benas.randombeans.api.EnhancedRandom;

/**
 * @author andrei.tuta
 *
 */
@ExtendWith(MockitoExtension.class)
class BraintreeJsonFactoryTest
{
	@InjectMocks
	private BraintreeJsonFactory factory;
	private Payments payment;
	private Subscription subscription;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception
	{
		payment = EnhancedRandom.random(Payments.class);
		subscription = EnhancedRandom.random(Subscription.class, "addOns", "statusHistory", "transactions");
	}

	/**
	 * Test method for
	 * {@link com.kmp.aeroparker.subscription.payments.braintree.BraintreeJsonFactory#convertPaymentToJson(com.kmp.aeroparker.subscription.payments.tables.pojos.Payments)}.
	 */
	@Test
	void testConvertPaymentToJson()
	{
		// empty json object
		assertTrue((factory.convertPaymentToJson(null)
				.size() == 0));
		assertTrue(factory.convertPaymentToJson(payment)
				.size() > 2);
	}

	/**
	 * Test method for
	 * {@link com.kmp.aeroparker.subscription.payments.braintree.BraintreeJsonFactory#convertSubscriptionToJson(com.braintreegateway.Subscription)}.
	 */
	@Test
	void testConvertSubscriptionToJson()
	{
		// empty json object
		assertTrue((factory.convertSubscriptionToJson(null)
				.size() == 0));
		assertTrue(factory.convertSubscriptionToJson(subscription)
				.size() > 2);
	}

}
