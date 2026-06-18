package com.kmp.aeroparker.subscription.payments.refund.processor;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.RefundProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardClient;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardJsonFactory;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardObjectFactory;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardRefundResponseValidator;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardRequestParameters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WirecardRefundProcessor extends AbstractRefundProcessor implements RefundProcessor
{
	private final WirecardObjectFactory wirecardObjectFactory;
	private final WirecardJsonFactory jsonFactory;
	private final WirecardClient wirecardClient;
	private final WirecardRefundResponseValidator refundResponseValidator;

	public WirecardRefundProcessor(final PaymentService paymentService, final WirecardObjectFactory wirecardObjectFactory,
			final WirecardJsonFactory jsonFactory, final WirecardClient wirecardClient, final WirecardRefundResponseValidator refundResponseValidator)
	{
		super(paymentService);
		this.wirecardObjectFactory = wirecardObjectFactory;
		this.jsonFactory = jsonFactory;
		this.wirecardClient = wirecardClient;
		this.refundResponseValidator = refundResponseValidator;
	}

	@Override
	public boolean process(final int affId, final Payments payment, final BigDecimal amount, final String timeZone)
	{
		boolean status = false;
		if (payment != null)
		{
			if (amount.compareTo(BigDecimal.ZERO) > 0)
			{
				WirecardCredentials wirecardCredentials =
						(WirecardCredentials) paymentService.fetchPaymentCredentials(affId, 0, PaymentGatewayType.WIRECARD);

				if (wirecardCredentials != null)
				{
					Map<String, String> paymentcustomValues = paymentService.fetchPaymentCustomValueByPaymentId(payment.getId());
					WirecardRequestParameters requestParameters = wirecardObjectFactory.buildRefundRequestParameters(payment.getTransactionId(),
							amount, wirecardCredentials, paymentcustomValues);
					JsonObject jsonRequest = jsonFactory.generateJsonRequestDataForPayment(requestParameters, true, false);
					JsonObject response = wirecardClient.sendPaymentRequest(wirecardCredentials, jsonRequest);
					if (response != null)
					{
						if (refundResponseValidator.validate(response))
						{
							status = processRefund(payment, amount, timeZone);
						}
						else
						{
							log.debug("Wirecard validation failed");
						}
					}
					else
					{
						log.debug("No response from wirecard, we assume refund failed");
					}
				}
			}
			else if (amount.compareTo(BigDecimal.ZERO) == 0)
			{
				log.debug("The amount is 0, no refund needed returning true.");
				status = true;
			}
			else
			{
				log.debug("The refund amount was negative");
			}
		}
		else
		{
			log.error("Wirecard refund request didn't have a Payment");
		}
		log.debug("Finished processing Braintree refund.");
		return status;
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.WIRECARD;
	}
}