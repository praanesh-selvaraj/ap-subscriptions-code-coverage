package com.kmp.aeroparker.subscription.payments.processor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.braintreegateway.ApplePayDetails;
import com.braintreegateway.CreditCard;
import com.braintreegateway.PayPalDetails;
import com.braintreegateway.PaymentInstrumentType;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.kmp.aeroparker.subscription.payments.enums.CustomValue;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentProcessor;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class BraintreePaymentProcessor implements IPaymentProcessor
{
	private final PaymentService paymentService;

	@Override
	public Payments process(final PaymentProcessorParameters paymentProcessorParameters)
	{
		log.info("Inside braintree payment processor");
		Payments payments = null;
		if (paymentProcessorParameters != null)
		{
			Transaction transaction = paymentProcessorParameters.getBraintreeTransaction();

			if (transaction != null)
			{
				payments = process(transaction, paymentProcessorParameters);
			}
		}
		return payments;
	}

	private Payments process(final Transaction transaction, final PaymentProcessorParameters paymentProcessorParameters)
	{
		// If a subscription is made and a payment is processed, the transaction from braintree doesn't have the booking
		// reference as the orderId, so the reference has to be fetched from the params
		String bookingReference = transaction.getOrderId() != null ? transaction.getOrderId()
				: paymentProcessorParameters.getBookingReference();
		log.info("Processing payments records for ref: {}", bookingReference);
		Payments payment = paymentService.createPayment(transaction.getId(), bookingReference, transaction.getAmount()
				.toPlainString(), getType().getId(), paymentProcessorParameters.getTimeZone());

		if (paymentService.savePayment(payment))
		{
			log.debug("Payment saved succesfully, Processing payment custom values, payment id: {}", payment.getId());
			Map<String, String> customValues = new HashMap<String, String>();
			// Check if this was a credit card or PayPal transaction and set
			// the necessary custom values.
			if (StringUtil.isEqual(transaction.getPaymentInstrumentType(), PaymentInstrumentType.CREDIT_CARD))
			{
				log.info("Processing payment custom values for card");
				CreditCard creditCard = transaction.getCreditCard();
				customValues.put(CustomValue.CARD_NUMBER.getField(),
						creditCard.getBin() + "XXXXXX" + creditCard.getLast4());
				customValues.put(CustomValue.CARD_EXPIRY_MONTH.getField(), creditCard.getExpirationMonth());
				customValues.put(CustomValue.CARD_EXPIRY_YEAR.getField(), creditCard.getExpirationYear()
						.substring(2, 4));
				customValues.put(CustomValue.CARD_SCHEME.getField(), creditCard.getDebit()
						.equals(CreditCard.Debit.YES) ? "debit" : "credit");
				customValues.put(CustomValue.CARD_TYPE.getField(), creditCard.getCardType());
			}
			else if (StringUtil.isEqual(transaction.getPaymentInstrumentType(), PaymentInstrumentType.PAYPAL_ACCOUNT))
			{
				log.info("Processing payment custom values for pay pal");
				PayPalDetails payPalDetails = transaction.getPayPalDetails();
				customValues.put(CustomValue.EMAIL_ADDRESS.getField(), payPalDetails.getPayerEmail());
				customValues.put(CustomValue.FIRST_NAME.getField(), payPalDetails.getPayerFirstName());
				customValues.put(CustomValue.LAST_NAME.getField(), payPalDetails.getPayerLastName());
				customValues.put(CustomValue.TOKEN.getField(), payPalDetails.getToken());
				customValues.put(CustomValue.CARD_SCHEME.getField(), "PayPal");
			}
			else if (StringUtil.isEqual(transaction.getPaymentInstrumentType(), PaymentInstrumentType.APPLE_PAY_CARD))
			{
				log.info("Processing payment custom values for apple pay");
				ApplePayDetails applePayDetails = transaction.getApplePayDetails();
				customValues.put(CustomValue.CARD_NUMBER.getField(), "XXXXXXXXXX" + applePayDetails.getLast4());
				customValues.put(CustomValue.CARD_EXPIRY_MONTH.getField(), applePayDetails.getExpirationMonth());
				customValues.put(CustomValue.CARD_EXPIRY_YEAR.getField(), applePayDetails.getExpirationYear()
						.substring(2, 4));
				customValues.put(CustomValue.CARD_TYPE.getField(), applePayDetails.getCardType());
				customValues.put(CustomValue.TOKEN.getField(), applePayDetails.getToken());
			}
			// Save the custom values.
			customValues.put(CustomValue.CURRENCY.getField(), transaction.getCurrencyIsoCode());
			if (paymentProcessorParameters.getBraintreeSubscription() != null)
			{
				Subscription braintreeSubscription = paymentProcessorParameters.getBraintreeSubscription();
				log.debug("Saving subscription id {} as a custom value.",
						braintreeSubscription.getId());
				// save the subscription Id
				customValues.put(CustomValue.BRAINTREE_SUB_ID.getField(),
						braintreeSubscription.getId());
			}
			if (paymentService.saveCustomValues(payment.getId(), customValues))
			{
				log.info("Payment custom values inserted");
			}
		}
		return payment;
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.BRAINTREE;
	}
}