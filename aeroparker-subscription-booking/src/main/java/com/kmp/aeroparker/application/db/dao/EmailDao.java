package com.kmp.aeroparker.application.db.dao;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.booking.kmp.Tables;
import com.kmp.aeroparker.subscription.booking.kmp.tables.daos.EmailQueueDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateCustomEmail;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.EmailQueue;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Emails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmail;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmailAppearance;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class EmailDao
{
	private DSLContext dsl;

	public SubscriptionEmail fetchSubscriptionEmailBySiteAndType(int siteId, String type)
	{
		return dsl.selectFrom(Tables.CRM_SUBSCRIPTION_EMAIL)
				.where(Tables.CRM_SUBSCRIPTION_EMAIL.SITE_ID.eq(siteId)
						.and(Tables.CRM_SUBSCRIPTION_EMAIL.TYPE.eq(type)))
				.fetchOneInto(SubscriptionEmail.class);
	}

	public SubscriptionEmailAppearance fetchSubscriptionEmailAppearanceByEmailIdAndLanguageId(int emailId, int langId)
	{
		return dsl.selectFrom(Tables.CRM_SUBSCRIPTION_EMAIL_APPEARANCE)
				.where(Tables.CRM_SUBSCRIPTION_EMAIL_APPEARANCE.SUBSCRIPTION_EMAIL_ID.eq(emailId)
						.and(Tables.CRM_SUBSCRIPTION_EMAIL_APPEARANCE.LANGUAGE_ID.eq(langId)))
				.fetchOneInto(SubscriptionEmailAppearance.class);
	}

	public void addToQueue(EmailQueue emailQueue)
	{
		new EmailQueueDao(dsl.configuration()).insert(emailQueue);
	}

	public AffiliateCustomEmail fetchActivateAccountEmail(int langId, int affiliateId)
	{
		// Number 4 in AFFILIATE_CUSTOM_EMAIL.EVENT_TYPE is a reference to EVENTTYPE_ACTIVATE_ACCOUNT value
		return dsl.selectFrom(Tables.AFFILIATE_CUSTOM_EMAIL)
				.where(Tables.AFFILIATE_CUSTOM_EMAIL.LANGUAGE_ID.eq(langId))
				.and(Tables.AFFILIATE_CUSTOM_EMAIL.AFFILIATE_ID.eq(affiliateId))
				.and(Tables.AFFILIATE_CUSTOM_EMAIL.EVENT_TYPE.eq(4))
				.fetchOneInto(AffiliateCustomEmail.class);
	}

	public List<Emails> fetchEmails(int ownerId)
	{
		// Event type = 4 is related to Email.EVENTTYPE_ACTIVATE_ACCOUNT
		// OwnerTipee = 5 is related to Email.OWNERTYPE_SITE
		return dsl.selectFrom(Tables.AB_EMAILS)
				.where(Tables.AB_EMAILS.EVENTTYPE.eq(4))
				.and(Tables.AB_EMAILS.OWNERTYPE.eq(5))
				.and(Tables.AB_EMAILS.OWNERID.eq(ownerId))
				.fetchInto(Emails.class);
	}
}