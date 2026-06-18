package com.kmp.aeroparker.application.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.engine.SubscriptionPurchaseRequest;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.application.web.SubscriptionControllerService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionDiscountedRenewal;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;

@ExtendWith(MockitoExtension.class)
class SubscriptionValidatorTest
{
	@Mock
	private SubscriptionControllerService service;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private SubscriptionService subService;
	@Mock
	private SubscriptionProductTerms productTerms;

	@InjectMocks
	private SubscriptionValidator validator;

	private Map<String, Object> response;
	private SubscriptionBookingDetails bookingDetails;
	private SubscriptionDiscountedRenewal renewalConfig;

	@BeforeEach
	void setUp()
	{
		response = new HashMap<>();
		bookingDetails = new SubscriptionBookingDetails();
		renewalConfig = new SubscriptionDiscountedRenewal();
		bookingDetails.setSubscriptionProductId(3);
	}

	@Test
	void testFetchBookingDetails_Found()
	{
		when(service.fetchSubscriptionBookingDetailsByEmail("test@example.com")).thenReturn(bookingDetails);
		SubscriptionBookingDetails result = validator.fetchBookingDetails("test@example.com", "guid123", response);

		assertThat(result).isNotNull();
		assertThat(response).doesNotContainKey("error");
	}

	@Test
	void testFetchBookingDetails_NotFound()
	{
		when(service.fetchSubscriptionBookingDetailsByEmail("test@example.com")).thenReturn(null);
		SubscriptionBookingDetails result = validator.fetchBookingDetails("test@example.com", "guid123", response);

		assertThat(result).isNull();
	}

	@Test
	void testValidateBookingDetails_Valid()
	{
		bookingDetails.setSubscriptionProductId(1);
		bookingDetails.setStartDate(java.sql.Date.valueOf(LocalDate.now()
				.minusMonths(2)));

		boolean result = validator.validateBookingDetails(bookingDetails, "guid123", response);
		assertTrue(result);
		assertThat(response).doesNotContainKey("error");
	}

	@Test
	void testValidateBookingDetails_Invalid()
	{
		bookingDetails.setSubscriptionProductId(null);
		bookingDetails.setStartDate(null);

		boolean result = validator.validateBookingDetails(bookingDetails, "guid123", response);
		assertFalse(result);
		assertThat(response).containsEntry("error", "Product ID or Start Date missing from subscription details.");
	}

	@Test
	void testValidateRenewalConfiguration_Valid()
	{
		renewalConfig.setDiscountedRenewalEnabled(true);
		renewalConfig.setDontApplyToNewBookings(false);
		boolean result = validator.validateRenewalConfiguration(1, renewalConfig, response);
		assertTrue(result);
		assertThat(response).doesNotContainKey("error");
	}

	@Test
	void testValidateRenewalConfiguration_Invalid()
	{
		renewalConfig.setDiscountedRenewalEnabled(false);

		boolean result = validator.validateRenewalConfiguration(1, renewalConfig, response);
		assertFalse(result);
	}

	@Test
	void testValidateRenewalConfiguration_Invalid_DontApplyToNewBookings()
	{
		renewalConfig.setDiscountedRenewalEnabled(true);
		renewalConfig.setDontApplyToNewBookings(true);
		boolean result = validator.validateRenewalConfiguration(1, renewalConfig, response);
		assertFalse(result);
	}

	@Test
	void testCheckRenewalWindow_ValidWindow()
	{
		LocalDate startDate = LocalDate.now()
				.minusMonths(1);
		SubscriptionMinimumTerm minimumTerm = SubscriptionMinimumTerm.ONE_MONTH;
		renewalConfig.setRenewalDays(30);

		when(requestBean.getTimeZone()).thenReturn("Europe/London");

		boolean result = validator.checkRenewalWindow(startDate, minimumTerm, renewalConfig, response);
		assertTrue(result);
		assertThat(response).doesNotContainKey("error");
	}

	@Test
	void testCheckRenewalWindow_BeforeWindow()
	{
		LocalDate startDate = LocalDate.now()
				.minusDays(5);
		SubscriptionMinimumTerm minimumTerm = SubscriptionMinimumTerm.ONE_MONTH;
		renewalConfig.setRenewalDays(10);

		when(requestBean.getTimeZone()).thenReturn("Europe/London");

		boolean result = validator.checkRenewalWindow(startDate, minimumTerm, renewalConfig, response);
		assertFalse(result);
		assertThat(response).containsEntry("error",
				"You already have an active subscription that is not yet due for renewal.");
	}

