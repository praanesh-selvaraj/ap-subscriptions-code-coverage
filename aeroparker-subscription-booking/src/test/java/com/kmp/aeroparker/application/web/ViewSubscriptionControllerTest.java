package com.kmp.aeroparker.application.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.CarParkService;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.payment.handler.PaymentHistoryHandler;
import com.kmp.aeroparker.application.utils.SubscriptionDetailsUtil;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionPaymentHistory;

@ExtendWith(MockitoExtension.class)
class ViewSubscriptionControllerTest
{
	@Mock
	private Model model;
	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private BookingService bookingService;
	@Mock
	private ContactService contactService;
	@Mock
	private SubscriptionDetailsUtil subscriptionUtils;
	@Mock
	private PaymentHistoryHandler paymentHistoryHandler;
	@Mock
	private CarParkService carParkService;
	@Mock
	private SubscriptionControllerService controllerService;
	@Mock
	private SubscriptionService subscriptionService;
	@Mock
	private SubscriptionProduct subscriptionProduct;
	@Mock
	private AffiliateService affiliateService;
	@Mock
	private AffiliateConfig configValues;
	@Mock
	private HttpSession session;

	@InjectMocks
	private ViewSubscriptionController controller;

	private static final int AFFILIATE_ID = 1;
	private static final String BOOKING_REFERENCE = "TEST123";

