package com.kmp.aeroparker.subscription.payments.model;

import javax.annotation.Generated;

import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.kmp.aeroparker.subscription.payments.klix.KlixResponse;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardPaymentResponse;
import com.stripe.model.PaymentIntent;

import lombok.Getter;

@Getter
public class PaymentProcessorParameters
{
	private final String bookingReference;
	private final String timeZone;
	private final String confirmationGuid;
	private final WirecardPaymentResponse wirecardPaymentResponse;
	private final String braintreeSubscriptionResponse;
	private final Transaction braintreeTransaction;
	private final Subscription braintreeSubscription;
	private final KlixResponse klixResponse;
	private final PaymentIntent paymentIntent;
	private final int affiliateId;

	@Generated("SparkTools")
	private PaymentProcessorParameters(final Builder builder)
	{
		this.bookingReference = builder.bookingReference;
		this.timeZone = builder.timeZone;
		this.confirmationGuid = builder.confirmationGuid;
		this.wirecardPaymentResponse = builder.wirecardPaymentResponse;
		this.braintreeSubscriptionResponse = builder.braintreeSubscriptionResponse;
		this.braintreeTransaction = builder.braintreeTransaction;
		this.braintreeSubscription = builder.braintreeSubscription;
		this.klixResponse = builder.klixResponse;
		this.paymentIntent = builder.paymentIntent;
		this.affiliateId = builder.affiliateId;
	}

	/**
	 * Creates builder to build {@link PaymentProcessorParameters}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder()
	{
		return new Builder();
	}

	/**
	 * Builder to build {@link PaymentProcessorParameters}.
	 */
	@Generated("SparkTools")
	public static final class Builder
	{
		private String bookingReference;
		private String timeZone;
		private String confirmationGuid;
		private WirecardPaymentResponse wirecardPaymentResponse;
		private String braintreeSubscriptionResponse;
		private Transaction braintreeTransaction;
		private Subscription braintreeSubscription;
		private KlixResponse klixResponse;
		private PaymentIntent paymentIntent;
		private int affiliateId;

		private Builder()
		{
		}

		public Builder withBookingReference(final String bookingReference)
		{
			this.bookingReference = bookingReference;
			return this;
		}

		public Builder withTimeZone(final String timeZone)
		{
			this.timeZone = timeZone;
			return this;
		}

		public Builder withConfirmationGuid(final String confirmationGuid)
		{
			this.confirmationGuid = confirmationGuid;
			return this;
		}

		public Builder withWirecardPaymentResponse(final WirecardPaymentResponse wirecardPaymentResponse)
		{
			this.wirecardPaymentResponse = wirecardPaymentResponse;
			return this;
		}

		public Builder withBraintreeSubscriptionResponse(final String braintreeSubscriptionResponse)
		{
			this.braintreeSubscriptionResponse = braintreeSubscriptionResponse;
			return this;
		}

		public Builder withBraintreeTransaction(final Transaction braintreeTransaction)
		{
			this.braintreeTransaction = braintreeTransaction;
			return this;
		}

		public Builder withBraintreeSubscription(Subscription braintreeSubscription)
		{
			this.braintreeSubscription = braintreeSubscription;
			return this;
		}

		public KlixResponse getKlixResponse()
		{
			return klixResponse;
		}

		public Builder setKlixResponse(KlixResponse klixResponse)
		{
			this.klixResponse = klixResponse;
			return this;
		}

		public Builder withPaymentIntent(PaymentIntent paymentIntent)
		{
			this.paymentIntent = paymentIntent;
			return this;
		}

		public Builder withAffiliateId(int affiliateId)
		{
			this.affiliateId = affiliateId;
			return this;
		}

		public PaymentProcessorParameters build()
		{
			return new PaymentProcessorParameters(this);
		}
	}
}