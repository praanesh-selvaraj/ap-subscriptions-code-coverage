package com.kmp.aeroparker.application.processor;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.builder.ContactBuilder;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ContactProcessor implements IProcessor
{
	private final ContactBuilder builder;
	private final ContactService service;

	public int process(final SubscriptionBookingData bookingData)
	{
		int contactId = 0;

		Contacts contact = Optional.ofNullable(service.fetchContactByEmailAndSiteId(bookingData.getEmail(), bookingData.getSiteId()))
				.orElseGet(() -> new Contacts());

		builder.build(contact, bookingData);
		if (service.saveContact(contact))
		{
			contactId = contact.getId();
		}
		return contactId;
	}

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.CONTACT;
	}
}