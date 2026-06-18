package com.kmp.aeroparker.subscription.payments.wirecard;

import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class WirecardReponseValidator
{
	private final WirecardResponsePaymentElementValidator paymentElementValidator;

	public boolean validatePaymentJsonResponse(final JsonObject jsonResponse)
	{
		boolean valid = false;
		if (jsonResponse != null && jsonResponse.has("payment"))
		{
			JsonObject payment = jsonResponse.getAsJsonObject("payment");
			if (payment.has("statuses"))
			{
				if (!payment.getAsJsonObject("statuses")
						.has("status"))
				{
					log.info("Couldn't save Wirecard response, there was no statuses: " + jsonResponse);
				}
				else
				{
					JsonArray statuses = payment.get("statuses")
							.getAsJsonObject()
							.getAsJsonArray("status");
					if (statuses.size() < 1)
					{
						log.warn("payment status was empty!");
					}
					else
					{
						JsonObject paymentStatus = statuses.get(0)
								.getAsJsonObject();
						if (!paymentStatus.get("severity")
								.getAsString()
								.equals("error"))
						{
							if (!paymentStatus.has("code"))
							{
								log.warn("payment status code wasn't sent!");
							}
							else
							{
								if (!paymentStatus.has("description"))
								{
									log.warn("payment status description wasn't sent!");
								}
								else
								{
									String description = paymentStatus.get("description")
											.getAsString();
									String code = paymentStatus.get("code")
											.getAsString();
									if (!code.matches("20.*"))
									{
										log.warn("payment status return code was " + code + "; description = " + description);
									}
									else
									{
										if (paymentElementValidator.validate(payment))
										{
											valid = true;
										}
									}
								}
							}
						}
						else
						{
							log.info("Error returned: " + jsonResponse);
						}
					}
				}
			}
			else
			{
				log.info("Couldn't save Wirecard response, there was no statuses: " + jsonResponse);
			}
		}
		return valid;
	}
}