package com.kmp.aeroparker.subscription.payments.wirecard;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WirecardRefundResponseValidator
{
	public boolean validate(final JsonObject jsonResponse)
	{
		boolean valid = false;
		if (jsonResponse != null)
		{
			// The refund request was sent to Wirecard, see if they acknowledged
			// it.
			if (jsonResponse.has("payment"))
			{
				JsonObject paymentObject = jsonResponse.getAsJsonObject("payment");
				if (paymentObject.has("transaction-state"))
				{
					String transactionState = paymentObject.get("transaction-state")
							.getAsString();
					if ("success".equals(transactionState))
					{
						valid = true;
					}
					else
					{
						log.warn("Wirecard response for refund was rejected - " + paymentObject.toString());
					}
				}
			}
			else
			{
				log.warn("Wirecard response didn't have a payment object!");
			}
		}
		else
		{
			log.warn("Tried to validate a null Wirecard response");
		}
		return valid;
	}
}