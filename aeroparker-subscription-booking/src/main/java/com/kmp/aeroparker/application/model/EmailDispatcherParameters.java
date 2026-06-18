package com.kmp.aeroparker.application.model;

import javax.annotation.Generated;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.model.enums.SubscriptionEmailType;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import lombok.Getter;

@Getter
public class EmailDispatcherParameters
{
	private SubscriptionEmailType emailType;
	private Sites site;
	private int currentLanguageId;
	private int defaultLanguageId;
	private SubscriptionBookingRecord bookingRecord;
	private int contactId;
	private String timeZone;
	private String servletSchema;
	private String servletName;
	private Affiliates affiliate;
	private boolean emailFromAffiliate;

	@Generated("SparkTools")
	private EmailDispatcherParameters(Builder builder)
	{
		this.emailType = builder.emailType;
		this.site = builder.site;
		this.currentLanguageId = builder.currentLanguageId;
		this.defaultLanguageId = builder.defaultLanguageId;
		this.bookingRecord = builder.bookingRecord;
		this.contactId = builder.contactId;
		this.timeZone = builder.timeZone;
		this.servletSchema = builder.servletSchema;
		this.servletName = builder.servletName;
		this.affiliate = builder.affiliate;
		this.emailFromAffiliate = builder.emailFromAffiliate;
	}

	/**
	 * Creates builder to build {@link EmailDispatcherParameters}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder()
	{
		return new Builder();
	}

	/**
	 * Builder to build {@link EmailDispatcherParameters}.
	 */
	@Generated("SparkTools")
	public static final class Builder
	{
		private SubscriptionEmailType emailType;
		private Sites site;
		private int currentLanguageId;
		private int defaultLanguageId;
		private SubscriptionBookingRecord bookingRecord;
		private int contactId;
		private String timeZone;
		private String servletSchema;
		private String servletName;
		private Affiliates affiliate;
		private boolean emailFromAffiliate;

		private Builder()
		{
		}

		public Builder withEmailType(SubscriptionEmailType emailType)
		{
			this.emailType = emailType;
			return this;
		}

		public Builder withSite(Sites site)
		{
			this.site = site;
			return this;
		}

		public Builder withCurrentLanguageId(int currentLanguageId)
		{
			this.currentLanguageId = currentLanguageId;
			return this;
		}

		public Builder withDefaultLanguageId(int defaultLanguageId)
		{
			this.defaultLanguageId = defaultLanguageId;
			return this;
		}

		public Builder withBookingRecord(SubscriptionBookingRecord bookingRecord)
		{
			this.bookingRecord = bookingRecord;
			return this;
		}

		public Builder withContactId(int contactId)
		{
			this.contactId = contactId;
			return this;
		}

		public Builder withTimeZone(String timeZone)
		{
			this.timeZone = timeZone;
			return this;
		}

		public Builder withServletSchema(String servletSchema)
		{
			this.servletSchema = servletSchema;
			return this;
		}

		public Builder withServletName(String servletName)
		{
			this.servletName = servletName;
			return this;
		}

		public Builder withAffiliate(Affiliates affiliate)
		{
			this.affiliate = affiliate;
			return this;
		}

		public Builder withEmailFromAffiliate(boolean emailFromAffiliate)
		{
			this.emailFromAffiliate = emailFromAffiliate;
			return this;
		}

		public EmailDispatcherParameters build()
		{
			return new EmailDispatcherParameters(this);
		}
	}
}
