package com.kmp.aeroparker.subscription.payments.refund.handler;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.RefundProcessorFactory;
import com.kmp.aeroparker.subscription.payments.interfaces.RefundProcessor;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class RefundHandler
{
	private final PaymentService service;
	private final RefundProcessorFactory factory;

	public boolean processRefund(final SubscriptionBookingData bookingData)
	{
		String bookingReference = bookingData.getBookingReference();
		BigDecimal amount = bookingData.getAmount();
		int affId = bookingData.getAffiliateId();
		boolean status = false;
		if (StringUtil.isEmpty(bookingReference))
		{
			return false;
		}
		log.debug("Loading payment for reference - {}", bookingReference);
		Payments payment = service.fetchPaymentByReference(bookingReference);

		if (payment != null)
		{
			BigDecimal refundableAmount = getRefundableAmount(payment);
			if (amount.compareTo(refundableAmount) > 0)
			{// The amount requested exceeds the amount that can be refunded,
				// set an error message for the user
				// and return to the original servlet.
				status = false;
			}
			else
			{
				RefundProcessor refundProcessor = factory.getInstance(PaymentGatewayType.getType(payment.getType()));
				if (bookingData.isPartialPaymentsEnabled())
				{
					status = refundProcessor.processPartial(affId, payment, amount, bookingData.getTimeZone());
				}
				else
				{
					status = refundProcessor.process(affId, payment, amount, bookingData.getTimeZone());
				}
			}
		}
		return status;
	}

	private BigDecimal getRefundableAmount(final Payments payment)
	{
		return new BigDecimal(payment.getAmount()).subtract(new BigDecimal(payment.getAmountRefunded()));
	}
}