package com.kmp.aeroparker.application.validator;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.i18n.LanguageFieldsList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PasswordChoiceValidator
{

	public static enum ValidatePasswordResponseEnum
	{
		INCORRECT_LENGTH, MUST_CONTAIN_NUMBER, MUST_CONTAIN_CAPITAL, PASSWORD_IS_VALID;

		public String getValidationMessage(final LanguageFieldsList languageFieldsList)
		{
			switch (this)
			{
				case INCORRECT_LENGTH:
					return languageFieldsList.getTranslation("Password must be between 6 and 15 characters in length");
				case MUST_CONTAIN_CAPITAL:
					return languageFieldsList.getTranslation("Password must contain at least 1 capital letter");
				case MUST_CONTAIN_NUMBER:
					return languageFieldsList.getTranslation("Password must contain at least 1 number");
				case PASSWORD_IS_VALID:
					return languageFieldsList.getTranslation("Password is valid");
			}

			log.error("failed to get error message for enum " + this);

			return "";
		}
	};

	public ValidatePasswordResponseEnum validatePassword(final String password)
	{
		if ((password.isEmpty()) || password.length() < 6 || password.length() > 15)
		{
			return ValidatePasswordResponseEnum.INCORRECT_LENGTH;
		}
		if (!password.matches(".*[0-9].*"))
		{
			return ValidatePasswordResponseEnum.MUST_CONTAIN_NUMBER;
		}
		if (!password.matches(".*[A-Z].*"))
		{
			return ValidatePasswordResponseEnum.MUST_CONTAIN_CAPITAL;
		}
		return ValidatePasswordResponseEnum.PASSWORD_IS_VALID;
	}
}
