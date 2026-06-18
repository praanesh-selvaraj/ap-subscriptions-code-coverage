package com.kmp.aeroparker.subscription.payments.handler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.PaymentProcessorFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHandler;
import com.kmp.aeroparker.subscription.payments.klix.KlixPaymentMethodGroup;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequest;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequestBuilder;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequestHandler;
import com.kmp.aeroparker.subscription.payments.klix.KlixResponse;
import com.kmp.aeroparker.subscription.payments.klix.KlixResponseBuilder;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.KlixSession;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KlixPaymentHandler implements IPaymentHandler
{
	private static final String KLIX_STATUS_PAID = "paid";
	private static final String KLIX_STATUS_CHARGE_PENDING = "pending_charge";
	private static final String KLIX_STATUS_EXECUTE_PENDING = "pending_execute";
	private static final String SUCCESS_CMD = "processSuccessRedirect";
	private static final String FAILURE_CMD = "processFailRedirect";
	private static final String DEFAULT_LANG_CODE = "en";

	private final PaymentService paymentService;
	private final KlixRequestHandler requestHandler;
	private final KlixRequestBuilder requestBuilder;
	private final PaymentProcessorFactory paymentProcessorFactory;
	private final KlixResponseBuilder responseBuilder;

	@Override
	public Map<String, Object> setUpTransaction(PaymentGatewayParameters paymentHandlerParams)
	{
		log.info("Setting up klix transaction");
		Map<String, Object> params = new HashMap<>();

		Affiliates affiliate = paymentHandlerParams.getAffiliate();
		int affiliateId = affiliate.getId();
		KlixCredentials credentials =
				(KlixCredentials) paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.KLIX);

		if (credentials != null)
		{
			List<KlixPaymentMethodGroup> klixPaymentMethods =
					requestHandler.getKlixPaymentMethods(credentials, paymentHandlerParams.getCurrency());
			PaymentGatewayTypes paymentGatewayTypes =
					paymentService.fetchPaymentGatewayTypesById(PaymentGatewayType.KLIX.getId());

			params.put("seamlessLocale", paymentHandlerParams.getLanguage()
					.getLanguageCode());
			params.put("paymentGatewayType", getType().getId());
			params.put("currency", paymentHandlerParams.getCurrency());
			params.put("amount", paymentHandlerParams.getAmount());
			params.put("paymentHtml", paymentGatewayTypes.getPaymentJsp()
					.replace(".jsp", ""));
			params.put("affiliateName", affiliate.getName()
					.replaceAll("'", "&#8217;"));
			params.put("affiliateId", affiliateId);
			params.put("klixPaymentMethods", klixPaymentMethods);
			params.put("isSeamlessPayment", klixPaymentMethods.size() > 0);
		}
		return params;
	}

	@Override
	public PaymentHandlerBean processRedirectPayment(SubscriptionBookingData bookingData)
	{
		PaymentHandlerBean paymentHandlerBean = new PaymentHandlerBean();
		if (!bookingData.isPaymentFail())
		{
			log.info("Processing klix payment");
			String reference = bookingData.getBookingReference();
			String languageCode = StringUtil.isEmpty(bookingData.getLanguageCode()) ? DEFAULT_LANG_CODE
					: bookingData.getLanguageCode();
			int affiliateId = bookingData.getAffiliateId();
			BigDecimal amount = bookingData.getAmount()
					.movePointRight(2)
					.setScale(2, RoundingMode.HALF_UP);

			KlixCredentials credentials =
					(KlixCredentials) paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.KLIX);

			if (credentials != null && !StringUtil.isEmpty(reference))
			{
				Affiliates affiliates = paymentService.fetchAffiliatesById(affiliateId);
				KlixRequest klixRequest = requestBuilder.getRequestParameters(bookingData, affiliates,
						bookingData.getServletAbsoluteUrl(), languageCode, credentials, amount);
				KlixResponse initialRequest = requestHandler.sendInitialRequest(klixRequest);

				String checkoutUrl = initialRequest.getCheckoutUrl();
				paymentHandlerBean.setCheckoutUrl(checkoutUrl);
				paymentHandlerBean.setRedirectRequired(true);
			}
		}
		return paymentHandlerBean;
	}

	@Override
	public boolean processPayment(SubscriptionBookingData bookingData)
	{
		boolean success = false;
		String guid = bookingData.getConfirmationGuid();

		if (bookingData.isPaymentSuccess())
		{
			log.info("Completing payment processing for affiliate ID {}", bookingData.getAffiliateId());
			log.info("GUID retrieved from payment guid {}", guid);

			Payments payment = null;
			PaymentProcessorParameters paymentProcessorParameters = PaymentProcessorParameters.builder()
					.setKlixResponse(bookingData.getKlixResponse())
					.withConfirmationGuid(guid)
					.withTimeZone(bookingData.getTimeZone())
					.build();
			String status = paymentProcessorParameters.getKlixResponse()
					.getStatus();

			if (KLIX_STATUS_PAID.equals(status) || KLIX_STATUS_CHARGE_PENDING.equals(status)
					|| KLIX_STATUS_EXECUTE_PENDING.equals(status))
			{
				success = true;
				payment = paymentProcessorFactory.getInstance(PaymentGatewayType.KLIX)
						.process(paymentProcessorParameters);
				bookingData.setPaymentId(Optional.ofNullable(payment.getId()).orElse(0));
			}
			else
			{
				log.info("Klix payment status was unknown for guid {}", guid);
			}

			if (payment == null)
			{
				log.info("Klix payment was null for {}", guid);
			}
		}
		return success;
	}

	@Override
	public SubscriptionBookingData processSubCmd(int affiliateId, String reference, String cmd, String callbackData,
			boolean isCallback, String guid)
	{
		SubscriptionBookingData subscriptionBookingData = null;
		if (SUCCESS_CMD.equals(cmd))
		{
			subscriptionBookingData = processSuccessRedirect(affiliateId, reference, callbackData, isCallback, guid);
		}
		else if (FAILURE_CMD.equals(cmd))
		{
			subscriptionBookingData = processFailRedirect(affiliateId, reference);
		}
		return subscriptionBookingData;
	}

	private SubscriptionBookingData processSuccessRedirect(int affiliateId, String bookingReference,
			String callbackData, boolean isCallback, String guid)
	{
		SubscriptionBookingData bookingData = null;
		KlixCredentials credentials =
				(KlixCredentials) paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.KLIX);

		KlixResponse response = isCallback ? getCallbackParameters(callbackData, bookingReference, affiliateId, guid)
				: requestHandler.getKlixResponse(affiliateId, bookingReference, credentials, 0);

		if (response != null)
		{
			bookingData = new SubscriptionBookingData();
			bookingData.setKlixResponse(response);
			bookingData.setPaymentSuccess(true);
			bookingData.setEmail(Optional.ofNullable(response.getEmail())
					.orElse(""));
		}

		return bookingData;
	}

	private SubscriptionBookingData processFailRedirect(int affiliateId, String bookingReference)
	{
		KlixCredentials credentials =
				(KlixCredentials) paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.KLIX);

		KlixResponse response = requestHandler.getKlixResponse(affiliateId, bookingReference, credentials, 0);

		SubscriptionBookingData bookingData = new SubscriptionBookingData();
		bookingData.setKlixResponse(response);
		bookingData.setPaymentFail(true);
		bookingData.setEmail(Optional.ofNullable(response.getEmail())
				.orElse(""));

		log.info("Failed to process klix payment for booking {}, errors {}", bookingReference,
				response.getErrorMessage());

		return bookingData;
	}

	private KlixResponse getCallbackParameters(String callbackData, final String bookingReference,
			final int affiliateId, final String guid)
	{
		KlixResponse parameters = null;
		if (!StringUtil.isEmpty(callbackData))
		{
			parameters = responseBuilder.buildResponseParameters(StringUtil.toJsonObject(callbackData));
			KlixSession klixSession =
					paymentService.fetchKlixSessionByReferenceAndAffiliateId(bookingReference, affiliateId);
			if (klixSession != null)
			{
				parameters.setBookingReference(bookingReference);
				parameters.setOriginalBookingReference(klixSession.getOriginalBookingReference());
				parameters.setCarParkId(0);
				parameters.setAmend(klixSession.getIsAmend());
			}
			requestHandler.insertKlixSession(guid, affiliateId, bookingReference,
					new BigDecimal(parameters.getAmount()), parameters.getId(), parameters.isAmend(),
					parameters.getOriginalBookingReference(), parameters.getStatus(), parameters.getResponseType(),
					parameters.getCurrency(), parameters.isRecurringToken(), parameters.isRecurringExecute());
		}
		return parameters;
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.KLIX;
	}
}