	@Test
	void testViewSubscription_Details()
	{
		List<SubscriptionAllBookingData> bookingDataList = new ArrayList<>();
		SubscriptionAllBookingData mockBookingData = mock(SubscriptionAllBookingData.class);
		bookingDataList.add(mockBookingData);
		SubscriptionPaymentHistory paymentHistory = new SubscriptionPaymentHistory();
		LocalDateTime paymentDate = LocalDateTime.of(2021, 03, 10, 0, 0);
		paymentHistory.put(paymentDate, BigDecimal.TEN);
		AffiliateConfig mockConfig = mock(AffiliateConfig.class);

		when(bookingService.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(anyString(), anyString()))
				.thenReturn(bookingDataList);
		when(subscriptionUtils.showVehicleDetails(any())).thenReturn(true);
		when(subscriptionUtils.getCustomerAddress(any())).thenReturn("Address");
		when(mockBookingData.getCardNumber()).thenReturn("CardNumber");
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getSiteId()).thenReturn(1);
		when(mockBookingData.getSubscriptionType()).thenReturn("RECURRING");
		when(paymentHistoryHandler.processHistory(any(), anyInt(), anyInt())).thenReturn(paymentHistory);
		when(requestBean.getAffiliateConfig()).thenReturn(mockConfig);
		when(mockConfig.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_EMAIL_CONFIRMATIONS)).thenReturn(true);
		when(mockBookingData.getProductId()).thenReturn(1);
		when(subscriptionService.fetchSubscriptionProductById(anyInt())).thenReturn(subscriptionProduct);
		when(subscriptionProduct.getHideCarPark()).thenReturn(true);
		when(affiliateService.fetchAffiliateConfigValues(AFFILIATE_ID)).thenReturn(configValues);
		when(configValues.getConfigValue_Boolean(any())).thenReturn(false);

		controller.viewSubscription(model, "Reference", "Email", true, null, session);

		verify(subscriptionUtils).formatCardExpiryDate(any());
		verify(paymentHistoryHandler).processHistory(any(), anyInt(), anyInt());
		verify(carParkService).fetchAllBookingCarParks(anyString());
		verify(model, times(11)).addAttribute(anyString(), any());
		verify(subscriptionService).fetchSubscriptionProductById(anyInt());
	}

	@Test
	void testViewSubscription_Details_RenewalError()
	{
		List<SubscriptionAllBookingData> bookingDataList = new ArrayList<>();
		SubscriptionAllBookingData mockBookingData = mock(SubscriptionAllBookingData.class);
		bookingDataList.add(mockBookingData);
		SubscriptionPaymentHistory paymentHistory = new SubscriptionPaymentHistory();
		LocalDateTime paymentDate = LocalDateTime.of(2021, 03, 10, 0, 0);
		paymentHistory.put(paymentDate, BigDecimal.TEN);
		AffiliateConfig mockConfig = mock(AffiliateConfig.class);

		when(bookingService.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(anyString(), anyString()))
				.thenReturn(bookingDataList);
		when(subscriptionUtils.showVehicleDetails(any())).thenReturn(true);
		when(subscriptionUtils.getCustomerAddress(any())).thenReturn("Address");
		when(mockBookingData.getCardNumber()).thenReturn("CardNumber");
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(requestBean.getSiteId()).thenReturn(1);
		when(mockBookingData.getSubscriptionType()).thenReturn("RECURRING");
		when(paymentHistoryHandler.processHistory(any(), anyInt(), anyInt())).thenReturn(paymentHistory);
		when(requestBean.getAffiliateConfig()).thenReturn(mockConfig);
		when(mockConfig.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_EMAIL_CONFIRMATIONS)).thenReturn(true);
		when(affiliateService.fetchAffiliateConfigValues(AFFILIATE_ID)).thenReturn(configValues);
		when(configValues.getConfigValue_Boolean(any())).thenReturn(false);

		controller.viewSubscription(model, "Reference", "Email", true, "Error Message", session);

		verify(subscriptionUtils).formatCardExpiryDate(any());
		verify(paymentHistoryHandler).processHistory(any(), anyInt(), anyInt());
		verify(carParkService).fetchAllBookingCarParks(anyString());
		verify(model, times(12)).addAttribute(anyString(), any());
	}

	@Test
	void testViewSubscription_Details_No_Booking()
	{
		List<SubscriptionAllBookingData> bookingDataList = new ArrayList<>();

		when(bookingService.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(anyString(), anyString()))
				.thenReturn(bookingDataList);

		controller.viewSubscription(model, "Reference", "Email", true, null, session);

		verify(model, times(4)).addAttribute(anyString(), any());
		verify(model).addAttribute("reference", "Reference");
	}

	@Test
	void testViewSubscription_Details_No_Card()
	{
		List<SubscriptionAllBookingData> bookingDataList = new ArrayList<>();
		SubscriptionAllBookingData mockBookingData = mock(SubscriptionAllBookingData.class);
		bookingDataList.add(mockBookingData);
		AffiliateConfig mockConfig = mock(AffiliateConfig.class);

		when(bookingService.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(anyString(), anyString()))
				.thenReturn(bookingDataList);
		when(subscriptionUtils.showVehicleDetails(any())).thenReturn(true);
		when(subscriptionUtils.getCustomerAddress(any())).thenReturn("Address");
		when(mockBookingData.getCardNumber()).thenReturn("");
		when(mockBookingData.getSubscriptionType()).thenReturn("FIXED");
		when(requestBean.getAffiliateConfig()).thenReturn(mockConfig);
		when(mockConfig.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_EMAIL_CONFIRMATIONS)).thenReturn(true);
		when(affiliateService.fetchAffiliateConfigValues(anyInt())).thenReturn(configValues);
		when(configValues.getConfigValue_Boolean(any())).thenReturn(false);

		controller.viewSubscription(model, "Reference", "Email", true, null, session);

		verify(subscriptionUtils, times(0)).formatCardExpiryDate(any());
		verify(paymentHistoryHandler, times(0)).processHistory(any(), anyInt(), anyInt());
		verify(model, times(11)).addAttribute(anyString(), any());
	}

	@Test
	void testViewSubscription_Search()
	{
		controller.viewSubscription(model, "", "", false, null, session);
		verify(model, times(1)).addAttribute(anyString(), any());
	}

	@Test
	void testViewSubscription_Search_Missing_Parameters()
	{
		controller.viewSubscription(model, "Reference", "", true, null, session);
		verify(model, times(3)).addAttribute(anyString(), any());
		verify(model).addAttribute("reference", "Reference");
	}

	@Test
	void testViewSubscription_PrepopulateReference()
	{
		controller.viewSubscription(model, BOOKING_REFERENCE, null, false, null, session);
		verify(model).addAttribute("reference", BOOKING_REFERENCE);
		verify(model).addAttribute("header", "Subscription Search");
		verify(model, times(2)).addAttribute(anyString(), any());
	}

	@Test
	void testBookingSearch()
	{
		Contacts mockContact = mock(Contacts.class);
		when(bookingService.fetchBookingByReferenceAffiliateIdAndEmail(anyString(), anyInt(), anyString()))
				.thenReturn(mock(SubscriptionBooking.class));

		controller.validateRefAndEmail(redirectAttributes, "Ref", "Email");

		verify(redirectAttributes, times(3)).addAttribute(anyString(), any());
	}

	@Test
	void testBookingSearch_Invalid_Booking()
	{
		when(bookingService.fetchBookingByReferenceAffiliateIdAndEmail(anyString(), anyInt(),anyString())).thenReturn(null);

		controller.validateRefAndEmail(redirectAttributes, "Ref", "Email");

		verify(redirectAttributes, times(1)).addAttribute(anyString(), any());
	}

	@Test
	void testBookingSearch_No_Match()
	{
		Contacts mockContact = mock(Contacts.class);
		when(bookingService.fetchBookingByReferenceAffiliateIdAndEmail(anyString(), anyInt(), anyString()))
				.thenReturn(null);

		controller.validateRefAndEmail(redirectAttributes, "Ref", "Email");

		verify(redirectAttributes, times(1)).addAttribute(anyString(), any());
	}
}
