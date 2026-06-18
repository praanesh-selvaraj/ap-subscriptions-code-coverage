package com.kmp.aeroparker.subscription.payments.wirecard;

import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.enums.PaymentMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WirecardResponsePaymentElementValidator
{
	public boolean validate(final JsonObject payment)
	{
		boolean valid = true;

		if (!payment.has("transaction-state"))
		{
			valid = false;
			log.warn("payment json didn't have a transaction-state!");
		}

		if (valid && !payment.get("transaction-state")
				.getAsString()
				.equals("success"))
		{
			valid = false;
			log.error("A wirecard response came back not 'success': " + payment.get("transaction-state")
					.getAsString());
		}
		String payMethodStr = "";
		if (valid && !payment.has("payment-methods") || !payment.get("payment-methods")
				.getAsJsonObject()
				.has("payment-method"))
		{
			valid = false;
			log.warn("payment json didn't have a payment-methods!");
		}
		else
		{
			JsonArray methods = payment.get("payment-methods")
					.getAsJsonObject()
					.get("payment-method")
					.getAsJsonArray();

			if (methods.size() < 1)
			{
				valid = false;
				log.warn("payment json didn't have a payment-methods!");
			}
			else
			{
				JsonElement payMethod = methods.get(0);
				if (payMethod.isJsonNull())
				{
					valid = false;
					log.warn("We didn't recognise the Wirecard reponse payment type, we cannot continue processing");
				}
				else
				{
					payMethodStr = payMethod.getAsJsonObject()
							.get("name")
							.getAsString();
				}
			}
		}

		if (valid && !PaymentMethod.getPaymentMethod(payMethodStr)
				.equals(PaymentMethod.CARD))
		{
			valid = false;
			log.debug("PAYMENT METHOD IS NOT CARD, WE ONLY ACCEPT CARD");
		}

		log.debug("Checking a response for a " + payMethodStr + " payment-method");

		String notificationType = payment.get("transaction-type")
				.getAsString();
		log.debug("wirecardNotification transaction-type was " + notificationType);

		if (valid && !payment.has("transaction-id"))
		{
			valid = false;
			log.error("A Wirecard payment notification doesn't have the transactionid!");
		}

		if (valid && !payment.has("request-id"))
		{
			valid = false;
			log.error("A Wirecard payment notification doesn't have the request-id!");
		}

		JsonObject amount = payment.getAsJsonObject("requested-amount");
		if (valid && amount == null)
		{
			valid = false;
			log.warn("requested-amount is null!");
		}

		if (valid && !amount.has("value"))
		{
			valid = false;
			log.warn("amount value wasn't found in Wirecard's requested-amount json - " + amount.toString());
		}

		if (valid && !amount.has("currency"))
		{
			valid = false;
			log.warn("amount currency wasn't found in Wirecard's requested-amount json - " + amount.toString());
		}
		return valid;
	}
}