package com.kmp.aeroparker.application.processor;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.ProcessorType;
import com.kmp.aeroparker.application.model.interfaces.IProcessor;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingPayment;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionBookingPaymentProcessor implements IProcessor
{
	private final PaymentService paymentService;
	private final BookingService bookingService;

	public void process(int bookingId, String bookingReference)
	{
		Optional.ofNullable(paymentService.fetchPaymentByReference(bookingReference))
				.ifPresent(payment ->
				{
					SubscriptionBookingPayment bookingPayment = new SubscriptionBookingPayment();
					bookingPayment.setPaymentId(payment.getId());
					bookingPayment.setSubBookingId(bookingId);
					if (bookingService.insertSubscriptionBookingPayment(bookingPayment))
					{
						log.debug("Linking table Subscription Booking Payment updated successfully with values, subscription booking ID: {} and Payment ID: {}",
								bookingId, payment.getId());
					}
				});
	}

	@Override
	public ProcessorType getType()
	{
		return ProcessorType.BOOKING_PAYMENT;
	}
}