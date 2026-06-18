package com.kmp.aeroparker.application.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.builder.ConfigBuilder;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.db.service.VehicleDetailsService;
import com.kmp.aeroparker.application.external.api.SubscriptionBookingRequestHandler;
import com.kmp.aeroparker.application.form.AmendSubscriptionForm;
import com.kmp.aeroparker.application.model.DetailsConfig;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.external.api.datatypes.CustomerDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.VehicleDetails;
import com.kmp.aeroparker.application.model.external.api.response.AmendSubscriptionResponse;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;

@ExtendWith(MockitoExtension.class)
public class SubscriptionEditDetailsControllerTest
{
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private SiteService siteService;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@Mock
	private BookingService bookingService;
	@Mock
	private SubscriptionBookingRequestHandler requestHandler;
	@Mock
	private VehicleDetailsService vehicleDetailsService;
	@Mock
	private AffiliateService affiliateService;
	@Mock
	private Model model;
	@Mock
	private HttpSession session;
	@Mock
	private SubscriptionBooking booking;
	@Mock
	private SubscriptionBookingRecord bookingRecord;
	@Mock
	private SubscriptionBookingVehicleDetails vehicleDetails;
	@Mock
	private SubscriptionBookingCustomerDetails customerDetails;
	@Mock
	private SubscriptionBookingReceiptDetails receiptDetails;
	@Mock
	private AffiliateConfig configValues;
	@Mock
	private List<PaymentStepFieldsSubscriptions> paymentStepFieldsSubscriptions;
	@Mock
	private AmendSubscriptionForm form;
	@Mock
	private HttpServletRequest request;
	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	private VehicleDetails newVehicleDetails;
	@Mock
	private CustomerDetails newCustomerDetails;
	@Mock
	private AmendSubscriptionResponse response;
	@Mock
	private AffiliateSubscriptionSettings affiliateSettings;
	@Mock
	private Locations location;
	@Mock
	private ConfigBuilder configBuilder;
	@Mock
	private DetailsConfig detailsConfig;
	@Mock
	private SubscriptionAllBookingData allBookingData;
	@Mock
	private SubscriptionControllerService service;

	@InjectMocks
	private SubscriptionEditDetailsController controller;

	@Mock
	private VehiclelookupAffiliateLogins vehicleLookupLogin;

	private static final String REFERENCE = "TEST123";
	private static final int SITE_ID = 1;
	private static final int AFFILIATE_ID = 1;
	private static final int BOOKING_ID = 1;

	@BeforeEach
	public void setUp()
	{
		when(session.getAttribute("reference")).thenReturn(REFERENCE);
	}

