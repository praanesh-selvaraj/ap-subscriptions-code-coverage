package com.kmp.aeroparker.subscription.payments.processor.history;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.enums.CustomValue;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHistoryProcessor;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionPaymentHistory;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class BraintreePaymentHistoryProcessor implements IPaymentHistoryProcessor
{
	private final PaymentService paymentService;
	private final BraintreeObjectFactory braintreeObjectFactory;

	@Override
	public SubscriptionPaymentHistory loadHistory(int affiliateId, int carParkId, String bookingReference)
	{
		log.debug("Fetching braintree payment history for booking: {}", bookingReference);
		SubscriptionPaymentHistory paymentHistory = new SubscriptionPaymentHistory();
		final BraintreeGateway gateway =
				braintreeObjectFactory.createGateway(getCredentials(affiliateId, carParkId, getType()));
		int paymentId = paymentService.fetchPaymentByReference(bookingReference)
				.getId();
		String subscriptionId = paymentService.fetchPaymentCustomValueByPaymentId(paymentId)
				.get(CustomValue.BRAINTREE_SUB_ID.getField());

		if (!StringUtil.isEmpty(subscriptionId))
		{
			Subscription subscription = gateway.subscription()
					.find(subscriptionId);
			if (subscription != null)
			{
				List<Transaction> transactions = subscription.getTransactions();

				for (Transaction transaction : transactions)
				{
					LocalDateTime createdDate = DateUtil.calendarToLocalDate(transaction.getCreatedAt())
							.atStartOfDay();
					paymentHistory.put(createdDate, transaction.getAmount());
				}
			}
			else
			{
				log.debug("No subscription was returned from braintree using id: {}", subscriptionId);
			}
		}
		else
		{
			log.debug("Unable to find a subscription Id for booking {}.", bookingReference);
		}

		return paymentHistory;
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.BRAINTREE;
	}

	private BraintreeCredentials getCredentials(final int affId, final int carParkId, final PaymentGatewayType type)
	{
		return (BraintreeCredentials) paymentService.fetchPaymentCredentials(affId, carParkId, type);
	}
}
