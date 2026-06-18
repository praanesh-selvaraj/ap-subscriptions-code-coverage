package com.kmp.aeroparker.application.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kmp.aeroparker.application.builder.PromotionBuilder;
import com.kmp.aeroparker.application.builder.ReservationBuilderObjectFactory;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.PromotionService;
import com.kmp.aeroparker.application.email.dispatcher.ActivateAccountEmailDispatcher;
import com.kmp.aeroparker.application.email.dispatcher.ConfirmationEmailDispatcher;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.model.EmailDispatcherParameters;
import com.kmp.aeroparker.application.model.InvoiceCreator;
import com.kmp.aeroparker.application.model.PurchaseQuery;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.enums.SubscriptionEmailType;
import com.kmp.aeroparker.application.payment.handler.PaymentHandler;
import com.kmp.aeroparker.application.processor.AncillaryBarcodeProcessor;
import com.kmp.aeroparker.application.validator.SubscriptionValidator;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSProducer;
import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSStatus;
import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSType;
import com.kmp.aeroparker.sqs.reports.ReportsSQSProducer;
import com.kmp.aeroparker.sqs.reports.ReportsSQSType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReservationData;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionPromoBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionSessionPromotion;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class BookingController
{
	private final SubscriptionConfigBean requestBean;
	private final SubscriptionControllerService service;
	private final PaymentHandler paymentHandler;
	private final ConfirmationEmailDispatcher emailDispatcher;
	private final Localise localise;
	private final ActivateAccountEmailDispatcher activateAccountEmailDispatcher;
	private final ReservationBuilderObjectFactory builderObjectFactory;
	private final CustomConnectionProvider customConnectionProvider;
	private final FreemarkerSQSProducer freemarkerSQSProducer;
	private final AncillaryBarcodeProcessor barcodeProcessor;
	private final SubscriptionRenewalService renewalService;
	private final SubscriptionValidator validator;
	private final InvoiceCreator invoiceCreator;
	private final ReportsSQSProducer reportsSQSProducer;
	private final PromotionService sessionPromotionService;
	private final PromotionBuilder promotionBuilder;
	private final BookingService bookingService;

	private static final String HTTPS = "https";
	private static final String HTTP_SCHEME = "http:";

	private static final String PAYMENT_ERROR =
			"There was a problem while processing your payment. Please try again or use a different card.";
	private static final String DUPLICATE_PAYMENT_ERROR = "Your payment has already been completed.";

	@GetMapping(value = "/booking")
	public String booking(@ModelAttribute("bookingData") SubscriptionBookingData bookingData,
			final RedirectAttributes redirectAttributes, HttpServletRequest req)
	{
		log.info("Inside booking controller");

		String customerGuid = bookingData.getCustomerGuid();
		String email = bookingData.getEmail();
		String confirmationGuid = UUID.randomUUID()
				.toString();
		bookingData.setConfirmationGuid(confirmationGuid);
		bookingData.setTimeZone(requestBean.getTimeZone());
		bookingData.setDateFormat(requestBean.getDateFormat());
		Sites site = requestBean.getSite();
		int siteId = site.getId();
		bookingData.setSiteId(siteId);
		int affId = requestBean.getAffiliateId();
		bookingData.setAffiliateId(affId);
		bookingData.setVatRate(requestBean.getAffiliateVatRate());
		int currentLanguageId = requestBean.getCurrentLanguageId();
		int defaultLanguageId = requestBean.getDefaultLanguageId();
		bookingData.setCurrentLanguageId(currentLanguageId);

		Map<String, Object> discountResponse =
				renewalService.validateSubscription(bookingData, requestBean.getAffiliate()
						.getCode());
		boolean discountApplied = false;

		if (discountResponse.containsKey("newStartDate"))
		{
			bookingData.setStartDate((String) discountResponse.get("newStartDate"));
			bookingData.setAmount(new BigDecimal((String) discountResponse.get("grandTotal")));
			discountApplied = true;
		}

		final String existingConfirmationGuid = bookingService.fetchConfirmationGuidByCustomerGuid(customerGuid);
		if (StringUtil.hasText(existingConfirmationGuid))
		{
			// Redirect back to details page if a booking has already been completed for this customer GUID,
			// preventing duplicate payments and refunds.
			log.info(
					"Found existing confirmation GUID of {} for customer GUID {}. Redirecting back to details page to avoid duplicate payment/refund.",
					existingConfirmationGuid, customerGuid);
			redirectAttributes.addAttribute("paymentError", DUPLICATE_PAYMENT_ERROR);
			setDetailsPageRedirectAttributes(redirectAttributes, bookingData, customerGuid);
			return "redirect:details";
		}

		Basket basket = service.getBasket(customerGuid, currentLanguageId, defaultLanguageId,
				requestBean.getAffiliate(), requestBean.getAffiliateSubscriptionSettings());

		if (basket != null)
		{
			log.info("Applying renewal discount to the basket if applicable for customer guid {}",
					customerGuid);
			SubscriptionBookingDetails bookingDetails =
					validator.fetchBookingDetails(email, customerGuid, discountResponse);
			if (discountApplied)
			{
				Basket discountedBasket = renewalService.fetchAndApplyDiscount(customerGuid, email, bookingDetails);

				if (discountedBasket != null)
				{
					basket = discountedBasket;
				}
				else
				{
					log.error("Failed to apply discount. Basket is null after applying discount for customer guid {}",
							customerGuid);
				}
			}
			// Get the reference
			// TODO needs to review, lock issue
			if (StringUtil.isEmpty(bookingData.getBookingReference()))
			{
				String reference = service.generateReference(requestBean.getAffiliate());
				bookingData.setBookingReference(reference);
			}
			basket.initialize(localise);
			if (!discountApplied)
			{
				bookingData.setAmount(basket.getGrandTotal());
				bookingData.setBookingFee(basket.getTotalBookingFee());
				bookingData.setRecurring(basket.isRecurring());
			}
			bookingData.setServletAbsoluteUrl(getSafeRequestURL(req));
			bookingData.setRawServletAbsoluteUrl(req.getRequestURL().toString());
			// if query transaction fails, redirect to details step
			// if successful go to confirmation to process booking and redirect
			// to
			// confirmation
			log.info("Preparing to process payment for reference: {} ", bookingData.getBookingReference());
			bookingData.setPartialPaymentsEnabled(service.isPartialPaymentsEnabled(siteId));
			boolean isPaymentRequired = basket.getGrandTotal().compareTo(BigDecimal.ZERO) > 0;
			PaymentHandlerBean paymentHandlerBean = paymentHandler.processRedirectPayment(bookingData);
			if (paymentHandler.processPayment(bookingData, basket))
			{
				SubscriptionBookingRecord booking = service.getBooking(affId, confirmationGuid);
				if (booking != null)
				{
					barcodeProcessor.generateNewAncillaryBarcode(booking);
					// Save promotion to permanent table if applied
					SubscriptionSessionPromotion appliedPromotion = sessionPromotionService.fetchSessionPromotionByGuid(customerGuid);
					if (appliedPromotion != null && appliedPromotion.getValid())
					{
						log.info("Saving promotion {} to permanent table for booking {}",
								appliedPromotion.getPromoCode(), booking.getBooking().getId());

						SubscriptionPromoBooking promoBooking =
								promotionBuilder.buildPromoBooking(booking.getBooking().getId(), appliedPromotion);

						sessionPromotionService.savePromoBooking(promoBooking);

						// Increment the promo code usage count
						if (appliedPromotion.getPromoCodeId() != null)
						{
							sessionPromotionService.incrementPromoCodeUses(appliedPromotion.getPromoCodeId());
						}
					}

					log.info("Payment processed successfully, preparing to send email if required, guid: {}", confirmationGuid);
					EmailDispatcherParameters emailDispatcherParameters = EmailDispatcherParameters.builder()
							.withEmailType(SubscriptionEmailType.CONFIRMATION)
							.withSite(site)
							.withCurrentLanguageId(currentLanguageId)
							.withDefaultLanguageId(defaultLanguageId)
							.withBookingRecord(booking)
							.withContactId(booking.getBooking()
									.getContactId())
							.withTimeZone(bookingData.getTimeZone())
							.withAffiliate(requestBean.getAffiliate())
							.withServletSchema(req.getScheme())
							.withServletName(req.getServerName())
							.withEmailFromAffiliate(requestBean.getAffiliateConfig()
									.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_ACTIVATE_ACCOUNT_EMAIL))
							.build();
					if (requestBean.getAffiliateConfig()
							.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_EMAIL_CONFIRMATIONS))
					{
						log.debug("Preparing to send email");
						if (service.isFreemarkerEnabled(siteId))
						{
							sendFreemarkerSQSMessage(booking.getBooking(), booking.getCustomerDetails(), siteId, false);

							int productId = booking.getBookingItemMap()
									.keySet()
									.stream()
									.findFirst()
									.map(SubscriptionBookingItem::getProductId)
									.orElse(0);
							// Ignore invoice if no payment was created.
							if (isPaymentRequired && ((bookingData.isReceiptCheckbox() || isInvoiceMandatory(bookingData, siteId))
									&& service.doesCompanyRecordExist(siteId, productId)))
							{
								invoiceCreator.savePaymentInvoice(bookingData.getPaymentId(), siteId, productId);
								sendFreemarkerSQSMessage(booking.getBooking(), booking.getCustomerDetails(), siteId,
										true);
							}
						}
						else
						{
							emailDispatcher.sendEmail(emailDispatcherParameters);
						}
					}
					activateAccountEmailDispatcher.sendEmail(emailDispatcherParameters);
					sendReportSQS(booking.getBooking().getId(), ReportsSQSType.SUBSCRIPTION);
				}

				redirectAttributes.addAttribute("guid", confirmationGuid);
				log.debug("Payment successful, redirecting to confirmation");
				return "redirect:confirmation";
			}
			if (paymentHandlerBean != null && paymentHandlerBean.isRedirectRequired())
			{
				SubscriptionBookingReservationData reservationData =
						builderObjectFactory.buildReservationData(bookingData);
				service.saveReservationData(reservationData);
				if (paymentHandlerBean.isStripeRedirect())
				{
					// Stripe handles the redirect in the JS AJAX call so we don't redirect here, we just return the redirectUrl
					req.setAttribute("stripeRedirectUrl", paymentHandlerBean.getCheckoutUrl());
					return "stripe-redirect";
				}
				else
				{
					// Redirects user to payment page on redirect
					return "redirect:" + paymentHandlerBean.getCheckoutUrl();
				}
			}
			redirectAttributes.addAttribute("paymentError", PAYMENT_ERROR);
		}

		setDetailsPageRedirectAttributes(redirectAttributes, bookingData, customerGuid);
		log.info("Error during payment, redirecting to details, customer Guid: {}  start date: {}", customerGuid,
				bookingData.getStartDate());

		return "redirect:details";
	}

	@PostMapping(value = "/basketupdate", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String doAjaxBasketUpdate(final Model model, @RequestBody final List<PurchaseQuery> basketUpdateQueries)
	{
		log.debug("Attempting to update basket");
		if (!basketUpdateQueries.isEmpty())
		{
			int defaultLanguageId = requestBean.getDefaultLanguageId();
			int currentLanguageId = requestBean.getCurrentLanguageId();
			Basket basket = service.buildBasket(basketUpdateQueries, currentLanguageId, defaultLanguageId,
					requestBean.getAffiliate(), requestBean.getAffiliateSubscriptionSettings());
			model.addAttribute("continueToNextStep", basket.getGrandTotal()
					.compareTo(BigDecimal.ZERO) == 1 ? true : false);
			model.addAttribute("basket", basket);
			log.debug("Basket updated, new grand total : {}", basket.getGrandTotal());
		}
		else
		{
			Basket basket = new Basket();
			basket.initialize(localise);
			model.addAttribute("basket", basket);
			log.debug("Showing empty basket");
		}
		return "fragments/details-side-bar :: details-side-bar";
	}

	@RequestMapping(value = "/processPaymentCmd", method = {RequestMethod.GET, RequestMethod.POST})
	public String processPaymentCmd(Model model, final RedirectAttributes redirectAttributes, HttpServletRequest req,
			@RequestParam(name = "affiliateId", required = false) int affiliateId,
			@RequestParam(name = "siteId", required = false) int siteId,
			@RequestParam(name = "bookingReference", required = false) String bookingReference,
			@RequestParam(name = "customerGuid", required = false) String customerGuid,
			@RequestParam(name = "cmd", required = false) String cmd,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "firstName", required = false) String firstName,
			@RequestParam(name = "lastName", required = false) String lastName,
			@RequestParam(name = "callback", required = false) boolean isCallback,
			@RequestParam(name = "payment_intent", required = false) String stripePaymentIntentId)
	{
		log.info("Processing payment callback: method={}, bookingReference={}, customerGuid={}, cmd={}, isCallback={}",
				req.getMethod(), bookingReference, customerGuid, cmd, isCallback);

		String callBackData = "";
		if (isCallback)
		{
			callBackData = getCallbackDataFromRequest(req);
			log.info("Received payment callback data for bookingReference={}, customerGuid={}, data={}",
					bookingReference, customerGuid, callBackData);
		}
		if (StringUtils.hasText(stripePaymentIntentId))
		{
			callBackData = stripePaymentIntentId;
		}

		SubscriptionBookingData bookingData =
				paymentHandler.processSubCmd(affiliateId, bookingReference, cmd, callBackData, isCallback, customerGuid);
		if (bookingData == null)
		{
			bookingData = new SubscriptionBookingData();
		}

		SubscriptionBookingReservationData reservationData = service.fetchReservationDataByGuid(customerGuid);
		builderObjectFactory.buildBookingData(reservationData, bookingData);

		bookingData.setStartDate(startDate);
		bookingData.setBookingReference(bookingReference);
		bookingData.setAffiliateId(affiliateId);
		bookingData.setSiteId(siteId);
		bookingData.setCustomerGuid(customerGuid);

		redirectAttributes.addFlashAttribute("bookingData", bookingData);

		return "redirect:booking";
	}

	private void setDetailsPageRedirectAttributes(final RedirectAttributes redirectAttributes,
			final SubscriptionBookingData bookingData, final String customerGuid)
	{
		redirectAttributes.addAttribute("startDate", bookingData.getStartDate());
		redirectAttributes.addAttribute("customerGuid", customerGuid);
		redirectAttributes.addFlashAttribute("bookingData", bookingData);
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
			url = requestURL.replace(0, 4, HTTPS).toString();
		}
		else
		{
			// If already https, keep the URL as is
			url = requestURL.toString();
		}
		return url;
	}

	private String getCallbackDataFromRequest(HttpServletRequest req)
	{
		String callbackData = "";
		try
		{
			callbackData = req.getReader()
					.lines()
					.collect(Collectors.joining());
		}
		catch (IOException e)
		{
			log.error("IOException when reading callback response from request error {}", e.getMessage(), e);
		}
		return callbackData;
	}

	private void sendFreemarkerSQSMessage(SubscriptionBooking booking,
			SubscriptionBookingCustomerDetails customerDetails, int siteId, boolean isInvoice)
	{
		freemarkerSQSProducer.sendMessage(requestBean.getFreemarkerQueueUrl(), booking.getId(), booking.getReference(),
				FreemarkerSQSStatus.NEW, FreemarkerSQSType.SUBSCRIPTION, customConnectionProvider.getSchema(), siteId,
				isInvoice, customerDetails.getEmailAddress());
	}

	private boolean isInvoiceMandatory(final SubscriptionBookingData bookingData, int siteId)
	{
		List<PaymentStepFieldsSubscriptions> paymentStepFields =
				service.fetchPaymentStepFieldsSubscriptionBySiteId(siteId);
		boolean isMandatory = paymentStepFields.stream()
				.anyMatch(field -> field.getMandatory() == 1);

		return isMandatory;
	}

	private void sendReportSQS(final int bookingId, final ReportsSQSType type)
	{
		reportsSQSProducer.sendMessage(requestBean.getReportsQueueUrl(), bookingId, type,
				customConnectionProvider.getSchema());
		log.info("Sending booking ID " + bookingId + " to reportSQS");
	}
}