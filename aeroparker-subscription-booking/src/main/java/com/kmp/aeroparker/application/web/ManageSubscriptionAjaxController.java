package com.kmp.aeroparker.application.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kmp.aeroparker.application.model.SubscriptionConfigBean;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class ManageSubscriptionAjaxController
{
	private final SubscriptionConfigBean requestBean;
	private final FreemarkerSubmitter freemarkerSubmitter;

	@PostMapping(value = "/resend-confirmation")
	@ResponseBody
	public String resendConfirmationEmail(HttpServletRequest req, final Model model,
			@RequestParam(value = "reference") final String reference,
			@RequestParam(value = "email") final String email)
	{
		return freemarkerSubmitter.resendConfirmationEmail(req, model, reference, requestBean);
	}
}
