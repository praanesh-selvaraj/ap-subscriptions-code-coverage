package com.kmp.aeroparker.subscription.payments.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHandler;
import com.kmp.aeroparker.subscription.payments.model.PaymentGatewayParameters;
import com.kmp.aeroparker.subscription.payments.model.PaymentHandlerBean;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentGatewayFactory
{
	private final PaymentService paymentService;
	private final PaymentHandlerFactory paymentHandlerFactory;

	public Map<String, Object> setUpTransaction(final PaymentGatewayParameters paymentHandlerParams)
	{
		Map<String, Object> paymentGatewayParams = new HashMap<>();
		Affiliates affiliate = paymentHandlerParams.getAffiliate();
		int affiliateId = affiliate.getId();
		int paymentGatewayType = paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(affiliateId, 0);
		if (paymentGatewayType != 0)
		{
			IPaymentHandler paymentHandler = getHandler(paymentGatewayType);

			if (paymentHandler != null)
			{
				paymentGatewayParams = paymentHandler.setUpTransaction(paymentHandlerParams);
			}
			else
			{
				log.info("Payment gateway not supported");
			}
		}
		return paymentGatewayParams;
	}

	public boolean processPayment(final SubscriptionBookingData bookingData)
	{
		boolean status = false;
		int affiliateId = bookingData.getAffiliateId();
		int paymentGatewayType = Optional.ofNullable(bookingData.getPaymentGatewayType() > 0 ? Integer.valueOf(bookingData.getPaymentGatewayType()) : null)
				.orElseGet(() -> paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(affiliateId, bookingData.getCarParkId()));

		if (paymentGatewayType != 0)
		{
			IPaymentHandler paymentHandler = getHandler(paymentGatewayType);

			if (paymentHandler != null)
			{
				status = paymentHandler.processPayment(bookingData);
			}
			else
			{
				log.info("Payment gateway not supported");
			}
		}
		log.info("Payment completed, status: {}", status);
		return status;
	}
	
	public PaymentHandlerBean processRedirectPayment(final SubscriptionBookingData bookingData)
	{
		PaymentHandlerBean paymentHandlerBean = null;
		int affiliateId = bookingData.getAffiliateId();
		int paymentGatewayType = Optional.ofNullable(bookingData.getPaymentGatewayType() > 0 ? Integer.valueOf(bookingData.getPaymentGatewayType()) : null)
				.orElseGet(() -> paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(affiliateId, bookingData.getCarParkId()));

		if (paymentGatewayType != 0)
		{
			IPaymentHandler paymentHandler = getHandler(paymentGatewayType);

			if (paymentHandler != null)
			{
				paymentHandlerBean = paymentHandler.processRedirectPayment(bookingData);
			}
			else
			{
				log.info("Payment gateway not supported");
			}
		}
		else
		{
			log.debug("Unable to process payment redirect as payment gateway type id was less than 1");
		}
		return paymentHandlerBean;
	}

	public SubscriptionBookingData processSubCmd(int affiliateId, String reference, String cmd, String callbackData,
			boolean isCallback, String guid)
	{
		SubscriptionBookingData bookingData = null;
		int paymentGatewayType = paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(affiliateId, 0);

		if (paymentGatewayType != 0)
		{
			IPaymentHandler paymentHandler = getHandler(paymentGatewayType);

			if (paymentHandler != null)
			{
				bookingData = paymentHandler.processSubCmd(affiliateId, reference, cmd , callbackData, isCallback, guid);
			}
			else
			{
				log.info("Payment gateway not supported");
			}
		}
		else
		{
			log.debug("Unable to process payment sub cmd as payment gateway type id was less than 1");
		}
		return bookingData;
	}

	public boolean processAfterBooking(SubscriptionBookingData bookingData)
	{
		boolean status = false;
		int affiliateId = bookingData.getAffiliateId();
		int paymentGatewayType = Optional.ofNullable(bookingData.getPaymentGatewayType() > 0 ? Integer.valueOf(bookingData.getPaymentGatewayType()) : null)
				.orElseGet(() -> paymentService.fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(affiliateId, bookingData.getCarParkId()));

		if (paymentGatewayType != 0)
		{
			IPaymentHandler paymentHandler = getHandler(paymentGatewayType);

			if (paymentHandler != null)
			{
				status = paymentHandler.processAfterBooking(bookingData);
			}
			else
			{
				log.info("Payment gateway not supported");
			}
		}
		log.info("Post booking payment processing completed with status: {}", status);
		return status;
	}

	private IPaymentHandler getHandler(final int paymentGatewayType)
	{
		return paymentHandlerFactory.getInstance(PaymentGatewayType.getType(paymentGatewayType));
	}
}