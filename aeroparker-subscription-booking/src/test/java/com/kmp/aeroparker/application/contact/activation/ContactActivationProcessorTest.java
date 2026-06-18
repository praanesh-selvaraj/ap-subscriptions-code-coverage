package com.kmp.aeroparker.application.contact.activation;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactsActivationCodes;

@ExtendWith(MockitoExtension.class)
class ContactActivationProcessorTest
{
	@Mock
	private ContactService contactService;
	@InjectMocks
	private ContactActivationProcessor contactActivationProcessor;

	@Test
	void testProcessContactActivation()
	{
		ContactsActivationCodes contactsActivationCodes = mock(ContactsActivationCodes.class);
		when(contactsActivationCodes.getActivationCode()).thenReturn("1");
		when(contactService.getActivationCode(anyInt())).thenReturn(contactsActivationCodes);

		contactActivationProcessor.processContactActivation(1, "gla");

		verify(contactService).getActivationCode(anyInt());
		verify(contactsActivationCodes).getActivationCode();
	}

	@Test
	void testProcessContactActivation_New_Activation_Code()
	{
		when(contactService.getActivationCode(anyInt())).thenReturn(null);
		assertNotNull("Shouldn't be null", contactActivationProcessor.processContactActivation(10, "gla"));
	}

	@Test
	void testCreateNewContactsActivationCodes()
	{
		ContactsActivationCodes activationCodes =
				contactActivationProcessor.createNewContactsActivationCodes(10, "gla");

		assert (10 == activationCodes.getContactId());
		assert (activationCodes.getActivationCode() != "");
	}
}