	@Test
	public void testEditDetails()
	{
		setupPopulateModelMocks();
		when(bookingRecord.getReceiptDetails()).thenReturn(receiptDetails);
		when(receiptDetails.getCompanyName()).thenReturn("Test Company");
		when(receiptDetails.getTaxIdentificationNumber()).thenReturn("123456789");
		when(receiptDetails.getAddress_1Receipt()).thenReturn("456 Business Ave");
		when(receiptDetails.getAddress_2Receipt()).thenReturn("Suite 100");
		when(receiptDetails.getPostcodeReceipt()).thenReturn("W1A 0AX");
		when(receiptDetails.getTownReceipt()).thenReturn("London");
		when(receiptDetails.getCountyReceipt()).thenReturn("Greater London");
		when(receiptDetails.getCountryReceipt()).thenReturn("UK");
		when(receiptDetails.getCompanyVatRegistrationNumber()).thenReturn("GB123456789");
		when(requestBean.getSiteId()).thenReturn(SITE_ID);
		when(requestBean.getAffiliateId()).thenReturn(AFFILIATE_ID);
		when(requestBean.getLocation()).thenReturn(location);
		when(location.getName()).thenReturn("ireland");
		when(booking.getId()).thenReturn(BOOKING_ID);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(REFERENCE, AFFILIATE_ID)).thenReturn(booking);
		when(bookingService.fetchSubscriptionBooking(BOOKING_ID, AFFILIATE_ID)).thenReturn(bookingRecord);
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(AFFILIATE_ID)).thenReturn(affiliateSettings);
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(BOOKING_ID)).thenReturn(vehicleDetails);
		when(affiliateSettings.getMaxLicensePlateChanges()).thenReturn(5);
		when(vehicleDetails.getRegistrationPeriodUpdateCount()).thenReturn(1);

		String result = controller.editDetails(model, session);

		assertEquals("subscription-edit-details", result);
	}

	@Test
	public void testEditDetails_SiteIdZero()
	{
		when(requestBean.getSiteId()).thenReturn(0);

		String result = controller.editDetails(model, session);

		assertEquals("redirect:/subscription-search", result);
	}

	@Test
	public void testSendEditDetails_NoLicensePlateChange()
	{
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(booking.getId()).thenReturn(BOOKING_ID);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(REFERENCE, AFFILIATE_ID)).thenReturn(booking);
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(BOOKING_ID))
				.thenReturn(vehicleDetails);
		when(form.toVehicleDetails()).thenReturn(newVehicleDetails);
		when(form.toCustomerDetails()).thenReturn(newCustomerDetails);
		when(form.getEmail()).thenReturn("test@example.com");
		when(newVehicleDetails.getLicensePlate()).thenReturn("ABC123");
		when(vehicleDetails.getCarRegistration()).thenReturn("ABC123");
		when(requestHandler.sendAmendRequest(REFERENCE, newCustomerDetails, newVehicleDetails,
				requestBean.getAffiliateConfig())).thenReturn(response);
		when(response.getError()).thenReturn(null);

		String result = controller.sendEditDetails(form, model, request, session, redirectAttributes);

		assertEquals("redirect:view-booking", result);
	}

	@Test
	public void testSendEditDetails_LicensePlateChange()
	{
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(booking.getId()).thenReturn(BOOKING_ID);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(REFERENCE, AFFILIATE_ID)).thenReturn(booking);
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(BOOKING_ID)).thenReturn(vehicleDetails);
		when(form.toVehicleDetails()).thenReturn(newVehicleDetails);
		when(form.toCustomerDetails()).thenReturn(newCustomerDetails);
		when(form.getEmail()).thenReturn("test@example.com");
		when(newVehicleDetails.getLicensePlate()).thenReturn("XYZ789");
		when(vehicleDetails.getCarRegistration()).thenReturn("ABC123");
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(AFFILIATE_ID)).thenReturn(affiliateSettings);
		when(affiliateSettings.getMaxLicensePlateChanges()).thenReturn(5);
		when(vehicleDetails.getRegistrationPeriodUpdateCount()).thenReturn(2);
		when(requestHandler.sendAmendRequest(REFERENCE, newCustomerDetails, newVehicleDetails,
				requestBean.getAffiliateConfig())).thenReturn(response);
		when(response.getError()).thenReturn(null);

		String result = controller.sendEditDetails(form, model, request, session, redirectAttributes);

		assertEquals("redirect:view-booking", result);
		verify(requestHandler).sendAmendRequest(REFERENCE, newCustomerDetails, newVehicleDetails, requestBean.getAffiliateConfig());
	}

	@Test
	public void testSendEditDetails_LicensePlateExceedsLimit()
	{
		when(requestBean.getAffiliateId()).thenReturn(1);
		when(booking.getId()).thenReturn(BOOKING_ID);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(REFERENCE, AFFILIATE_ID)).thenReturn(booking);
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(BOOKING_ID)).thenReturn(vehicleDetails);
		when(form.toVehicleDetails()).thenReturn(newVehicleDetails);
		when(newVehicleDetails.getLicensePlate()).thenReturn("XYZ789");
		when(vehicleDetails.getCarRegistration()).thenReturn("ABC123");
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(AFFILIATE_ID)).thenReturn(affiliateSettings);
		when(affiliateSettings.getMaxLicensePlateChanges()).thenReturn(2);
		when(vehicleDetails.getRegistrationPeriodUpdateCount()).thenReturn(3);

		String result = controller.sendEditDetails(form, model, request, session, redirectAttributes);

		assertEquals("redirect:view-booking", result);
		verify(redirectAttributes).addAttribute("errorMessage", "The maximum number of car registration changes has been reached.");
	}

	@Test
	public void testEditDetails_NullReceiptDetails()
	{
		setupPopulateModelMocks();
		when(bookingRecord.getReceiptDetails()).thenReturn(null);
		when(requestBean.getSiteId()).thenReturn(SITE_ID);
		when(requestBean.getAffiliateId()).thenReturn(AFFILIATE_ID);
		when(requestBean.getLocation()).thenReturn(location);
		when(location.getName()).thenReturn("ireland");
		when(booking.getId()).thenReturn(BOOKING_ID);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(REFERENCE, AFFILIATE_ID)).thenReturn(booking);
		when(bookingService.fetchSubscriptionBooking(BOOKING_ID, AFFILIATE_ID)).thenReturn(bookingRecord);
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(AFFILIATE_ID)).thenReturn(affiliateSettings);
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(BOOKING_ID)).thenReturn(vehicleDetails);
		when(affiliateSettings.getMaxLicensePlateChanges()).thenReturn(5);
		when(vehicleDetails.getRegistrationPeriodUpdateCount()).thenReturn(1);

		String result = controller.editDetails(model, session);

		assertEquals("subscription-edit-details", result);
	}

	@Test
	public void testSetVehicleLookupDetails_LoginIsNull()
	{
		setupPopulateModelMocks();
		when(requestBean.getSiteId()).thenReturn(SITE_ID);
		when(requestBean.getAffiliateId()).thenReturn(AFFILIATE_ID);
		when(requestBean.getLocation()).thenReturn(location);
		when(location.getName()).thenReturn("Ireland");
		when(booking.getId()).thenReturn(BOOKING_ID);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(REFERENCE, AFFILIATE_ID)).thenReturn(booking);
		when(bookingService.fetchSubscriptionBooking(BOOKING_ID, AFFILIATE_ID)).thenReturn(bookingRecord);
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(AFFILIATE_ID)).thenReturn(affiliateSettings);
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(BOOKING_ID)).thenReturn(vehicleDetails);
		when(affiliateSettings.getMaxLicensePlateChanges()).thenReturn(5);
		when(vehicleDetails.getRegistrationPeriodUpdateCount()).thenReturn(1);
		when(bookingRecord.getReceiptDetails()).thenReturn(null);
		when(service.getVehicleLookupLogin(AFFILIATE_ID)).thenReturn(null);

		controller.editDetails(model, session);

		verify(model).addAttribute("shouldShowVehicleLookup", false);
		verify(model).addAttribute("lookupMode", "DISABLED");
		verify(model).addAttribute("unrecognisedPlateValidation", false);
	}

	@Test
	public void testSetVehicleLookupDetails_LoginExists_ShowVehicleLookupEnabled()
	{
		setupPopulateModelMocks();
		when(requestBean.getSiteId()).thenReturn(SITE_ID);
		when(requestBean.getAffiliateId()).thenReturn(AFFILIATE_ID);
		when(requestBean.getLocation()).thenReturn(location);
		when(location.getName()).thenReturn("Ireland");
		when(booking.getId()).thenReturn(BOOKING_ID);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(REFERENCE, AFFILIATE_ID)).thenReturn(booking);
		when(bookingService.fetchSubscriptionBooking(BOOKING_ID, AFFILIATE_ID)).thenReturn(bookingRecord);
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(AFFILIATE_ID)).thenReturn(affiliateSettings);
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(BOOKING_ID)).thenReturn(vehicleDetails);
		when(affiliateSettings.getMaxLicensePlateChanges()).thenReturn(5);
		when(vehicleDetails.getRegistrationPeriodUpdateCount()).thenReturn(1);
		when(bookingRecord.getReceiptDetails()).thenReturn(null);
		when(service.getVehicleLookupLogin(AFFILIATE_ID)).thenReturn(vehicleLookupLogin);
		when(configValues.getConfigValue_Boolean(AffiliateConfigKeys.SHOWVEHICLELOOKUP)).thenReturn(true);
		when(vehicleLookupLogin.getLookupMode()).thenReturn("AUTO");
		when(vehicleLookupLogin.getUnrecPlateValidation()).thenReturn(true);

		controller.editDetails(model, session);

		verify(model).addAttribute("shouldShowVehicleLookup", true);
		verify(model).addAttribute("lookupMode", "AUTO");
		verify(model).addAttribute("unrecognisedPlateValidation", true);
	}

	@Test
	public void testSetVehicleLookupDetails_LoginExists_ShowVehicleLookupDisabled()
	{
		setupPopulateModelMocks();
		when(requestBean.getSiteId()).thenReturn(SITE_ID);
		when(requestBean.getAffiliateId()).thenReturn(AFFILIATE_ID);
		when(requestBean.getLocation()).thenReturn(location);
		when(location.getName()).thenReturn("Ireland");
		when(booking.getId()).thenReturn(BOOKING_ID);
		when(bookingService.fetchBookingByReferenceAndAffiliateId(REFERENCE, AFFILIATE_ID)).thenReturn(booking);
		when(bookingService.fetchSubscriptionBooking(BOOKING_ID, AFFILIATE_ID)).thenReturn(bookingRecord);
		when(affiliateService.fetchSubscriptionSettingsByAffiliateId(AFFILIATE_ID)).thenReturn(affiliateSettings);
		when(vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(BOOKING_ID)).thenReturn(vehicleDetails);
		when(affiliateSettings.getMaxLicensePlateChanges()).thenReturn(5);
		when(vehicleDetails.getRegistrationPeriodUpdateCount()).thenReturn(1);
		when(bookingRecord.getReceiptDetails()).thenReturn(null);
		when(service.getVehicleLookupLogin(AFFILIATE_ID)).thenReturn(vehicleLookupLogin);
		when(configValues.getConfigValue_Boolean(AffiliateConfigKeys.SHOWVEHICLELOOKUP)).thenReturn(false);
		when(vehicleLookupLogin.getLookupMode()).thenReturn("MANUAL");
		when(vehicleLookupLogin.getUnrecPlateValidation()).thenReturn(false);

		controller.editDetails(model, session);

		verify(model).addAttribute("shouldShowVehicleLookup", false);
		verify(model).addAttribute("lookupMode", "MANUAL");
		verify(model).addAttribute("unrecognisedPlateValidation", false);
	}

	private void setupPopulateModelMocks()
	{
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(vehicleDetails);
		when(bookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(customerDetails.getTitle()).thenReturn("Mr");
		when(customerDetails.getFirstName()).thenReturn("John");
		when(customerDetails.getLastName()).thenReturn("Doe");
		when(customerDetails.getEmailAddress()).thenReturn("john.doe@example.com");
		when(customerDetails.getPhoneNumber()).thenReturn("1234567890");
		when(customerDetails.getAddress1()).thenReturn("123 Main St");
		when(customerDetails.getAddress2()).thenReturn("Apt 4B");
		when(customerDetails.getTown()).thenReturn("London");
		when(customerDetails.getCounty()).thenReturn("Greater London");
		when(customerDetails.getPostcode()).thenReturn("SW1A 1AA");
		when(customerDetails.getCountry()).thenReturn("UK");
		when(booking.getReference()).thenReturn(REFERENCE);
		when(booking.getAffiliateId()).thenReturn(AFFILIATE_ID);
		when(requestBean.getAffiliateConfig()).thenReturn(configValues);
		when(bookingService.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(REFERENCE,
				"john.doe@example.com")).thenReturn(Collections.singletonList(allBookingData));
		when(allBookingData.getProductId()).thenReturn(123);
		when(configBuilder.build(SITE_ID, AFFILIATE_ID, configValues, 123)).thenReturn(detailsConfig);
		when(detailsConfig.getPaymentStepFields()).thenReturn(Arrays.asList(1, 2, 3));
		when(detailsConfig.getMandatoryFields()).thenReturn(Arrays.asList(2, 3));
	}
}
