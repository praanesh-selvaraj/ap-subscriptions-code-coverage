package com.kmp.aeroparker.subscription.payments.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.braintreegateway.ApplePayDetails;
import com.braintreegateway.CreditCard;
import com.braintreegateway.PayPalDetails;
import com.braintreegateway.PaymentInstrumentType;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class BraintreePaymentProcessorTest
{
	@Mock
	private PaymentService paymentService;
	@InjectMocks
	private BraintreePaymentProcessor processor;

	@Mock
	private PaymentProcessorParameters paymentProcessorParameters;

	@Test
	void testProcess_Payment_Not_Saved()
	{
		Transaction transaction = mock(Transaction.class);
		when(transaction.getId()).thenReturn("1");
		when(transaction.getOrderId()).thenReturn("reference");
		when(transaction.getAmount()).thenReturn(BigDecimal.TEN);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		// when(paymentProcessorParameters.getConfirmationGuid()).thenReturn("guid");
		when(paymentProcessorParameters.getBraintreeTransaction()).thenReturn(transaction);
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(0);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		when(paymentService.savePayment(any())).thenReturn(false);
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class)
				.hasFieldOrPropertyWithValue("id", 0);
		verify(paymentService).savePayment(any());
		verify(paymentService, times(0)).saveCustomValues(anyInt(), any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_Transaction_Null()
	{
		when(paymentProcessorParameters.getBraintreeTransaction()).thenReturn(null);
		assertThat(processor.process(paymentProcessorParameters)).isNull();
		verifyNoInteractions(paymentService);
	}

	@Test
	void testProcess_PaymentProcessorParameters_Null()
	{
		assertThat(processor.process(null)).isNull();
		verifyNoInteractions(paymentService);
	}

	@Test
	void testProcess_CREDIT_CARD_Debit()
	{
		Transaction transaction = mock(Transaction.class);
		when(transaction.getId()).thenReturn("1");
		when(transaction.getOrderId()).thenReturn("reference");
		when(transaction.getAmount()).thenReturn(BigDecimal.TEN);
		when(transaction.getPaymentInstrumentType()).thenReturn(PaymentInstrumentType.CREDIT_CARD);
		CreditCard creditCard = mock(CreditCard.class);
		when(transaction.getCreditCard()).thenReturn(creditCard);
		when(creditCard.getDebit()).thenReturn(CreditCard.Debit.YES);
		when(creditCard.getCardType()).thenReturn("VISA");
		when(creditCard.getExpirationYear()).thenReturn("2019");
		when(creditCard.getLast4()).thenReturn("1452");
		when(creditCard.getExpirationMonth()).thenReturn("10");
		when(paymentProcessorParameters.getBraintreeTransaction()).thenReturn(transaction);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(1);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(true);
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService).savePayment(any());
		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_CREDIT_CARD_Debit_Subscription()
	{
		Transaction transaction = mock(Transaction.class);
		when(transaction.getId()).thenReturn("1");
		when(transaction.getOrderId()).thenReturn(null);
		when(transaction.getAmount()).thenReturn(BigDecimal.TEN);
		when(transaction.getPaymentInstrumentType()).thenReturn(PaymentInstrumentType.CREDIT_CARD);
		CreditCard creditCard = mock(CreditCard.class);
		when(transaction.getCreditCard()).thenReturn(creditCard);
		when(creditCard.getDebit()).thenReturn(CreditCard.Debit.YES);
		when(creditCard.getCardType()).thenReturn("VISA");
		when(creditCard.getExpirationYear()).thenReturn("2019");
		when(creditCard.getLast4()).thenReturn("1452");
		when(creditCard.getExpirationMonth()).thenReturn("10");
		when(paymentProcessorParameters.getBraintreeTransaction()).thenReturn(transaction);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentProcessorParameters.getBookingReference()).thenReturn("reference");
		Subscription subscription = mock(Subscription.class);
		when(paymentProcessorParameters.getBraintreeSubscription()).thenReturn(subscription);
		when(subscription.getId()).thenReturn("testId");
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(1);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(true);
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService).savePayment(any());
		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_CREDIT_CARD_Credit()
	{
		Transaction transaction = mock(Transaction.class);
		when(transaction.getId()).thenReturn("1");
		when(transaction.getOrderId()).thenReturn("reference");
		when(transaction.getAmount()).thenReturn(BigDecimal.TEN);
		when(transaction.getPaymentInstrumentType()).thenReturn(PaymentInstrumentType.CREDIT_CARD);
		CreditCard creditCard = mock(CreditCard.class);
		when(transaction.getCreditCard()).thenReturn(creditCard);
		when(creditCard.getDebit()).thenReturn(CreditCard.Debit.NO);
		when(creditCard.getCardType()).thenReturn("VISA");
		when(creditCard.getExpirationYear()).thenReturn("2019");
		when(creditCard.getLast4()).thenReturn("1452");
		when(creditCard.getExpirationMonth()).thenReturn("10");
		when(paymentProcessorParameters.getBraintreeTransaction()).thenReturn(transaction);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentService.savePayment(any())).thenReturn(true);
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(1);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(true);
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService).savePayment(any());
		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_CREDIT_CARD_Credit_CustomValues_Not_Saved()
	{
		Transaction transaction = mock(Transaction.class);
		when(transaction.getId()).thenReturn("1");
		when(transaction.getOrderId()).thenReturn("reference");
		when(transaction.getAmount()).thenReturn(BigDecimal.TEN);
		when(transaction.getPaymentInstrumentType()).thenReturn(PaymentInstrumentType.CREDIT_CARD);
		CreditCard creditCard = mock(CreditCard.class);
		when(transaction.getCreditCard()).thenReturn(creditCard);
		when(creditCard.getDebit()).thenReturn(CreditCard.Debit.NO);
		when(creditCard.getCardType()).thenReturn("VISA");
		when(creditCard.getExpirationYear()).thenReturn("2019");
		when(creditCard.getLast4()).thenReturn("1452");
		when(creditCard.getExpirationMonth()).thenReturn("10");
		when(paymentProcessorParameters.getBraintreeTransaction()).thenReturn(transaction);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentService.savePayment(any())).thenReturn(true);
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(1);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(false);
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService).savePayment(any());
		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_PAYPAL_ACCOUNT()
	{
		Transaction transaction = mock(Transaction.class);
		when(transaction.getId()).thenReturn("1");
		when(transaction.getOrderId()).thenReturn("reference");
		when(transaction.getAmount()).thenReturn(BigDecimal.TEN);
		when(transaction.getPaymentInstrumentType()).thenReturn(PaymentInstrumentType.PAYPAL_ACCOUNT);
		when(transaction.getPayPalDetails()).thenReturn(EnhancedRandom.random(PayPalDetails.class));
		when(paymentProcessorParameters.getBraintreeTransaction()).thenReturn(transaction);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(true);
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(1);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService).savePayment(any());
		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_APPLE_PAY_CARD()
	{
		Transaction transaction = mock(Transaction.class);
		when(transaction.getId()).thenReturn("1");
		when(transaction.getOrderId()).thenReturn("reference");
		when(transaction.getAmount()).thenReturn(BigDecimal.TEN);
		when(transaction.getCurrencyIsoCode()).thenReturn("isoCode");
		when(transaction.getPaymentInstrumentType()).thenReturn(PaymentInstrumentType.APPLE_PAY_CARD);
		ApplePayDetails applePayDetails = mock(ApplePayDetails.class);
		when(applePayDetails.getLast4()).thenReturn("4123");
		when(applePayDetails.getExpirationMonth()).thenReturn("10");
		when(applePayDetails.getExpirationYear()).thenReturn("2019");
		when(applePayDetails.getCardType()).thenReturn("type");
		when(applePayDetails.getToken()).thenReturn("token");
		when(transaction.getApplePayDetails()).thenReturn(applePayDetails);
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(1);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		when(paymentProcessorParameters.getBraintreeTransaction()).thenReturn(transaction);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(true);
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService).savePayment(any());
		verify(paymentService).saveCustomValues(anyInt(), any());
		verify(paymentService).createPayment(anyString(), anyString(), anyString(), anyInt(), anyString());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testProcess_Unknown_Payment_Type()
	{
		Transaction transaction = mock(Transaction.class);
		when(transaction.getId()).thenReturn("1");
		when(transaction.getOrderId()).thenReturn("reference");
		when(transaction.getAmount()).thenReturn(BigDecimal.TEN);
		when(transaction.getCurrencyIsoCode()).thenReturn("isoCode");
		when(transaction.getPaymentInstrumentType()).thenReturn("");
		when(paymentProcessorParameters.getBraintreeTransaction()).thenReturn(transaction);
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		Payments payments = mock(Payments.class);
		when(payments.getId()).thenReturn(1);
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(payments);
		when(paymentService.savePayment(any())).thenReturn(true);
		when(paymentService.saveCustomValues(anyInt(), any())).thenReturn(true);
		assertThat(processor.process(paymentProcessorParameters)).isNotNull()
				.isInstanceOf(Payments.class);
		verify(paymentService).savePayment(any());
		verify(paymentService).saveCustomValues(anyInt(), any());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testGetType()
	{
		assertThat(processor.getType()).isEqualTo(PaymentGatewayType.BRAINTREE);
	}
}