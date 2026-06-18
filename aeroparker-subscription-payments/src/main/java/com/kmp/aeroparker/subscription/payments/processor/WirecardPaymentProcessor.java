package com.kmp.aeroparker.subscription.payments.processor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.CustomValue;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentProcessor;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardPaymentResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class WirecardPaymentProcessor implements IPaymentProcessor
{
	private final PaymentService paymentService;

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.WIRECARD;
	}

	@Override
	public Payments process(final PaymentProcessorParameters paymentProcessorParameters)
	{
		log.debug("Inside wirecard payment processor");
		Payments payment = null;
		if (paymentProcessorParameters != null)
		{
			WirecardPaymentResponse paymentResponse = paymentProcessorParameters.getWirecardPaymentResponse();

			if (paymentResponse != null)
			{
				log.debug("Inserting wirecard payment");
				payment = paymentService.createPayment(paymentResponse.getTransactionId(), paymentResponse.getOrderNumber(),
						paymentResponse.getRequestedAmount(), getType().getId(), paymentProcessorParameters.getTimeZone());

				if (paymentService.savePayment(payment))
				{
					Map<String, String> customValues = createPaymentCustomValues(paymentProcessorParameters.getConfirmationGuid(), paymentResponse);
					if (paymentService.saveCustomValues(payment.getId(), customValues))
					{
						log.debug("Payment custom values inserted");
					}
				}
			}
		}
		return payment;
	}

	private Map<String, String> createPaymentCustomValues(final String confirmationGuid, final WirecardPaymentResponse paymentResponse)
	{
		log.debug("Processing payment custom values");
		Map<String, String> customValues = new HashMap<>();
		customValues.put(CustomValue.CURRENCY.getField(), paymentResponse.getRequestedAmountCurrency());
		customValues.put(CustomValue.FIRST_NAME.getField(), paymentResponse.getFirstName());
		customValues.put(CustomValue.LAST_NAME.getField(), paymentResponse.getLastName());
		customValues.put(CustomValue.CARD_NUMBER.getField(), paymentResponse.getMaskedAccountNumber());
		customValues.put(CustomValue.PAYMENT_TYPE.getField(), paymentResponse.getPaymentMethodName());
		customValues.put(CustomValue.TRANSACTION_AUTH_NO.getField(), paymentResponse.getAuthorizationCode());
		customValues.put(CustomValue.PARENT_TRANSACTION_ID.getField(), paymentResponse.getParentTransactionId());
		customValues.put(CustomValue.TRANSACTION_AUTH_NO.getField(), confirmationGuid);
		return customValues;
	}
}