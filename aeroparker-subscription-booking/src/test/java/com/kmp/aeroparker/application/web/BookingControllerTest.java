package com.kmp.aeroparker.application.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.builder.PromotionBuilder;
import com.kmp.aeroparker.application.builder.ReservationBuilderObjectFactory;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.contact.activation.ContactActivationProcessor;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.PromotionService;
import com.kmp.aeroparker.application.email.dispatcher.ActivateAccountEmailDispatcher;
import com.kmp.aeroparker.application.email.dispatcher.ConfirmationEmailDispatcher;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.model.InvoiceCreator;
import com.kmp.aeroparker.application.model.PurchaseQuery;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.payment.handler.PaymentHandler;
import com.kmp.aeroparker.application.processor.AncillaryBarcodeProcessor;
import com.kmp.aeroparker.application.validator.SubscriptionValidator;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSProducer;
import com.kmp.aeroparker.sqs.reports.ReportsSQSProducer;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPromoBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Languages;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest
{
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String EXPECTED_CUSTOMER_GUID = "customerGuid";
	public static final String EXISTING_CONFIRMATION_GUID = "existingConfirmationGuid";

	@Mock
	private Localise localise;
	@Mock
	private ConfirmationEmailDispatcher emailDispatcher;
	@Mock
	private PaymentHandler paymentHandler;
	@Mock
	private SubscriptionControllerService service;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private BookingService bookingService;
	@InjectMocks
	private BookingController controller;

	@Mock
	private Model model;

	@Mock
	private ContactActivationProcessor contactActivationProcessor;

	@Mock
	private ContactService contactService;

	@Mock
	private RedirectAttributes redirectAttributes;

	@Mock
	private Sites site;

	@Mock
	private Languages currentLaguages;

	@Mock
	private Affiliates affiliates;

	@Mock
	private Locations location;

	@Mock
	private ActivateAccountEmailDispatcher ActivateAccountEmailDispatcher;
	@Mock
	private HttpServletRequest request;
	@Mock
	private ReservationBuilderObjectFactory builderObjectFactory;
	@Mock
	private CustomConnectionProvider connectionProvider;
	@Mock
	private FreemarkerSQSProducer freemarkerSQSProducer;
	@Mock
	private AncillaryBarcodeProcessor barcodeProcessor;
	@Mock
	private SubscriptionRenewalService renewalService;
	@Mock
	private SubscriptionValidator validator;
	@Mock
	private InvoiceCreator invoiceCreator;
	@Mock
	private ReportsSQSProducer reportsSQSProducer;
	@Mock
	private PromotionService sessionPromotionService;
	@Mock
	private PromotionBuilder promotionBuilder;

	@Test
	void testBooking()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getCustomerGuid()).thenReturn("guid");

		Basket basket = mock(Basket.class);
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);

		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(renewalService.validateSubscription(eq(bookingData), eq(null)))
				.thenReturn(Collections.emptyMap());

		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);

		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);

		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		StringBuffer stringBuffer = new StringBuffer("localhost:8443/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
		when(service.isFreemarkerEnabled(anyInt())).thenReturn(false);

		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(service).isFreemarkerEnabled(anyInt());
		verify(service).isPartialPaymentsEnabled(anyInt());
		verify(emailDispatcher).sendEmail(any());
		verify(ActivateAccountEmailDispatcher).sendEmail(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBookingAccountAlreadyActivated()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(affiliates.getCode()).thenReturn("AFF_CODE");
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getCustomerGuid()).thenReturn("guid");

		Basket basket = mock(Basket.class);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);
		when(service.isFreemarkerEnabled(anyInt())).thenReturn(false);

		HttpServletRequest servlet = mock(HttpServletRequest.class);
		when(servlet.getScheme()).thenReturn("");
		when(servlet.getServerName()).thenReturn("");
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("");
		when(servlet.getRequestURL()).thenReturn(stringBuffer);

		controller.booking(bookingData, redirectAttributes, servlet);

		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(service).isFreemarkerEnabled(anyInt());
		verify(service).isPartialPaymentsEnabled(anyInt());
		verify(ActivateAccountEmailDispatcher).sendEmail(any());
		verify(emailDispatcher).sendEmail(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBooking_SubscriptionBookingRecord_Null()
	{
		// No email sent
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		Basket basket = mock(Basket.class);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.getBooking(anyInt(), anyString())).thenReturn(null);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("");
		when(request.getRequestURL()).thenReturn(stringBuffer);
		controller.booking(bookingData, redirectAttributes, request);
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(service).isPartialPaymentsEnabled(anyInt());
		verify(emailDispatcher, times(0)).sendEmail(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBooking_AffiliateConfig_Email_Confirmation_False()
	{
		// No email sent
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(false);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		Basket basket = mock(Basket.class);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);

		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);
		when(subscriptionBooking.getContactId()).thenReturn(1);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);

		HttpServletRequest servlet = mock(HttpServletRequest.class);
		when(servlet.getScheme()).thenReturn("");
		when(servlet.getServerName()).thenReturn("");
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("");
		when(servlet.getRequestURL()).thenReturn(stringBuffer);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		controller.booking(bookingData, redirectAttributes, servlet);
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(service).isPartialPaymentsEnabled(anyInt());
		verify(emailDispatcher, times(0)).sendEmail(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBooking_Payment_Failed()
	{
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		Basket basket = mock(Basket.class);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(false);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("");
		when(request.getRequestURL()).thenReturn(stringBuffer);
		controller.booking(bookingData, redirectAttributes, request);
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).isPartialPaymentsEnabled(anyInt());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBooking_Basket_Null()
	{
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(null);

		controller.booking(bookingData, redirectAttributes, request);

		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service, times(0)).generateReference(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testDoAjaxBasketUpdate()
	{
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);
		List<PurchaseQuery> basketUpdateQueries = EnhancedRandom.randomListOf(1, PurchaseQuery.class);
		Basket basket = mock(Basket.class);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.ZERO);
		when(service.buildBasket(anyList(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		controller.doAjaxBasketUpdate(model, basketUpdateQueries);
		verify(service).buildBasket(anyList(), anyInt(), anyInt(), any(), any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testDoAjaxBasketUpdate_Contnue_To_NextStep()
	{
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);
		List<PurchaseQuery> basketUpdateQueries = EnhancedRandom.randomListOf(1, PurchaseQuery.class);
		Basket basket = mock(Basket.class);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.buildBasket(anyList(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		controller.doAjaxBasketUpdate(model, basketUpdateQueries);
		verify(service).buildBasket(anyList(), anyInt(), anyInt(), any(), any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testDoAjaxBasketUpdate_PurchaseQuery_Empty()
	{
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		controller.doAjaxBasketUpdate(model, Collections.emptyList());
		verifyNoInteractions(service);
	}

	@Test
	public void testProcessPaymentCmd()
	{
		when(paymentHandler.processSubCmd(anyInt(), anyString(), anyString(), anyString(), anyBoolean(), anyString()))
				.thenReturn(mock(SubscriptionBookingData.class));

		controller.processPaymentCmd(model, redirectAttributes, request, 0, 0, "ref", "guid", "cmd", "date", "fname",
				"lname", false, "");

		verify(paymentHandler).processSubCmd(anyInt(), anyString(), anyString(),  anyString(), anyBoolean(), anyString());
	}

	@Test
	public void testProcessPaymentCmd_Callback() throws IOException
	{
		BufferedReader mockReader = mock(BufferedReader.class);

		when(paymentHandler.processSubCmd(anyInt(), anyString(), anyString(), anyString(), anyBoolean(), anyString()))
				.thenReturn(mock(SubscriptionBookingData.class));
		when(request.getReader()).thenReturn(mockReader);

		controller.processPaymentCmd(model, redirectAttributes, request, 0, 0, "ref", "guid", "cmd", "date", "fname",
				"lname", true, "");

		verify(paymentHandler).processSubCmd(anyInt(), anyString(), anyString(), anyString(), anyBoolean(),
				anyString());
	}

	@Test
	public void testProcessPaymentCmd_StripeCallback()
	{
		when(paymentHandler.processSubCmd(anyInt(), anyString(), anyString(), anyString(), anyBoolean(), anyString()))
				.thenReturn(mock(SubscriptionBookingData.class));

		controller.processPaymentCmd(model, redirectAttributes, request, 0, 0, "ref", "guid", "cmd", "date", "fname",
				"lname", false, "paymentIntentId");

		verify(paymentHandler).processSubCmd(anyInt(), anyString(), anyString(),  eq("paymentIntentId"), anyBoolean(), anyString());
	}

	@Test
	public void testProcessPaymentCmd_Callback_IOException() throws IOException
	{
		when(paymentHandler.processSubCmd(anyInt(), anyString(), anyString(), anyString(), anyBoolean(), anyString()))
				.thenReturn(mock(SubscriptionBookingData.class));
		when(request.getReader()).thenThrow(IOException.class);

		controller.processPaymentCmd(model, redirectAttributes, request, 0, 0, "ref", "guid", "cmd", "date", "fname",
				"lname", true, "");

		verify(paymentHandler).processSubCmd(anyInt(), anyString(), anyString(), anyString(), anyBoolean(),
				anyString());
	}

	@Test
	public void testProcessPaymentCmd_Null_BookingData()
	{
		controller.processPaymentCmd(model, redirectAttributes, request, 0, 0, "ref", "guid", "cmd", "date", "fname",
				"lname", false, "");

		verify(paymentHandler).processSubCmd(anyInt(), anyString(), anyString(),  anyString(), anyBoolean(), anyString());
	}

	@Test
	public void testBooking_RedirectPayment()
	{
		Basket basket = mock(Basket.class);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		PaymentHandlerBean handlerBean = mock(PaymentHandlerBean.class);
		StringBuffer stringBuffer = new StringBuffer();

		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		when(site.getId()).thenReturn(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processRedirectPayment(any())).thenReturn(handlerBean);
		stringBuffer.append("localhost:8443/subscriptions/test/booking");
		when(request.getRequestURL()).thenReturn(stringBuffer);
		when(handlerBean.isRedirectRequired()).thenReturn(true);

		controller.booking(bookingData, redirectAttributes, request);

		verify(paymentHandler).processRedirectPayment(any());
	}

	@Test
	public void testBooking_RedirectPayment_StripeRedirect()
	{
		Basket basket = mock(Basket.class);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		PaymentHandlerBean handlerBean = mock(PaymentHandlerBean.class);
		StringBuffer stringBuffer = new StringBuffer();

		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		when(site.getId()).thenReturn(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processRedirectPayment(any())).thenReturn(handlerBean);
		stringBuffer.append("localhost:8443/subscriptions/test/booking");
		when(request.getRequestURL()).thenReturn(stringBuffer);
		when(handlerBean.isRedirectRequired()).thenReturn(true);
		when(handlerBean.isStripeRedirect()).thenReturn(true);
		when(handlerBean.getCheckoutUrl()).thenReturn("stripeRedirectUrl");
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		assertEquals("stripe-redirect", controller.booking(bookingData, redirectAttributes, request));

		verify(paymentHandler).processRedirectPayment(any());
		verify(request).setAttribute("stripeRedirectUrl", "stripeRedirectUrl");
	}

	@Test
	public void testBooking_RedirectPayment_Redirect_Not_Required()
	{
		Basket basket = mock(Basket.class);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		PaymentHandlerBean handlerBean = mock(PaymentHandlerBean.class);
		StringBuffer stringBuffer = new StringBuffer();

		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		when(bookingData.getBookingReference()).thenReturn("ref");
		when(site.getId()).thenReturn(1);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(paymentHandler.processRedirectPayment(any())).thenReturn(handlerBean);
		stringBuffer.append("localhost:8443/subscriptions/test/booking");
		when(request.getRequestURL()).thenReturn(stringBuffer);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		controller.booking(bookingData, redirectAttributes, request);

		verify(paymentHandler).processRedirectPayment(any());
	}

	@Test
	void testBooking_WithDiscountApplied()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		Basket basket = mock(Basket.class);
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		Map<String, Object> discountResponse = new HashMap<>();
		discountResponse.put("newStartDate", "2024-10-10");
		discountResponse.put("grandTotal", "100.00");

		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(affiliates.getCode()).thenReturn("AFF_CODE");
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		when(renewalService.validateSubscription(any(), anyString())).thenReturn(discountResponse);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);
		StringBuffer stringBuffer = new StringBuffer("localhost:8443/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		verify(bookingData).setStartDate("2024-10-10");
		verify(bookingData).setAmount(new BigDecimal("100.00"));
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(emailDispatcher).sendEmail(any());
		verify(ActivateAccountEmailDispatcher).sendEmail(any());
	}

	@Test
	void testBooking_WithoutDiscountApplied()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		Basket basket = mock(Basket.class);
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(affiliates.getCode()).thenReturn("AFF_CODE");
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		Map<String, Object> discountResponse = Collections.emptyMap();
		when(renewalService.validateSubscription(any(), anyString())).thenReturn(discountResponse);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);
		StringBuffer stringBuffer = new StringBuffer("localhost:8443/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		verify(bookingData, times(0)).setStartDate(anyString());
		verify(bookingData, times(1)).setAmount(any(BigDecimal.class));
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(emailDispatcher).sendEmail(any());
		verify(ActivateAccountEmailDispatcher).sendEmail(any());
	}

	@Test
	void testBooking_DiscountedBasketNotNull()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		SubscriptionBookingDetails bookingDetails = new SubscriptionBookingDetails();
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		Basket basket = mock(Basket.class);
		Basket discountedBasket = mock(Basket.class);
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		Map<String, Object> discountResponse = new HashMap<>();
		discountResponse.put("newStartDate", "2024-10-10");
		discountResponse.put("grandTotal", "100.00");

		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(affiliates.getCode()).thenReturn("AFF_CODE");
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		when(renewalService.validateSubscription(any(), anyString())).thenReturn(discountResponse);
		when(discountedBasket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);
		when(renewalService.fetchAndApplyDiscount("guid", null, null)).thenReturn(discountedBasket);
		StringBuffer stringBuffer = new StringBuffer("localhost:8443/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);

		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		verify(renewalService).fetchAndApplyDiscount("guid", null, null);
		verify(paymentHandler).processPayment(any(), eq(discountedBasket));
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(emailDispatcher).sendEmail(any());
		verify(ActivateAccountEmailDispatcher).sendEmail(any());
	}

	@Test
	void testBooking_DiscountedBasketIsNull()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		SubscriptionBookingDetails bookingDetails = mock(SubscriptionBookingDetails.class);
		Basket basket = mock(Basket.class);
		Basket discountedBasket = null;
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		Map<String, Object> discountResponse = new HashMap<>();

		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(affiliates.getCode()).thenReturn("AFF_CODE");
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		discountResponse.put("newStartDate", "2024-10-10");
		discountResponse.put("grandTotal", "100.00");
		when(renewalService.validateSubscription(any(), anyString())).thenReturn(discountResponse);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);
		StringBuffer stringBuffer = new StringBuffer("localhost:8443/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);

		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		verify(paymentHandler).processPayment(any(), eq(basket));
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(emailDispatcher).sendEmail(any());
		verify(ActivateAccountEmailDispatcher).sendEmail(any());
	}

	public void testBooking_Freemarker_Enabled()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		Basket basket = mock(Basket.class);
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);
		SubscriptionBookingCustomerDetails customerDetails = mock(SubscriptionBookingCustomerDetails.class);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		StringBuffer stringBuffer = new StringBuffer();

		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getFreemarkerQueueUrl()).thenReturn("freemarkerurl");

		when(site.getId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);
		when(subscriptionBooking.getReference()).thenReturn("ref");
		when(subscriptionBookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		when(customerDetails.getEmailAddress()).thenReturn("abc@email.com");

		stringBuffer.append("localhost:8443/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
		when(service.isFreemarkerEnabled(anyInt())).thenReturn(true);

		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(service).isFreemarkerEnabled(anyInt());
	}

	@Test
	public void testBooking_Freemarker_Enabled_Invoice_Requested()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		Basket basket = mock(Basket.class);
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);
		SubscriptionBookingCustomerDetails customerDetails = mock(SubscriptionBookingCustomerDetails.class);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		StringBuffer stringBuffer = new StringBuffer();

		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getFreemarkerQueueUrl()).thenReturn("freemarkerurl");

		when(site.getId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		when(bookingData.isReceiptCheckbox()).thenReturn(true);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);
		when(subscriptionBooking.getReference()).thenReturn("ref");
		when(subscriptionBookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		when(customerDetails.getEmailAddress()).thenReturn("abc@email.com");

		stringBuffer.append("localhost:8443/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
		when(service.isFreemarkerEnabled(anyInt())).thenReturn(true);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(service).isFreemarkerEnabled(anyInt());
	}

	@Test
	public void testBooking_Freemarker_Enabled_Invoice_Mandatory()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		Basket basket = mock(Basket.class);
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);
		SubscriptionBookingCustomerDetails customerDetails = mock(SubscriptionBookingCustomerDetails.class);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		PaymentStepFieldsSubscriptions paymentStepFieldsMock = mock(PaymentStepFieldsSubscriptions.class);
		StringBuffer stringBuffer = new StringBuffer();
		List<PaymentStepFieldsSubscriptions> paymentStepFields = new ArrayList<>();
		paymentStepFields.add(paymentStepFieldsMock);

		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getFreemarkerQueueUrl()).thenReturn("freemarkerurl");

		when(site.getId()).thenReturn(1);
		when(bookingData.getCustomerGuid()).thenReturn("guid");
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(service.fetchPaymentStepFieldsSubscriptionBySiteId(anyInt())).thenReturn(paymentStepFields);
		when(service.doesCompanyRecordExist(anyInt(), anyInt())).thenReturn(true);
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);
		when(subscriptionBooking.getReference()).thenReturn("ref");
		when(subscriptionBookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		when(customerDetails.getEmailAddress()).thenReturn("abc@email.com");
		when(paymentStepFieldsMock.getMandatory()).thenReturn(1);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		stringBuffer.append("localhost:8443/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("http");
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
		when(service.isFreemarkerEnabled(anyInt())).thenReturn(true);

		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(service).isFreemarkerEnabled(anyInt());
	}

	@Test
	public void testBooking_httpsUrl()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		StringBuffer stringBuffer = new  StringBuffer();
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn(DATE_FORMAT);
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(BigDecimal.ONE);
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		SubscriptionBookingData bookingData = mock(SubscriptionBookingData.class);
		when(bookingData.getCustomerGuid()).thenReturn("guid");

		Basket basket = mock(Basket.class);
		SubscriptionBookingRecord subscriptionBookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking subscriptionBooking = mock(SubscriptionBooking.class);

		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("reference");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);

		when(service.getBooking(anyInt(), anyString())).thenReturn(subscriptionBookingRecord);
		when(subscriptionBookingRecord.getBooking()).thenReturn(subscriptionBooking);
		when(subscriptionBooking.getContactId()).thenReturn(1);

		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		stringBuffer.append("https://sim.aeroparker.com/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("https");
		when(httpServletRequest.getServerName()).thenReturn("sim.aeroparker.com");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.TEN);
		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		verify(bookingData).setServletAbsoluteUrl("https://sim.aeroparker.com/subscriptions/test/booking");
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(service).generateReference(any());
		verify(service).getBooking(anyInt(), anyString());
		verify(service).isFreemarkerEnabled(anyInt());
		verify(service).isPartialPaymentsEnabled(anyInt());
		verify(emailDispatcher).sendEmail(any());
		verify(ActivateAccountEmailDispatcher).sendEmail(any());
		verifyNoMoreInteractions(service);
	}

	@Test
	void testBooking_WithValidPromotion()
	{
		// Setup mocks
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn("dd/MM/yyyy");
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(new BigDecimal("20"));
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);

		Basket basket = mock(Basket.class);
		when(basket.getGrandTotal()).thenReturn(new BigDecimal("90.00"));
		when(basket.getTotalBookingFee()).thenReturn(new BigDecimal("5.00"));
		when(basket.isRecurring()).thenReturn(false);

		SubscriptionBookingData bookingData = new SubscriptionBookingData();
		bookingData.setEmail("test@example.com");
		bookingData.setCustomerGuid("guid-123");

		// Setup promotion
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid("guid-123");
		sessionPromotion.setPromoCode("SAVE10");
		sessionPromotion.setValid(true);
		sessionPromotion.setDiscountAmount(new BigDecimal("10.00"));
		sessionPromotion.setPromoCodeId(200);

		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking booking = new SubscriptionBooking();
		booking.setId(100);
		when(bookingRecord.getBooking()).thenReturn(booking);
		booking.setContactId(1);
		SubscriptionPromoBooking promoBooking = new SubscriptionPromoBooking();
		promoBooking.setSubBookingId(100);
		promoBooking.setCode("SAVE10");

		when(affiliates.getCode()).thenReturn("code");
		when(renewalService.validateSubscription(any(SubscriptionBookingData.class), anyString())).thenReturn(new HashMap<>());
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("REF-123");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(bookingRecord);
		when(sessionPromotionService.fetchSessionPromotionByGuid("guid-123")).thenReturn(sessionPromotion);
		when(promotionBuilder.buildPromoBooking(100, sessionPromotion)).thenReturn(promoBooking);
		when(service.isFreemarkerEnabled(anyInt())).thenReturn(false);
		when(service.isPartialPaymentsEnabled(anyInt())).thenReturn(false);

		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("https://sim.aeroparker.com/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("https");
		when(httpServletRequest.getServerName()).thenReturn("sim.aeroparker.com");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);

		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		// Verify promotion was saved
		verify(sessionPromotionService).fetchSessionPromotionByGuid("guid-123");
		verify(promotionBuilder).buildPromoBooking(100, sessionPromotion);
		verify(sessionPromotionService).savePromoBooking(promoBooking);
		// Verify promo code uses was incremented
		verify(sessionPromotionService).incrementPromoCodeUses(200);
	}

	@Test
	void testBooking_WithInvalidPromotion()
	{
		// Setup mocks
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn("dd/MM/yyyy");
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(new BigDecimal("20"));
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);

		Basket basket = mock(Basket.class);
		when(basket.getGrandTotal()).thenReturn(new BigDecimal("100.00"));
		when(basket.getTotalBookingFee()).thenReturn(new BigDecimal("5.00"));
		when(basket.isRecurring()).thenReturn(false);

		SubscriptionBookingData bookingData = new SubscriptionBookingData();
		bookingData.setEmail("test@example.com");
		bookingData.setCustomerGuid("guid-456");

		// Setup invalid promotion
		SubscriptionSessionPromotion sessionPromotion = new SubscriptionSessionPromotion();
		sessionPromotion.setGuid("guid-456");
		sessionPromotion.setPromoCode("INVALID");
		sessionPromotion.setValid(false);

		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking booking = new SubscriptionBooking();
		booking.setId(101);
		when(bookingRecord.getBooking()).thenReturn(booking);
		booking.setContactId(1);
		when(affiliates.getCode()).thenReturn("code");
		when(renewalService.validateSubscription(any(SubscriptionBookingData.class), anyString())).thenReturn(new HashMap<>());
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("REF-124");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(bookingRecord);
		when(sessionPromotionService.fetchSessionPromotionByGuid("guid-456")).thenReturn(sessionPromotion);
		when(service.isFreemarkerEnabled(anyInt())).thenReturn(false);
		when(service.isPartialPaymentsEnabled(anyInt())).thenReturn(false);

		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("https://sim.aeroparker.com/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("https");
		when(httpServletRequest.getServerName()).thenReturn("sim.aeroparker.com");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);

		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		// Verify invalid promotion was NOT saved
		verify(sessionPromotionService).fetchSessionPromotionByGuid("guid-456");
		verify(promotionBuilder, times(0)).buildPromoBooking(anyInt(), any());
		verify(sessionPromotionService, times(0)).savePromoBooking(any());
	}

	@Test
	void testBooking_WithoutPromotion()
	{
		// Setup mocks
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		when(requestBean.getAffiliateConfig()).thenReturn(affiliateConfig);
		when(affiliateConfig.getConfigValue_Boolean(any())).thenReturn(true);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(requestBean.getTimeZone()).thenReturn("Europe/London");
		when(requestBean.getDateFormat()).thenReturn("dd/MM/yyyy");
		when(requestBean.getSite()).thenReturn(site);
		when(site.getId()).thenReturn(1);
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getAffiliateVatRate()).thenReturn(new BigDecimal("20"));
		when(requestBean.getCurrentLanguageId()).thenReturn(1);
		when(requestBean.getDefaultLanguageId()).thenReturn(1);

		Basket basket = mock(Basket.class);
		when(basket.getGrandTotal()).thenReturn(new BigDecimal("100.00"));
		when(basket.getTotalBookingFee()).thenReturn(new BigDecimal("5.00"));
		when(basket.isRecurring()).thenReturn(false);

		SubscriptionBookingData bookingData = new SubscriptionBookingData();
		bookingData.setEmail("test@example.com");
		bookingData.setCustomerGuid("guid-789");

		SubscriptionBookingRecord bookingRecord = mock(SubscriptionBookingRecord.class);
		SubscriptionBooking booking = new SubscriptionBooking();
		booking.setId(102);
		booking.setContactId(1);
		when(bookingRecord.getBooking()).thenReturn(booking);

		when(affiliates.getCode()).thenReturn("code");
		when(renewalService.validateSubscription(any(SubscriptionBookingData.class), anyString())).thenReturn(new HashMap<>());
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(service.generateReference(any())).thenReturn("REF-125");
		when(paymentHandler.processPayment(any(), any())).thenReturn(true);
		when(service.getBooking(anyInt(), anyString())).thenReturn(bookingRecord);
		when(sessionPromotionService.fetchSessionPromotionByGuid("guid-789")).thenReturn(null);
		when(service.isFreemarkerEnabled(anyInt())).thenReturn(false);
		when(service.isPartialPaymentsEnabled(anyInt())).thenReturn(false);

		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("https://sim.aeroparker.com/subscriptions/test/booking");
		when(httpServletRequest.getScheme()).thenReturn("https");
		when(httpServletRequest.getServerName()).thenReturn("sim.aeroparker.com");
		when(httpServletRequest.getRequestURL()).thenReturn(stringBuffer);

		controller.booking(bookingData, redirectAttributes, httpServletRequest);

		// Verify no promotion handling when none exists
		verify(sessionPromotionService).fetchSessionPromotionByGuid("guid-789");
		verify(promotionBuilder, times(0)).buildPromoBooking(anyInt(), any());
		verify(sessionPromotionService, times(0)).savePromoBooking(any());
	}

	@Test
	void testBooking_ExistingConfirmationGuid_RedirectsToConfirmation()
	{
		final SubscriptionBookingData subscriptionBookingData = new SubscriptionBookingData();
		subscriptionBookingData.setCustomerGuid(EXPECTED_CUSTOMER_GUID);

		when(requestBean.getSite()).thenReturn(site);
		when(requestBean.getAffiliate()).thenReturn(affiliates);
		when(bookingService.fetchConfirmationGuidByCustomerGuid(EXPECTED_CUSTOMER_GUID)).thenReturn(
				EXISTING_CONFIRMATION_GUID);

		final String result = controller.booking(subscriptionBookingData, redirectAttributes, request);

		assertEquals("redirect:details", result);
		verifyNoInteractions(paymentHandler);
	}
}