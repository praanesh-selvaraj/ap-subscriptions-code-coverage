package com.kmp.aeroparker.application.email.dispatcher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmailAppearance;

import io.github.benas.randombeans.api.EnhancedRandom;

class EmailAppearanceValidatorTest
{
	private EmailAppearanceValidator emailValidator = new EmailAppearanceValidator();
	private SubscriptionEmailAppearance appearance = EnhancedRandom.random(SubscriptionEmailAppearance.class);

	@Test
	void testValidateAppearance_Null()
	{
		assertFalse(emailValidator.validateAppearance(null));
	}

	@Test
	void testValidateAppearance()
	{
		assertTrue(emailValidator.validateAppearance(appearance));
	}

	@Test
	void testValidateAppearance_SenderName_Empty()
	{
		appearance.setSenderName("");
		assertFalse(emailValidator.validateAppearance(appearance));
	}

	@Test
	void testValidateAppearance_SenderAddress_Empty()
	{
		appearance.setSenderAddress("");
		assertFalse(emailValidator.validateAppearance(appearance));
	}

	@Test
	void testValidateAppearance_Subject_Empty()
	{
		appearance.setSubject("");
		assertFalse(emailValidator.validateAppearance(appearance));
	}

	@Test
	void testValidateAppearance_Body_Empty()
	{
		appearance.setBody("");
		assertFalse(emailValidator.validateAppearance(appearance));
	}
}