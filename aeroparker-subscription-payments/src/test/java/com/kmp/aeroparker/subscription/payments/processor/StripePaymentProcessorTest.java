package com.kmp.aeroparker.subscription.payments.processor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.stripe.StripePaymentIntentProcessor;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethod.Card;

@ExtendWith(MockitoExtension.class)
public class StripePaymentProcessorTest
{
	@Mock
	private PaymentService paymentService;
	@Mock
	private StripePaymentIntentProcessor stripePaymentIntentProcessor;
	@Mock
	private PaymentIntent paymentIntent;
	@Mock
	private Payments parentPayment;
	@Mock
	private PartialPayments partialPayment;
	@Mock
	private PaymentProcessorParameters paymentProcessorParameters;
	
	private StripePaymentProcessor processor;

	@BeforeEach
	public void setUp()
	{
		processor = new StripePaymentProcessor(paymentService, stripePaymentIntentProcessor);
	}
	
	private void setupProcessorParameters()
	{
		when(paymentProcessorParameters.getTimeZone()).thenReturn("Europe/London");
		when(paymentProcessorParameters.getPaymentIntent()).thenReturn(paymentIntent);
		when(paymentProcessorParameters.getBookingReference()).thenReturn("ref");
		when(paymentProcessorParameters.getAffiliateId()).thenReturn(1);
	}

	@Test
	public void testProcessPartial()
	{
		setupProcessorParameters();
		when(paymentService.savePayment(any(Payments.class))).thenReturn(true);
		when(paymentService.savePartialPayment(any(PartialPayments.class))).thenReturn(true);
		when(parentPayment.getId()).thenReturn(1);
		PaymentMethod paymentMethod = mock(PaymentMethod.class);
		Card card = mock(Card.class);
		when(paymentMethod.getCard()).thenReturn(card);
		when(card.getLast4()).thenReturn("1234");
		when(card.getExpMonth()).thenReturn(12L);
		when(card.getExpYear()).thenReturn(2050L);
		when(card.getBrand()).thenReturn("Visa");
		when(stripePaymentIntentProcessor.getPaymentMethod(anyInt(), any())).thenReturn(paymentMethod);
		when(paymentIntent.getAmount()).thenReturn(200000l);
		when(parentPayment.getAmount()).thenReturn(BigDecimal.TEN.toString());

		assertNotNull(processor.processPartial(paymentProcessorParameters, parentPayment));

		verify(paymentService).savePaymentCustomValues(any(), anyInt(), anyInt());
	}

	@Test
	public void testProcessPartial_NullParentPayment()
	{
		setupProcessorParameters();
		when(paymentService.createPayment(anyString(), anyString(), anyString(), anyInt(), anyString()))
				.thenReturn(parentPayment);
		when(paymentService.savePayment(any(Payments.class))).thenReturn(true);
		when(paymentService.savePartialPayment(any(PartialPayments.class))).thenReturn(true);
		PaymentMethod paymentMethod = mock(PaymentMethod.class);
		Card card = mock(Card.class);
		when(paymentMethod.getCard()).thenReturn(card);
		when(card.getLast4()).thenReturn("1234");
		when(card.getExpMonth()).thenReturn(12L);
		when(card.getExpYear()).thenReturn(2050L);
		when(card.getBrand()).thenReturn("Visa");
		when(stripePaymentIntentProcessor.getPaymentMethod(anyInt(), any())).thenReturn(paymentMethod);

		assertNotNull(processor.processPartial(paymentProcessorParameters, null));

		verify(paymentService).savePaymentCustomValues(any(), anyInt(), anyInt());
	}

	@Test
	public void testProcessPartial_FailedSavingParentPayment()
	{
		setupProcessorParameters();
		when(parentPayment.getAmount()).thenReturn(BigDecimal.TEN.toString());
		when(paymentService.savePayment(any(Payments.class))).thenReturn(false);
		when(parentPayment.getAmount()).thenReturn(BigDecimal.TEN.toString());

		assertNull(processor.processPartial(paymentProcessorParameters, parentPayment));

		verify(paymentService, times(0)).savePaymentCustomValues(any(), anyInt(), anyInt());
	}

	@Test
	public void testProcessPartial_FailedSavingPartialPayment()
	{
		setupProcessorParameters();
		when(parentPayment.getAmount()).thenReturn(BigDecimal.TEN.toString());
		when(paymentService.savePayment(any(Payments.class))).thenReturn(true);
		when(paymentService.savePartialPayment(any(PartialPayments.class))).thenReturn(false);

		assertNull(processor.processPartial(paymentProcessorParameters, parentPayment));

		verify(paymentService, times(0)).savePaymentCustomValues(any(), anyInt(), anyInt());
	}
	
	@Test
	public void testProcessPartial_DoNotAttemptToSavePartialPaymentPayment()
	{
		setupProcessorParameters();
		when(parentPayment.getAmount()).thenReturn(BigDecimal.TEN.toString());
		when(paymentService.fetchPartialPaymentsByTransactionIdReferenceAndType(any(), anyString(), anyInt())).thenReturn(partialPayment);
		when(paymentService.savePayment(any(Payments.class))).thenReturn(true);

		assertNull(processor.processPartial(paymentProcessorParameters, parentPayment));

		verify(paymentService, times(1)).savePayment(any());
		verify(paymentService, times(0)).savePartialPayment(partialPayment);
		verify(paymentService, times(0)).savePaymentCustomValues(any(), anyInt(), anyInt());
	}
	
	@Test
	public void testGetType()
	{
		assertEquals(PaymentGatewayType.STRIPE, processor.getType());
	}
	
	@Test
	public void testProcess()
	{
		assertNull(processor.process(paymentProcessorParameters));
	}
}
