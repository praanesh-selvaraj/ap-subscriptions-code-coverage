package com.kmp.aeroparker.subscription.payments.wirecard;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.payments.enums.CustomValue;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsWirecardResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WirecardObjectFactory
{
	public WirecardRequestParameters buildWirecardRequestParameters(final String merchantId, final SubscriptionBookingData bookingData)
	{
		log.debug("Building wirecard parameter list");
		WirecardRequestParameters params = new WirecardRequestParameters();
		String originalReference = bookingData.getBookingReference();
		String parentTransactionId = bookingData.getPaymentReference();
		params.setAffiliateId(bookingData.getAffiliateId());
		params.setCurrency(bookingData.getCurrency());
		params.setAmount(bookingData.getAmount()
				.toPlainString());
		params.setEmail(bookingData.getEmail());
		params.setFirstName(bookingData.getFname());
		params.setLastName(bookingData.getLname());
		params.setLanguageCode(bookingData.getLanguageCode());
		params.setReference(bookingData.getBookingReference());
		params.setParentTransactionId(parentTransactionId);
		params.setOriginalReference(originalReference);
		params.setMerchantId(merchantId);
		params.setGuid(bookingData.getConfirmationGuid());
		log.debug("Finished building wirecard parameter list: {}", params.toString());
		return params;
	}

	public PaymentsWirecardResponse buildPaymentsWirecardResponse(final WirecardPaymentResponse wirecardPaymentResponse)
	{
		log.debug("Building wirecard payment response list");
		PaymentsWirecardResponse paymentResponse = new PaymentsWirecardResponse();
		paymentResponse.setRequestId(wirecardPaymentResponse.getRequestId());
		paymentResponse.setCompletionTypeStamp(wirecardPaymentResponse.getCompletionTypeStamp());
		paymentResponse.setMerchantAccountId(wirecardPaymentResponse.getMerchantAccountId());
		paymentResponse.setTransactionId(wirecardPaymentResponse.getTransactionId());
		paymentResponse.setRequestId(wirecardPaymentResponse.getRequestId());
		paymentResponse.setTokenId(wirecardPaymentResponse.getTokenId());
		paymentResponse.setRequestedAmount(wirecardPaymentResponse.getRequestedAmount());
		paymentResponse.setRequestedAmountCurrency(wirecardPaymentResponse.getRequestedAmountCurrency());
		paymentResponse.setFirstName(wirecardPaymentResponse.getFirstName());
		paymentResponse.setLastName(wirecardPaymentResponse.getLastName());
		paymentResponse.setEmail(wirecardPaymentResponse.getEmail());
		paymentResponse.setPhone(wirecardPaymentResponse.getPhone());
		paymentResponse.setStreet1(wirecardPaymentResponse.getStreet1());
		paymentResponse.setCity(wirecardPaymentResponse.getCity());
		paymentResponse.setCountry(wirecardPaymentResponse.getCountry());
		paymentResponse.setPostalCode(wirecardPaymentResponse.getPostalCode());
		paymentResponse.setIpAddress(wirecardPaymentResponse.getIpAddress());
		paymentResponse.setPaymentMethodName(wirecardPaymentResponse.getPaymentMethodName());
		paymentResponse.setOrderNumber(wirecardPaymentResponse.getOrderNumber());
		paymentResponse.setTransactionType(wirecardPaymentResponse.getTransactionType());
		paymentResponse.setTransactionState(wirecardPaymentResponse.getTransactionState());
		paymentResponse.setStatusCode(wirecardPaymentResponse.getStatusCode());
		log.debug("Wirecard payment response: {}", paymentResponse.toString());
		return paymentResponse;
	}

	public WirecardRequestParameters buildRefundRequestParameters(final String transactionId, final BigDecimal amount,
			final WirecardCredentials credentials, final Map<String, String> paymentCustomValues)
	{
		log.debug("Creating refung parameters");
		WirecardRequestParameters requestParameters = new WirecardRequestParameters();
		requestParameters.setMerchantId(credentials.getMerchantId());
		requestParameters.setRefundEmail(paymentCustomValues.getOrDefault(CustomValue.EMAIL_ADDRESS.getField(), ""));
		requestParameters.setTransactionToRefund(transactionId);
		requestParameters.setRefundCurrency(paymentCustomValues.getOrDefault(CustomValue.CURRENCY.getField(), ""));
		requestParameters.setAmount(amount.toPlainString());
		return requestParameters;
	}
}