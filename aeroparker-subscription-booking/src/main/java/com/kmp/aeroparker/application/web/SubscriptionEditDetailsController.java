package com.kmp.aeroparker.application.web;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.kmp.aeroparker.application.model.enums.PaymentStepFields;
import com.kmp.aeroparker.application.model.external.api.datatypes.CustomerDetails;
import com.kmp.aeroparker.application.model.external.api.datatypes.Error;
import com.kmp.aeroparker.application.model.external.api.datatypes.VehicleDetails;
import com.kmp.aeroparker.application.model.external.api.response.AmendSubscriptionResponse;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Locations;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class SubscriptionEditDetailsController
{
	private static final String EDIT_DETAILS = "Edit Details";
	private static final String SUBSCRIPTION_SEARCH_PAGE = "redirect:/subscription-search";
	private static final String VIEW_BOOKING_PAGE = "redirect:view-booking";
	private static final String EDIT_DETAILS_PAGE = "subscription-edit-details";
	private static final String IRELAND = "Ireland";
	private static final String UNITED_STATES = "united states";
	private static final String DISABLED = "DISABLED";
	private final SubscriptionConfigBean requestBean;
	private final SiteService siteService;
	private final LanguageFieldsList languageFieldsList;
	private final BookingService bookingService;
	private final SubscriptionBookingRequestHandler requestHandler;
	private final VehicleDetailsService vehicleDetailsService;
	private final AffiliateService affiliateService;
	private final ConfigBuilder configBuilder;
	private final SubscriptionControllerService service;

	@GetMapping(value = "/edit-details")
	public String editDetails(final Model model, final HttpSession session)
	{
		String reference = (String) session.getAttribute("reference");
		int siteId = requestBean.getSiteId();
		int affiliateId = requestBean.getAffiliateId();
		SubscriptionBooking booking = bookingService.fetchBookingByReferenceAndAffiliateId(reference, affiliateId);

		if (siteId == 0 || StringUtil.isNullOrEmpty(reference))
		{
			return SUBSCRIPTION_SEARCH_PAGE;
		}

		// Form data
		SubscriptionBookingRecord bookingRecord = bookingService.fetchSubscriptionBooking(booking.getId(), affiliateId);
		int bookingId = booking.getId();
		SubscriptionBookingVehicleDetails bookingVehicleDetails =
				vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(bookingId);
		AffiliateSubscriptionSettings affiliateSettings =
				affiliateService.fetchSubscriptionSettingsByAffiliateId(requestBean.getAffiliateId());

		if (bookingRecord == null || bookingVehicleDetails == null || affiliateSettings == null)
		{
			log.info("Cannot display edit details, one or more required objects are null: booking record, vehicle details, or affiliate settings.");
			return SUBSCRIPTION_SEARCH_PAGE;
		}

		boolean canUpdateRegistration = canUpdateRegistration(affiliateSettings, bookingVehicleDetails);
		populateModelForEditDetails(model, bookingRecord, siteId, canUpdateRegistration);

		return EDIT_DETAILS_PAGE;
	}

	@PostMapping(value = "/edit-details")
	public String sendEditDetails(@ModelAttribute AmendSubscriptionForm form, final Model model,
			HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes)
	{
		String reference = (String) session.getAttribute("reference");
		log.info("Sending edit details request for reference {}", reference);

		SubscriptionBooking booking =
				bookingService.fetchBookingByReferenceAndAffiliateId(reference, requestBean.getAffiliateId());
		if (booking == null)
		{
			return VIEW_BOOKING_PAGE;
		}
		int bookingId = booking.getId();
		SubscriptionBookingVehicleDetails bookingVehicleDetails =
				vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(bookingId);
		VehicleDetails newVehicleDetails = form.toVehicleDetails();
		boolean hasLicensePlateChanged = newVehicleDetails.getLicensePlate() != null
				&& !Objects.equals(newVehicleDetails.getLicensePlate(), bookingVehicleDetails.getCarRegistration());
		String email = form.getEmail();
		AffiliateSubscriptionSettings affiliateSettings =
				affiliateService.fetchSubscriptionSettingsByAffiliateId(requestBean.getAffiliateId());

		if (hasLicensePlateChanged && !canUpdateRegistration(affiliateSettings, bookingVehicleDetails))
		{
			addMaxLicensePlateChangesErrorAndDetailsToAttributes(email, reference, bookingId, redirectAttributes);
			return VIEW_BOOKING_PAGE;
		}

		CustomerDetails customerDetails = form.toCustomerDetails();
		AmendSubscriptionResponse response = requestHandler.sendAmendRequest(reference, customerDetails,
				newVehicleDetails, requestBean.getAffiliateConfig());

		if (!isApiResponseSuccessful(response))
		{
			addApiErrorToRedirectAttributes(response, reference, redirectAttributes);
			return VIEW_BOOKING_PAGE;
		}

		if (hasLicensePlateChanged)
		{
			updateRegistrationDetails(reference, request, booking);
		}

		redirectAttributes.addAttribute("email", email);
		redirectAttributes.addAttribute("reference", reference);

		return VIEW_BOOKING_PAGE;
	}

	private void populateModelForEditDetails(Model model, SubscriptionBookingRecord bookingRecord, int siteId,
			boolean canUpdateRegistration)
	{
		SubscriptionBooking booking = bookingRecord.getBooking();
		SubscriptionBookingVehicleDetails vehicleDetails = bookingRecord.getSubscriptionBookingVehicleDetails();
		SubscriptionBookingCustomerDetails customerDetails = bookingRecord.getCustomerDetails();
		SubscriptionBookingReceiptDetails receiptDetails = bookingRecord.getReceiptDetails();

		List<SubscriptionAllBookingData> bookingDataList =
				bookingService.fetchAllSubscriptionBookingDataWithoutDupesByReferenceAndEmail(booking.getReference(), customerDetails.getEmailAddress());
		SubscriptionAllBookingData firstBookingData = bookingDataList.get(0);
		int productId = Optional.ofNullable(firstBookingData.getProductId()).orElse(0);

		DetailsConfig config = configBuilder.build(requestBean.getSiteId(), booking.getAffiliateId(), requestBean.getAffiliateConfig(), productId);

		Locations location = requestBean.getLocation();
		String locationName = location.getName();

		model.addAttribute("languageFieldsList", languageFieldsList);
		model.addAttribute("paymentStepFieldsList", PaymentStepFields.class);
		model.addAttribute("paymentStepFields", config.getPaymentStepFields());
		model.addAttribute("mandatoryFields", config.getMandatoryFields());
		model.addAttribute("vehicleDetails", vehicleDetails);
		model.addAttribute("titleFields", PaymentStepFields.getTitleFieldIds());
		model.addAttribute("canUpdateRegistration", canUpdateRegistration);
		model.addAttribute("affiliateId", booking.getAffiliateId());

		// Customer Details
		model.addAttribute("title", customerDetails.getTitle());
		model.addAttribute("fname", customerDetails.getFirstName());
		model.addAttribute("lname", customerDetails.getLastName());
		model.addAttribute("email", customerDetails.getEmailAddress());
		model.addAttribute("telno", customerDetails.getPhoneNumber());
		model.addAttribute("addr1", customerDetails.getAddress1());
		model.addAttribute("addr2", customerDetails.getAddress2());
		model.addAttribute("town", customerDetails.getTown());
		model.addAttribute("county", customerDetails.getCounty());
		model.addAttribute("postcode", customerDetails.getPostcode());
		model.addAttribute("country", customerDetails.getCountry());
		model.addAttribute("countries", service.getCountriesMap(requestBean.getSiteTitle(), languageFieldsList, true));

		// Receipt Details
		if (receiptDetails != null)
		{
			model.addAttribute("company", receiptDetails.getCompanyName());
			model.addAttribute("taxIdentificationNumber", receiptDetails.getTaxIdentificationNumber());
			model.addAttribute("address1Receipt", receiptDetails.getAddress_1Receipt());
			model.addAttribute("address2Receipt", receiptDetails.getAddress_2Receipt());
			model.addAttribute("postcodeReceipt", receiptDetails.getPostcodeReceipt());
			model.addAttribute("townReceipt", receiptDetails.getTownReceipt());
			model.addAttribute("countyReceipt", receiptDetails.getCountyReceipt());
			model.addAttribute("countryReceipt", receiptDetails.getCountryReceipt());
			model.addAttribute("companyVatRegistrationNumber", receiptDetails.getCompanyVatRegistrationNumber());
		}
		setVehicleLookupDetails(model);

		// Affiliate flags
		model.addAttribute("showCountyDropDown", StringUtil.isEqualAtLeastOne(locationName, UNITED_STATES, IRELAND));
		
		// Static
		model.addAttribute("header", EDIT_DETAILS);
	}

	private boolean canUpdateRegistration(AffiliateSubscriptionSettings affiliateSettings,
			SubscriptionBookingVehicleDetails bookingVehicleDetails)
	{
		int registrationPeriodUpdateCount = bookingVehicleDetails.getRegistrationPeriodUpdateCount();
		int maxLicensePlateChanges =
				affiliateSettings.getMaxLicensePlateChanges() != null ? affiliateSettings.getMaxLicensePlateChanges()
						: 0;
		return registrationPeriodUpdateCount <= maxLicensePlateChanges;
	}

	private void addMaxLicensePlateChangesErrorAndDetailsToAttributes(String email, String reference, int bookingId,
			RedirectAttributes redirectAttributes)
	{
		log.error("Customer has exceeded the maximum car registration change limit for booking with id {}", bookingId);
		redirectAttributes.addAttribute("email", email);
		redirectAttributes.addAttribute("reference", reference);
		redirectAttributes.addAttribute("errorMessage",
				"The maximum number of car registration changes has been reached.");
	}

	private boolean isApiResponseSuccessful(AmendSubscriptionResponse response)
	{
		return response != null && response.getError() == null;
	}

	private void addApiErrorToRedirectAttributes(AmendSubscriptionResponse response, String reference,
			RedirectAttributes redirectAttributes)
	{
		if (response == null)
		{
			log.error("Amend response from external api was null for reference {}", reference);
			redirectAttributes.addAttribute("errorMessage", "Failed to update booking.");
		}
		else if (response.getError() != null)
		{
			Error error = response.getError();
			log.error("Failed to amend booking with reference: {} using external API. Error code: {} Message: {}",
					reference, error.getCode(), error.getMessage());
			redirectAttributes.addAttribute("errorMessage", "Failed to update booking.");
		}
	}

	private void updateRegistrationDetails(String reference, HttpServletRequest request, SubscriptionBooking booking)
	{
		SubscriptionBookingVehicleDetails bookingVehicleDetails =
				vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(booking.getId());
		bookingVehicleDetails
				.setRegistrationPeriodUpdateCount(bookingVehicleDetails.getRegistrationPeriodUpdateCount() + 1);
		bookingVehicleDetails.setLastRegistrationUpdate(new Timestamp(System.currentTimeMillis()));
		vehicleDetailsService.saveSubscriptionBookingVehicleDetails(bookingVehicleDetails);
	}
	
	private void setVehicleLookupDetails(final Model model)
	{
		VehiclelookupAffiliateLogins vehicleLookupLogin = service.getVehicleLookupLogin(requestBean.getAffiliateId());

		boolean isShowVehicleLookup = requestBean.getAffiliateConfig()
				.getConfigValue_Boolean(AffiliateConfigKeys.SHOWVEHICLELOOKUP);
		model.addAttribute("lookupMode", vehicleLookupLogin != null ? vehicleLookupLogin.getLookupMode() : DISABLED);
		model.addAttribute("shouldShowVehicleLookup", isShowVehicleLookup && vehicleLookupLogin != null);
		model.addAttribute("unrecognisedPlateValidation",
				vehicleLookupLogin != null && vehicleLookupLogin.getUnrecPlateValidation());
	}
}
