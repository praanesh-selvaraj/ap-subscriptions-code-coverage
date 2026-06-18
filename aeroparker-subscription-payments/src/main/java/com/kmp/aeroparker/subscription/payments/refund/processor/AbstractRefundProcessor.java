package com.kmp.aeroparker.subscription.payments.refund.processor;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Refunds;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public abstract class AbstractRefundProcessor
{
	protected final PaymentService paymentService;

	protected boolean processRefund(final Payments payment, final BigDecimal amount, final String timeZone)
	{
		boolean completed = false;
		// Create a refund.
		Refunds refund = new Refunds();
		refund.setPaymentId(payment.getId());
		refund.setType(payment.getType());
		refund.setCreated(DateUtil.localDateTimeToTimestamp(DateUtil.nowLocalDateTime(timeZone)));
		refund.setReference(payment.getReference());
		refund.setAmount(amount.toPlainString());
		refund.setNotes("");
		// Attempt to insert it.
		if (paymentService.insertRefund(refund))
		{
			// The refund insert was successful, update the amount refunded on
			// the payment.
			payment.setAmountRefunded(amount.toPlainString());
			completed = paymentService.savePayment(payment);
		}
		return completed;
	}
}