package com.kmp.aeroparker.subscription.payments.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.PaymentProcessorFactory;
import com.kmp.aeroparker.subscription.payments.klix.KlixPaymentMethodGroup;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequest;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequestBuilder;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequestHandler;
import com.kmp.aeroparker.subscription.payments.klix.KlixResponse;
import com.kmp.aeroparker.subscription.payments.klix.KlixResponseBuilder;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.processor.KlixPaymentProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.KlixSession;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

@ExtendWith(MockitoExtension.class)
class KlixPaymentHandlerTest
{
	@InjectMocks
	private KlixPaymentHandler handler;
	@Mock
	private PaymentService paymentService;
	@Mock
	private SubscriptionBookingData bookingData;
	@Mock
	private Affiliates affiliate;
	@Mock
	private Languages language;
	@Mock
	private KlixCredentials credentials;
	@Mock
	private PaymentGatewayTypes gatewayType;
	@Mock
	private PaymentGatewayParameters paymentHandlerParams;
	@Mock
	private KlixRequestHandler requestHandler;
	@Mock
	private PaymentProcessorFactory paymentProcessorFactory;
	@Mock
	private KlixPaymentProcessor processor;
	@Mock
	private KlixResponse klixResponse;
	@Mock
	private Payments payment;
	@Mock
	private KlixRequestBuilder builder;
	@Mock
	private KlixResponseBuilder responseBuilder;

