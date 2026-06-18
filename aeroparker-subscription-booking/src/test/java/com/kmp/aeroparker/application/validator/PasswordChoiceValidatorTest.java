package com.kmp.aeroparker.application.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.validator.PasswordChoiceValidator.ValidatePasswordResponseEnum;
import com.kmp.aeroparker.i18n.LanguageFieldsList;


@ExtendWith(MockitoExtension.class)
class PasswordChoiceValidatorTest
{
	@InjectMocks
	PasswordChoiceValidator passwordValidator;

	@Test
	public void testGetValidationMessage()
	{
		final LanguageFieldsList languageFieldsList = mock(LanguageFieldsList.class);
		when(languageFieldsList.getTranslation(anyString())).thenReturn("Hello");

		for (final ValidatePasswordResponseEnum value : ValidatePasswordResponseEnum.values())
		{
			assertEquals("Hello", value.getValidationMessage(languageFieldsList));
		}
	}
	
	@Test
	public void testValidatePassword()
	{
		assertEquals(ValidatePasswordResponseEnum.INCORRECT_LENGTH, passwordValidator.validatePassword(""));
		assertEquals(ValidatePasswordResponseEnum.INCORRECT_LENGTH, passwordValidator.validatePassword(""));
		assertEquals(ValidatePasswordResponseEnum.INCORRECT_LENGTH, passwordValidator.validatePassword("Pass1"));
		assertEquals(ValidatePasswordResponseEnum.INCORRECT_LENGTH,
				passwordValidator.validatePassword("Password1Password1Password1"));
		assertEquals(ValidatePasswordResponseEnum.MUST_CONTAIN_CAPITAL,
				passwordValidator.validatePassword("password1"));
		assertEquals(ValidatePasswordResponseEnum.MUST_CONTAIN_NUMBER, passwordValidator.validatePassword("Password"));
		assertEquals(ValidatePasswordResponseEnum.PASSWORD_IS_VALID, passwordValidator.validatePassword("Password1"));
	}
}