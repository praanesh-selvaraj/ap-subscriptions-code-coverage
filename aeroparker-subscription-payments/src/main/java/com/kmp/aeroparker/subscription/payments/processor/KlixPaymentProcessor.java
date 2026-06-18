package com.kmp.aeroparker.subscription.payments.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.CustomValue;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentProcessor;
import com.kmp.aeroparker.subscription.payments.klix.KlixResponse;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class KlixPaymentProcessor implements IPaymentProcessor
{
	private final PaymentService paymentService;

	@Override
	public Payments process(PaymentProcessorParameters paymentProcessorParameters)
	{
		log.info("Inside Klix payment processor");
		Payments payment = null;

		if (paymentProcessorParameters != null)
		{
			KlixResponse response = paymentProcessorParameters.getKlixResponse();
			if (response != null && response.getId() != null)
			{
				payment = paymentService.createPayment(response.getId(), response.getBookingReference(),
						new BigDecimal(response.getAmount()).setScale(2, RoundingMode.HALF_UP)
								.toPlainString(),
						PaymentGatewayType.KLIX.getId(), paymentProcessorParameters.getTimeZone());

				if (paymentService.savePayment(payment))
				{
					Map<String, String> customValues = createPaymentCustomValues(
							paymentProcessorParameters.getConfirmationGuid(), response, payment.getId());
					if (paymentService.saveCustomValues(payment.getId(), customValues))
					{
						log.debug("Payment custom values inserted");
					}
				}
			}
		}
		return payment;
	}

	private Map<String, String> createPaymentCustomValues(String confirmationGuid, KlixResponse response, int paymentId)
	{
		Map<String, String> customValues = new HashMap<>();

		log.info("Saving Klix transaction for payment {}", paymentId);
		customValues.put(CustomValue.CARD_NUMBER.getField(), response.getMaskedPan());
		customValues.put(CustomValue.EMAIL_ADDRESS.getField(), response.getEmail());

		return customValues;
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.KLIX;
	}
}
