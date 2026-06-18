package com.kmp.aeroparker.subscription.payments.model;

import java.math.BigDecimal;

import javax.annotation.Generated;

import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.Getter;

@Getter
public class PaymentGatewayParameters
{
	private final Affiliates affiliate;
	private final Sites site;
	private final Languages language;
	private final int carParkId;
	private final String currency;
	private final BigDecimal amount;

	@Generated("SparkTools")
	private PaymentGatewayParameters(final Builder builder)
	{
		this.affiliate = builder.affiliate;
		this.site = builder.site;
		this.language = builder.language;
		this.carParkId = builder.carParkId;
		this.currency = builder.currency;
		this.amount = builder.amount;
	}

	/**
	 * Creates builder to build {@link PaymentGatewayParameters}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder()
	{
		return new Builder();
	}

	/**
	 * Builder to build {@link PaymentGatewayParameters}.
	 */
	@Generated("SparkTools")
	public static final class Builder
	{
		private Affiliates affiliate;
		private Sites site;
		private Languages language;
		private int carParkId;
		private String currency;
		private BigDecimal amount;

		private Builder()
		{
		}

		public Builder withAffiliate(final Affiliates affiliate)
		{
			this.affiliate = affiliate;
			return this;
		}

		public Builder withSite(final Sites site)
		{
			this.site = site;
			return this;
		}

		public Builder withLanguage(final Languages language)
		{
			this.language = language;
			return this;
		}

		public Builder withCarParkId(final int carParkId)
		{
			this.carParkId = carParkId;
			return this;
		}

		public Builder withCurrency(final String currency)
		{
			this.currency = currency;
			return this;
		}

		public Builder withAmount(final BigDecimal amount)
		{
			this.amount = amount;
			return this;
		}

		public PaymentGatewayParameters build()
		{
			return new PaymentGatewayParameters(this);
		}
	}
}