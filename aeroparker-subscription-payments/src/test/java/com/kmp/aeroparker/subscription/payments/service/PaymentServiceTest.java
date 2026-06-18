package com.kmp.aeroparker.subscription.payments.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.dao.PaymentDao;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.CredentialsFactory;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.KlixSession;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPaymentAmount;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPaymentVersion;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentTransaction;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsCustomValues;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsProcessedToken;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsWirecardResponse;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Refunds;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SelectedPaymentGateway;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest
{
	@Mock
	private PaymentDao dao;
	@Mock
	private CredentialsFactory credentialsFactory;
	@Mock
	private ObjectMapper objectMapper;
	@InjectMocks
	private PaymentService service;
	@Mock
	private KlixSession klixSession;
	@Mock
	private Payments mockPayment;

	@Test
	void testFetchSelectedPaymentGatewayTypeByAffiliateAndCarPark()
	{
		final List<SelectedPaymentGateway> list = new ArrayList<>();
		final SelectedPaymentGateway selectedPaymentGateway = EnhancedRandom.random(SelectedPaymentGateway.class);
		selectedPaymentGateway.setType(1);
		list.add(selectedPaymentGateway);
		when(dao.fetchSelectedPaymentGateway(anyInt(), anyInt())).thenReturn(list);
		assertThat(service.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(1, 1)).isNotZero();
	}

	@Test
	void testFetchSelectedPaymentGatewayTypeByAffiliateAndCarPark_SelectedPaymentGateway_Null()
	{
		final List<SelectedPaymentGateway> list = new ArrayList<>();
		when(dao.fetchSelectedPaymentGateway(anyInt(), anyInt())).thenReturn(list);
		assertThat(service.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(1, 1)).isZero();
	}

	@Test
	void testFetchSelectedPaymentGatewayTypeByAffiliateAndCarPark_Invalid_Params()
	{
		assertThat(service.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(0, 1)).isZero();
		assertThat(service.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(0, 0)).isZero();
		assertThat(service.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(1, 0)).isZero();
	}

	@Test
	void testFetchSelectedPaymentGateway_Affiliate()
	{
		final List<SelectedPaymentGateway> list = new ArrayList<>();
		final SelectedPaymentGateway selectedPaymentGateway = EnhancedRandom.random(SelectedPaymentGateway.class);
		selectedPaymentGateway.setType(1);
		selectedPaymentGateway.setGatewayType("AFFILIATE");
		list.add(selectedPaymentGateway);
		when(dao.fetchSelectedPaymentGateway(anyInt(), anyInt())).thenReturn(list);
		assertThat(service.fetchSelectedPaymentGateway(1, 1)).isNotNull()
				.hasFieldOrPropertyWithValue("gatewayType", "AFFILIATE");
	}

	@Test
	void testFetchSelectedPaymentGateway_CarPark()
	{
		final List<SelectedPaymentGateway> list = new ArrayList<>();
		final SelectedPaymentGateway selectedPaymentGateway = EnhancedRandom.random(SelectedPaymentGateway.class);
		selectedPaymentGateway.setType(1);
		selectedPaymentGateway.setGatewayType("CARPARK");
		list.add(selectedPaymentGateway);
		when(dao.fetchSelectedPaymentGateway(anyInt(), anyInt())).thenReturn(list);
		assertThat(service.fetchSelectedPaymentGateway(1, 1)).isNotNull()
				.hasFieldOrPropertyWithValue("gatewayType", "CARPARK");
	}

	@Test
	void testFetchSelectedPaymentGateway_Invalid_Params()
	{
		assertThat(service.fetchSelectedPaymentGateway(1, 0)).isNull();
		assertThat(service.fetchSelectedPaymentGateway(0, 1)).isNull();
		assertThat(service.fetchSelectedPaymentGateway(0, 0)).isNull();
	}

	@Test
	void testFetchPaymentCredentials() throws JsonParseException, JsonMappingException, IOException
	{
		final SelectedPaymentGateway selectedPaymentGateway = EnhancedRandom.random(SelectedPaymentGateway.class);
		selectedPaymentGateway.setCredentialsJson("{\"type\":\"Braintree\"," + "\"merchantId\":\"y2znqk7tzdqtvrx5\","
				+ "\"publicKey\":\"85k9ck264r7dnwms\"," + "\"privateKey\":\"273d99105f1295ee8a34c5f02898e613\"," + "\"cvvCheckEnabled\":false,"
				+ "\"payPalEnabled\":false," + "\"applePayEnabled\":true}");
		selectedPaymentGateway.setType(1);
		BraintreeCredentials braintreeCredentials = new BraintreeCredentials();
		doReturn(braintreeCredentials.getClass()).when(credentialsFactory)
				.getInstance(eq(PaymentGatewayType.BRAINTREE));
		doReturn(braintreeCredentials).when(objectMapper)
				.readValue(anyString(), eq(braintreeCredentials.getClass()));
		List<SelectedPaymentGateway> list = new ArrayList<>();
		list.add(EnhancedRandom.random(SelectedPaymentGateway.class));
		selectedPaymentGateway.setGatewayType("CARPARK");
		list.add(selectedPaymentGateway);
		when(dao.fetchSelectedPaymentGateway(anyInt(), anyInt())).thenReturn(list);
		assertThat(service.fetchPaymentCredentials(1, 1, PaymentGatewayType.BRAINTREE)).isNotNull()
				.isInstanceOf(BraintreeCredentials.class);
	}

	@Test
	void testFetchPaymentCredentials_Empty_Credentials() throws JsonParseException, JsonMappingException, IOException
	{
		final SelectedPaymentGateway selectedPaymentGateway = EnhancedRandom.random(SelectedPaymentGateway.class, "credentialsJson");
		selectedPaymentGateway.setType(1);
		BraintreeCredentials braintreeCredentials = new BraintreeCredentials();
		doReturn(braintreeCredentials.getClass()).when(credentialsFactory)
				.getInstance(eq(PaymentGatewayType.BRAINTREE));
		List<SelectedPaymentGateway> list = new ArrayList<>();
		list.add(EnhancedRandom.random(SelectedPaymentGateway.class));
		selectedPaymentGateway.setGatewayType("CARPARK");
		list.add(selectedPaymentGateway);
		when(dao.fetchSelectedPaymentGateway(anyInt(), anyInt())).thenReturn(list);
		assertThat(service.fetchPaymentCredentials(1, 1, PaymentGatewayType.BRAINTREE)).isNull();
	}

	@Test
	void testFetchPaymentCredentials_SelectedPaymentGateway_Null() throws JsonParseException, JsonMappingException, IOException
	{
		List<SelectedPaymentGateway> list = new ArrayList<>();
		when(dao.fetchSelectedPaymentGateway(anyInt(), anyInt())).thenReturn(list);
		assertThat(service.fetchPaymentCredentials(1, 1, PaymentGatewayType.BRAINTREE)).isNull();
	}

	@Test
	void testFetchPaymentCredentials_Invalid_Params() throws JsonParseException, JsonMappingException, IOException
	{
		assertThat(service.fetchPaymentCredentials(1, 0, PaymentGatewayType.BRAINTREE)).isNull();
		assertThat(service.fetchPaymentCredentials(0, 1, PaymentGatewayType.BRAINTREE)).isNull();
		assertThat(service.fetchPaymentCredentials(0, 0, PaymentGatewayType.BRAINTREE)).isNull();
	}

	@Test
	void testFetchPaymentCredentials_Throw_IOException() throws JsonParseException, JsonMappingException, IOException
	{
		final SelectedPaymentGateway selectedPaymentGateway = EnhancedRandom.random(SelectedPaymentGateway.class);
		selectedPaymentGateway.setCredentialsJson("{\"type\":\"Braintree\"," + "\"merchantId\":\"y2znqk7tzdqtvrx5\","
				+ "\"publicKey\":\"85k9ck264r7dnwms\"," + "\"privateKey\":\"273d99105f1295ee8a34c5f02898e613\"," + "\"cvvCheckEnabled\":false,"
				+ "\"payPalEnabled\":false," + "\"applePayEnabled\":true}");
		selectedPaymentGateway.setType(1);
		BraintreeCredentials braintreeCredentials = new BraintreeCredentials();
		doReturn(braintreeCredentials.getClass()).when(credentialsFactory)
				.getInstance(eq(PaymentGatewayType.BRAINTREE));
		doThrow(mock(JsonProcessingException.class)).when(objectMapper)
				.readValue(anyString(), eq(braintreeCredentials.getClass()));
		List<SelectedPaymentGateway> list = new ArrayList<>();
		list.add(EnhancedRandom.random(SelectedPaymentGateway.class));
		selectedPaymentGateway.setGatewayType("CARPARK");
		list.add(selectedPaymentGateway);
		when(dao.fetchSelectedPaymentGateway(anyInt(), anyInt())).thenReturn(list);
		assertThat(service.fetchPaymentCredentials(1, 1, PaymentGatewayType.BRAINTREE)).isNull();
	}

	@Test
	void testFetchPaymentGatewayTypesById()
	{
		PaymentGatewayTypes gatewayType = mock(PaymentGatewayTypes.class);
		when(dao.fetchPaymentGatewayTypesById(anyInt())).thenReturn(gatewayType);
		assertThat(service.fetchPaymentGatewayTypesById(1)).isNotNull()
				.isInstanceOf(PaymentGatewayTypes.class);
	}

	@Test
	void testFetchPaymentGatewayTypesById_Invalid_Params()
	{
		assertThat(service.fetchPaymentGatewayTypesById(0)).isNull();
	}

	@Test
	void testSavePayment()
	{
		when(dao.savePayment(any())).thenReturn(true);
		assertThat(service.savePayment(mock(Payments.class))).isTrue();
		verify(dao).savePayment(any());
	}

	@Test
	void testSavePayment_Payment_Null()
	{
		assertThat(service.savePayment(null)).isFalse();
		verify(dao, times(0)).savePayment(any());
	}

	@Test
	void testFetchAllCustomFields()
	{
		Map<String, String> customValues = new HashMap<>();
		customValues.put("cardNumber", "444111");
		Map<String, Integer> paymentCustomFields = new HashMap<String, Integer>();
		paymentCustomFields.put("cardNumber", 17);
		when(dao.fetchAllCustomFields()).thenReturn(paymentCustomFields);
		assertThat(service.saveCustomValues(1, customValues)).isTrue();
	}

	@Test
	void testFetchAllCustomFields_Empty_CustomValue()
	{
		Map<String, Integer> paymentCustomFields = new HashMap<String, Integer>();
		paymentCustomFields.put("cardNumber", 17);
		when(dao.fetchAllCustomFields()).thenReturn(paymentCustomFields);
		assertThat(service.saveCustomValues(1, Collections.emptyMap())).isFalse();
	}

	@Test
	void testFetchAllCustomFields_Empty_CustomValue_Value()
	{
		Map<String, String> customValues = new HashMap<>();
		customValues.put("cardNumber", "");
		Map<String, Integer> paymentCustomFields = new HashMap<String, Integer>();
		paymentCustomFields.put("cardNumber", 17);
		when(dao.fetchAllCustomFields()).thenReturn(paymentCustomFields);
		assertThat(service.saveCustomValues(1, customValues)).isFalse();
	}

	@Test
	void testFetchAllCustomFields_Empty_CustomValue_Key_Zero()
	{
		Map<String, String> customValues = new HashMap<String, String>();
		customValues.put("cardNumber", "444111");
		Map<String, Integer> paymentCustomFields = new HashMap<>();
		paymentCustomFields.put("cardNumber", 0);
		when(dao.fetchAllCustomFields()).thenReturn(paymentCustomFields);
		assertThat(service.saveCustomValues(1, customValues)).isFalse();
	}

	@Test
	void testFetchPaymentByReference()
	{
		when(dao.fetchPaymentByReference(anyString())).thenReturn(mock(Payments.class));
		assertThat(service.fetchPaymentByReference("bookingReference")).isNotNull()
				.isInstanceOf(Payments.class);
	}

	@Test
	void testFetchPaymentByReference_Reference_Empty()
	{
		assertThat(service.fetchPaymentByReference("")).isNull();
		verifyNoInteractions(dao);
	}

	@Test
	void testInsertRefund()
	{
		assertThat(service.insertRefund(mock(Refunds.class))).isTrue();
		verify(dao).insertRefund(any());
	}

	@Test
	void testInsertRefund_Refund_Null()
	{
		assertThat(service.insertRefund(null)).isFalse();
		verify(dao, times(0)).insertRefund(any());
	}

	@Test
	void testFetchPaymentCustomValueByPaymentId()
	{
		List<PaymentsCustomValues> listCustomValues = new ArrayList<>();
		PaymentsCustomValues paymentCustomValues = new PaymentsCustomValues();
		paymentCustomValues.setField(1);
		paymentCustomValues.setValue("5018XXXXXX2222");
		listCustomValues.add(paymentCustomValues);
		when(dao.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(listCustomValues);
		Map<String, Integer> customFields = new HashMap<>();
		customFields.put("cardNumber", 1);
		customFields.put("cardScheme", 8);
		when(dao.fetchAllCustomFields()).thenReturn(customFields);
		assertThat(service.fetchPaymentCustomValueByPaymentId(1)).isNotEmpty()
				.hasSize(1)
				.containsEntry("cardNumber", "5018XXXXXX2222");
	}

	@Test
	void testFetchPaymentCustomValueByPaymentId_PaymentsCustomValues_Empty()
	{
		when(dao.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(Collections.emptyList());
		assertThat(service.fetchPaymentCustomValueByPaymentId(1)).isEmpty();
	}

	@Test
	void testFetchPaymentCustomValueByPaymentId_PaymentId_Zero()
	{
		assertThat(service.fetchPaymentCustomValueByPaymentId(0)).isEmpty();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchProcessedToken()
	{
		when(dao.fetchProcessedToken(anyString())).thenReturn(mock(PaymentsProcessedToken.class));
		assertThat(service.fetchProcessedToken("token")).isNotNull()
				.isInstanceOf(PaymentsProcessedToken.class);
	}

	@Test
	void testFetchProcessedToken_Token_Empty()
	{
		assertThat(service.fetchProcessedToken("")).isNull();
		verifyNoMoreInteractions(dao);
	}

	@Test
	void testCreatePayment()
	{
		assertThat(service.createPayment("transactionid", "bookingReference", "20.00", 1, "Europe/London")).isNotNull()
				.isInstanceOf(Payments.class)
				.hasFieldOrPropertyWithValue("transactionId", "transactionid")
				.hasFieldOrPropertyWithValue("type", 1)
				.hasFieldOrPropertyWithValue("reference", "bookingReference")
				.hasFieldOrPropertyWithValue("amount", "20.00");
	}

	@Test
	void testInsertWirecardResponse()
	{
		when(dao.insertWirecardResponse(any())).thenReturn(true);
		assertThat(service.insertWirecardResponse(mock(PaymentsWirecardResponse.class))).isTrue();
	}

	@Test
	void testInsertWirecardResponse_Param_Null()
	{
		assertThat(service.insertWirecardResponse(null)).isFalse();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchPaymentById_Null()
	{
		assertThat(service.fetchPaymentById(0)).isNull();
		verifyNoInteractions(dao);
	}

	@Test
	void testFetchPaymentById()
	{
		when(dao.fetchPaymentById(anyInt())).thenReturn(mock(Payments.class));
		assertThat(service.fetchPaymentById(1)).isNotNull()
				.isInstanceOf(Payments.class);
	}

	@Test
	void testInsertSubscriptionScheduledRecurringPayment()
	{
		when(dao.insertSubscriptionScheduledRecurringPayment(any())).thenReturn(true);
		assertThat(service.insertSubscriptionScheduledRecurringPayment(mock(SubscriptionScheduledRecurringPayment.class))).isTrue();
	}

	@Test
	void testInsertSubscriptionScheduledRecurringPayment_Null()
	{
		assertThat(service.insertSubscriptionScheduledRecurringPayment(null)).isFalse();
	}
	
	@Test
	void testFetchRecurringPaymentSentByBookingReference()
	{
		List<Payments> payments = new ArrayList<>();
		Payments mockPayment = mock(Payments.class);
		payments.add(mockPayment);
		when(dao.fetchRecurringPaymentSentByBookingReference(anyString())).thenReturn(payments);
		assertThat(service.fetchRecurringPaymentSentByBookingReference("Reference")).isNotEmpty();
	}
	
	@Test
	void testFetchRecurringPaymentSentByBookingReference_Empty_Reference()
	{
		assertThat(service.fetchRecurringPaymentSentByBookingReference("")).isEmpty();
	}

	@Test
	public void testSaveKlixSession()
	{
		when(dao.saveKlixSession(any())).thenReturn(true);

		assertTrue(service.saveKlixSession(klixSession));
	}

	@Test
	public void testSaveKlixSession_False()
	{
		assertFalse(service.saveKlixSession(null));
	}

	@Test
	public void testFetchKlixSessionByReferenceAndAffiliateId()
	{
		when(dao.fetchKlixSessionByReferenceAndAffiliateId(anyString(), anyInt())).thenReturn(klixSession);

		assertNotNull(service.fetchKlixSessionByReferenceAndAffiliateId("ref", 1));
	}

	@Test
	public void testFetchKlixSessionByReferenceAndAffiliateId_Null_Reference()
	{
		assertNull(service.fetchKlixSessionByReferenceAndAffiliateId(null, 1));
	}

	@Test
	public void testFetchKlixSessionByReferenceAndAffiliateId_0_AffiliateId()
	{
		assertNull(service.fetchKlixSessionByReferenceAndAffiliateId("Ref", 0));
	}

	@Test
	public void testFetchAffiliatesById()
	{
		Affiliates affiliate = mock(Affiliates.class);
		when(dao.fetchAffiliatesbyId(anyInt())).thenReturn(affiliate);

		assertNotNull(service.fetchAffiliatesById(1));
	}

	@Test
	public void testFetchAffiliatesById_0()
	{
		assertNull(service.fetchAffiliatesById(0));
	}

	@Test
	void testSavePayment_NullPayment()
	{
		assertFalse(service.savePayment(null));
	}

	@Test
	void testSavePartialPayment()
	{
		when(dao.savePartialPayment(any())).thenReturn(true);

		assertTrue(service.savePartialPayment(mock(PartialPayments.class)));
	}

	@Test
	void testSavePartialPayment_NullPayment()
	{
		assertFalse(service.savePartialPayment(null));
	}

	@Test
	public void testSavePartialPaymentAmount()
	{
		PartialPaymentAmount partialPaymentAmount = new PartialPaymentAmount();
		partialPaymentAmount.setReference("TEST");
		when(dao.savePartialPaymentAmount(any(PartialPaymentAmount.class))).thenReturn(true);
		when(dao.savePartialPaymentVersion(any(PartialPaymentVersion.class))).thenReturn(true);

		assertTrue(service.savePartialPaymentAmount(partialPaymentAmount));
		verify(dao).savePartialPaymentVersion(any(PartialPaymentVersion.class));
	}

	@Test
	public void testSavePartialPaymentAmount_False()
	{
		assertFalse(service.savePartialPaymentAmount(null));
		verifyNoMoreInteractions(dao);
	}
	
	@Test
	public void testFetchAllPartialPaymentsByReferenceAndType() throws DataAccessException, SQLException
	{
		PartialPayments partialPayments = mock(PartialPayments.class);

		when(dao.fetchAllPartialPaymentsByReferenceAndType(anyString(), anyInt())).thenThrow(SQLException.class)
				.thenThrow(DataAccessException.class)
				.thenReturn(ImmutableList.of(partialPayments));

		// Invalid params
		assertTrue(service.fetchAllPartialPaymentsByReferenceAndType("", 0)
				.isEmpty());
		assertTrue(service.fetchAllPartialPaymentsByReferenceAndType("ref", 0)
				.isEmpty());
		// Exception
		assertTrue(service.fetchAllPartialPaymentsByReferenceAndType("ref", 1)
				.isEmpty());
		assertTrue(service.fetchAllPartialPaymentsByReferenceAndType("ref", 1)
				.isEmpty());
		// Success
		assertFalse(service.fetchAllPartialPaymentsByReferenceAndType("ref", 1)
				.isEmpty());
	}

	@Test
	public void testSavePaymentCustomValues()
	{
		when(dao.savePaymentCustomValues(any(), anyString())).thenReturn(true);
		Map<String, String> customValues = new HashMap<>();
		customValues.put("key", "value");

		assertTrue(service.savePaymentCustomValues(customValues, 1, 1));
	}

	@Test
	public void testSavePaymentCustomValues_EmptyCustomValues()
	{
		assertFalse(service.savePaymentCustomValues(new HashMap<String, String>(), 0, 0));
	}

	@Test
	public void testSavePaymentCustomValues_InvalidPaymentId()
	{
		Map<String, String> customValues = new HashMap<>();
		customValues.put("key", "value");

		assertFalse(service.savePaymentCustomValues(customValues, 0, 0));
	}

	@Test
	public void testSavePaymentCustomValues_InvalidPartialPaymentId()
	{
		Map<String, String> customValues = new HashMap<>();
		customValues.put("key", "value");

		assertFalse(service.savePaymentCustomValues(customValues, 1, 0));
	}

	@Test
	public void testFetchPartialPaymentsByTransactionIdReferenceAndType_ValidInputs() throws DataAccessException, SQLException
	{
		String transactionId = "validTransactionId";
		String reference = "validReference";
		int type = 1;
		PartialPayments mockPayment = mock(PartialPayments.class);
		when(dao.fetchPartialPaymentsByTransactionIdReferenceAndType(transactionId, reference, type))
				.thenReturn(mockPayment);

		PartialPayments result =
				service.fetchPartialPaymentsByTransactionIdReferenceAndType(transactionId, reference, type);

		assertNotNull(result);
		verify(dao).fetchPartialPaymentsByTransactionIdReferenceAndType(transactionId, reference, type);
	}

	@Test
	public void testFetchPartialPaymentsByTransactionIdReferenceAndType_NullTransactionId() throws DataAccessException, SQLException
	{
		String reference = "validReference";
		int type = 1;

		PartialPayments result = service.fetchPartialPaymentsByTransactionIdReferenceAndType(null, reference, type);

		assertNull(result);
		verify(dao, never()).fetchPartialPaymentsByTransactionIdReferenceAndType(anyString(), anyString(), anyInt());
	}

	@Test
	public void testFetchPartialPaymentsByTransactionIdReferenceAndType_EmptyReference() throws DataAccessException, SQLException
	{
		String transactionId = "validTransactionId";
		int type = 1;

		PartialPayments result = service.fetchPartialPaymentsByTransactionIdReferenceAndType(transactionId, "", type);

		assertNull(result);
		verify(dao, never()).fetchPartialPaymentsByTransactionIdReferenceAndType(anyString(), anyString(), anyInt());
	}

	@Test
	public void testFetchPartialPaymentsByTransactionIdReferenceAndType_NonPositiveType() throws DataAccessException, SQLException
	{
		String transactionId = "validTransactionId";
		String reference = "validReference";

		PartialPayments result =
				service.fetchPartialPaymentsByTransactionIdReferenceAndType(transactionId, reference, 0);

		assertNull(result);
		verify(dao, never()).fetchPartialPaymentsByTransactionIdReferenceAndType(anyString(), anyString(), anyInt());
	}

	@Test
	public void testSetAmountRefunded_Success()
	{
		Payments payment = mock(Payments.class);
		BigDecimal refundAmount = new BigDecimal("10.00");

		when(payment.getAmountRefunded()).thenReturn("5.00");
		when(dao.savePayment(payment)).thenReturn(true);

		assertTrue(service.setAmountRefunded(payment, refundAmount));

		verify(payment).setAmountRefunded("15.00");
		verify(dao).savePayment(payment);
	}

	@Test
	public void testSetAmountRefunded_FailedToSave()
	{
		Payments payment = mock(Payments.class);
		BigDecimal refundAmount = new BigDecimal("10.00");

		when(payment.getAmountRefunded()).thenReturn("5.00");
		when(dao.savePayment(payment)).thenReturn(false);

		assertFalse(service.setAmountRefunded(payment, refundAmount));

		verify(payment).setAmountRefunded("15.00");
		verify(dao).savePayment(payment);
	}

	@Test
	public void testSetAmountRefunded_PaymentNull()
	{
		assertFalse(service.setAmountRefunded(null, BigDecimal.TEN));
	}
	
	@Test
	public void savePaymentTransaction()
	{
		when(dao.savePaymentTransaction(any())).thenReturn(true);
		
		assertTrue(service.savePaymentTransaction(mock(PaymentTransaction.class)));
	}
	
	@Test
	public void savePaymentTransaction_NullTransaction()
	{
		assertFalse(service.savePaymentTransaction(null));
		
		verifyNoInteractions(dao);
	}
}