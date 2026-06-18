package com.kmp.aeroparker.subscription.payments.klix;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.KlixSession;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class KlixRequestHandler
{
	private final PaymentService paymentService;
	private final KlixHttpClient client;
	private final KlixRequestBuilder requestBuilder;
	private final KlixResponseBuilder responseBuilder;

	public KlixResponse sendInitialRequest(KlixRequest parameters)
	{
		log.info("Attempting to send initial request to Klix");
		KlixResponse responseParameters = null;
		KlixCredentials credentials = parameters.getCredentials();
		if (credentials != null)
		{
			JsonObject klixRequestJson = requestBuilder.generateJsonForInitialRequest(parameters);
			log.info("Initial Klix request: " + klixRequestJson.toString());
			responseParameters =
					responseBuilder.buildResponseParameters(client.sendPaymentRequest(credentials, klixRequestJson));
			insertKlixSession(parameters.getGuid(), parameters.getAffiliate()
					.getId(), parameters.getBookingReference(), new BigDecimal(parameters.getAmount()).movePointLeft(2),
					responseParameters.getId(), false, parameters.getOriginalBookingReference(),
					responseParameters.getStatus(), responseParameters.getResponseType(),
					responseParameters.getCurrency(), responseParameters.isRecurringToken(),
					responseParameters.isRecurringExecute());
		}
		else
		{
			log.info("Unable to send initial request: credentials parameter was null");
		}
		return responseParameters;
	}

	public KlixResponse sendRefundRequest(KlixCredentials credentials, String paymentId, String bookingReference,
			int affiliateId, JsonObject body)
	{
		log.info("Attempting to send refund request to Klix");
		KlixResponse resp = null;
		if (credentials != null)
		{
			resp = responseBuilder.buildResponseParameters(client.sendRefundRequest(credentials, paymentId, body));
			insertKlixSession(UUID.randomUUID()
					.toString(), affiliateId, bookingReference, new BigDecimal(resp.getAmount()), resp.getId(), false,
					null, resp.getStatus(), resp.getPaymentType(), resp.getCurrency(), resp.isRecurringToken(), false);
		}
		else
		{
			log.info("Unable to send Klix refund request: credentials parameter is null");
		}
		return resp;
	}

	public KlixResponse getKlixResponse(int affiliateId, String bookingReference, KlixCredentials credentials,
			int carParkId)
	{
		log.info("Attempting to get response from Klix");
		KlixResponse responseParameters = null;
		if (credentials == null)
		{
			log.info("Unable to get Klix response: credentials parameter is null");
		}
		else
		{
			KlixSession klixSession =
					paymentService.fetchKlixSessionByReferenceAndAffiliateId(bookingReference, affiliateId);
			if (klixSession != null)
			{
				responseParameters = responseBuilder
						.buildResponseParameters(client.getKlixResponse(credentials, klixSession.getPaymentId()));
				responseParameters.setBookingReference(bookingReference);
				responseParameters.setOriginalBookingReference(klixSession.getOriginalBookingReference());
				responseParameters.setCarParkId(carParkId);
				responseParameters.setAmend(klixSession.getIsAmend());
			}
			else
			{
				log.info("Unable to fetch Klix Response Klix session for booking with reference {} was null",
						bookingReference);
			}
		}
		return responseParameters;
	}

	public List<KlixPaymentMethodGroup> getKlixPaymentMethods(KlixCredentials credentials, String currency)
	{
		List<KlixPaymentMethodGroup> paymentMethods = null;
		log.info("Attempting to get available payment methods from Klix");
		if (credentials == null)
		{
			log.info("Unable to get Klix payment methods: credentials parameter is null");
		}
		else if (StringUtil.isEmpty(currency))
		{
			log.info("Unable to get Klix payment methods: currency parameter is invalid");
		}
		else
		{
			paymentMethods = responseBuilder.buildPaymentMethods(client.getPaymentMethods(credentials, currency));
		}
		return paymentMethods;
	}

	public void insertKlixSession(String guid, int affiliateId, String bookingReference, BigDecimal amount,
			String paymentId, boolean isAmend, String originalBookingReference, String status, String sessionType,
			String currency, boolean isRecurringToken, boolean isRecurringExecute)
	{
		KlixSession klixSession = new KlixSession();
		klixSession.setGuid(guid);
		klixSession.setAffiliateId(affiliateId);
		klixSession.setBookingReference(bookingReference);
		klixSession.setOriginalBookingReference(originalBookingReference);
		klixSession.setAmount(amount);
		klixSession.setPaymentId(paymentId);
		klixSession.setCreatedOn(DateUtil.nowTimestamp());
		klixSession.setIsAmend(isAmend);
		klixSession.setStatus(status);
		klixSession.setSessionType(sessionType);
		klixSession.setCurrency(currency);
		klixSession.setRecurringToken(isRecurringToken);
		klixSession.setRecurringExecute(isRecurringExecute);
		if (paymentService.saveKlixSession(klixSession))
		{
			log.info("Successfully inserted Klix session");
		}
		else
		{
			log.info("Error when inserting Klix session");
		}
	}
}
