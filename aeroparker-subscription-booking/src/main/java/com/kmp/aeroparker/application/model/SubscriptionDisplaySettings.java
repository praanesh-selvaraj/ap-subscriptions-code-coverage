package com.kmp.aeroparker.application.model;

import javax.annotation.Generated;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscription;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionMedia;

public class SubscriptionDisplaySettings
{
	private final AffiliateSubscription affiliateSubscription;
	private final AffiliateSubscriptionMedia affiliateSubscriptionMedia;

	@Generated("SparkTools")
	private SubscriptionDisplaySettings(final Builder builder)
	{
		this.affiliateSubscription = builder.affiliateSubscription;
		this.affiliateSubscriptionMedia = builder.affiliateSubscriptionMedia;
	}

	public AffiliateSubscription getAffiliateSubscription()
	{
		return affiliateSubscription;
	}

	public AffiliateSubscriptionMedia getAffiliateSubscriptionMedia()
	{
		return affiliateSubscriptionMedia;
	}

	/**
	 * Creates builder to build {@link SubscriptionDisplaySettings}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder()
	{
		return new Builder();
	}

	/**
	 * Builder to build {@link SubscriptionDisplaySettings}.
	 */
	@Generated("SparkTools")
	public static final class Builder
	{
		private AffiliateSubscription affiliateSubscription;
		private AffiliateSubscriptionMedia affiliateSubscriptionMedia;

		private Builder()
		{
		}

		public Builder withAffiliateSubscription(final AffiliateSubscription affiliateSubscription)
		{
			this.affiliateSubscription = affiliateSubscription;
			return this;
		}

		public Builder withAffiliateSubscriptionMedia(final AffiliateSubscriptionMedia affiliateSubscriptionMedia)
		{
			this.affiliateSubscriptionMedia = affiliateSubscriptionMedia;
			return this;
		}

		public SubscriptionDisplaySettings build()
		{
			return new SubscriptionDisplaySettings(this);
		}
	}
}