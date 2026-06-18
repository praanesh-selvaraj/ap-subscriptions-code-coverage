package com.kmp.aeroparker.subscription.payments.factory;

import org.springframework.stereotype.Component;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.TransactionRequest;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class BraintreeObjectFactory
{
	private final GlobalProperties globalProperties;

	public BraintreeGateway createGateway(final BraintreeCredentials credentials)
	{
		boolean production = !(globalProperties.isDev() || globalProperties.isStaging());
		log.info("Creating  braintree gateway for isProduction: {}", production);
		return new BraintreeGateway(production ? Environment.PRODUCTION : Environment.SANDBOX, credentials.getMerchantId(),
				credentials.getPublicKey(), credentials.getPrivateKey());
	}

	public CustomerRequest createCustomerRequest()
	{
		return new CustomerRequest();
	}

	public TransactionRequest createTransactionRequest()
	{
		return new TransactionRequest();
	}
	
	public SubscriptionRequest createSubscriptionRequest()
	{
		return new SubscriptionRequest();
	}
}