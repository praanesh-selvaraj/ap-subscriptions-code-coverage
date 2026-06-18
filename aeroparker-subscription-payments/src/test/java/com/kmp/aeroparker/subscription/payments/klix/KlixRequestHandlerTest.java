package com.kmp.aeroparker.subscription.payments.klix;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.KlixSession;

@ExtendWith(MockitoExtension.class)
class KlixRequestHandlerTest
{
	@InjectMocks
	private KlixRequestHandler handler;
	private KlixRequest request;
	@Mock
	private KlixCredentials credentials;
	@Mock
	private Affiliates affiliates;
	@Mock
	private KlixRequestBuilder builder;
	@Mock
	private PaymentService paymentService;
	@Mock
	private KlixHttpClient client;
	@Mock
	private KlixResponseBuilder responseBuilder;
	@Mock
	private KlixResponse response;
	@Mock
	private KlixSession klixSession;
	@Mock
	private KlixPaymentMethodGroup klixPaymentMethodGroup;

	@BeforeEach
	public void init()
	{
		request = new KlixRequest("successRedirect", "failureRedirect", "successCallback", "language", "productName",
				"10", "email", affiliates, "firstName", "originalBookingReference", "city", "zipCode", "streetAddress",
				credentials, "currency", "phoneNumber", "country", "lastName", "guid", "bookingReference", 10,
				"savedCardToken", true, true, "paymentMethod");
	}

	@Test
	public void testSendInitialRequest()
	{
		when(responseBuilder.buildResponseParameters(any())).thenReturn(response);
		when(builder.generateJsonForInitialRequest(any())).thenReturn(new JsonObject());
		when(client.sendPaymentRequest(any(), any())).thenReturn(new JsonObject());
		when(paymentService.saveKlixSession(any())).thenReturn(true);

		assertNotNull(handler.sendInitialRequest(request));
		verify(paymentService).saveKlixSession(any());
	}

	@Test
	public void testSendInitialRequest_Null_Credentials()
	{
		request = new KlixRequest("successRedirect", "failureRedirect", "successCallback", "language", "productName",
				"10", "email", affiliates, "firstName", "originalBookingReference", "city", "zipCode", "streetAddress",
				null, "currency", "phoneNumber", "country", "lastName", "guid", "bookingReference", 10,
				"savedCardToken", true, true, "paymentMethod");

		assertNull(handler.sendInitialRequest(request));
	}

	@Test
	public void testInsertKlixSession_False()
	{
		when(paymentService.saveKlixSession(any())).thenReturn(false);

		handler.insertKlixSession("guid", 1, "ref", BigDecimal.TEN, "abc", false, "ogref", "success", "type", "GBP",
				false, false);

		verify(paymentService).saveKlixSession(any());
	}

	@Test
	public void testSendRefundRequest()
	{
		when(responseBuilder.buildResponseParameters(any())).thenReturn(response);
		when(client.sendRefundRequest(any(), anyString(), any())).thenReturn(new JsonObject());

		assertNotNull(handler.sendRefundRequest(credentials, "paymentId", "bookingReference", 1, new JsonObject()));

		verify(client).sendRefundRequest(any(), anyString(), any());
		verify(paymentService).saveKlixSession(any());
	}

	@Test
	public void testSendRefundRequest_Null_Creds()
	{
		assertNull(handler.sendRefundRequest(null, "paymentId", "bookingReference", 1, new JsonObject()));
	}

	@Test
	public void testGetKlixResponse()
	{
		when(responseBuilder.buildResponseParameters(any())).thenReturn(response);
		when(klixSession.getPaymentId()).thenReturn("paymentId");
		when(klixSession.getOriginalBookingReference()).thenReturn("originalBookingReference");
		when(paymentService.fetchKlixSessionByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(klixSession);
		when(client.getKlixResponse(any(), anyString())).thenReturn(new JsonObject());

		assertNotNull(handler.getKlixResponse(1, "bookingReference", credentials, 0));

		verify(client).getKlixResponse(any(), anyString());
	}

	@Test
	public void testGetKlixResponse_Null_Credentials()
	{
		assertNull(handler.getKlixResponse(1, "bookingReference", null, 0));

		verify(paymentService, times(0)).fetchKlixSessionByReferenceAndAffiliateId(anyString(), anyInt());
	}

	@Test
	public void testGetKlixResponse_Null_Session()
	{
		assertNull(handler.getKlixResponse(1, "bookingReference", credentials, 0));

		verify(paymentService).fetchKlixSessionByReferenceAndAffiliateId(anyString(), anyInt());
	}

	@Test
	public void testGetKlixPaymentMethods()
	{
		List<KlixPaymentMethodGroup> list = new ArrayList<KlixPaymentMethodGroup>();
		list.add(klixPaymentMethodGroup);

		when(responseBuilder.buildPaymentMethods(any())).thenReturn(list);

		assertEquals(1, handler.getKlixPaymentMethods(credentials, "GBP")
				.size());
	}

	@Test
	public void testGetKlixPaymentMethods_Null_Creds()
	{
		assertNull(handler.getKlixPaymentMethods(null, "GBP"));
	}

	@Test
	public void testGetKlixPaymentMethods_Null_Currency()
	{
		assertNull(handler.getKlixPaymentMethods(credentials, null));
	}
}
