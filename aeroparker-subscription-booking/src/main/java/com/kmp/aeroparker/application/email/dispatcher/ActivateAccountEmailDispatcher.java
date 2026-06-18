package com.kmp.aeroparker.application.email.dispatcher;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.contact.activation.ContactActivationProcessor;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.EmailService;
import com.kmp.aeroparker.application.model.EmailDispatcherParameters;
import com.kmp.aeroparker.application.model.interfaces.EmailDispatcher;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateCustomEmail;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.EmailQueue;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Emails;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component

public class ActivateAccountEmailDispatcher implements EmailDispatcher
{
	private final EmailService emailService;
	private final ContactService contactService;
	private final PlaceHolderReplacer placeHolderReplacer;
	private final ContactActivationProcessor contactActivationProcessor;
	private final GlobalProperties globalProperties;

	public void sendEmail(EmailDispatcherParameters emailDispatcherParameters)
	{
		int contactId = emailDispatcherParameters.getContactId();
		String timeZone = emailDispatcherParameters.getTimeZone();
		String servletSchema = emailDispatcherParameters.getServletSchema();
		String servletName = emailDispatcherParameters.getServletName();
		Sites site = emailDispatcherParameters.getSite();
		int currentLanguageId = emailDispatcherParameters.getCurrentLanguageId();
		int defaultLanguageId = emailDispatcherParameters.getDefaultLanguageId();
		Affiliates affiliates = emailDispatcherParameters.getAffiliate();

		Contacts contact = contactService.fetchById(contactId);
		if (contact != null && contact.getAccountCreated() == null)
		{
			String activactionCode = contactActivationProcessor.processContactActivation(contactId, timeZone);
			if (!StringUtil.isEmpty(activactionCode))
			{
				String url = servletSchema + "://" + servletName
						+ (globalProperties.isStaging() || globalProperties.isDev() ? "/Shop/" : "/Book/")
						+ affiliates.getCode() + "/MyAccount?cmd=activateAccount&code=" + activactionCode;
				EmailQueue emailQueue = new EmailQueue();

				// Build Email From affiliate
				if (emailDispatcherParameters.isEmailFromAffiliate())
				{
					AffiliateCustomEmail ace =
							emailService.fetchAffiliateCustomEmail(currentLanguageId, affiliates.getId());
					if (ace != null)
					{
						populateEmailQueue(ace.getSubject(), ace.getSenderAddress(), ace.getSenderName(),
								placeHolderReplacer.replacePlaceHoldersActivationEmail(ace.getBodyHtml(), contact, url),
								emailQueue);
					}
				}
				// If ENABLE_ACTIVATE_ACCOUNT_EMAIL is false OR if there is no element in affiliate table the emailQueue
				// is fetched from ab_emails
				if (StringUtil.isEmpty(emailQueue.getBody()))
				{
					Emails email = emailService.fetchEmails(site.getId(), currentLanguageId, defaultLanguageId);

					if (email != null)
					{
						populateEmailQueue(
								email.getSubject(), email.getSenderAddress(), email.getSenderName(), placeHolderReplacer
										.replacePlaceHoldersActivationEmail(email.getBodyHtml(), contact, url),
								emailQueue);
					}
				}
				// If the body is empty the email is not sent
				if (!StringUtil.isEmpty(emailQueue.getBody()))
				{
					emailQueue.setToAddress(contact.getEmailAddress());
					emailQueue.setToName(contact.getFirstName() + " " + contact.getLastName());
					emailQueue.setSent((short) 0);
					emailQueue.setSiteId(site.getId());

					emailService.addToQueue(emailQueue);
				}
			}
		}
	}

	private void populateEmailQueue(String subject, String fromAddress, String fromName, String body,
			EmailQueue emailQueue)
	{
		emailQueue.setSubject(subject);
		emailQueue.setFromAddress(fromAddress);
		emailQueue.setFromName(fromName);
		emailQueue.setBody(body);
	}
}