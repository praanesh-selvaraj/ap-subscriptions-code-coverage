package com.kmp.aeroparker.subscription.payments.braintree;

import org.springframework.stereotype.Component;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionRequest;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Component
@Slf4j
public class BraintreeSubscriptionHandler
{
	private final BraintreeObjectFactory braintreeObjectFactory;
	private final GlobalProperties globalProperties;

	public Result<Subscription> processSubscriptionRequest(final String customerId, final String currency,
			final BraintreeCredentials credentials)
	{
		final BraintreeGateway gateway = braintreeObjectFactory.createGateway(credentials);
		Result<Subscription> subscriptionRequest = null;
		Customer customer = gateway.customer()
				.find(customerId);
		if (customer != null)
		{
			log.info("Customer found with id {}", customerId);
			final String paymentMethodToken = customer.getPaymentMethods()
					.get(0)
					.getToken();
			if (!StringUtil.isEmpty(paymentMethodToken))
			{
				final SubscriptionRequest request =
						createSubscriptionRequest(currency, paymentMethodToken, credentials.getPlanId());
				subscriptionRequest = gateway.subscription()
						.create(request);
			}
		}
		return subscriptionRequest;
	}

	private SubscriptionRequest createSubscriptionRequest(final String currency, final String paymentMethodToken,
			final String planId)
	{
		log.info("Creating subscription request, currency: {}, plan_id: {}", currency, planId);
		SubscriptionRequest request = braintreeObjectFactory.createSubscriptionRequest()
				.paymentMethodToken(paymentMethodToken)
				.planId(planId);
		if (globalProperties.isStaging() || globalProperties.isDev())
		{
			if (StringUtil.isEqual(currency, "GBP"))
			{
				request = request.merchantAccountId("kmp-gbp-paypal");
			}
		}

		return request;
	}
}
