package com.kmp.aeroparker.application.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.availability.PriceDetails;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.PurchaseRequestList;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseAgreement;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.application.validator.SubscriptionValidator;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionDiscountedRenewal;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

@ExtendWith(MockitoExtension.class)
class SubscriptionRenewalServiceTest
{

	@InjectMocks
	private SubscriptionRenewalService subscriptionRenewalService;

	@Mock
	private SubscriptionControllerService service;

	@Mock
	private SubscriptionConfigBean requestBean;

	@Mock
	private SubscriptionValidator validator;

	@Mock
	private Localise localiseService;

	@Mock
	private Basket basket;

	@Mock
	private SubscriptionDiscountedRenewal renewalConfig;

	private SubscriptionBookingData bookingData;
	private SubscriptionBookingDetails bookingDetails;

	@BeforeEach
	void setUp()
	{
		bookingData = new SubscriptionBookingData();
		bookingData.setEmail("test@example.com");
		bookingData.setCustomerGuid("guid123");

		bookingDetails = new SubscriptionBookingDetails();
		bookingDetails.setSubscriptionProductId(1);
		bookingDetails.setStartDate(Date.valueOf(LocalDate.now()
				.minusMonths(1)));
	}

	@Test
	void testValidateSubscription_Successfull()
	{
		SubscriptionPurchaseRequest purchaseRequest = mock(SubscriptionPurchaseRequest.class);
		SubscriptionPurchaseAgreement agreement = mock(SubscriptionPurchaseAgreement.class);
		PriceDetails priceDetails = mock(PriceDetails.class);

		when(validator.fetchBookingDetails(anyString(), anyString(), anyMap())).thenReturn(bookingDetails);
		when(validator.validateBookingDetails(any(), anyString(), anyMap())).thenReturn(true);
		when(service.fetchSubscriptionDiscountedRenewalProductId(anyInt())).thenReturn(renewalConfig);
		when(validator.validateRenewalConfiguration(anyInt(), any(), anyMap())).thenReturn(true);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);

		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		purchaseRequestList.add(purchaseRequest);
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);

		when(purchaseRequest.getPurchaseAgreement()).thenReturn(agreement);
		when(agreement.getPriceDetails()).thenReturn(priceDetails);

		when(renewalConfig.getDiscountedRenewalEnabled()).thenReturn(true);
		when(renewalConfig.getDiscountType()).thenReturn("flat");
		when(renewalConfig.getDiscountAmount()).thenReturn(BigDecimal.valueOf(30));
		when(purchaseRequest.getOriginalGrandTotal()).thenReturn(BigDecimal.valueOf(200.0));
		when(validator.checkRenewalWindow(any(), any(), any(), anyMap())).thenReturn(true);
		when(basket.getGrandTotal()).thenReturn(BigDecimal.valueOf(100.0));
		when(basket.getPriceIncludePennyPlaceholdersHtml()).thenReturn("HTML here");
		when(requestBean.getTimeZone()).thenReturn("America/New_York");
		when(requestBean.getDateFormat()).thenReturn("MM-dd-yyyy");
		when(purchaseRequest.getStartDate()).thenReturn(LocalDate.now());
		when(purchaseRequest.getMinimumTerm()).thenReturn(SubscriptionMinimumTerm.EIGHT_MONTHS);
		when(purchaseRequest.getEndDate()).thenReturn(LocalDate.now()
				.plusMonths(1));

		subscriptionRenewalService.validateSubscription(bookingData, "affCode");

		verify(validator).fetchBookingDetails(anyString(), anyString(), anyMap());
		verify(validator).validateBookingDetails(any(), anyString(), anyMap());
		verify(service, times(2)).fetchSubscriptionDiscountedRenewalProductId(anyInt());
		verify(validator).validateRenewalConfiguration(anyInt(), any(), anyMap());
		verify(service).getBasket(anyString(), anyInt(), anyInt(), any(), any());
		verify(basket).getGrandTotal();
		verify(basket).getPriceIncludePennyPlaceholdersHtml();
		verify(priceDetails).setDiscountedAmount(BigDecimal.valueOf(30.0)
				.setScale(2, RoundingMode.HALF_UP));
		verify(basket).initialize(localiseService);
	}

	@Test
	void testValidateSubscription_FailsOnInvalidBookingDetails()
	{
		when(validator.fetchBookingDetails(anyString(), anyString(), anyMap())).thenReturn(bookingDetails);
		when(validator.validateBookingDetails(any(), anyString(), anyMap())).thenReturn(false);

		subscriptionRenewalService.validateSubscription(bookingData, "affCode");

		verify(validator).fetchBookingDetails(anyString(), anyString(), anyMap());
		verify(validator).validateBookingDetails(any(), anyString(), anyMap());
		verify(service, never()).fetchSubscriptionDiscountedRenewalProductId(anyInt());
	}

	@Test
	void testValidateSubscription_FailsOnMissingRenewalConfig()
	{
		when(validator.fetchBookingDetails(anyString(), anyString(), anyMap())).thenReturn(bookingDetails);
		when(validator.validateBookingDetails(any(), anyString(), anyMap())).thenReturn(true);
		when(service.fetchSubscriptionDiscountedRenewalProductId(anyInt())).thenReturn(null);

		subscriptionRenewalService.validateSubscription(bookingData, "affCode");

		verify(service).fetchSubscriptionDiscountedRenewalProductId(anyInt());
	}

	@Test
	void testValidateSubscription_FailsOnCheckRenewalWindow()
	{
		SubscriptionPurchaseRequest purchaseRequest = mock(SubscriptionPurchaseRequest.class);
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		purchaseRequestList.add(purchaseRequest);

		when(validator.fetchBookingDetails(anyString(), anyString(), anyMap())).thenReturn(bookingDetails);
		when(validator.validateBookingDetails(any(), anyString(), anyMap())).thenReturn(true);
		when(service.fetchSubscriptionDiscountedRenewalProductId(anyInt())).thenReturn(renewalConfig);
		when(validator.validateRenewalConfiguration(anyInt(), any(), anyMap())).thenReturn(true);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);
		when(validator.checkRenewalWindow(any(), any(), any(), anyMap())).thenReturn(false);
		when(requestBean.getTimeZone()).thenReturn("America/New_York");
		when(requestBean.getDateFormat()).thenReturn("MM-dd-yyyy");
		when(purchaseRequest.getMinimumTerm()).thenReturn(SubscriptionMinimumTerm.EIGHT_MONTHS);

		subscriptionRenewalService.validateSubscription(bookingData, "affCode");

		verify(validator).checkRenewalWindow(any(), any(), any(), anyMap());
		verify(basket, never()).getGrandTotal();
	}

	@Test
	void testValidateSubscription_WithPercentageDiscount()
	{
		SubscriptionPurchaseRequest purchaseRequest = mock(SubscriptionPurchaseRequest.class);
		SubscriptionPurchaseAgreement agreement = mock(SubscriptionPurchaseAgreement.class);
		PriceDetails priceDetails = mock(PriceDetails.class);

		when(validator.fetchBookingDetails(anyString(), anyString(), anyMap())).thenReturn(bookingDetails);
		when(validator.validateBookingDetails(any(), anyString(), anyMap())).thenReturn(true);
		when(service.fetchSubscriptionDiscountedRenewalProductId(anyInt())).thenReturn(renewalConfig);
		when(validator.validateRenewalConfiguration(anyInt(), any(), anyMap())).thenReturn(true);
		when(service.getBasket(anyString(), anyInt(), anyInt(), any(), any())).thenReturn(basket);
		when(requestBean.getDateFormat()).thenReturn("MM-dd-yyyy");
		when(purchaseRequest.getStartDate()).thenReturn(LocalDate.now());
		when(purchaseRequest.getMinimumTerm()).thenReturn(SubscriptionMinimumTerm.EIGHT_MONTHS);


		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		purchaseRequestList.add(purchaseRequest);
		when(basket.getPurchaseRequestList()).thenReturn(purchaseRequestList);

		when(purchaseRequest.getPurchaseAgreement()).thenReturn(agreement);
		when(agreement.getPriceDetails()).thenReturn(priceDetails);
		when(requestBean.getTimeZone()).thenReturn("America/New_York");
		when(renewalConfig.getDiscountedRenewalEnabled()).thenReturn(true);
		when(renewalConfig.getDiscountType()).thenReturn("percentage");
		when(renewalConfig.getDiscountAmount()).thenReturn(BigDecimal.valueOf(25)); // 25%
		when(purchaseRequest.getOriginalGrandTotal()).thenReturn(BigDecimal.valueOf(200.0));

		subscriptionRenewalService.validateSubscription(bookingData, "affCode");

		verify(priceDetails).setDiscountedAmount(BigDecimal.valueOf(50.0)
				.setScale(2, RoundingMode.HALF_UP));
	}
}