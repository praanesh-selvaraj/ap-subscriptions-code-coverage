package com.kmp.aeroparker.subscription.payments.handler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.braintree.BraintreeSubscriptionHandler;
import com.kmp.aeroparker.subscription.payments.braintree.BraintreeTransactionHandler;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;
import com.kmp.aeroparker.subscription.payments.factory.PaymentProcessorFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHandler;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class BraintreePaymentHandler implements IPaymentHandler
{
	private final PaymentService paymentService;
	private final BraintreeTransactionHandler transactionHandler;
	private final BraintreeSubscriptionHandler subscriptionHandler;
	private final GlobalProperties globalProperties;
	private final BraintreeObjectFactory braintreeObjectFactory;
	private final PaymentProcessorFactory paymentProcessorFactory;

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.BRAINTREE;
	}

	@Override
	public Map<String, Object> setUpTransaction(final PaymentGatewayParameters paymentHandlerParams)
	{
		log.debug("Setting up Braintree transaction.");
		Affiliates affiliate = paymentHandlerParams.getAffiliate();
		int affId = affiliate.getId();
		BraintreeCredentials credentials = getCredentials(affId, 0, getType());
		Map<String, Object> params = new HashMap<>();
		if (credentials != null)
		{
			log.info("Credentials not null, setting up transaction");
			final String clientToken = generateClientToken(paymentHandlerParams.getCurrency(), credentials);
			PaymentGatewayTypes braintreeGatewayInfo =
					paymentService.fetchPaymentGatewayTypesById(PaymentGatewayType.BRAINTREE.getId());
			params.put("paymentGatewayType", getType().getId());
			params.put("currency", paymentHandlerParams.getCurrency());
			params.put("languageCode", paymentHandlerParams.getLanguage()
					.getLanguageCode());
			params.put("environment",
					(globalProperties.isDev() || globalProperties.isStaging()) ? "sandbox" : "production");
			params.put("amount", paymentHandlerParams.getAmount());
			params.put("clientToken", clientToken);
			params.put("paymentHtml", braintreeGatewayInfo.getPaymentJsp()
					.replace(".jsp", ""));
			params.put("affiliateName", affiliate.getName()
					.replaceAll("'", "&#8217;"));
			params.put("applePayEnabled", credentials.isApplePayEnabled());
			params.put("payPalEnabled", credentials.isPayPalEnabled());
			params.put("cvvCheckEnabled", credentials.isCvvCheckEnabled());
			params.put("affiliateId", affId);
			params.put("is3DS2Enabled", credentials.isThreeds2Enabled());
			log.debug("Finished setting up transaction.");
		}
		return params;
	}

	@Override
	public boolean processPayment(final SubscriptionBookingData bookingData)
	{
		log.info("Processing Braintree payment.");
		boolean status = false;
		String nonceToken = bookingData.getPaymentReference();
		String bookingReference = bookingData.getBookingReference();
		int affId = bookingData.getAffiliateId();
		log.info("Affiliate Id {} , Booking Reference: {} ", affId, bookingReference);
		Payments payments = null;
		if (!StringUtil.isEmpty(nonceToken))
		{
			log.info("Braintree payment nonce is not empty ,  processing payment");
			LocalDate nowDate = DateUtil.nowLocalDate(bookingData.getTimeZone());
			LocalDate subStartDate = DateUtil.strToLocalDate(bookingData.getStartDate(), bookingData.getDateFormat());
			boolean isRecurringTicket = bookingData.isRecurring();
			boolean makeSubscriptionNow = nowDate.equals(subStartDate);
			BigDecimal amount = bookingData.getAmount();
			log.info("Processing............");
			final Result<Transaction> result =
					transactionHandler.processTransactionRequest(bookingReference, nonceToken, amount,
							bookingData.getCurrency(), getCredentials(affId, bookingData.getCarParkId(), getType()), isRecurringTicket);
			
			if (result.isSuccess())
			{
				log.info("Braintree transaction successful for bookingReference {}, amount: {} ", bookingReference, amount);
				Subscription braintreeSubscription = null;
				Transaction transaction = result.getTarget();
				// boolean set to true unless there are errors creating the subscription
				boolean invalidSubscription = false;
				if (isRecurringTicket && makeSubscriptionNow)
				{
					log.info("Processing recurring Braintree payment for bookingReference " + bookingReference);
					final Result<Subscription> subscriptionResult = subscriptionHandler.processSubscriptionRequest(
							transaction.getCustomer()
									.getId(),
							bookingData.getCurrency(), getCredentials(affId, bookingData.getCarParkId(), getType()));
					if (subscriptionResult.isSuccess())
					{
						log.info("Recurring Braintree payment is successful");
						braintreeSubscription = subscriptionResult.getTarget();
						log.info("Braintree subscription {} created successfully", braintreeSubscription.getId());
					}
					else
					{
						log.info("Error creating subscription for customer {}", transaction.getCustomer()
								.getId());
						log.error("Error creating subscription {}: {}", bookingData.getBookingReference(),
								subscriptionResult.getMessage());
						invalidSubscription = true;
					}
				}
				// Transaction with braintree is successful
				log.info("Braintree transaction successful");
				PaymentProcessorParameters paymentProcessorParameters = PaymentProcessorParameters.builder()
						.withConfirmationGuid(bookingData.getConfirmationGuid())
						.withTimeZone(bookingData.getTimeZone())
						.withBraintreeTransaction(transaction)
						.withBraintreeSubscription(braintreeSubscription)
						.build();
				payments = paymentProcessorFactory.getInstance(getType())
						.process(paymentProcessorParameters);

				if (payments != null && payments.getId() != 0 && !invalidSubscription)
				{
					bookingData.setPaymentId(payments.getId());
					log.info("Payment record created successfully, payment_id: {}", payments.getId());
					status = true;
				}
			}
		}
		else
		{
			log.info("Braintree transaction returned empty nonce token");
		}
		log.debug("Finished querying transaction.");
		return status;
	}

	private BraintreeCredentials getCredentials(final int affId, final int carParkId, final PaymentGatewayType type)
	{
		return (BraintreeCredentials) paymentService.fetchPaymentCredentials(affId, carParkId, type);
	}
	
	private String generateClientToken(String currency, BraintreeCredentials credentials)
	{
		// Create a Braintree gateway and generate the client token
		log.info("Generating braintree client token");
		final BraintreeGateway gateway = braintreeObjectFactory.createGateway(credentials);
		ClientTokenRequest request = new ClientTokenRequest();
		request = transactionHandler.setMerchantAccountId(currency, request::merchantAccountId);
		String clientToken = gateway.clientToken()
				.generate(request);
		return clientToken;
	}
}