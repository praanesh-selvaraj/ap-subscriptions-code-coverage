package com.kmp.aeroparker.application.email.dispatcher;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.EmailService;
import com.kmp.aeroparker.application.model.EmailDispatcherParameters;
import com.kmp.aeroparker.application.model.ReplacePlaceholdersParameters;
import com.kmp.aeroparker.application.model.enums.SubscriptionEmailType;
import com.kmp.aeroparker.application.model.interfaces.EmailDispatcher;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmail;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmailAppearance;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ConfirmationEmailDispatcher implements EmailDispatcher
{
	private final EmailService emailService;
	private final ContactService contactService;
	private final EmailAppearanceValidator validator;
	private final PlaceHolderReplacer placeHolderReplacer;
	private final Localise localise;
	private final LanguageFieldsList languageFieldsList;

	@Override
	public void sendEmail(final EmailDispatcherParameters emailDispatcherObj)
	{
		SubscriptionEmailType emailType = emailDispatcherObj.getEmailType();
		Sites site = emailDispatcherObj.getSite();
		int currentLanguageId = emailDispatcherObj.getCurrentLanguageId();
		int defaultLanguageId = emailDispatcherObj.getDefaultLanguageId();
		SubscriptionBookingRecord bookingRecord = emailDispatcherObj.getBookingRecord();

		int siteId = site.getId();
		SubscriptionEmail email = emailService.fetchSubscriptionEmailBySiteAndType(siteId, emailType.toString());

		if (email != null)
		{
			SubscriptionEmailAppearance appearance =
					Optional.ofNullable(emailService.fetchSubscriptionEmailAppearanceByEmailIdAndType(email.getId(), currentLanguageId))
							.orElseGet(() -> emailService.fetchSubscriptionEmailAppearanceByEmailIdAndType(email.getId(), defaultLanguageId));

			if (validator.validateAppearance(appearance))
			{
				SubscriptionBooking booking = bookingRecord.getBooking();
				SubscriptionBookingCustomerDetails bookingCustomerDetails = bookingRecord.getCustomerDetails();
				Contacts contact = contactService.fetchById(booking.getContactId());
				if (contact != null)
				{
					String customerName = contact.getFirstName() + " " + contact.getLastName();
					ReplacePlaceholdersParameters replacePlaceholdersParameters = ReplacePlaceholdersParameters.builder()
							.withAffiliate(emailDispatcherObj.getAffiliate())
							.withAppearance(appearance)
							.withBookingRecord(bookingRecord)
							.withLanguageFieldsList(languageFieldsList)
							.withLocalise(localise)
							.withSite(site)
							.build();
					String body = placeHolderReplacer.replacePlaceHolders(replacePlaceholdersParameters);
					if (!StringUtil.isEmpty(body))
					{
						send(site, customerName, bookingCustomerDetails.getEmailAddress(), appearance, body);
					}
					else
					{
						log.debug("Body empty, no email will be sent");
					}
				}
				else
				{
					log.debug("No existing contact, no email will be sent");
				}
			}
			else
			{
				log.debug("Email appearance validation failed, no email will be sent");
			}
		}
		else
		{
			log.debug("No Email set up, no email will be sent");
		}
	}

	private void send(final Sites site, final String customerName, final String emailAddress, final SubscriptionEmailAppearance appearance,
			final String body)
	{
		if (emailService.addToQueue(site, customerName, appearance, emailAddress, body))
		{
			log.info("Email sent successfully");
		}
	}
}