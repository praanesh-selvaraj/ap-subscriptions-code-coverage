package com.kmp.aeroparker.subscription.payments.wirecard;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.security.utils.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WirecardAjaxResponseHelper
{
	@Value("${url.context.path:/subscriptions}")
	private String contextPath;

	/**
	 * Called as part of doSetupTransaction; ensures we are able to use the
	 * Wirecard payment form.
	 * 
	 * @param context
	 * @param amount
	 * @param currency
	 * @param credentials
	 * @return
	 */
	public WirecardAjaxResponse generateJsonRequestDataForDisplayPaymentForm(final String amount, final String currency,
			final WirecardCredentials credentials)
	{
		WirecardAjaxResponse ajaxResponse = new WirecardAjaxResponse();
		String requestTime = LocalDateTime.parse(Instant.now()
				.toString()
				.replaceAll("Z", ""))
				.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String requestId = UUID.randomUUID()
				.toString();

		ajaxResponse.setRequest_time_stamp(requestTime);
		ajaxResponse.setRequest_id(requestId);
		ajaxResponse.setMerchant_account_id(credentials.getMerchantId());
		ajaxResponse.setTransaction_type("authorization-only");
		ajaxResponse.setRequested_amount("0.00");
		ajaxResponse.setRequested_amount_currency(currency);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(requestTime);
		stringBuilder.append(requestId);
		stringBuilder.append(credentials.getMerchantId());
		stringBuilder.append("authorization-only");
		stringBuilder.append("0.00");
		stringBuilder.append(currency);

		String requestSignature = SecurityUtil.tosha256(stringBuilder.toString() + credentials.getSecret());
		ajaxResponse.setRequest_signature(requestSignature);

		// Some params not required for the hash here
		ajaxResponse.setPayment_method("creditcard");
		ajaxResponse.setAmount(amount);
		ajaxResponse.setServletcontext(contextPath);

		log.debug("Response generated to display payment form");
		return ajaxResponse;
	}
}