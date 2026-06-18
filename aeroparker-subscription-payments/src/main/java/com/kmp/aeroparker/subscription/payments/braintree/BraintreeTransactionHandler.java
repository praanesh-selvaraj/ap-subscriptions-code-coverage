package com.kmp.aeroparker.subscription.payments.braintree;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class BraintreeTransactionHandler
{
	private static final String KMP_GBP_PAYPAL = "kmp-gbp-paypal";
	private static final String GBP = "GBP";
	private final GlobalProperties globalProperties;
	private final BraintreeObjectFactory braintreeObjectFactory;
	private static final String TRANSACTION_SOURCE = "unscheduled";

	public Result<Transaction> processTransactionRequest(final String reference, final String nonceToken,
			final BigDecimal amount, final String currency, final BraintreeCredentials credentials, boolean isRecurringTicket)
	{
		log.info("Perparing transaction request, reference: {}, amount: {}, is recurring: {} ", reference, amount, isRecurringTicket);
		Result<Transaction> result = null;
		final TransactionRequest request = createTransactionRequest(amount, reference, currency, nonceToken);
		final BraintreeGateway gateway = braintreeObjectFactory.createGateway(credentials);
		if (!isRecurringTicket)
		{
			log.info("Creating sale transaction for non recurring ticket");
			result = gateway.transaction()
					.sale(request);
		}
		else
		{
			log.info("Creating customer request for recurring ticket");
			final CustomerRequest customerRequest = braintreeObjectFactory.createCustomerRequest();
			Result<Customer> customerResult = gateway.customer()
					.create(customerRequest);
			if (customerResult.isSuccess())
			{
				log.info("Customer request for recurring ticket built successfully");
				request.customerId(customerResult.getTarget()
						.getId())
						.options()
						.storeInVault(true);
				log.info("Creating sale transaction");
				result = gateway.transaction()
						.sale(request);
			}
		}
		return result;
	}

	private TransactionRequest createTransactionRequest(final BigDecimal amount, final String reference,
			final String currency, final String nonceToken)
	{
		TransactionRequest request = braintreeObjectFactory.createTransactionRequest()
				.amount(amount)
				.orderId(reference);

		request = setMerchantAccountId(currency, request::merchantAccountId);
		// in this way we process just payment by credit card.
		request = request.paymentMethodNonce(nonceToken)
				.transactionSource(TRANSACTION_SOURCE);
		request = request.options()
				.submitForSettlement(true)
				.done();
		log.info("Finished building transaction request");
		return request;
	}

	public Transaction fetchTransactionById(final BraintreeCredentials credentials, final String transactionId)
	{
		Transaction transaction = null;
		final BraintreeGateway gateway = braintreeObjectFactory.createGateway(credentials);
		transaction = gateway.transaction()
				.find(transactionId);
		return transaction;
	}
	
	public <R extends com.braintreegateway.Request> R setMerchantAccountId(String currency, final java.util.function.Function<String, R> function)
	{
		String merchantAccountId = null;
		if (globalProperties.isStaging() || globalProperties.isDev())
		{
			/*
			 * We have multiple currencies on the braintree developer and paypal payments require the correct currency set. Usually don't run into
			 * this problem on live because there is only one currency on an account, we will have to make merchant account IDs content manageable in
			 * admin if we get a client who wants to take multiple currencies.
			 */

			if (StringUtil.isEqual(currency, GBP))
			{
				merchantAccountId = KMP_GBP_PAYPAL;
			}
		}
		return function.apply(merchantAccountId);
	}
}