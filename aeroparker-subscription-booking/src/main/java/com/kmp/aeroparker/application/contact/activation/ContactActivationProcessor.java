package com.kmp.aeroparker.application.contact.activation;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactsActivationCodes;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ContactActivationProcessor
{
	private final ContactService contactService;

	public String processContactActivation(int contactId, String timeZone)
	{
		String activationCode = "";

		ContactsActivationCodes contactsActivationCodes =
				Optional.ofNullable(contactService.getActivationCode(contactId))
						.orElseGet(() -> createNewContactsActivationCodes(contactId, timeZone));
		activationCode = contactsActivationCodes.getActivationCode();

		return activationCode;
	}

	public ContactsActivationCodes createNewContactsActivationCodes(int contactId, String timeZone)
	{
		ContactsActivationCodes activationCodes = new ContactsActivationCodes();
		activationCodes.setActivationCode(generateActivationCode());
		activationCodes.setContactId(contactId);
		activationCodes.setCreated(DateUtil.localDateTimeToTimestamp(DateUtil.nowLocalDateTime((timeZone))));

		insertActivationCodeInDb(activationCodes);

		return activationCodes;
	}

	private String generateActivationCode()
	{
		return UUID.randomUUID()
				.toString()
				.replaceAll("-", "");
	}

	private boolean insertActivationCodeInDb(ContactsActivationCodes contactsActivationCodes)
	{
		return contactService.insertActivationCode(contactsActivationCodes);
	}
}