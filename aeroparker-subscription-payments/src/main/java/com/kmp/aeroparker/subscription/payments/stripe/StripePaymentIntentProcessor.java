package com.kmp.aeroparker.subscription.payments.stripe;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kmp.aeroparker.subscription.payments.credentials.StripeCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import com.stripe.param.RefundCreateParams;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class StripePaymentIntentProcessor
{
	private static final String SUCCEEDED = "succeeded";
	
	private PaymentService service;
	private StripePaymentRequestSender requestSender;
	
	public PaymentIntent generatePaymentIntent(int affiliateId, String amount, String currency)
	{
		log.info("Attempting to created payment intent for affiliateId: {}, amount: {}, currency: {}" + affiliateId, amount, currency);
		RequestOptions requestOptions = getAPIKeyForRequestOptions(affiliateId);
		// Create a PaymentIntent with the order amount and currency.
		PaymentIntentCreateParams params = requestSender.paramBuilder(amount, currency);

		PaymentIntent paymentIntent = null;
		try
		{
			paymentIntent = requestSender.create(params, requestOptions);
			if (paymentIntent != null)
			{
				log.info("Successfully created payment intent with payment intent id : {}", paymentIntent.getId());
			}
			else
			{
				log.info("Error while trying to generate the payment intent for Stripe. The payment intent is null");
			}
		}
		catch (StripeException e)
		{
			log.error("Error trying to generate the payment intent for Stripe: {}", e.getMessage(), e);
		}

		return paymentIntent;
	}

	public boolean updatePaymentIntent(String paymentIntentId, int affiliateId, String amount)
			throws StripeException
	{
		log.info("Attempting to update the payment intent with payment intent id {}, affiliateId: {}, amount: {}", paymentIntentId, affiliateId, amount);
		RequestOptions requestOptions = getAPIKeyForRequestOptions(affiliateId);
		boolean success = false;
		String amountString =
				new BigDecimal(Float.toString(Float.valueOf(amount))).setScale(2, BigDecimal.ROUND_HALF_UP)
						.movePointRight(2)
						.toPlainString();
		Map<String, Object> params = new HashMap<>();
		params.put("amount", amountString);

		PaymentIntent intent = requestSender.retrieve(paymentIntentId, requestOptions);
		if (SUCCEEDED.equals(intent.getStatus()))
		{
			log.info("Payment intent with Id: {} is already confirmed.", paymentIntentId);
			success = true;
		}
		
		if (!success)
		{
			PaymentIntent updatedPaymentIntent = requestSender.update(intent, params, requestOptions);

			if (updatedPaymentIntent != null && amountString.equals(updatedPaymentIntent.getAmount().toString()))
			{
				log.info("Successfully updated the payment intent with payment intent id: {} for new amount: {}",
						updatedPaymentIntent.getId(), amountString);
				success = true;
			}
			else
			{
				log.info("Failed to updated the payment intent");
			}
		}

		return success;
	}

	public Refund stripeRefundPayment(int affiliateId, BigDecimal amount, String transactionId)
	{
		log.info("Attempting to refund the payment intent with payment intent id {}, affiliateId: {}, amount: {}", transactionId, affiliateId, amount);
		RequestOptions requestOptions = getAPIKeyForRequestOptions(affiliateId);
		RefundCreateParams params = requestSender.paramBuilderRefunds(amount, transactionId);

		Refund refund = null;
		try
		{
			refund = requestSender.createRefund(params, requestOptions);
			if (refund != null)
			{
				log.info("Successfully refunded the payment");
			}
			else
			{
				log.info("Error while trying to refund the payment for Stripe. The refund is null");
			}
		}
		catch (StripeException e)
		{
			log.error("Error trying to refund the Stripe payment: {}", e.getMessage(), e);
		}
		return refund;
	}

	public PaymentIntent getPaymentIntent(int affiliateId, String paymentIntent)
	{
		log.info("Attempting to retrieve the payment intent with payment intent id {}, affiliateId: {}", paymentIntent, affiliateId);
		RequestOptions requestOptions = getAPIKeyForRequestOptions(affiliateId);

		PaymentIntent intent = null;
		try
		{
			intent = requestSender.retrieve(paymentIntent, requestOptions);
			if (intent != null)
			{
				log.info("Successfully retrieved payment intent with payment intent id : {}", intent.getId());
			}
			else
			{
				log.info("Error while trying to retrieve the payment intent for Stripe. The payment intent is null");
			}
		}
		catch (StripeException e)
		{
			log.error("Error trying to get the Stripe payment intent: {}", e.getMessage(), e);
		}
		return intent;
	}

	public PaymentMethod getPaymentMethod(int affiliateId, PaymentIntent paymentIntent)
	{
		log.info("Attempting to get the payment method for the payment intent with payment intent id {} and affiliateId: {}",
				paymentIntent.getId(), affiliateId);
		RequestOptions requestOptions = getAPIKeyForRequestOptions(affiliateId);
		PaymentMethod paymentMethod = null;
		try
		{
			paymentMethod = requestSender.retievePaymentMethod(paymentIntent.getPaymentMethod(), requestOptions);
			if (paymentMethod != null)
			{
				log.info("Successfully retrieved payment method for payment intent id : {}", paymentIntent.getId());
			}
			else
			{
				log.info("Error while trying to retrieve the payment method for Stripe. The payment method is null");
			}
		}
		catch (StripeException e)
		{
			log.error("Error trying to get the payment method for Stripe payment: {}", e.getMessage(), e);
		}
		return paymentMethod;
	}
	
	private RequestOptions getAPIKeyForRequestOptions(int affiliateId)
	{
		RequestOptions requestOptions = null;
		StripeCredentials credentials = (StripeCredentials) service.fetchPaymentCredentials(
				affiliateId, 0, PaymentGatewayType.STRIPE);
		if (credentials != null)
		{
			// The secret key should be set in admin.
			Stripe.apiKey = credentials.getSecretKey();
			requestOptions = RequestOptions.builder()
					.setApiKey(credentials.getSecretKey())
					.build();
		}
		else
		{
			log.info("Credentials are null for affiliate: " + affiliateId);
		}
		return requestOptions;
	}

	public boolean updatePaymentIntentWithMetadata(String reference, String affiliateCode, String customerEmail,
			String customerPhone, String vehicleRegistration, String paymentIntentId, int affiliateId, String guid)
	{
		return updatePaymentIntentWithMetadata(reference, affiliateCode, customerEmail, customerPhone, vehicleRegistration,
				paymentIntentId, affiliateId, guid, null, false);
	}

	public boolean updatePaymentIntentWithMetadata(String reference, String affiliateCode, String customerEmail,
			String customerPhone, String vehicleRegistration, String paymentIntentId, int affiliateId, String guid,
			String membershipId)
	{
		return updatePaymentIntentWithMetadata(reference, affiliateCode, customerEmail, customerPhone, vehicleRegistration,
				paymentIntentId, affiliateId, guid, membershipId, false);
	}
	
	public boolean updatePaymentIntentWithMetadata(String reference, String affiliateCode, String customerEmail,
			String customerPhone, String vehicleRegistration, String paymentIntentId, int affiliateId, String guid,
			String membershipId, boolean isRenewal)
	{
		log.info("Attempting to update Payment Intent for payment intent ID {} booking reference {}", paymentIntentId, reference);
		RequestOptions requestOptions = getAPIKeyForRequestOptions(affiliateId);
		// Create parameters
		String affiliateIdString = "" + affiliateId;
		PaymentIntentUpdateParams params = StringUtils.hasText(membershipId)
				? requestSender.buildParametersForMetadata(reference, affiliateCode, customerEmail, customerPhone,
						vehicleRegistration, guid, affiliateIdString, membershipId, String.valueOf(isRenewal))
				: requestSender.buildParametersForMetadata(reference, affiliateCode, customerEmail, customerPhone,
						vehicleRegistration, guid, affiliateIdString, String.valueOf(isRenewal));
		boolean success = false;
		try
		{
			PaymentIntent intent = requestSender.retrieve(paymentIntentId, requestOptions);
			PaymentIntent updatedPaymentIntent = requestSender.updateMetadata(intent, params, requestOptions);

			if (updatedPaymentIntent != null)
			{
				log.info("Successfully updated metadata for payment intent with payment intent id: {}", updatedPaymentIntent.getId());
				success = true;
			}
			else
			{
				log.info("Error while trying to update metadata for Stripe payment intent. The payment intent is null");
			}
		}
		catch (StripeException e)
		{
			log.error("Error trying to update the payment intent for Stripe with metadata : {}", e.getMessage(), e);
		}

		return success;
	}

	public boolean updatePaymentIntentWithMembershipId(String reference, int affiliateId, PaymentIntent intent, String membershipId)
	{
		log.info("Attempting to update Payment Intent metadata with membership ID for payment intent ID {} booking reference {}", intent.getId(), reference);
		RequestOptions requestOptions = getAPIKeyForRequestOptions(affiliateId);
		PaymentIntentUpdateParams params = requestSender.buildMembershipIdParameterForMetadata(membershipId);
		boolean success = false;
		try
		{
			PaymentIntent updatedPaymentIntent = requestSender.updateMetadata(intent, params, requestOptions);

			if (updatedPaymentIntent != null)
			{
				log.info("Successfully updated membership ID in metadata for payment intent with payment intent id: {}", updatedPaymentIntent.getId());
				success = true;
			}
			else
			{
				log.info("Error while trying to update membership ID in metadata for Stripe payment intent. The payment intent is null");
			}
		}
		catch (StripeException e)
		{
			log.error("Error trying to update the membership ID for the payment intent for Stripe with metadata : {}", e.getMessage(), e);
		}

		return success;
	}
	
	public Charge getCharge(String chargeId, int affiliateId, String paymentIntent)
	{
		log.info("Attempting to retrieve the charge for payment intent id " + paymentIntent + " and affiliateId: "
				+ affiliateId);
		RequestOptions requestOptions = getAPIKeyForRequestOptions(affiliateId);
		Charge charge = null;
		
		try
		{
			charge = requestSender.retrieveCharge(chargeId, requestOptions);
		}
		catch (StripeException e)
		{
			log.error("Error getting charge by charge id " + chargeId + ": " + e.getMessage(), e);
		}
		
		return charge;
	}
}
