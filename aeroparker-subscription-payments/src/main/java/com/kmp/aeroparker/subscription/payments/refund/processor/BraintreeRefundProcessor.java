package com.kmp.aeroparker.subscription.payments.refund.processor;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.RefundProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BraintreeRefundProcessor extends AbstractRefundProcessor implements RefundProcessor
{
	private final BraintreeObjectFactory braintreeObjectFactory;
	private final BraintreeRefundTransactionProcessor refundTransactionProcessor;

	public BraintreeRefundProcessor(final PaymentService paymentService, final BraintreeObjectFactory braintreeObjectFactory,
			final BraintreeRefundTransactionProcessor refundTransactionProcessor)
	{
		super(paymentService);
		this.braintreeObjectFactory = braintreeObjectFactory;
		this.refundTransactionProcessor = refundTransactionProcessor;
	}

	@Override
	public boolean process(final int affId, final Payments payment, final BigDecimal amount, final String timeZone)
	{
		boolean status = false;
		if (payment != null)
		{
			if (amount.compareTo(BigDecimal.ZERO) > 0)
			{
				// Create a Braintree gateway.
				BraintreeGateway gateway = null;
				BraintreeCredentials credentials = (BraintreeCredentials) paymentService.fetchPaymentCredentials(affId, 0, getType());
				if (credentials != null)
				{
					gateway = braintreeObjectFactory.createGateway(credentials);
				}

				if (gateway != null)
				{
					// Refund the amount.
					Result<Transaction> result = refundTransactionProcessor.processRefund(gateway, payment.getTransactionId(), amount);
					if (result.isSuccess())
					{
						log.info("Braintree refund successful for " + payment.getReference());
						// The refund was successful insert a refund in the DB
						// and
						// update the payment.
						status = processRefund(payment, amount, timeZone);
					}
					else
					{
						log.info("Braintree refund failed for " + payment.getReference());
						// The refund failed for some reason, refunds cannot be
						// made
						// on Braintree transactions until they are settled
						// so it is possible that it hasn't been settled yet, we
						// can
						// check for that here.
						result = refundTransactionProcessor.processFailedRefund(result, payment, gateway);

						if (result.isSuccess())
						{
							log.info("Braintree transaction voided successfully for " + payment.getReference());
							// The void was successful insert a refund and
							// update the payment.
							status = processRefund(payment, new BigDecimal(payment.getAmount()), timeZone);
						}
					}
				}
			}
			else if (amount.compareTo(BigDecimal.ZERO) == 0)
			{
				log.debug("The amount is 0, no refund needed returning true.");
				status = true;
			}
			else
			{
				log.debug("The refund amount was negative");
			}
		}
		else
		{
			log.error("Braintree refund request didn't have a Payment");
		}
		log.debug("Finished processing Braintree refund.");
		return status;
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.BRAINTREE;
	}
}