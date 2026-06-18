package com.kmp.aeroparker.application.web;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.builder.ConfigBuilder;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.db.service.VehicleDetailsService;
import com.kmp.aeroparker.application.external.api.SubscriptionBookingRequestHandler;
import com.kmp.aeroparker.application.model.AnalyticsLocations;
import com.kmp.aeroparker.application.model.DetailsConfig;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.external.api.datatypes.AvailabilityWindow;
import com.kmp.aeroparker.application.model.external.api.datatypes.Error;
import com.kmp.aeroparker.application.model.external.api.datatypes.SubscriptionQuote;
import com.kmp.aeroparker.application.model.external.api.response.RenewSubscriptionResponse;
import com.kmp.aeroparker.application.model.external.api.response.SubscriptionAvailabilityResponse;
import com.kmp.aeroparker.application.payment.handler.PaymentHandler;
import com.kmp.aeroparker.application.processor.SubscriptionAnalyticsProcessor;
import com.kmp.aeroparker.application.validator.SubscriptionValidator;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesContent;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesDisplay;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionContactMembership;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.security.utils.SecurityUtil;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(value = "/subscriptions/{affCode}")
public class SubscriptionRenewalBookingController extends AbstractController
{
	private final SubscriptionConfigBean requestBean;
	private final SubscriptionBookingRequestHandler requestHandler;
	private final SubscriptionControllerService controllerService;
	private final PaymentHandler paymentHandler;
	private final AffiliateService affiliateService;
	private final BookingService bookingService;
	private final ContactService contactService;
	private final PaymentService paymentService;
	private final ConfigBuilder configBuilder;
	private final VehicleDetailsService vehicleDetailsService;
	private final SubscriptionValidator subValidator;
	private final SubscriptionAnalyticsProcessor analyticsProcessor;

	private static final String TAKE_PAYMENT_HEADER = "Take Payment";
	private static final String PAYMENT_DETAILS_HEADER = "Payment Details";
	private static final String AVAILABILITY_RESPONSE = "availabilityResponse";
	private static final String EMAIL = "email";
	private static final String REFERENCE = "reference";
	private static final String HTTPS = "https";
	private static final String HTTP_SCHEME = "http:";
	private static final String END_OF_DAY_TIME = "T23:59:59";
	private static final String DISPLAY_TAKE_PAYMENT = "subscription-display-take-payment";
	private static final String VIEW_BOOKING = "redirect:view-booking";
	private static final String RENEWAL_PAYMENT = "subscription-renewal-payment";
	private static final String STRIPE_REDIRECT = "stripe-redirect";
	private static final String RENEWAL_CONFIRMATION = "redirect:renewal-confirmation";
	private static final String MISSING_SCOPE_ERROR_CODE = "323";