	@Test
	public void testSetUpTransaction()
	{
		KlixPaymentMethodGroup methodGroup = mock(KlixPaymentMethodGroup.class);
		List<KlixPaymentMethodGroup> list = new ArrayList<KlixPaymentMethodGroup>();
		list.add(methodGroup);

		when(affiliate.getName()).thenReturn("affName");
		when(paymentHandlerParams.getLanguage()).thenReturn(language);
		when(language.getLanguageCode()).thenReturn("en");
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		when(paymentHandlerParams.getCurrency()).thenReturn("GBP");
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.KLIX)))
				.thenReturn(credentials);
		when(gatewayType.getPaymentJsp()).thenReturn("wklixPayment.jsp");
		when(paymentService.fetchPaymentGatewayTypesById(anyInt())).thenReturn(gatewayType);
		when(requestHandler.getKlixPaymentMethods(any(), anyString())).thenReturn(list);

		Map<String, Object> result = handler.setUpTransaction(paymentHandlerParams);

		assertEquals(9, result.size());

		verify(paymentService).fetchPaymentGatewayTypesById(anyInt());
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
	}

	@Test
	public void testSetUpTransaction_Null_Creds()
	{
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);

		Map<String, Object> result = handler.setUpTransaction(paymentHandlerParams);

		assertEquals(0, result.size());
	}

	@Test
	public void testProcessRedirectPayment()
	{
		KlixRequest klixRequest = new KlixRequest(null, null, null, null, null, null, null, affiliate, null, null, null,
				null, null, credentials, null, null, null, null, null, null, 0, null, false, false, null);

		when(bookingData.getAmount()).thenReturn(BigDecimal.TEN);
		when(bookingData.getBookingReference()).thenReturn("ref");
		when(bookingData.getServletAbsoluteUrl()).thenReturn("url");
		when(bookingData.getLanguageCode()).thenReturn("en");
		when(paymentService.fetchAffiliatesById(anyInt())).thenReturn(affiliate);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(credentials);
		when(builder.getRequestParameters(any(), any(), anyString(), anyString(), any(), any()))
				.thenReturn(klixRequest);
		when(requestHandler.sendInitialRequest(any())).thenReturn(klixResponse);
		when(klixResponse.getCheckoutUrl()).thenReturn("checkoutUrl");

		PaymentHandlerBean paymentHandlerBean = handler.processRedirectPayment(bookingData);

		assertTrue(paymentHandlerBean.isRedirectRequired());
		assertEquals("checkoutUrl", paymentHandlerBean.getCheckoutUrl());
	}

	@Test
	public void testProcessRedirectPayment_Null_Creds()
	{
		when(bookingData.getAmount()).thenReturn(BigDecimal.TEN);

		PaymentHandlerBean paymentHandlerBean = handler.processRedirectPayment(bookingData);

		assertFalse(paymentHandlerBean.isRedirectRequired());
		assertNull(paymentHandlerBean.getCheckoutUrl());
	}

	@Test
	public void testProcessRedirectPayment_Failed_Payment()
	{
		when(bookingData.isPaymentFail()).thenReturn(true);

		PaymentHandlerBean paymentHandlerBean = handler.processRedirectPayment(bookingData);

		assertFalse(paymentHandlerBean.isRedirectRequired());
		assertNull(paymentHandlerBean.getCheckoutUrl());
	}

	@Test
	public void testProcessRedirectPayment_Null_Reference()
	{
		when(bookingData.getAmount()).thenReturn(BigDecimal.TEN);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), any())).thenReturn(credentials);

		PaymentHandlerBean paymentHandlerBean = handler.processRedirectPayment(bookingData);

		assertFalse(paymentHandlerBean.isRedirectRequired());
		assertNull(paymentHandlerBean.getCheckoutUrl());
	}

	@Test
	public void testProcessPayment()
	{
		when(bookingData.isPaymentSuccess()).thenReturn(true);
		when(bookingData.getKlixResponse()).thenReturn(klixResponse);
		when(klixResponse.getStatus()).thenReturn("paid");
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.KLIX))).thenReturn(processor);
		when(processor.process(any())).thenReturn(payment);

		assertTrue(handler.processPayment(bookingData));
	}

	@Test
	public void testProcessPayment_PendingCharge()
	{
		when(bookingData.isPaymentSuccess()).thenReturn(true);
		when(bookingData.getKlixResponse()).thenReturn(klixResponse);
		when(klixResponse.getStatus()).thenReturn("pending_charge");
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.KLIX))).thenReturn(processor);
		when(processor.process(any())).thenReturn(payment);

		assertTrue(handler.processPayment(bookingData));
	}

	@Test
	public void testProcessPayment_PendingExecute()
	{
		when(bookingData.isPaymentSuccess()).thenReturn(true);
		when(bookingData.getKlixResponse()).thenReturn(klixResponse);
		when(klixResponse.getStatus()).thenReturn("pending_execute");
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.KLIX))).thenReturn(processor);
		when(processor.process(any())).thenReturn(payment);

		assertTrue(handler.processPayment(bookingData));
	}

	@Test
	public void testProcessPayment_UnknownStatus()
	{
		when(bookingData.isPaymentSuccess()).thenReturn(true);
		when(bookingData.getKlixResponse()).thenReturn(klixResponse);
		when(klixResponse.getStatus()).thenReturn("unknown");

		assertFalse(handler.processPayment(bookingData));
	}

	@Test
	public void testProcessPayment_Unsuccessful()
	{
		assertFalse(handler.processPayment(bookingData));
	}

	@Test
	public void testProcessSubCmd_ProcessSuccessRedirect()
	{
		when(requestHandler.getKlixResponse(anyInt(), anyString(), any(), anyInt())).thenReturn(klixResponse);
		when(klixResponse.getEmail()).thenReturn("email");

		assertNotNull(handler.processSubCmd(2, "ref", "processSuccessRedirect", "", false, ""));
	}

	@Test
	public void testProcessSubCmd_ProcessSuccessRedirect_Callback()
	{
		when(responseBuilder.buildResponseParameters(any())).thenReturn(klixResponse);
		when(paymentService.fetchKlixSessionByReferenceAndAffiliateId(anyString(), anyInt()))
				.thenReturn(mock(KlixSession.class));
		when(klixResponse.getEmail()).thenReturn("email");

		assertNotNull(
				handler.processSubCmd(2, "ref", "processSuccessRedirect", "{\"callback\":response}", true, "guid"));
	}

	@Test
	public void testProcessSubCmd_ProcessSuccessRedirect_Callback_EmptyData()
	{
		assertNull(handler.processSubCmd(2, "ref", "processSuccessRedirect", "", true, "guid"));
	}

	@Test
	public void testProcessSubCmd_ProcessSuccessRedirect_Callback_Null_Session()
	{
		when(responseBuilder.buildResponseParameters(any())).thenReturn(klixResponse);
		when(klixResponse.getEmail()).thenReturn("email");

		assertNotNull(
				handler.processSubCmd(2, "ref", "processSuccessRedirect", "{\"callback\":response}", true, "guid"));
	}

	@Test
	public void testProcessSubCmd_ProcessFailRedirect()
	{
		when(requestHandler.getKlixResponse(anyInt(), anyString(), any(), anyInt())).thenReturn(klixResponse);
		when(klixResponse.getEmail()).thenReturn("email");

		assertNotNull(handler.processSubCmd(2, "ref", "processFailRedirect", "", false, ""));
	}

	@Test
	public void testProcessSubCmd_Unknown_CMD()
	{
		assertNull(handler.processSubCmd(1, "ref", "unknown", "", false, ""));
	}

	@Test
	public void testGetType()
	{
		assertEquals(PaymentGatewayType.KLIX, handler.getType());
	}
}
