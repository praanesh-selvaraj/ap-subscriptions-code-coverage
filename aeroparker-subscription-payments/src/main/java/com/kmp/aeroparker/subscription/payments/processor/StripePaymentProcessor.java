package com.kmp.aeroparker.subscription.payments.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.enums.CustomValue;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentProcessor;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.stripe.StripePaymentIntentProcessor;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPaymentAmount;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethod.Card;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class StripePaymentProcessor implements IPaymentProcessor
{
	private final PaymentService paymentService;
	private final StripePaymentIntentProcessor stripePaymentIntentProcessor;
	
	@Override
	public Payments processPartial(PaymentProcessorParameters paymentProcessorParameters, Payments parentPayment)
	{
		String timeZone = paymentProcessorParameters.getTimeZone();
		PaymentIntent paymentIntent = paymentProcessorParameters.getPaymentIntent();
		String reference = paymentProcessorParameters.getBookingReference();
		int affiliateId = paymentProcessorParameters.getAffiliateId();
		
		if (parentPayment == null)
		{
			parentPayment = paymentService.createPayment("", reference,
					new BigDecimal(paymentIntent.getAmount()).movePointLeft(2)
							.setScale(2, RoundingMode.HALF_UP).toPlainString(), 
					PaymentGatewayType.STRIPE.getId(), timeZone);
		}
		else
		{
			parentPayment.setAmount(new BigDecimal(parentPayment.getAmount())
					.add(new BigDecimal(paymentIntent.getAmount()).movePointLeft(2)
							.setScale(2, RoundingMode.HALF_UP)).toPlainString());
		}

		if (!paymentService.savePayment(parentPayment))
		{
			log.error("Error saving payment, the payment for reference: {} was successful but the insert to the DB failed.", reference);
			parentPayment = null;
		}
		else
		{
			int parentPaymentId = parentPayment.getId() != null ? parentPayment.getId() : 0;
			PartialPayments partialPayment = new PartialPayments();
			partialPayment.setId(0);
			partialPayment.setTransactionId(paymentIntent.getId());
			partialPayment.setType(PaymentGatewayType.STRIPE.getId());
			partialPayment.setReference(reference);
			partialPayment.setAmount(new BigDecimal(paymentIntent.getAmount()).movePointLeft(2)
					.setScale(2, RoundingMode.HALF_UP));
			partialPayment.setCreated(Timestamp.valueOf(DateUtil.nowLocalDateTime(timeZone)));
			partialPayment.setParentPayment(parentPaymentId);
			partialPayment.setCarparkId(null);

			PartialPayments partialPaymentFromDb = paymentService.fetchPartialPaymentsByTransactionIdReferenceAndType(
					paymentIntent.getId(), reference, PaymentGatewayType.STRIPE.getId());
			if (partialPaymentFromDb != null)
			{
				log.info("Partial payment already exists for booking reference {}", reference);
				return null;
			}
			if (paymentService.savePartialPayment(partialPayment))
			{
				PartialPaymentAmount partialPaymentAmount = new PartialPaymentAmount();
				partialPaymentAmount.setAmount(new BigDecimal(paymentIntent.getAmount())
						.movePointLeft(2)
						.setScale(2, RoundingMode.HALF_UP)
						.toString());
				partialPaymentAmount.setReference(reference);
				partialPaymentAmount.setPartialPaymentId(partialPayment.getId());
				paymentService.savePartialPaymentAmount(partialPaymentAmount);

				saveCustomValues(parentPaymentId, partialPayment.getId(), paymentIntent, affiliateId);
			}
			else
			{
				log.error("Parent payment object with ID {} saved correctly, but the partial payment for reference: {}, DB insert failed.",
						parentPayment.getId(), reference);
				parentPayment = null;
			}
		}
		return parentPayment;
	}
	
	private void saveCustomValues(int paymentId, int partialPaymentId, PaymentIntent paymentIntent, int affiliateId)
	{
		PaymentMethod paymentMethod = stripePaymentIntentProcessor.getPaymentMethod(affiliateId, paymentIntent);
		if (paymentMethod != null)
		{
			Map<String, String> customValues = new HashMap<String, String>();

			Card card = paymentMethod.getCard();

			customValues.put(CustomValue.CARD_NUMBER.getField(), card.getLast4());
			customValues.put(CustomValue.CARD_EXPIRY_MONTH.getField(), card.getExpMonth()
					.toString());
			customValues.put(CustomValue.CARD_EXPIRY_YEAR.getField(), card.getExpYear()
					.toString());
			customValues.put(CustomValue.CARD_TYPE.getField(), card.getBrand());

			paymentService.savePaymentCustomValues(customValues, paymentId, partialPaymentId);

		}
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.STRIPE;
	}

	@Override
	public Payments process(PaymentProcessorParameters paymentProcessorParameters)
	{
		// Normal payments not supported for Stripe
		return null;
	}
}
