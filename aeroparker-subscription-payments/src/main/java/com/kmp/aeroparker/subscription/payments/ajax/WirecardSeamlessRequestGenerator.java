package com.kmp.aeroparker.subscription.payments.ajax;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardAjaxResponse;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardAjaxResponseHelper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class WirecardSeamlessRequestGenerator
{
	private final PaymentService paymentService;
	private final WirecardAjaxResponseHelper wirecardRequestHandler;

	@PostMapping(value = ("/seamless/generate"))
	@ResponseBody
	public WirecardAjaxResponse processRequest(@RequestParam(value = "cmd") final String cmd,
			@RequestParam(value = "affiliateId") final int affiliateId, @RequestParam(value = "currency") final String currency,
			@RequestParam(value = "amount") final String amount)
	{
		WirecardAjaxResponse ajaxResponse = new WirecardAjaxResponse();

		if (affiliateId == 0)
		{
			log.info("affiliateId wasn't set in the request, we cannot load WirecardCredentials i.e. save a payment!");
		}
		else
		{
			WirecardCredentials credentials =
					(WirecardCredentials) paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.WIRECARD);
			if (credentials != null)
			{
				ajaxResponse = getSeamlessResponse(cmd, credentials, amount, currency);
			}
			else
			{
				log.info("WirecardCredentials could not be retreieved from the DB!");
			}
		}
		return ajaxResponse;
	}

	private WirecardAjaxResponse getSeamlessResponse(final String cmd, final WirecardCredentials credentials, final String amount,
			final String currency)
	{
		WirecardAjaxResponse ajaxResponse = new WirecardAjaxResponse();
		if (cmd.equals("init"))
		{
			ajaxResponse = wirecardRequestHandler.generateJsonRequestDataForDisplayPaymentForm(amount, currency, credentials);
		}
		else
		{
			log.info("Wirecard getSeamlessResponse was called, but the cmd wasn't init: " + cmd);
		}

		return ajaxResponse;
	}
}