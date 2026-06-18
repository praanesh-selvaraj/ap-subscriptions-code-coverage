package com.kmp.aeroparker.application.processor;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.application.validator.PasswordChoiceValidator;
import com.kmp.aeroparker.application.validator.PasswordChoiceValidator.ValidatePasswordResponseEnum;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactHashedPassword;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class PasswordChoiceProcessor implements IProcessor
{
	private final ContactService service;
	private final PasswordChoiceValidator passwordChoiceValidator;
	private final LanguageFieldsList languageFieldsList;

	public boolean process(final int contactId, String password)
	{
		boolean processPassword = false;
		if (validatePassword(languageFieldsList, password).isEmpty())
		{
			// check if user already has a password
			ContactHashedPassword hashedPassword = service.fetchContactHashedPasswordByContactId(contactId);
			if (hashedPassword == null)
			{
				log.info("Creating hashed password for contact ID " + contactId);
				// Hash the password.
				final String createHashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
				// Set contact to be activated
				if (service.setContactActivated(contactId))
				{
					// Save it in the DB, this will carry out an insert on duplicate key update
					processPassword = service.saveHashedPassword(contactId, createHashedPassword);
					return processPassword;
				}
			}
			else
			{
				processPassword = true;
			}
		}
		else
		{
			log.error("Password entered is incorrect");
			return false;
		}
		return processPassword;
	}

	public Map<String, String> validatePassword(final LanguageFieldsList languageFieldList, String password)
	{
		final Map<String, String> errorList = new HashMap<>();
		final ValidatePasswordResponseEnum responseEnum = passwordChoiceValidator.validatePassword(password);
		if (responseEnum != ValidatePasswordResponseEnum.PASSWORD_IS_VALID)
		{
			errorList.put("loginGlobalAlert", responseEnum.getValidationMessage(languageFieldsList));
		}
		return errorList;
	}

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.PASSWORD_CHOICE;
	}
}