	@GetMapping(value = "/setup-booking-renewal")
	public String setupBookingRenewal(HttpSession session, Model model, RedirectAttributes redirectAttributes,
			@RequestParam(name = "email") String email, @RequestParam(name = "reference") String reference,
			@RequestParam(name = "startDate") String originalStartDateString,
			@RequestParam(name = "endDate") String originalEndDateString)
	{
		log.info("Setting up renewal booking page for {}", reference);
		resetSessionValues(session);

		boolean isRenewalFeatureFlagEnabled = controllerService.isSubscriptionRenewalEnabled(requestBean.getSiteId());
		int paymentGatewayType =
				paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(requestBean.getAffiliateId(), 0);
		boolean isPaymentGatewaySupported = isPaymentGatewaySupported(paymentGatewayType);

		if (!isRenewalFeatureFlagEnabled || !isPaymentGatewaySupported)
		{
			log.info("Feature flag disabled or unsupported payment gateway for {}", reference);
			return redirectToViewBooking(redirectAttributes, email, reference,
					"Subscription booking renewal is not allowed");
		}
		
		boolean isValidRenewalPeriod = subValidator.validateRenewalPeriod(reference, originalEndDateString);
		
		if (!isValidRenewalPeriod)
		{
			log.info("Booking {} does not fall within the renewal period", reference);
			return redirectToViewBooking(redirectAttributes, email, reference,
					"Subscription end date is outside of the renewal period");
		}

		log.info("Sending renewal availability request for {}", reference);
		SubscriptionAvailabilityResponse availabilityResponse =
				requestHandler.getRenewalAvailability(requestBean.getAffiliateConfig(), reference);
		
		if (availabilityResponse != null && availabilityResponse.getError() != null
				&& MISSING_SCOPE_ERROR_CODE.equals((availabilityResponse).getError()
						.getCode()))
		{
			log.info("Renewal not available for {}. The api user is missing renewal scope", reference);
			return redirectToViewBooking(redirectAttributes, email, reference,
					"We are unable to renew your booking due to a technical issue. Please try again later.");
		}

		if (availabilityResponse == null || availabilityResponse.getSubscriptionQuotes() == null
				|| CollectionUtils.isEmpty(availabilityResponse.getSubscriptionQuotes()
						.getQuotes()))
		{
			log.info("Renewal not available for {}", reference);
			return redirectToViewBooking(redirectAttributes, email, reference,
					"Renewal is not available for this booking");
		}

		session.setAttribute(AVAILABILITY_RESPONSE, availabilityResponse);
		session.setAttribute(EMAIL, urlEncodeString(email));
		session.setAttribute(REFERENCE, urlEncodeString(reference));

		AvailabilityWindow window = availabilityResponse.getAvailabilityWindow();
		SubscriptionQuote quote = availabilityResponse.getSubscriptionQuotes()
				.getQuotes()
				.get(0);

		LocalDateTime originalStartDate =
				LocalDateTime.parse(originalStartDateString + END_OF_DAY_TIME, DateTimeFormatter.ISO_DATE_TIME);
		LocalDate originalEndDate = LocalDate.parse(originalEndDateString, DateTimeFormatter.ISO_DATE);

		LocalDateTime newStartDate = LocalDateTime.parse(window.getArrivalDateTime(), DateTimeFormatter.ISO_DATE_TIME);
		LocalDateTime newEndDate = LocalDateTime.parse(window.getReturnDateTime(), DateTimeFormatter.ISO_DATE_TIME);

		if (originalEndDate.isAfter(LocalDate.now()))
		{
			newStartDate = originalStartDate;
		}

		model.addAttribute("startDate", newStartDate);
		model.addAttribute("endDate", newEndDate);
		model.addAttribute("productName", quote.getSubscriptionProduct()
				.getName());
		model.addAttribute("price", quote.getPrice()
				.getValue());
		model.addAttribute("header", TAKE_PAYMENT_HEADER);

		return DISPLAY_TAKE_PAYMENT;
	}
	
	private String redirectToViewBooking(RedirectAttributes redirectAttributes, String email, String reference,
			String message)
	{
		redirectAttributes.addAttribute("errorMessage", message);
		redirectAttributes.addAttribute("email", email);
		redirectAttributes.addAttribute("reference", reference);
		redirectAttributes.addAttribute("submitted", 1);
		return VIEW_BOOKING;
	}

	@PostMapping(value = "/display-renewal-payment")
	public String displayTakeBookingRenewal(HttpSession session, Model model, RedirectAttributes redirectAttributes,
			@RequestParam(name = "email") String email, @RequestParam(name = "reference") String reference)
	{
		SubscriptionAvailabilityResponse availabilityResponse =
				getSessionObjectOfType(session, AVAILABILITY_RESPONSE, SubscriptionAvailabilityResponse.class);
		if (availabilityResponse != null)
		{
			log.info("Displaying renewal payment page for reference: {}", reference);
			SubscriptionQuote quote = availabilityResponse.getSubscriptionQuotes()
					.getQuotes()
					.get(0);
			model.addAttribute("header", PAYMENT_DETAILS_HEADER);
			model.addAllAttributes(paymentGatewaySetup(quote.getPrice()
					.getValue()));
			DetailsConfig config = configBuilder.build(requestBean.getSiteId(), requestBean.getAffiliateId(),
					requestBean.getAffiliateConfig(), quote.getSubscriptionProduct().getId());
			model.addAttribute("config", config);
			model.addAttribute("isRenewal", true);
			int affiliateId = requestBean.getAffiliateId();
			SubscriptionBooking booking = bookingService.fetchBookingByReferenceAndAffiliateId(reference, affiliateId);
			SubscriptionBookingRecord bookingRecord = bookingService.fetchSubscriptionBooking(booking.getId(), affiliateId);
			analyticsProcessor.processSubscriptionAnalytics(model, bookingRecord, requestBean.getCurrency(), 
					AnalyticsLocations.SUBSCRIPTION_PAYMENT_DETAIL.getId());
			
			setTermsAndCondition(model);
			return RENEWAL_PAYMENT;
		}
		log.info("Availability from session was null for reference: {}", reference);

		redirectAttributes.addAttribute("email", email);
		redirectAttributes.addAttribute("reference", reference);
		redirectAttributes.addAttribute("submitted", 1);
		return VIEW_BOOKING;
	}

