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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.kmp.aeroparker.subscription.payments.braintree.BraintreeSubscriptionHandler;
import com.kmp.aeroparker.subscription.payments.braintree.BraintreeTransactionHandler;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;
import com.kmp.aeroparker.subscription.payments.factory.PaymentProcessorFactory;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.processor.BraintreePaymentProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class BraintreePaymentHandlerTest
{
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	@Mock
	private PaymentService paymentService;
	@Mock
	private BraintreeTransactionHandler transactionHandler;
	@Mock
	private BraintreeSubscriptionHandler subscriptionHandler;
	@Mock
	private GlobalProperties globalProperties;
	@Mock
	private BraintreeObjectFactory braintreeObjectFactory;
	@Mock
	private PaymentProcessorFactory paymentProcessorFactory;
	@InjectMocks
	private BraintreePaymentHandler handler;
	@Mock
	private Affiliates affiliate;
	@Mock
	private Languages language;

	@Test
	void testGetType()
	{
		assertThat(handler.getType()).isEqualTo(PaymentGatewayType.BRAINTREE);
	}

	@Test
	void testSetUpTransaction()
	{
		when(affiliate.getName()).thenReturn("affName");
		PaymentGatewayParameters paymentHandlerParams = mock(PaymentGatewayParameters.class);
		when(paymentHandlerParams.getLanguage()).thenReturn(language);
		when(language.getLanguageCode()).thenReturn("en");
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		BraintreeGateway gateway = mock(BraintreeGateway.class);
		when(transactionHandler.setMerchantAccountId(anyString(), any())).thenReturn(mock(ClientTokenRequest.class));
		ClientTokenGateway clientTokenGateway = mock(ClientTokenGateway.class);
		when(clientTokenGateway.generate(any())).thenReturn(EnhancedRandom.random(String.class));
		when(paymentHandlerParams.getCurrency()).thenReturn("GBP");
		when(gateway.clientToken()).thenReturn(clientTokenGateway);
		when(braintreeObjectFactory.createGateway(any())).thenReturn(gateway);
		PaymentGatewayTypes braintreeGatewayInfo = mock(PaymentGatewayTypes.class);
		when(braintreeGatewayInfo.getPaymentJsp()).thenReturn("brainTreePayment.jsp");
		when(paymentService.fetchPaymentGatewayTypesById(anyInt())).thenReturn(braintreeGatewayInfo);
		when(globalProperties.isDev()).thenReturn(true);
		assertThat(handler.setUpTransaction(paymentHandlerParams)).isNotEmpty()
				.hasSize(13)
				.containsKeys("paymentGatewayType", "languageCode", "clientToken", "affiliateName");
		verify(paymentService).fetchPaymentGatewayTypesById(anyInt());
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verify(braintreeObjectFactory).createGateway(any());
		verify(transactionHandler).setMerchantAccountId(anyString(), any());
		verifyNoMoreInteractions(paymentService, braintreeObjectFactory);
	}

	@Test
	void testSetUpTransaction_GlobalProperties_Staging()
	{
		when(affiliate.getName()).thenReturn("affName");
		PaymentGatewayParameters paymentHandlerParams = mock(PaymentGatewayParameters.class);
		when(paymentHandlerParams.getLanguage()).thenReturn(language);
		when(language.getLanguageCode()).thenReturn("en");
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		BraintreeGateway gateway = mock(BraintreeGateway.class);
		when(transactionHandler.setMerchantAccountId(anyString(), any())).thenReturn(mock(ClientTokenRequest.class));
		ClientTokenGateway clientTokenGateway = mock(ClientTokenGateway.class);
		when(clientTokenGateway.generate(any())).thenReturn(EnhancedRandom.random(String.class));
		when(paymentHandlerParams.getCurrency()).thenReturn("GBP");
		when(gateway.clientToken()).thenReturn(clientTokenGateway);
		when(braintreeObjectFactory.createGateway(any())).thenReturn(gateway);
		PaymentGatewayTypes braintreeGatewayInfo = mock(PaymentGatewayTypes.class);
		when(braintreeGatewayInfo.getPaymentJsp()).thenReturn("brainTreePayment.jsp");
		when(paymentService.fetchPaymentGatewayTypesById(anyInt())).thenReturn(braintreeGatewayInfo);
		when(globalProperties.isDev()).thenReturn(false);
		when(globalProperties.isStaging()).thenReturn(true);
		assertThat(handler.setUpTransaction(paymentHandlerParams)).isNotEmpty()
				.hasSize(13)
				.containsKeys("paymentGatewayType", "languageCode", "clientToken", "affiliateName");
		verify(paymentService).fetchPaymentGatewayTypesById(anyInt());
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verify(braintreeObjectFactory).createGateway(any());
		verify(transactionHandler).setMerchantAccountId(anyString(), any());
		verifyNoMoreInteractions(paymentService, braintreeObjectFactory);
	}

	@Test
	void testSetUpTransaction_GlobalProperties_Live()
	{
		when(affiliate.getName()).thenReturn("affName");
		PaymentGatewayParameters paymentHandlerParams = mock(PaymentGatewayParameters.class);
		when(paymentHandlerParams.getLanguage()).thenReturn(language);
		when(language.getLanguageCode()).thenReturn("en");
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		BraintreeGateway gateway = mock(BraintreeGateway.class);
		when(transactionHandler.setMerchantAccountId(anyString(), any())).thenReturn(mock(ClientTokenRequest.class));
		ClientTokenGateway clientTokenGateway = mock(ClientTokenGateway.class);
		when(clientTokenGateway.generate(any())).thenReturn(EnhancedRandom.random(String.class));
		when(paymentHandlerParams.getCurrency()).thenReturn("GBP");
		when(gateway.clientToken()).thenReturn(clientTokenGateway);
		when(braintreeObjectFactory.createGateway(any())).thenReturn(gateway);
		PaymentGatewayTypes braintreeGatewayInfo = mock(PaymentGatewayTypes.class);
		when(braintreeGatewayInfo.getPaymentJsp()).thenReturn("brainTreePayment.jsp");
		when(paymentService.fetchPaymentGatewayTypesById(anyInt())).thenReturn(braintreeGatewayInfo);
		when(globalProperties.isDev()).thenReturn(false);
		when(globalProperties.isStaging()).thenReturn(false);
		assertThat(handler.setUpTransaction(paymentHandlerParams)).isNotEmpty()
				.hasSize(13)
				.containsKeys("paymentGatewayType", "languageCode", "clientToken", "affiliateName");
		verify(paymentService).fetchPaymentGatewayTypesById(anyInt());
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verify(braintreeObjectFactory).createGateway(any());
		verify(transactionHandler).setMerchantAccountId(anyString(), any());
		verifyNoMoreInteractions(paymentService, braintreeObjectFactory);
	}

	@Test
	void testSetUpTransaction_BraintreeCredentials_Null()
	{
		PaymentGatewayParameters paymentHandlerParams = mock(PaymentGatewayParameters.class);
		when(paymentHandlerParams.getAffiliate()).thenReturn(affiliate);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(null);
		assertThat(handler.setUpTransaction(paymentHandlerParams)).isEmpty();
		verify(paymentService, times(0)).fetchPaymentGatewayTypesById(anyInt());
		verify(paymentService).fetchPaymentCredentials(anyInt(), anyInt(), any());
		verify(braintreeObjectFactory, times(0)).createGateway(any());
		verifyNoMoreInteractions(paymentService, braintreeObjectFactory);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testQueryTransaction_Subscription_Now()
	{
		SubscriptionBookingData bookingData =
				EnhancedRandom.random(SubscriptionBookingData.class, "isRecurring", "startDate");
		bookingData.setStartDate(LocalDate.now()
				.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
		bookingData.setRecurring(true);
		Result<Transaction> transactionResult = mock(Result.class);
		Result<Subscription> subscResult = mock(Result.class);
		when(transactionResult.isSuccess()).thenReturn(true);
		Transaction transaction = mock(Transaction.class);
		Customer customer = mock(Customer.class);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(transactionResult.getTarget()).thenReturn(transaction);
		when(transaction.getCustomer()).thenReturn(customer);
		when(customer.getId()).thenReturn("test");
		when(subscResult.isSuccess()).thenReturn(true);
		when(subscResult.getTarget()).thenReturn(mock(Subscription.class));
		when(transactionHandler.processTransactionRequest(anyString(), anyString(), any(), any(), any(), anyBoolean()))
				.thenReturn(transactionResult);
		when(subscriptionHandler.processSubscriptionRequest(anyString(), anyString(), any())).thenReturn(subscResult);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		BraintreePaymentProcessor braintreePaymentProcessor = mock(BraintreePaymentProcessor.class);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreePaymentProcessor);
		Payments payments = mock(Payments.class);
		when(braintreePaymentProcessor.process(any())).thenReturn(payments);
		when(payments.getId()).thenReturn(1);
		assertThat(handler.processPayment(bookingData)).isTrue();
	}

	@SuppressWarnings("unchecked")
	@Test
	void testQueryTransaction_Subscription_Now_Fail()
	{
		SubscriptionBookingData bookingData =
				EnhancedRandom.random(SubscriptionBookingData.class, "isRecurring", "startDate");
		bookingData.setStartDate(LocalDate.now()
				.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
		bookingData.setRecurring(true);
		Result<Transaction> transactionResult = mock(Result.class);
		Result<Subscription> subscResult = mock(Result.class);
		when(transactionResult.isSuccess()).thenReturn(true);
		Transaction transaction = mock(Transaction.class);
		Customer customer = mock(Customer.class);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(transactionResult.getTarget()).thenReturn(transaction);
		when(transaction.getCustomer()).thenReturn(customer);
		when(customer.getId()).thenReturn("test");
		when(subscResult.isSuccess()).thenReturn(false);
		when(subscResult.getMessage()).thenReturn("Failed test");
		when(transactionHandler.processTransactionRequest(anyString(), anyString(), any(), any(), any(), anyBoolean()))
				.thenReturn(transactionResult);
		when(subscriptionHandler.processSubscriptionRequest(anyString(), anyString(), any())).thenReturn(subscResult);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		BraintreePaymentProcessor braintreePaymentProcessor = mock(BraintreePaymentProcessor.class);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreePaymentProcessor);
		Payments payments = mock(Payments.class);
		when(braintreePaymentProcessor.process(any())).thenReturn(payments);
		when(payments.getId()).thenReturn(1);
		assertThat(handler.processPayment(bookingData)).isFalse();
	}

	@SuppressWarnings("unchecked")
	@Test
	void testQueryTransaction_Subscription_Later()
	{
		SubscriptionBookingData bookingData =
				EnhancedRandom.random(SubscriptionBookingData.class, "startDate", "dateFormat");
		// Set the start date to now + a week to avoid test throwing errors later
		bookingData.setStartDate(LocalDate.now()
				.plusDays(7)
				.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
		bookingData.setDateFormat(DATE_FORMAT);
		Result<Transaction> transactionResult = mock(Result.class);
		when(transactionResult.isSuccess()).thenReturn(true);
		Transaction transaction = mock(Transaction.class);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(transactionResult.getTarget()).thenReturn(transaction);
		when(transactionHandler.processTransactionRequest(anyString(), anyString(), any(), any(), any(), anyBoolean()))
				.thenReturn(transactionResult);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		BraintreePaymentProcessor braintreePaymentProcessor = mock(BraintreePaymentProcessor.class);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreePaymentProcessor);
		Payments payments = mock(Payments.class);
		when(braintreePaymentProcessor.process(any())).thenReturn(payments);
		when(payments.getId()).thenReturn(1);
		assertThat(handler.processPayment(bookingData)).isTrue();
	}

	@SuppressWarnings("unchecked")
	@Test
	void testQueryTransaction_Transaction_False()
	{
		SubscriptionBookingData bookingData = EnhancedRandom.random(SubscriptionBookingData.class, "startDate");
		bookingData.setStartDate(LocalDate.now()
				.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
		Result<Transaction> result = mock(Result.class);
		when(result.isSuccess()).thenReturn(false);
		when(transactionHandler.processTransactionRequest(anyString(), anyString(), any(), any(), any(), anyBoolean()))
				.thenReturn(result);
		assertThat(handler.processPayment(bookingData)).isFalse();
		verify(paymentProcessorFactory, times(0)).getInstance(any());
	}

	@Test
	void testQueryTransaction_Empty_Nonce_Token()
	{
		SubscriptionBookingData bookingData = EnhancedRandom.random(SubscriptionBookingData.class, "paymentReference");
		assertThat(handler.processPayment(bookingData)).isFalse();
		verify(paymentProcessorFactory, times(0)).getInstance(any());
	}

	@SuppressWarnings("unchecked")
	@Test
	void testQueryTransaction_Payment_Null_Recurring()
	{
		SubscriptionBookingData bookingData =
				EnhancedRandom.random(SubscriptionBookingData.class, "isRecurring", "startDate");
		bookingData.setStartDate(LocalDate.now()
				.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
		bookingData.setRecurring(true);
		Result<Transaction> transactionResult = mock(Result.class);
		Result<Subscription> subscResult = mock(Result.class);
		when(transactionResult.isSuccess()).thenReturn(true);
		Transaction transaction = mock(Transaction.class);
		Customer customer = mock(Customer.class);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(transactionResult.getTarget()).thenReturn(transaction);
		when(transaction.getCustomer()).thenReturn(customer);
		when(customer.getId()).thenReturn("test");
		when(subscResult.isSuccess()).thenReturn(true);
		when(subscResult.getTarget()).thenReturn(mock(Subscription.class));
		when(transactionHandler.processTransactionRequest(anyString(), anyString(), any(), any(), any(), anyBoolean()))
				.thenReturn(transactionResult);
		when(subscriptionHandler.processSubscriptionRequest(anyString(), anyString(), any())).thenReturn(subscResult);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		BraintreePaymentProcessor braintreePaymentProcessor = mock(BraintreePaymentProcessor.class);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreePaymentProcessor);
		when(braintreePaymentProcessor.process(any())).thenReturn(null);
		assertThat(handler.processPayment(bookingData)).isFalse();
	}

	@SuppressWarnings("unchecked")
	@Test
	void testQueryTransaction_Payment_Null_Season_Ticket()
	{
		SubscriptionBookingData bookingData =
				EnhancedRandom.random(SubscriptionBookingData.class, "isRecurring", "startDate");
		bookingData.setStartDate(LocalDate.now()
				.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
		bookingData.setRecurring(false);
		Result<Transaction> transactionResult = mock(Result.class);
		when(transactionResult.isSuccess()).thenReturn(true);
		Transaction transaction = mock(Transaction.class);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(transactionResult.getTarget()).thenReturn(transaction);
		when(transactionHandler.processTransactionRequest(anyString(), anyString(), any(), any(), any(), anyBoolean()))
				.thenReturn(transactionResult);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		BraintreePaymentProcessor braintreePaymentProcessor = mock(BraintreePaymentProcessor.class);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreePaymentProcessor);
		when(braintreePaymentProcessor.process(any())).thenReturn(null);
		assertThat(handler.processPayment(bookingData)).isFalse();
	}

	@SuppressWarnings("unchecked")
	@Test
	void testQueryTransaction_Payment_ID_Zero_Recurring()
	{
		SubscriptionBookingData bookingData =
				EnhancedRandom.random(SubscriptionBookingData.class, "isRecurring", "startDate");
		bookingData.setStartDate(LocalDate.now()
				.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
		bookingData.setDateFormat(DATE_FORMAT);
		bookingData.setRecurring(true);
		Result<Transaction> transactionResult = mock(Result.class);
		Result<Subscription> subscResult = mock(Result.class);
		when(transactionResult.isSuccess()).thenReturn(true);
		Transaction transaction = mock(Transaction.class);
		Customer customer = mock(Customer.class);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(transactionResult.getTarget()).thenReturn(transaction);
		when(transaction.getCustomer()).thenReturn(customer);
		when(customer.getId()).thenReturn("test");
		when(subscResult.isSuccess()).thenReturn(true);
		when(subscResult.getTarget()).thenReturn(mock(Subscription.class));
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		when(transactionHandler.processTransactionRequest(anyString(), anyString(), any(), any(), any(), anyBoolean()))
				.thenReturn(transactionResult);
		when(subscriptionHandler.processSubscriptionRequest(anyString(), anyString(), any())).thenReturn(subscResult);
		BraintreePaymentProcessor braintreePaymentProcessor = mock(BraintreePaymentProcessor.class);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreePaymentProcessor);
		Payments payments = mock(Payments.class);
		when(braintreePaymentProcessor.process(any())).thenReturn(payments);
		when(payments.getId()).thenReturn(0);
		assertThat(handler.processPayment(bookingData)).isFalse();
	}

	@SuppressWarnings("unchecked")
	@Test
	void testQueryTransaction_Payment_ID_Zero_Season_Ticket()
	{
		SubscriptionBookingData bookingData =
				EnhancedRandom.random(SubscriptionBookingData.class, "isRecurring", "startDate");
		bookingData.setStartDate(LocalDate.now()
				.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
		bookingData.setRecurring(false);
		Result<Transaction> transactionResult = mock(Result.class);
		when(transactionResult.isSuccess()).thenReturn(true);
		Transaction transaction = mock(Transaction.class);
		BraintreeCredentials braintreeCredentials = mock(BraintreeCredentials.class);
		when(transactionResult.getTarget()).thenReturn(transaction);
		when(paymentService.fetchPaymentCredentials(anyInt(), anyInt(), eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreeCredentials);
		when(transactionHandler.processTransactionRequest(anyString(), anyString(), any(), any(), any(), anyBoolean()))
				.thenReturn(transactionResult);
		BraintreePaymentProcessor braintreePaymentProcessor = mock(BraintreePaymentProcessor.class);
		when(paymentProcessorFactory.getInstance(eq(PaymentGatewayType.BRAINTREE)))
				.thenReturn(braintreePaymentProcessor);
		Payments payments = mock(Payments.class);
		when(braintreePaymentProcessor.process(any())).thenReturn(payments);
		when(payments.getId()).thenReturn(0);
		assertThat(handler.processPayment(bookingData)).isFalse();
	}
}