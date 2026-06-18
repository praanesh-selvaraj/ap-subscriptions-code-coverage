package com.kmp.aeroparker.application.db.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.dao.EmailDao;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateCustomEmail;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.EmailQueue;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Emails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmail;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmailAppearance;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.html.utils.HtmlUtil;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class EmailService
{
	private final EmailDao dao;

	public SubscriptionEmail fetchSubscriptionEmailBySiteAndType(final int siteId, final String type)
	{
		SubscriptionEmail email = null;
		if (siteId < 1 || StringUtil.isEmpty(type))
		{
			log.info("Site id / email type is empty, no email will be fetched");
		}
		else
		{
			log.debug("Attempting to fetch subscription email by site ID: {} and type: {}", siteId, type);
			email = dao.fetchSubscriptionEmailBySiteAndType(siteId, type);
			log.debug("Successfully fetched subscription email by site ID: {} and type: {}", siteId, type);
		}
		return email;
	}

	public SubscriptionEmailAppearance fetchSubscriptionEmailAppearanceByEmailIdAndType(final int emailId,
			final int langId)
	{
		SubscriptionEmailAppearance emailAppearance = null;
		if (emailId < 1 || langId < 1)
		{
			log.info("Email id / language Id is not valid, no subscription email appearance will be fetched");
		}
		else
		{
			log.debug("Attempting to fetch subscription email appearance by email ID: {} and language ID: {}", emailId,
					langId);
			emailAppearance = dao.fetchSubscriptionEmailAppearanceByEmailIdAndLanguageId(emailId, langId);
			log.debug("Successfully fetched subscription email appearance by email ID: {} and language ID: {}", emailId,
					langId);
		}
		return emailAppearance;
	}

	public AffiliateCustomEmail fetchAffiliateCustomEmail(int langId, int affiliateId)
	{
		AffiliateCustomEmail affiliateCustomEmail = null;
		if (langId < 1)
		{
			log.debug("Attempting to fetch affiliateCustomEmail lang ID failed, language id is not greater than zero");
		}
		else if (affiliateId < 1)
		{
			log.debug(
					"Attempting to fetch affiliateCustomEmail affiliate  ID failed, affiliate id is not greater than zero");
		}
		else
		{
			affiliateCustomEmail = dao.fetchActivateAccountEmail(langId, affiliateId);
		}
		return affiliateCustomEmail;
	}

	public Emails fetchEmails(int siteId, int currentLanguageId, int defaultLanguageId)
	{
		List<Emails> emailList = null;
		Emails emailReturn = null;
		if (siteId < 1)
		{
			log.debug("Attempting to fetch ab_Emails failed, site Id not greater than zero");
		}
		else
		{
			emailList = dao.fetchEmails(siteId);
			if (emailList.isEmpty())
			{
				log.debug("No email templates set for email.");
				return null;
			}

			for (Emails emailTemplate : emailList)
			{
				if (emailTemplate.getLanguageId() == currentLanguageId)
				{
					emailReturn = emailTemplate;
					break;
				}
			}
			if (emailReturn == null)
			{
				for (Emails emailTemplate : emailList)
				{
					if (emailTemplate.getLanguageId() == defaultLanguageId)
					{
						emailReturn = emailTemplate;
						break;
					}
				}
			}
			if (emailReturn == null)
			{
				log.debug("No email templates set for email, the email will not be sent");
			}
		}
		return emailReturn;
	}

	public boolean addToQueue(final EmailQueue emailQueue)
	{
		boolean saved = false;
		if (emailQueue != null)
		{
			log.debug("Attempting to add subscription email to queue");
			dao.addToQueue(emailQueue);
			saved = true;
			log.debug("Email added successfully to queue");
		}
		else
		{
			log.debug("Email queue is null, email queue will not be saved");
		}
		return saved;
	}

	public boolean addToQueue(final Sites site, final String customerName, final SubscriptionEmailAppearance appearance,
			final String emailAddress, final String body)
	{
		return addToQueue(createEmailQueue(site, customerName, appearance, emailAddress, body));
	}

	public EmailQueue createEmailQueue(final Sites site, final String customerName,
			final SubscriptionEmailAppearance appearance, final String address, final String body)
	{
		log.debug("Attempting to create subscription email queue");
		EmailQueue emailQueue = new EmailQueue();
		emailQueue.setFromAddress(appearance.getSenderAddress());
		emailQueue.setFromName(HtmlUtil.htmlUnescape(appearance.getSenderName()));
		emailQueue.setToAddress(address);
		emailQueue.setToName(customerName);
		emailQueue.setSubject(HtmlUtil.htmlUnescape(appearance.getSubject()));
		emailQueue.setBody(body);
		emailQueue.setCreated(DateUtil.localDateTimeToTimestamp(DateUtil.nowLocalDateTime(site.getTimezone())));
		emailQueue.setSiteId(site.getId());
		emailQueue.setSent((short) 0);
		log.debug("Subscription email queue created successfully");
		return emailQueue;
	}
}