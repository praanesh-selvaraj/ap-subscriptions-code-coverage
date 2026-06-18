package com.kmp.aeroparker.application.payment.handler;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.processor.SubscriptionBookingProcessor;
import com.kmp.aeroparker.subscription.payments.factory.PaymentGatewayFactory;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.refund.handler.RefundHandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Controller
public class PaymentHandler
{
	private final PaymentGatewayFactory paymentGatewayFactory;
	private final SubscriptionBookingProcessor bookingProcessor;
	private final RefundHandler refundHandler;

	public boolean processPayment(final SubscriptionBookingData bookingData, final Basket basket)
	{
		log.info("Inside processing payment");
		boolean status = true;
		boolean isPaymentRequired = basket.getGrandTotal().compareTo(BigDecimal.ZERO) > 0;
		if (isPaymentRequired && !paymentGatewayFactory.processPayment(bookingData))
		{
			log.debug("Processing payment failed");
			status = false;
		}
		else
		{
			if (!bookingProcessor.process(bookingData, basket))
			{
				log.debug("Processing booking failed");
				status = false;
			}
		}

		// For redirect PSPs like Klix and Stripe, we do enter the refundHandler below (and do nothing because we find no payment to refund)
		// as the payment requires a redirect to succeed. Once the payment has succeeded, we come here again with the status being true.
		if (!status)
		{
			if (refundHandler.processRefund(bookingData))
			{
				log.debug("Refund processed successfully");
			}
			else
			{
				log.debug("Refund failed");
			}
			status = false;
		}
		else
		{
			if (isPaymentRequired)
			{
				paymentGatewayFactory.processAfterBooking(bookingData);
			}
		}
		return status;
	}

	public Map<String, Object> setUpTransaction(final PaymentGatewayParameters paymentHandlerParams)
	{
		return paymentGatewayFactory.setUpTransaction(paymentHandlerParams);
	}

	public PaymentHandlerBean processRedirectPayment(SubscriptionBookingData bookingData)
	{
		return paymentGatewayFactory.processRedirectPayment(bookingData);
	}

	public SubscriptionBookingData processSubCmd(int affiliateId, String reference, String cmd, String callbackData,
			boolean isCallback, String guid)
	{
		return paymentGatewayFactory.processSubCmd(affiliateId, reference, cmd, callbackData, isCallback, guid);
	}
}