	@GetMapping(value = "/take-renewal-payment")
	public String takeRenewalPayment(HttpServletRequest req, HttpSession session,
			@ModelAttribute("bookingData") SubscriptionBookingData bookingData)
	{
		log.info("Taking renewal payment");
		bookingData.setServletAbsoluteUrl(getSafeRequestURL(req));
		bookingData.setRawServletAbsoluteUrl(req.getRequestURL()
				.toString());
		bookingData.setRenewal(true);

		String email = urlDecodeString(getSessionObjectOfType(session, EMAIL, String.class));
		String reference = getSessionObjectOfType(session, REFERENCE, String.class);

		addDetailsToBookingData(bookingData, reference, email);

		PaymentHandlerBean paymentHandlerBean = paymentHandler.processRedirectPayment(bookingData);
		// Stripe handles the redirect in the JS AJAX call so we don't redirect here, we just return the
		// redirectUrl
		req.setAttribute("stripeRedirectUrl", paymentHandlerBean.getCheckoutUrl());
		return STRIPE_REDIRECT;
	}

	private void addDetailsToBookingData(SubscriptionBookingData bookingData, String reference, String email)
	{
		SubscriptionBooking booking =
				bookingService.fetchBookingByReferenceAndAffiliateId(reference, bookingData.getAffiliateId());
		SubscriptionBookingRecord bookingRecord =
				bookingService.fetchSubscriptionBooking(booking.getId(), bookingData.getAffiliateId());
		Affiliates affiliate = affiliateService.fetchAffiliateById(bookingData.getAffiliateId());
		Contacts contact = contactService.fetchContactByEmailAndSiteId(urlDecodeString(email), affiliate.getSiteid());
		SubscriptionContactMembership membership =
				contactService.fetchSubscriptionContactMembershipIdByContactId(contact != null ? contact.getId() : 0);
		bookingData.setMembershipId(membership != null ? membership.getMembershipId() : null);
		bookingData.setBookingReference(reference);
		bookingData.setEmail(email);
		bookingData.setTelno(bookingRecord.getCustomerDetails().getPhoneNumber());
		bookingData.setCarReg(bookingRecord.getSubscriptionBookingVehicleDetails().getCarRegistration());
	}

	@GetMapping(value = "/send-renewal")
	public String sendRenewal(HttpSession session, RedirectAttributes redirectAttributes,
			@RequestParam(name = "token") String token, @RequestParam(name = "amount") BigDecimal amount,
			@RequestParam(name = "currency") String currency)
	{
		String email = getSessionObjectOfType(session, EMAIL, String.class);
		String reference = getSessionObjectOfType(session, REFERENCE, String.class);
		SubscriptionAvailabilityResponse availability =
				getSessionObjectOfType(session, AVAILABILITY_RESPONSE, SubscriptionAvailabilityResponse.class);
		resetSessionValues(session);

		int paymentGatewayType =
				paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(requestBean.getAffiliateId(), 0);
		PartialPayments partialPayment = paymentService.fetchPartialPaymentsByTransactionIdReferenceAndType(
				token, reference, paymentGatewayType);
		if (partialPayment != null)
		{
			log.info("Booking already renewed, displaying confirmation for reference {}", reference);
			redirectAttributes.addAttribute("encryptedReference", SecurityUtil.encryptString(reference));
			return RENEWAL_CONFIRMATION;
		}

		log.info("Sending renewal request for reference {}", reference);

		RenewSubscriptionResponse response = requestHandler.sendRenewalRequest(requestBean.getAffiliateConfig(),
				reference, availability.getAvailabilityWindow(), token, amount, currency);
		if (response == null)
		{
			log.error("Renewal response from external api was null for reference {}", reference);
			redirectAttributes.addAttribute("errorMessage", "Failed to update booking");
		}
		else if (response.getError() != null)
		{
			Error error = response.getError();
			log.error("Failed to update booking reference: {} using external API. Error code: {} Message: {}",
					reference, error.getCode(), error.getMessage());
			redirectAttributes.addAttribute("errorMessage", error.getMessage());
		}
		else
		{
			log.info("Successfully renewed booking, displaying confirmation for reference {}", reference);
			redirectAttributes.addAttribute("encryptedReference", SecurityUtil.encryptString(reference));
			resetRegistrationPeriodUpdateCount(reference);
			return RENEWAL_CONFIRMATION;
		}

		redirectAttributes.addAttribute("email", email);
		redirectAttributes.addAttribute("reference", reference);
		redirectAttributes.addAttribute("submitted", 1);
		return VIEW_BOOKING;

	}

