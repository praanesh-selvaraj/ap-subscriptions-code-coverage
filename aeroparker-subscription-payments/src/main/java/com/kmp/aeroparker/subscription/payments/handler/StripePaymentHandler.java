package com.kmp.aeroparker.subscription.payments.handler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.credentials.StripeCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.PaymentProcessorFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHandler;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.PaymentProcessorParameters;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.stripe.StripePaymentIntentProcessor;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentTransaction;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.webutil.utils.WebUtil;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeError;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StripePaymentHandler implements IPaymentHandler
{
	private final StripePaymentIntentProcessor stripePaymentIntentProcessor;
	private final PaymentService paymentService;
	private final PaymentProcessorFactory paymentProcessorFactory;
	
	private static final String METADATA_EMAIL = "Customer_email";
	
	@Override
	public Map<String, Object> setUpTransaction(PaymentGatewayParameters paymentHandlerParams)
	{
		log.info("Setting up Stripe Transation");
		Map<String, Object> params = new HashMap<>();
		
		Affiliates affiliate = paymentHandlerParams.getAffiliate();
		int affiliateId = affiliate.getId();
		StripeCredentials credentials = (StripeCredentials) paymentService.fetchPaymentCredentials(
				affiliateId, 0, PaymentGatewayType.STRIPE);
		PaymentGatewayTypes paymentGatewayTypes = paymentService.fetchPaymentGatewayTypesById(PaymentGatewayType.STRIPE.getId());
		
		String stringAmount = paymentHandlerParams.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP)
				.movePointRight(2)
				.toPlainString();
		PaymentIntent paymentIntent =
				stripePaymentIntentProcessor.generatePaymentIntent(affiliateId, stringAmount, paymentHandlerParams.getCurrency());

		if (credentials != null && StringUtils.hasText(paymentIntent.getId()))
		{
			params.put("paymentIntentId", paymentIntent.getId());
			params.put("credentials", credentials);
			params.put("clientSecret", paymentIntent.getClientSecret());
			params.put("seamlessLocale", paymentHandlerParams.getLanguage().getLanguageCode());
			params.put("paymentGatewayType", getType().getId());
			params.put("currency", paymentHandlerParams.getCurrency());
			params.put("amount", paymentHandlerParams.getAmount());
			params.put("affiliateName", affiliate.getName().replaceAll("'", "&#8217;"));
			params.put("affiliateId", affiliateId);
			params.put("isSeamlessPayment", true);
			params.put("paymentHtml", paymentGatewayTypes.getPaymentJsp().replace(".jsp", ""));
		}
		
		return params;
	}
	
	/**
	 * When someone clicks pay on the details step for Stripe, we send an AJAX request and expect a response
	 * with the redirectUrl for Stripe to redirect to. We use processRedirectPayment to return the redirectUrl.
	 * We then come back to this method after the redirect is done and process the payment.
	 */
	@Override
	public boolean processPayment(SubscriptionBookingData bookingData)
	{
		if (!bookingData.isPaymentSuccess())
		{
			return false;
		}
		String reference = bookingData.getBookingReference();
		int affiliateId = bookingData.getAffiliateId();
		String paymentIntentId = bookingData.getPaymentIntentId();
		PaymentIntent intent = stripePaymentIntentProcessor.getPaymentIntent(affiliateId, paymentIntentId);
		if (intent == null)
		{
			log.info("Payment intent retrieved was somehow null after payment was flagged as success, payment will not be procesed"
					+ " for payment intent ID {}", paymentIntentId);
			return false;
		}
		stripePaymentIntentProcessor.updatePaymentIntentWithMembershipId(reference, affiliateId, intent, "");
		Payments parentPayment = paymentService.fetchPaymentByReference(reference);
		// Stripe is only used by Dublin for Subscriptions who use Partial Payments so non-partial are not supported
		PartialPayments partialPayment = paymentService.fetchPartialPaymentsByTransactionIdReferenceAndType(
				intent.getId(), reference, PaymentGatewayType.STRIPE.getId());
		if (partialPayment == null)
		{
			PaymentProcessorParameters paymentProcessorParameters = PaymentProcessorParameters.builder()
					.withPaymentIntent(intent)
					.withTimeZone(bookingData.getTimeZone())
					.withBookingReference(reference)
					.withAffiliateId(affiliateId)
					.build();
			parentPayment = paymentProcessorFactory.getInstance(PaymentGatewayType.STRIPE)
					.processPartial(paymentProcessorParameters, parentPayment);
		}
		else
		{
			log.info("Payment already exists for reference {}", reference);
		}
		// Save exact date time when transaction was created in Stripe
		updatePaymentTransaction(intent, affiliateId, bookingData.getTimeZone(), parentPayment, reference,
				PaymentGatewayType.STRIPE.getId());
		bookingData.setPaymentId(Optional.ofNullable(parentPayment.getId()).orElse(0));
		return true;
	}
	
	@Override
	public PaymentHandlerBean processRedirectPayment(SubscriptionBookingData bookingData)
	{
		int affiliateId = bookingData.getAffiliateId();
		String guid = HtmlUtils.htmlUnescape(bookingData.getCustomerGuid());
		String reference = bookingData.getBookingReference();
		String servletUrl = bookingData.getRawServletAbsoluteUrl();
		String cardholderName = bookingData.getCardholderName();
		StripeCredentials credentials = (StripeCredentials)
				paymentService.fetchPaymentCredentials(affiliateId, 0, PaymentGatewayType.STRIPE);
		boolean cardholderNameEnabled = credentials.isCardholderNameEnabled();
		String emailAddress = bookingData.getEmail();
		String phoneNo = bookingData.getTelno();
		String registration = bookingData.getCarReg();
		boolean isRenewal = bookingData.isRenewal();
		String membershipId = bookingData.getMembershipId();
		boolean isNonCardPayment = bookingData.isNonCardPayment();

		if (!isRenewal && !isNonCardPayment && cardholderNameEnabled && !StringUtils.hasText(cardholderName))
		{
			log.error("Invalid input: Cardholder name cannot be empty.");
			return null;
		}
		String frameAncestor = servletUrl.substring(0, servletUrl.lastIndexOf("/"));
		String stripeRedirectUrl = frameAncestor + "/processPaymentCmd?" + "cmd=stripeConfirmation" + "&affiliateId=" + affiliateId 
				+ "&redirectUrl=" + WebUtil.urlEncode(servletUrl) + "&customerGuid=" + guid + "&bookingReference=" + reference + "&startDate="
				+ bookingData.getStartDate() + "&firstName=" + bookingData.getFname()
				+ "&lastName=" + bookingData.getLname() + "&siteId=" + bookingData.getSiteId();
		String paymentIntentId = bookingData.getPaymentIntentId();

		if (isRenewal)
		{
			stripeRedirectUrl =
					frameAncestor + "/send-renewal?token=" + paymentIntentId + "&amount=" + bookingData.getAmount()
							.setScale(2, RoundingMode.HALF_UP) + "&currency=" + bookingData.getCurrency();
		}

		// Update payment intent with metadata if Send Metadata is enabled in Admin
		updatePaymentIntentWithMetadata(credentials, affiliateId, reference, paymentIntentId, emailAddress, phoneNo,
				registration, guid, membershipId, isRenewal);
		bookingData.setRedirectUrl(stripeRedirectUrl);

		PaymentHandlerBean paymentBean = new PaymentHandlerBean();
		paymentBean.setRedirectRequired(true);
		paymentBean.setStripeRedirect(true);
		paymentBean.setCheckoutUrl(stripeRedirectUrl);
		return paymentBean;
	}
	
	private void updatePaymentIntentWithMetadata(StripeCredentials credentials, int affiliateId,
			String reference, String paymentIntentId, String emailAddress, String phoneNo, String registration, String guid, String membershipId,
			boolean isRenewal)
	{
		if (credentials != null && credentials.isSendMetadata())
		{
			Affiliates affiliate = paymentService.fetchAffiliatesById(affiliateId);
			String affiliateCode = affiliate != null ? affiliate.getCode() : "";
			stripePaymentIntentProcessor.updatePaymentIntentWithMetadata(reference, affiliateCode, emailAddress,
					phoneNo, registration, paymentIntentId, affiliateId, guid, membershipId, isRenewal);
		}
	}
	
	@Override
	public SubscriptionBookingData processSubCmd(int affiliateId, String reference, String cmd, String callbackData,
			boolean isCallback, String guid)
	{
		SubscriptionBookingData subscriptionBookingData = null;
		if (cmd.equals("stripeConfirmation"))
		{
			subscriptionBookingData = processStripeConfirmation(affiliateId, reference, guid, callbackData);
		}
		else
		{
			log.info("Command: {}, is not recognised", cmd);
		}
		return subscriptionBookingData;
	}

	private SubscriptionBookingData processStripeConfirmation(int affiliateId, String reference, String guid, String paymentIntentId)
	{
		SubscriptionBookingData bookingData = new SubscriptionBookingData();
		
		PaymentIntent intent = stripePaymentIntentProcessor.getPaymentIntent(affiliateId, paymentIntentId);
		if (intent == null)
		{
			log.info("Payment intent is null for payment intent id: {}", paymentIntentId);
			bookingData.setPaymentFail(true);
			return bookingData;
		}
		
		if ("succeeded".equals(intent.getStatus()) || "processing".equals(intent.getStatus()))
		{
			bookingData.setPaymentIntentId(paymentIntentId);
			bookingData.setPaymentSuccess(true);
			Map<String, String> metaData = intent.getMetadata();
			String emailAddress = !CollectionUtils.isEmpty(metaData) ? metaData.getOrDefault(METADATA_EMAIL, "") : "";
			if (StringUtils.hasText(emailAddress))
			{
				bookingData.setEmail(emailAddress);
			}
			else
			{
				log.info("Could not find email address in meta data for PaymentIntentID {}, email not set for booking", paymentIntentId);
			}
		}
		else
		{
			log.info("Payment intent status not supported for status: {}", intent.getStatus());
			StripeError error = intent.getLastPaymentError();
			if (error != null)
			{
				String errorMessage = error.getMessage();
				log.info("Error with Stripe payment for booking: {} with guid {}. Error code: {}, Decline code: {}, Error message: {}",
						reference, guid, error.getCode(), error.getDeclineCode(), errorMessage);
			}
			bookingData.setPaymentSuccess(false);
		}
		
		return bookingData;
	}

	@Override
	public boolean processAfterBooking(SubscriptionBookingData bookingData)
	{
		String reference = bookingData.getBookingReference();
		int affiliateId = bookingData.getAffiliateId();
		String paymentIntentId = bookingData.getPaymentIntentId();
		PaymentIntent intent = stripePaymentIntentProcessor.getPaymentIntent(affiliateId, paymentIntentId);
		if (intent == null)
		{
			log.info("Payment intent retrieved was somehow null after payment was flagged as success {}", paymentIntentId);
			return false;
		}
		String membershipId = bookingData.getMembershipId();
		return stripePaymentIntentProcessor.updatePaymentIntentWithMembershipId(reference, affiliateId, intent, membershipId);
	}
	
	private boolean updatePaymentTransaction(PaymentIntent intent, int affiliateId, String timezone, Payments payment,
			String reference, int type)
	{
		boolean success = false;
		String paymentIntentId = intent.getId();
		Charge latestCharge = stripePaymentIntentProcessor
				.getCharge(intent.getLatestCharge(), affiliateId, paymentIntentId);
		if (latestCharge == null)
		{
			log.debug("Failed to retrieve charge from Stripe.");
		}
		else
		{
			PaymentTransaction transaction = new PaymentTransaction();
			transaction.setTransactionId(paymentIntentId);
			transaction.setCreatedUpdatedDateTime(DateUtil.epochSecondsToTimestamp(latestCharge.getCreated(),
					timezone));
			
			if (payment != null)
			{
				transaction.setPaymentId(payment.getId());
			}
			
			PartialPayments partialPayment = paymentService.fetchPartialPaymentsByTransactionIdReferenceAndType(
					intent.getId(), reference, type);
			if (partialPayment != null)
			{
				transaction.setPartialPaymentId(partialPayment.getId());
			}
			
			success = paymentService.savePaymentTransaction(transaction);
		}
		
		return success;
	}
	
	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.STRIPE;
	}
}
