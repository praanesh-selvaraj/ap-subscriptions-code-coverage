package com.kmp.aeroparker.application.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.validator.PasswordChoiceValidator;
import com.kmp.aeroparker.application.validator.PasswordChoiceValidator.ValidatePasswordResponseEnum;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.ContactHashedPassword;

@ExtendWith(MockitoExtension.class)
class PasswordChoiceProcessorTest
{
	@Mock
	private ContactService service;
	@Mock
	private PasswordChoiceValidator passwordChoiceValidator;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@InjectMocks
	private PasswordChoiceProcessor processor;

	@Test
	void testProcess()
	{
		ContactHashedPassword hashedPassword = mock(ContactHashedPassword.class);
		when(service.fetchContactHashedPasswordByContactId(anyInt())).thenReturn(hashedPassword);
		when(passwordChoiceValidator.validatePassword(anyString()))
				.thenReturn(ValidatePasswordResponseEnum.PASSWORD_IS_VALID);
		assertThat(processor.process(1, "ABC")).isTrue();
		verify(service).fetchContactHashedPasswordByContactId(anyInt());
	}

	@Test
	void testProcess_EmptyList()
	{
		when(passwordChoiceValidator.validatePassword(anyString()))
				.thenReturn(ValidatePasswordResponseEnum.MUST_CONTAIN_CAPITAL);
		assertThat(processor.process(1, "ABC")).isFalse();
	}
	
	@Test
	void testProcess_EmptyPassword()
	{
		ContactHashedPassword hashedPassword = mock(ContactHashedPassword.class);
		when(service.fetchContactHashedPasswordByContactId(anyInt())).thenReturn(hashedPassword);
		when(passwordChoiceValidator.validatePassword(anyString()))
				.thenReturn(ValidatePasswordResponseEnum.PASSWORD_IS_VALID);
		assertThat(processor.process(1, "")).isTrue();
		verify(service).fetchContactHashedPasswordByContactId(anyInt());
	}

	@Test
	void testProcess_when_contactActivated_false()
	{
		when(service.fetchContactHashedPasswordByContactId(anyInt())).thenReturn(null);
		when(passwordChoiceValidator.validatePassword(anyString()))
				.thenReturn(ValidatePasswordResponseEnum.PASSWORD_IS_VALID);
		when(service.setContactActivated(anyInt())).thenReturn(false);
		assertThat(processor.process(1, "ABC")).isFalse();
		verify(service).fetchContactHashedPasswordByContactId(anyInt());
		verify(service).setContactActivated(anyInt());
	}
	
	@Test
	void testProcess_when_Hashedpassword_null()
	{
		when(service.fetchContactHashedPasswordByContactId(anyInt())).thenReturn(null);
		when(passwordChoiceValidator.validatePassword(anyString()))
				.thenReturn(ValidatePasswordResponseEnum.PASSWORD_IS_VALID);
		when(service.setContactActivated(anyInt())).thenReturn(true);
		when(service.saveHashedPassword(anyInt(), anyString())).thenReturn(true);
		assertThat(processor.process(1, "ABC")).isTrue();
		verify(service).fetchContactHashedPasswordByContactId(anyInt());
		verify(service).setContactActivated(anyInt());
		verify(service).setContactActivated(anyInt());
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(ProcessorType.PASSWORD_CHOICE);
	}
}