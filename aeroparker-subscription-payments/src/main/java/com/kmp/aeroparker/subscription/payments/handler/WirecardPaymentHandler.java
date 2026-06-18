package com.kmp.aeroparker.subscription.payments.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.PaymentProcessorFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHandler;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsProcessedToken;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsWirecardResponse;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardClient;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardJsonFactory;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardObjectFactory;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardPaymentResponse;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardRequestParameters;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class WirecardPaymentHandler implements IPaymentHandler
{
	private static final int ZERO = 0;
	private final PaymentService paymentService;
	private final WirecardObjectFactory wirecardObjectFactory;
	private final WirecardJsonFactory wirecardJsonFactory;
	private final WirecardClient wirecardClient;
	private final PaymentProcessorFactory paymentProcessorFactory;

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.WIRECARD;
	}

	@Override
	public Map<String, Object> setUpTransaction(final PaymentGatewayParameters paymentHandlerParams)
	{
		log.info("Setting up wirecard transaction");
		Affiliates affiliate = paymentHandlerParams.getAffiliate();
		int affId = affiliate.getId();
		WirecardCredentials credentials = getCredentials(affId, ZERO, getType());
		Map<String, Object> params = new HashMap<>();
		if (credentials != null)
		{
			PaymentGatewayTypes wirecardGatewayInfo = paymentService.fetchPaymentGatewayTypesById(getType().getId());
			params.put("seamlessLocale", paymentHandlerParams.getLanguage()
					.getLanguageCode());
			params.put("paymentGatewayType", getType().getId());
			params.put("currency", paymentHandlerParams.getCurrency());
			params.put("amount", paymentHandlerParams.getAmount());
			params.put("paymentHtml", wirecardGatewayInfo.getPaymentJsp()
					.replace(".jsp", ""));
			params.put("affiliateName", affiliate.getName()
					.replaceAll("'", "&#8217;"));
			params.put("payPalEnabled", credentials.isPayPalEnabled());
			params.put("sofortEnabled", credentials.isSofortEnabled());
			params.put("paymentJsLocation", credentials.getPaymentJsLocation());
			params.put("affiliateId", affId);
			log.debug("Finished setting up transaction, params list : ", params.toString());
		}
		else
		{
			log.warn("Error getting WirecardCredentials from database for affiliate: " + affId);
		}
		return params;
	}

	@Override
	public boolean processPayment(final SubscriptionBookingData bookingData)
	{
		log.info("Processing wirecard payment");
		boolean status = false;
		Payments payments = null;
		String paymentReference = bookingData.getPaymentReference();
		int affId = bookingData.getAffiliateId();

		if (!StringUtil.isEmpty(paymentReference))
		{
			PaymentsProcessedToken processedToken = paymentService.fetchProcessedToken(paymentReference);
			if (processedToken != null)
			{
				// If the paymentReference is already in the table, show an
				// error
				log.warn("Entry already exists.Aborting!");
			}
			else
			{
				WirecardCredentials wirecardCredentials =
						(WirecardCredentials) paymentService.fetchPaymentCredentials(affId, ZERO, PaymentGatewayType.WIRECARD);

				if (wirecardCredentials != null)
				{
					WirecardPaymentResponse wirecardPaymentResponse = doWirecardRequest(affId, bookingData, wirecardCredentials);

					if (wirecardPaymentResponse != null)
					{
						PaymentsWirecardResponse paymentResponse = wirecardObjectFactory.buildPaymentsWirecardResponse(wirecardPaymentResponse);

						if (!paymentService.insertWirecardResponse(paymentResponse))
						{
							log.info("Failed to write a response to the payments_wirecard_response table");
						}
						else
						{
							PaymentProcessorParameters paymentProcessorParameters = PaymentProcessorParameters.builder()
									.withWirecardPaymentResponse(wirecardPaymentResponse)
									.withConfirmationGuid(bookingData.getConfirmationGuid())
									.withTimeZone(bookingData.getTimeZone())
									.build();
							payments = paymentProcessorFactory.getInstance(getType())
									.process(paymentProcessorParameters);

							if (payments != null && payments.getId() != 0)
							{
								status = true;
							}
						}
					}
					else
					{
						log.debug("No response from wirecard");
					}
				}
				else
				{
					log.debug("No credentianls for wirecard found");
				}
			}
		}
		else
		{
			log.debug("No payment reference");
		}
		return status;
	}

	private WirecardPaymentResponse doWirecardRequest(final int affiliateId, final SubscriptionBookingData bookingData,
			final WirecardCredentials wirecardCredentials)
	{
		WirecardPaymentResponse paymentResponse = null;
		log.debug("Preparing wirecard request");
		WirecardRequestParameters requestParameters =
				wirecardObjectFactory.buildWirecardRequestParameters(wirecardCredentials.getMerchantId(), bookingData);
		JsonObject jsonRequest = wirecardJsonFactory.generateJsonRequestDataForPayment(requestParameters, false, bookingData.isRecurring());
		paymentResponse = mapToWirecardResponse(wirecardClient.sendPaymentRequest(wirecardCredentials, jsonRequest));
		return paymentResponse;
	}

	private WirecardPaymentResponse mapToWirecardResponse(final JsonObject response)
	{
		WirecardPaymentResponse paymentResponse = null;
		if (!StringUtil.isEmpty(response))
		{
			paymentResponse = wirecardJsonFactory.buildWirecardResponse(response);
		}
		return paymentResponse;
	}

	private WirecardCredentials getCredentials(final int affId, final int carParkId, final PaymentGatewayType type)
	{
		return (WirecardCredentials) paymentService.fetchPaymentCredentials(affId, carParkId, type);
	}
}