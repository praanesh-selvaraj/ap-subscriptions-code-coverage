package com.kmp.aeroparker.subscription.payments.refund.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.json.utils.JsonUtil;
import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardClient;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardJsonFactory;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardObjectFactory;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardRefundResponseValidator;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardRequestParameters;

@ExtendWith(MockitoExtension.class)
class WirecardRefundProcessorTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private WirecardObjectFactory wirecardObjectFactory;
	@Mock
	private WirecardJsonFactory jsonFactory;
	@Mock
	private WirecardClient wirecardClient;
	@Mock
	private WirecardRefundResponseValidator refundResponseValidator;
	@InjectMocks
	private WirecardRefundProcessor processor;

	@Mock
	private Payments payment;

	@Test
	void testProcess()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(WirecardCredentials.class));
		Map<String, String> customFields = new HashMap<>();
		customFields.put("cardNumber", "5018XXXXXX2222");
		when(paymentService.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(customFields);
		when(wirecardObjectFactory.buildRefundRequestParameters(anyString(), any(), any(), any())).thenReturn(mock(WirecardRequestParameters.class));
		JsonObject jsonRequest = JsonUtil.toJsonObject("{amount: 10}");
		when(jsonFactory.generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean())).thenReturn(jsonRequest);
		JsonObject jsonResponse = JsonUtil.toJsonObject("{status : success}");
		when(wirecardClient.sendPaymentRequest(any(), any())).thenReturn(jsonResponse);
		when(refundResponseValidator.validate(any())).thenReturn(true);
		when(payment.getId()).thenReturn(1);
		when(payment.getTransactionId()).thenReturn("transation_id");
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.insertRefund(any())).thenReturn(true);
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isTrue();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verify(paymentService).fetchPaymentCustomValueByPaymentId(anyInt());
		verify(wirecardObjectFactory).buildRefundRequestParameters(anyString(), any(), any(), any());
		verify(jsonFactory).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardClient).sendPaymentRequest(any(), any());
		verify(refundResponseValidator).validate(any());
		verify(paymentService).insertRefund(any());
		verify(paymentService).savePayment(any());
		verifyNoMoreInteractions(paymentService, wirecardObjectFactory, jsonFactory, wirecardClient, refundResponseValidator);
	}

	@Test
	void testProcess_Validate_Failed()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(WirecardCredentials.class));
		Map<String, String> customFields = new HashMap<>();
		customFields.put("cardNumber", "5018XXXXXX2222");
		when(paymentService.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(customFields);
		when(wirecardObjectFactory.buildRefundRequestParameters(anyString(), any(), any(), any())).thenReturn(mock(WirecardRequestParameters.class));
		JsonObject jsonRequest = JsonUtil.toJsonObject("{amount: 10}");
		when(jsonFactory.generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean())).thenReturn(jsonRequest);
		JsonObject jsonResponse = JsonUtil.toJsonObject("{status : success}");
		when(wirecardClient.sendPaymentRequest(any(), any())).thenReturn(jsonResponse);
		when(refundResponseValidator.validate(any())).thenReturn(false);
		when(payment.getId()).thenReturn(1);
		when(payment.getTransactionId()).thenReturn("transation_id");
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isFalse();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verify(paymentService).fetchPaymentCustomValueByPaymentId(anyInt());
		verify(wirecardObjectFactory).buildRefundRequestParameters(anyString(), any(), any(), any());
		verify(jsonFactory).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardClient).sendPaymentRequest(any(), any());
		verify(refundResponseValidator).validate(any());
		verify(paymentService, times(0)).insertRefund(any());
		verify(paymentService, times(0)).savePayment(any());
		verifyNoMoreInteractions(paymentService, wirecardObjectFactory, jsonFactory, wirecardClient, refundResponseValidator);
	}

	@Test
	void testProcessJsonResponse_Null()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(mock(WirecardCredentials.class));
		Map<String, String> customFields = new HashMap<>();
		customFields.put("cardNumber", "5018XXXXXX2222");
		when(paymentService.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(customFields);
		when(wirecardObjectFactory.buildRefundRequestParameters(anyString(), any(), any(), any())).thenReturn(mock(WirecardRequestParameters.class));
		JsonObject jsonRequest = JsonUtil.toJsonObject("{amount: 10}");
		when(jsonFactory.generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean())).thenReturn(jsonRequest);
		when(wirecardClient.sendPaymentRequest(any(), any())).thenReturn(null);
		when(payment.getId()).thenReturn(1);
		when(payment.getTransactionId()).thenReturn("transation_id");
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isFalse();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verify(paymentService).fetchPaymentCustomValueByPaymentId(anyInt());
		verify(wirecardObjectFactory).buildRefundRequestParameters(anyString(), any(), any(), any());
		verify(jsonFactory).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardClient).sendPaymentRequest(any(), any());
		verify(refundResponseValidator, times(0)).validate(any());
		verify(paymentService, times(0)).insertRefund(any());
		verify(paymentService, times(0)).savePayment(any());
		verifyNoMoreInteractions(paymentService, wirecardObjectFactory, jsonFactory, wirecardClient, refundResponseValidator);
	}

	@Test
	void testProcess_WirecardCredentials_Null()
	{
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(null);
		assertThat(processor.process(1, payment, BigDecimal.TEN, "Europe/London")).isFalse();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verify(paymentService, times(0)).fetchPaymentCustomValueByPaymentId(anyInt());
		verify(wirecardObjectFactory, times(0)).buildRefundRequestParameters(anyString(), any(), any(), any());
		verify(jsonFactory, times(0)).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardClient, times(0)).sendPaymentRequest(any(), any());
		verify(refundResponseValidator, times(0)).validate(any());
		verify(paymentService, times(0)).insertRefund(any());
		verify(paymentService, times(0)).savePayment(any());
		verifyNoMoreInteractions(paymentService, wirecardObjectFactory, jsonFactory, wirecardClient, refundResponseValidator);
	}

	@Test
	void testProcess_Payment_Null()
	{
		assertThat(processor.process(1, null, BigDecimal.TEN, "Europe/London")).isFalse();
		verifyNoInteractions(paymentService, wirecardObjectFactory, jsonFactory, wirecardClient, refundResponseValidator);
	}

	@Test
	void testProcess_Amount_Zero()
	{
		assertThat(processor.process(1, payment, BigDecimal.ZERO, "Europe/London")).isTrue();
		verifyNoInteractions(paymentService, wirecardObjectFactory, jsonFactory, wirecardClient, refundResponseValidator);
	}

	@Test
	void testProcess_Amount_Negative()
	{
		assertThat(processor.process(1, payment, new BigDecimal(-10), "Europe/London")).isFalse();
		verifyNoInteractions(paymentService, wirecardObjectFactory, jsonFactory, wirecardClient, refundResponseValidator);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(PaymentGatewayType.WIRECARD);
	}
}