	private void resetSessionValues(HttpSession session)
	{
		session.removeAttribute(AVAILABILITY_RESPONSE);
		session.removeAttribute(EMAIL);
		session.removeAttribute(REFERENCE);
	}

	private boolean isPaymentGatewaySupported(int paymentGatewayType)
	{
		return PaymentGatewayType.STRIPE.equals(PaymentGatewayType.getType(paymentGatewayType));
	}

	private void setTermsAndCondition(final Model model)
	{
		boolean showTermsAndConditions = requestBean.getAffiliateConfig()
				.getConfigValue_Boolean(AffiliateConfigKeys.DISPLAY_TCS_SECTION);
		model.addAttribute("showTermsAndConditions", showTermsAndConditions);
		boolean useTermsUrl = false;

		AffiliatesContent affiliatesContent = requestBean.getAffiliateContent();
		String termsAndConditions = "";
		if (affiliatesContent != null)
		{
			if (showTermsAndConditions)
			{
				termsAndConditions = StringEscapeUtils.unescapeHtml4(affiliatesContent.getTermsAndConditions());
				if (!StringUtils.hasText(termsAndConditions))
				{
					AffiliatesDisplay affiliatesDisplay = requestBean.getAffiliatesDisplay();
					termsAndConditions = affiliatesDisplay == null ? "" : affiliatesDisplay.getTermsUrl();
					useTermsUrl = true;
				}
				model.addAttribute("useTermsUrl", useTermsUrl);
				model.addAttribute("termsAndConditions", termsAndConditions);
			}
			model.addAttribute("personalData", StringEscapeUtils.unescapeHtml4(affiliatesContent.getPersonalData()));
		}
	}

	private Map<String, Object> paymentGatewaySetup(final BigDecimal totalPrice)
	{
		log.debug("Building params for payment gateway");
		PaymentGatewayParameters paymentHandlerParams = PaymentGatewayParameters.builder()
				.withAffiliate(requestBean.getAffiliate())
				.withSite(requestBean.getSite())
				.withLanguage(requestBean.getCurrentLanguage())
				.withCurrency(requestBean.getCurrency())
				.withAmount(totalPrice)
				.build();
		return paymentHandler.setUpTransaction(paymentHandlerParams);
	}

	private <T> T getSessionObjectOfType(HttpSession session, String key, Class<T> objectType)
	{
		Object readObject = session.getAttribute(key);
		if (readObject != null && objectType.isAssignableFrom(readObject.getClass()))
		{
			return objectType.cast(readObject);
		}
		return null;
	}

	private String getSafeRequestURL(HttpServletRequest req)
	{
		StringBuffer requestURL = req.getRequestURL();
		String url = "";

		log.debug("Getting safe request URL for {}", requestURL.toString());
		if (StringUtil.hasText(requestURL) && requestURL.substring(0, 5)
				.equals(HTTP_SCHEME))
		{
			// Check if the URL starts with "http:"
			log.debug("Chaging request URL scheme to https");
			url = requestURL.replace(0, 4, HTTPS)
					.toString();
		}
		else
		{
			// If already https, keep the URL as is
			url = requestURL.toString();
		}
		return url;
	}

	private String urlEncodeString(String stringToEncode)
	{
		try
		{
			return URLEncoder.encode(stringToEncode, StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException e)
		{
			log.error("Unable to url encode string {}", stringToEncode, e.getMessage(), e);
			return stringToEncode;
		}
	}

	private String urlDecodeString(String stringToDecode)
	{
		String decodedString = stringToDecode;
		try
		{
			decodedString = URLDecoder.decode(stringToDecode, StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException e)
		{
			log.error("Unable to url decode string {}, {}", stringToDecode, e.getMessage(), e);
		}
		return decodedString;
	}
	
	private void resetRegistrationPeriodUpdateCount(String reference)
	{
		SubscriptionBooking booking =
				bookingService.fetchBookingByReferenceAndAffiliateId(reference, requestBean.getAffiliateId());
		if (booking != null)
		{
			SubscriptionBookingVehicleDetails bookingVehicleDetails =
					vehicleDetailsService.fetchSubscriptionBookingVehicleDetailsByBookingId(booking.getId());
			
			if (bookingVehicleDetails == null)
			{
				log.warn("Vehicle details not found for booking ID {} - unable to reset registration period update count.", booking.getId());
				return;
			}

			bookingVehicleDetails.setRegistrationPeriodUpdateCount(0);
			bookingVehicleDetails.setLastRegistrationUpdate(new Timestamp(System.currentTimeMillis()));
			vehicleDetailsService.saveSubscriptionBookingVehicleDetails(bookingVehicleDetails);
		}
	}
}
