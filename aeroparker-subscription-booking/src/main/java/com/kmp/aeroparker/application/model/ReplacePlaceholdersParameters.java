package com.kmp.aeroparker.application.model;

import javax.annotation.Generated;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmailAppearance;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.Getter;

@Getter
public class ReplacePlaceholdersParameters
{
	private final Localise localise;
	private final LanguageFieldsList languageFieldsList;
	private final SubscriptionBookingRecord bookingRecord;
	private final SubscriptionEmailAppearance appearance;
	private final Sites site;
	private final Affiliates affiliate;

	@Generated("SparkTools")
	private ReplacePlaceholdersParameters(final Builder builder)
	{
		this.localise = builder.localise;
		this.languageFieldsList = builder.languageFieldsList;
		this.bookingRecord = builder.bookingRecord;
		this.appearance = builder.appearance;
		this.site = builder.site;
		this.affiliate = builder.affiliate;
	}

	/**
	 * Creates builder to build {@link ReplacePlaceholdersParameters}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder()
	{
		return new Builder();
	}

	/**
	 * Builder to build {@link ReplacePlaceholdersParameters}.
	 */
	@Generated("SparkTools")
	public static final class Builder
	{
		private Localise localise;
		private LanguageFieldsList languageFieldsList;
		private SubscriptionBookingRecord bookingRecord;
		private SubscriptionEmailAppearance appearance;
		private Sites site;
		private Affiliates affiliate;

		private Builder()
		{
		}

		public Builder withLocalise(final Localise localise)
		{
			this.localise = localise;
			return this;
		}

		public Builder withLanguageFieldsList(final LanguageFieldsList languageFieldsList)
		{
			this.languageFieldsList = languageFieldsList;
			return this;
		}

		public Builder withBookingRecord(final SubscriptionBookingRecord bookingRecord)
		{
			this.bookingRecord = bookingRecord;
			return this;
		}

		public Builder withAppearance(final SubscriptionEmailAppearance appearance)
		{
			this.appearance = appearance;
			return this;
		}

		public Builder withSite(final Sites site)
		{
			this.site = site;
			return this;
		}

		public Builder withAffiliate(final Affiliates affiliate)
		{
			this.affiliate = affiliate;
			return this;
		}

		public ReplacePlaceholdersParameters build()
		{
			return new ReplacePlaceholdersParameters(this);
		}
	}
}