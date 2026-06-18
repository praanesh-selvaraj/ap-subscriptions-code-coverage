package com.kmp.aeroparker.application.payment.handler;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.CarParkService;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.PaymentHistoryProcessorFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHistoryProcessor;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionPaymentHistory;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentHistoryHandler
{
	private final PaymentService paymentService;
	private final CarParkService carParkService;
	private final PaymentHistoryProcessorFactory processorFactory;

	public SubscriptionPaymentHistory processHistory(SubscriptionAllBookingData bookingData, int affiliateId, int siteId)
	{
		log.debug("Processing payment history for booking {}.", bookingData.getSubscriptionReference());
		SubscriptionPaymentHistory paymentHistory = null;
		Payments payment = paymentService.fetchPaymentByReference(bookingData.getSubscriptionReference());
		
		if (payment == null)
		{
			log.debug("No payment record found for the booking.");
		}
		else
		{
			PaymentGatewayType type = PaymentGatewayType
					.getType(payment.getType());
			if (type != null)
			{
				IPaymentHistoryProcessor paymentHistoryProcesor = processorFactory.getInstance(type);
				if (paymentHistoryProcesor == null)
				{
					log.debug("Payment type is not supported for payment history.");
					return null;
				}
				int carParkId = carParkService.fetchCarParkIdByNameAndSiteId(bookingData.getCarparkName(), siteId);
				paymentHistory = paymentHistoryProcesor.loadHistory(affiliateId, carParkId, bookingData.getSubscriptionReference());
			}
			else
			{
				log.debug("Payment type is not supported for the subscriptions web app.");
			}
		}
		
		return paymentHistory;
	}
}
