package com.kmp.aeroparker.subscription.payments.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.json.utils.JsonUtil;
import com.kmp.aeroparker.subscription.payments.credentials.WirecardCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.PaymentProcessorFactory;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.processor.WirecardPaymentProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsProcessedToken;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsWirecardResponse;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardClient;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardJsonFactory;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardObjectFactory;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardPaymentResponse;
import com.kmp.aeroparker.subscription.payments.wirecard.WirecardRequestParameters;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class WirecardPaymentHandlerTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private WirecardObjectFactory wirecardObjectFactory;
	@Mock
	private WirecardJsonFactory wirecardJsonFactory;
	@Mock
	private WirecardClient wirecardClient;
	@Mock
	private PaymentProcessorFactory paymentProcessorFactory;
	@InjectMocks
	private WirecardPaymentHandler handler;
	@Mock
	private Affiliates affiliate;
	@Mock
	private Languages language;
	@Mock
	private SubscriptionBookingData bookingData;
	@Mock
	private WirecardPaymentProcessor processor;

	@Test
	void testGetType()
	{
		assertThat(handler.getType()).isEqualTo(PaymentGatewayType.WIRECARD);
	}

	@Test
	void testSetUpTransaction()
	{
		when(affiliate.getName()).thenReturn("affName");
		PaymentGatewayParameters paymentHandlerParams = mock(PaymentGatewayParameters.class);
		when(paymentHandlerParams.getLanguage()).thenReturn(language);
		when(language.getLanguageCode()).thenReturn("en");
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		WirecardCredentials wirecardCredentials = mock(WirecardCredentials.class);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD))).thenReturn(wirecardCredentials);
		PaymentGatewayTypes wirecardGatewayInfo = mock(PaymentGatewayTypes.class);
		when(wirecardGatewayInfo.getPaymentJsp()).thenReturn("wirecardPayment.jsp");
		when(paymentService.fetchPaymentGatewayTypesById(anyInt())).thenReturn(wirecardGatewayInfo);
		assertThat(handler.setUpTransaction(paymentHandlerParams)).isNotEmpty()
				.hasSize(10)
				.containsKeys("paymentGatewayType", "sofortEnabled", "paymentJsLocation", "affiliateName");
		verify(paymentService).fetchPaymentGatewayTypesById(anyInt());
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testSetUpTransaction_WirecardCredentials_Null()
	{
		PaymentGatewayParameters paymentHandlerParams = mock(PaymentGatewayParameters.class);
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD))).thenReturn(null);
		assertThat(handler.setUpTransaction(paymentHandlerParams)).isEmpty();
		verify(paymentService, times(0)).fetchPaymentGatewayTypesById(anyInt());
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcessPayment()
	{
		when(bookingData.getPaymentReference()).thenReturn("paymentReference");
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("5b1e1ad9-9588-484f-856f-7a972f315c14");
		when(bookingData.getTimeZone()).thenReturn("London/Europe");
		when(paymentService.fetchProcessedToken(anyString())).thenReturn(null);
		WirecardCredentials wirecardCredentials = mock(WirecardCredentials.class);
		when(wirecardCredentials.getMerchantId()).thenReturn("merchant_id");
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD))).thenReturn(wirecardCredentials);
		when(wirecardObjectFactory.buildWirecardRequestParameters(anyString(), any()))
				.thenReturn(EnhancedRandom.random(WirecardRequestParameters.class));
		JsonObject jsonObject = JsonUtil.toJsonObject("{test : test}");
		when(wirecardJsonFactory.generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean())).thenReturn(jsonObject);
		JsonObject jsonResponse = JsonUtil.toJsonObject("{status : success}");
		when(wirecardClient.sendPaymentRequest(any(), any())).thenReturn(jsonResponse);
		when(wirecardJsonFactory.buildWirecardResponse(any())).thenReturn(mock(WirecardPaymentResponse.class));
		when(wirecardObjectFactory.buildPaymentsWirecardResponse(any())).thenReturn(mock(PaymentsWirecardResponse.class));
		when(paymentService.insertWirecardResponse(any())).thenReturn(true);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.WIRECARD))).thenReturn(processor);
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(100);
		when(processor.process(any())).thenReturn(payments);
		assertThat(handler.processPayment(bookingData)).isTrue();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD));
		verify(paymentService).fetchProcessedToken(anyString());
		verify(paymentService).insertWirecardResponse(any());
		verify(wirecardObjectFactory).buildWirecardRequestParameters(anyString(), any());
		verify(wirecardJsonFactory).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardJsonFactory).buildWirecardResponse(any());
		verify(wirecardClient).sendPaymentRequest(any(), any());
		verify(wirecardObjectFactory).buildPaymentsWirecardResponse(any());
		verify(paymentProcessorFactory).getInstance(any());
		verifyNoMoreInteractions(paymentService, wirecardClient, wirecardJsonFactory, wirecardObjectFactory);
	}

	@Test
	void testProcessPayment_PaymentId_Zero()
	{
		when(bookingData.getPaymentReference()).thenReturn("paymentReference");
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("5b1e1ad9-9588-484f-856f-7a972f315c14");
		when(bookingData.getTimeZone()).thenReturn("London/Europe");
		when(paymentService.fetchProcessedToken(anyString())).thenReturn(null);
		WirecardCredentials wirecardCredentials = mock(WirecardCredentials.class);
		when(wirecardCredentials.getMerchantId()).thenReturn("merchant_id");
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD))).thenReturn(wirecardCredentials);
		when(wirecardObjectFactory.buildWirecardRequestParameters(anyString(), any()))
				.thenReturn(EnhancedRandom.random(WirecardRequestParameters.class));
		JsonObject jsonObject = JsonUtil.toJsonObject("{test : test}");
		when(wirecardJsonFactory.generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean())).thenReturn(jsonObject);
		JsonObject jsonResponse = JsonUtil.toJsonObject("{status : success}");
		when(wirecardClient.sendPaymentRequest(any(), any())).thenReturn(jsonResponse);
		when(wirecardJsonFactory.buildWirecardResponse(any())).thenReturn(mock(WirecardPaymentResponse.class));
		when(wirecardObjectFactory.buildPaymentsWirecardResponse(any())).thenReturn(mock(PaymentsWirecardResponse.class));
		when(paymentService.insertWirecardResponse(any())).thenReturn(true);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.WIRECARD))).thenReturn(processor);
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(0);
		when(processor.process(any())).thenReturn(payments);
		assertThat(handler.processPayment(bookingData)).isFalse();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD));
		verify(paymentService).fetchProcessedToken(anyString());
		verify(paymentService).insertWirecardResponse(any());
		verify(wirecardObjectFactory).buildWirecardRequestParameters(anyString(), any());
		verify(wirecardJsonFactory).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardJsonFactory).buildWirecardResponse(any());
		verify(wirecardClient).sendPaymentRequest(any(), any());
		verify(wirecardObjectFactory).buildPaymentsWirecardResponse(any());
		verify(paymentProcessorFactory).getInstance(any());
		verifyNoMoreInteractions(paymentService, wirecardClient, wirecardJsonFactory, wirecardObjectFactory);
	}

	@Test
	void testProcessPayment_Payment_Null()
	{
		when(bookingData.getPaymentReference()).thenReturn("paymentReference");
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(bookingData.getConfirmationGuid()).thenReturn("5b1e1ad9-9588-484f-856f-7a972f315c14");
		when(bookingData.getTimeZone()).thenReturn("London/Europe");
		when(paymentService.fetchProcessedToken(anyString())).thenReturn(null);
		WirecardCredentials wirecardCredentials = mock(WirecardCredentials.class);
		when(wirecardCredentials.getMerchantId()).thenReturn("merchant_id");
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD))).thenReturn(wirecardCredentials);
		when(wirecardObjectFactory.buildWirecardRequestParameters(anyString(), any()))
				.thenReturn(EnhancedRandom.random(WirecardRequestParameters.class));
		JsonObject jsonObject = JsonUtil.toJsonObject("{test : test}");
		when(wirecardJsonFactory.generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean())).thenReturn(jsonObject);
		JsonObject jsonResponse = JsonUtil.toJsonObject("{status : success}");
		when(wirecardClient.sendPaymentRequest(any(), any())).thenReturn(jsonResponse);
		when(wirecardObjectFactory.buildPaymentsWirecardResponse(any())).thenReturn(mock(PaymentsWirecardResponse.class));
		when(wirecardJsonFactory.buildWirecardResponse(any())).thenReturn(mock(WirecardPaymentResponse.class));
		when(paymentService.insertWirecardResponse(any())).thenReturn(true);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.WIRECARD))).thenReturn(processor);
		when(processor.process(any())).thenReturn(null);
		assertThat(handler.processPayment(bookingData)).isFalse();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD));
		verify(paymentService).fetchProcessedToken(anyString());
		verify(paymentService).insertWirecardResponse(any());
		verify(wirecardObjectFactory).buildWirecardRequestParameters(anyString(), any());
		verify(wirecardJsonFactory).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardJsonFactory).buildWirecardResponse(any());
		verify(wirecardClient).sendPaymentRequest(any(), any());
		verify(wirecardObjectFactory).buildPaymentsWirecardResponse(any());
		verify(paymentProcessorFactory).getInstance(any());
		verifyNoMoreInteractions(paymentService, wirecardClient, wirecardJsonFactory, wirecardObjectFactory);
	}

	@Test
	void testProcessPayment_WirecardPaymentResponse_Null()
	{
		when(bookingData.getPaymentReference()).thenReturn("paymentReference");
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchProcessedToken(anyString())).thenReturn(null);
		WirecardCredentials wirecardCredentials = mock(WirecardCredentials.class);
		when(wirecardCredentials.getMerchantId()).thenReturn("merchant_id");
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD))).thenReturn(wirecardCredentials);
		when(wirecardObjectFactory.buildWirecardRequestParameters(anyString(), any()))
				.thenReturn(EnhancedRandom.random(WirecardRequestParameters.class));
		JsonObject jsonObject = JsonUtil.toJsonObject("{test : test}");
		when(wirecardJsonFactory.generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean())).thenReturn(jsonObject);
		when(wirecardClient.sendPaymentRequest(any(), any())).thenReturn(null);
		assertThat(handler.processPayment(bookingData)).isFalse();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD));
		verify(paymentService).fetchProcessedToken(anyString());
		verify(paymentService, times(0)).insertWirecardResponse(any());
		verify(wirecardObjectFactory).buildWirecardRequestParameters(anyString(), any());
		verify(wirecardJsonFactory).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardClient).sendPaymentRequest(any(), any());
		verify(wirecardObjectFactory, times(0)).buildPaymentsWirecardResponse(any());
		verify(paymentProcessorFactory, times(0)).getInstance(any());
		verifyNoMoreInteractions(paymentService, wirecardClient, wirecardJsonFactory, wirecardObjectFactory);
	}

	@Test
	void testProcessPayment_InsertWirecardResponse_Failed()
	{
		when(bookingData.getPaymentReference()).thenReturn("paymentReference");
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchProcessedToken(anyString())).thenReturn(null);
		WirecardCredentials wirecardCredentials = mock(WirecardCredentials.class);
		when(wirecardCredentials.getMerchantId()).thenReturn("merchant_id");
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD))).thenReturn(wirecardCredentials);
		when(wirecardObjectFactory.buildWirecardRequestParameters(anyString(), any()))
				.thenReturn(EnhancedRandom.random(WirecardRequestParameters.class));
		JsonObject jsonObject = JsonUtil.toJsonObject("{test : test}");
		when(wirecardJsonFactory.generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean())).thenReturn(jsonObject);
		JsonObject jsonResponse = JsonUtil.toJsonObject("{status : success}");
		when(wirecardClient.sendPaymentRequest(any(), any())).thenReturn(jsonResponse);
		when(wirecardJsonFactory.buildWirecardResponse(any())).thenReturn(mock(WirecardPaymentResponse.class));
		when(paymentService.insertWirecardResponse(any())).thenReturn(false);
		assertThat(handler.processPayment(bookingData)).isFalse();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD));
		verify(paymentService).fetchProcessedToken(anyString());
		verify(paymentService).insertWirecardResponse(any());
		verify(wirecardObjectFactory).buildWirecardRequestParameters(anyString(), any());
		verify(wirecardJsonFactory).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardJsonFactory).buildWirecardResponse(any());
		verify(wirecardClient).sendPaymentRequest(any(), any());
		verify(wirecardObjectFactory).buildPaymentsWirecardResponse(any());
		verify(paymentProcessorFactory, times(0)).getInstance(any());
		verifyNoMoreInteractions(paymentService, wirecardClient, wirecardJsonFactory, wirecardObjectFactory);
	}

	@Test
	void testProcessPayment_WirecardCredentials_Null()
	{
		when(bookingData.getPaymentReference()).thenReturn("paymentReference");
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchProcessedToken(anyString())).thenReturn(null);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD))).thenReturn(null);
		assertThat(handler.processPayment(bookingData)).isFalse();
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD));
		verify(paymentService).fetchProcessedToken(anyString());
		verify(paymentService, times(0)).insertWirecardResponse(any());
		verify(wirecardObjectFactory, times(0)).buildWirecardRequestParameters(anyString(), any());
		verify(wirecardJsonFactory, times(0)).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardClient, times(0)).sendPaymentRequest(any(), any());
		verify(wirecardObjectFactory, times(0)).buildPaymentsWirecardResponse(any());
		verify(paymentProcessorFactory, times(0)).getInstance(any());
		verifyNoMoreInteractions(paymentService, wirecardClient, wirecardJsonFactory, wirecardObjectFactory);
	}

	@Test
	void testProcessPayment_PaymentsProcessedToken_Not_Null()
	{
		when(bookingData.getPaymentReference()).thenReturn("paymentReference");
		when(bookingData.getAffiliateId()).thenReturn(1);
		when(paymentService.fetchProcessedToken(anyString())).thenReturn(mock(PaymentsProcessedToken.class));
		assertThat(handler.processPayment(bookingData)).isFalse();
		verify(paymentService, times(0)).fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD));
		verify(paymentService).fetchProcessedToken(anyString());
		verify(paymentService, times(0)).insertWirecardResponse(any());
		verify(wirecardObjectFactory, times(0)).buildWirecardRequestParameters(anyString(), any());
		verify(wirecardJsonFactory, times(0)).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardClient, times(0)).sendPaymentRequest(any(), any());
		verify(wirecardObjectFactory, times(0)).buildPaymentsWirecardResponse(any());
		verify(paymentProcessorFactory, times(0)).getInstance(any());
		verifyNoMoreInteractions(paymentService, wirecardClient, wirecardJsonFactory, wirecardObjectFactory);
	}

	@Test
	void testProcessPayment_PaymentsReference_Empty()
	{
		when(bookingData.getPaymentReference()).thenReturn("");
		when(bookingData.getAffiliateId()).thenReturn(1);
		assertThat(handler.processPayment(bookingData)).isFalse();
		verify(paymentService, times(0)).fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.WIRECARD));
		verify(paymentService, times(0)).fetchProcessedToken(anyString());
		verify(paymentService, times(0)).insertWirecardResponse(any());
		verify(wirecardObjectFactory, times(0)).buildWirecardRequestParameters(anyString(), any());
		verify(wirecardJsonFactory, times(0)).generateJsonRequestDataForPayment(any(), anyBoolean(), anyBoolean());
		verify(wirecardClient, times(0)).sendPaymentRequest(any(), any());
		verify(wirecardObjectFactory, times(0)).buildPaymentsWirecardResponse(any());
		verify(paymentProcessorFactory, times(0)).getInstance(any());
		verifyNoMoreInteractions(paymentService, wirecardClient, wirecardJsonFactory, wirecardObjectFactory);
	}
}