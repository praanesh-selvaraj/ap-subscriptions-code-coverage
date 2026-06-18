package com.kmp.aeroparker.application.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.kmp.aeroparker.application.model.SubscriptionConfigBean;

@ExtendWith(MockitoExtension.class)
class ManageSubscriptionAjaxControllerTest
{
	@Mock
	private Model model;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private FreemarkerSubmitter freemarkerSubmitter;

	@InjectMocks
	private ManageSubscriptionAjaxController controller;

	@Test
	void testResendEmailConfirmation()
	{
		when(freemarkerSubmitter.resendConfirmationEmail(any(), any(), anyString(), any())).thenReturn("{\"sent\":true}");

		assertEquals(controller.resendConfirmationEmail(httpServletRequest, model, "reference", "test@aeroparker.com"),
				"{\"sent\":true}");
	}

	@Test
	void testResendEmailConfirmation_FalseReturned()
	{
		when(freemarkerSubmitter.resendConfirmationEmail(any(), any(), anyString(), any())).thenReturn("{\"sent\":false}");

		assertEquals(controller.resendConfirmationEmail(httpServletRequest, model, "reference", "test@aeroparker.com"),
				"{\"sent\":false}");
	}
}