	@Test
	void testCheckRenewalWindow_AfterWindow()
	{
		LocalDate startDate = LocalDate.now()
				.minusMonths(6);
		SubscriptionMinimumTerm minimumTerm = SubscriptionMinimumTerm.ONE_MONTH;
		renewalConfig.setRenewalDays(10);

		when(requestBean.getTimeZone()).thenReturn("Europe/London");

		boolean result = validator.checkRenewalWindow(startDate, minimumTerm, renewalConfig, response);
		assertFalse(result);
		assertThat(response).containsEntry("message",
				"Your subscription has expired, but no renewal discount can be applied.");
	}

	@Test
	void testPrepareResponse()
	{
		Basket basket = mock(Basket.class);
		SubscriptionPurchaseRequest purchaseRequest = mock(SubscriptionPurchaseRequest.class);
		SubscriptionBookingData bookingData = new SubscriptionBookingData();
		BigDecimal grandTotal = BigDecimal.valueOf(100.00);

		when(basket.getGrandTotal()).thenReturn(grandTotal);
		when(requestBean.getDateFormat()).thenReturn("MM-dd-yyyy");
		when(basket.getPriceIncludePennyPlaceholdersHtml()).thenReturn("<html>Price</html>");
		when(purchaseRequest.getStartDate()).thenReturn(LocalDate.now());
		when(purchaseRequest.getEndDate()).thenReturn(LocalDate.now()
				.plusMonths(1));

		validator.prepareResponse(basket, purchaseRequest, response, bookingData);

		assertThat(response).containsEntry("newStartDate", LocalDate.now()
				.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
		assertThat(response).containsEntry("newEndDate", LocalDate.now()
				.plusMonths(1)
				.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
		assertThat(response).containsEntry("grandTotal", grandTotal.toString());
		assertThat(response).containsEntry("priceIncludePennyPlaceholdersHtml", "<html>Price</html>");
		assertThat(bookingData.getAmount()).isEqualTo(grandTotal);
	}

	@Test
	public void testValidateRenewalPeriod()
	{
		LocalDate insidePeriod = LocalDate.now()
				.plusDays(3);
		when(subService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		when(productTerms.getRenewalEligibilityPeriod()).thenReturn(5);
		when(subService.fetchSubscriptionBookingDetailsByRef(anyString())).thenReturn(bookingDetails);
		assertTrue(validator.validateRenewalPeriod("test_email", insidePeriod.toString()));
	}

	@Test
	public void testValidateRenewalPeriod_OutsideRenewalPeriod()
	{
		LocalDate outsidePeriod = LocalDate.now()
				.plusDays(6);
		when(subService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		when(productTerms.getRenewalEligibilityPeriod()).thenReturn(5);
		when(subService.fetchSubscriptionBookingDetailsByRef(anyString())).thenReturn(bookingDetails);
		assertFalse(validator.validateRenewalPeriod("test_email", outsidePeriod.toString()));
	}

	@Test
	public void testValidateRenewalPeriod_NullProductTerms()
	{
		when(subService.fetchSubscriptionBookingDetailsByRef(anyString())).thenReturn(bookingDetails);
		assertTrue(validator.validateRenewalPeriod("test_email", "2011-12-03+01:00"));
	}

	@Test
	public void testValidateRenewalPeriod_NullBookingDetails()
	{
		assertTrue(validator.validateRenewalPeriod("test_email", "2011-12-03+01:00"));
	}

	@Test
	public void testValidateRenewalPeriod_BlankEndDate()
	{
		when(subService.fetchSubscriptionBookingDetailsByRef(anyString())).thenReturn(bookingDetails);
		when(subService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		when(productTerms.getRenewalEligibilityPeriod()).thenReturn(5);
		assertTrue(validator.validateRenewalPeriod("test_email", ""));
	}

	@Test
	public void testValidateRenewalPeriod_BlankRenewalPeriod()
	{
		when(subService.fetchSubscriptionBookingDetailsByRef(anyString())).thenReturn(bookingDetails);
		when(subService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		when(productTerms.getRenewalEligibilityPeriod()).thenReturn(null);
		assertTrue(validator.validateRenewalPeriod("test_email", "2011-12-03+01:00"));
	}
	
	@Test
	public void testValidateRenewalPeriod_DateTimeParseException()
	{
		when(subService.fetchSubscriptionProductTermsBySubProductId(anyInt())).thenReturn(productTerms);
		when(productTerms.getRenewalEligibilityPeriod()).thenReturn(5);
		when(subService.fetchSubscriptionBookingDetailsByRef(anyString())).thenReturn(bookingDetails);
		assertFalse(validator.validateRenewalPeriod("test_email", "2025-02-30"));
	}
}