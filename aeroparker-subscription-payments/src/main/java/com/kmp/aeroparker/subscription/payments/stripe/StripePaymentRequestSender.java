package com.kmp.aeroparker.subscription.payments.stripe;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import com.stripe.param.RefundCreateParams;

@Component
public class StripePaymentRequestSender
{
	public static final String PAYMENT_SOURCE_DUBLIN = "Dublin Aeroparker";
	public static final String METADATA_MEMBERSHIP_ID = "Membership_ID";
	public static final String METADATA_SUBSCRIPTION = "Subscription";
	public static final String METADATA_RENEWAL = "Renewal";

	public PaymentIntentCreateParams paramBuilder(String amount, String currency)
	{
		PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
				// Amount is in the smallest currency, e.g. 500 = £5.
				.setAmount(Long.parseLong(amount))
				.setCurrency(currency.toLowerCase())
				.setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
						.setEnabled(true)
						.build());

		return builder.build();
	}

	public RefundCreateParams paramBuilderRefunds(BigDecimal amount, String transactionId)
	{
		return RefundCreateParams.builder()
				.setPaymentIntent(transactionId)
				.setAmount(Long.parseLong(amount.setScale(2, BigDecimal.ROUND_HALF_UP)
						.movePointRight(2)
						.toPlainString()))
				.build();
	}

	public RefundCreateParams paramBuilderRefunds(String paymentIntent)
	{
		return RefundCreateParams.builder()
				.setPaymentIntent(paymentIntent)
				.build();
	}

	public PaymentIntentUpdateParams buildParametersForMetadata(String reference, String affiliateCode,
			String customerEmail, String customerPhone, String vehicleRegistration, String guid, String affiliateId, String isRenewal)
	{
		return buildParametersForMetadata(reference, affiliateCode, customerEmail, customerPhone, vehicleRegistration,
				guid, affiliateId, null, isRenewal);
	}
	
	public PaymentIntentUpdateParams buildParametersForMetadata(String reference, String affiliateCode, String customerEmail,
			String customerPhone, String vehicleRegistration, String guid, String affiliateId, String membershipId, String isRenewal)
	{
		PaymentIntentUpdateParams params =
				PaymentIntentUpdateParams.builder()
						.putMetadata("Booking_reference", reference)
						.putMetadata("Payment_source", PAYMENT_SOURCE_DUBLIN)
						.putMetadata("Affiliate", affiliateCode)
						.putMetadata("Customer_email", customerEmail)
						.putMetadata("Customer_phone", customerPhone)
						.putMetadata("Customer_car_reg", vehicleRegistration)
						.putMetadata("Reservation_guid", guid)
						.putMetadata("Affiliate_id", affiliateId)
						.putMetadata(METADATA_MEMBERSHIP_ID, membershipId)
						.putMetadata(METADATA_SUBSCRIPTION, "true")
						.putMetadata(METADATA_RENEWAL, isRenewal)
						.build();
		
		return params;
	}

	public PaymentIntentUpdateParams buildMembershipIdParameterForMetadata(String membershipId)
	{
			return PaymentIntentUpdateParams.builder()
				.putMetadata(METADATA_MEMBERSHIP_ID, membershipId)
				.build();
	}
	
	public PaymentIntent create(PaymentIntentCreateParams params, RequestOptions requestOptions) throws StripeException
	{
		return PaymentIntent.create(params, requestOptions);
	}

	public PaymentIntent retrieve(String paymentIntentId, RequestOptions requestOptions) throws StripeException
	{
		return PaymentIntent.retrieve(paymentIntentId, requestOptions);
	}

	public PaymentMethod retievePaymentMethod(String paymentMethod, RequestOptions requestOptions) throws StripeException
	{
		return PaymentMethod.retrieve(paymentMethod, requestOptions);
	}

	public PaymentIntent update(PaymentIntent intent, Map<String, Object> params, RequestOptions requestOptions) throws StripeException
	{
		return intent.update(params, requestOptions);
	}

	public Refund createRefund(RefundCreateParams params, RequestOptions requestOptions) throws StripeException
	{
		return Refund.create(params, requestOptions);
	}

	public PaymentIntent updateMetadata(PaymentIntent intent, PaymentIntentUpdateParams params, RequestOptions requestOptions) throws StripeException
	{
		return intent.update(params, requestOptions);
	}
	
	public Charge retrieveCharge(String chargeId, RequestOptions requestOptions) throws StripeException
	{
		return Charge.retrieve(chargeId, requestOptions);
	}
}
