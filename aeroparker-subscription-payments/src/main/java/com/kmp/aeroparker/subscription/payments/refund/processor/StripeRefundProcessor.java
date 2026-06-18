package com.kmp.aeroparker.subscription.payments.refund.processor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.RefundProcessor;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.stripe.StripePaymentIntentProcessor;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPaymentAmount;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Refunds;
import com.stripe.model.Refund;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StripeRefundProcessor extends AbstractRefundProcessor implements RefundProcessor
{
	private final StripePaymentIntentProcessor stripePaymentIntentProcessor;

	public StripeRefundProcessor(PaymentService paymentService, StripePaymentIntentProcessor stripePaymentIntentProcessor)
	{
		super(paymentService);
		this.stripePaymentIntentProcessor = stripePaymentIntentProcessor;
	}

	@Override
	public boolean processPartial(int affiliateId, Payments parentPayment, BigDecimal amount, String timeZone)
	{
		boolean success = false;
		log.info("Processing Stripe partial refund for reference {}", parentPayment.getReference());

		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) > 0)
		{
			List<PartialPayments> partialPayments = paymentService.fetchAllPartialPaymentsByReferenceAndType(
					parentPayment.getReference(), PaymentGatewayType.STRIPE.getId());

			// If for some reason we don't have any partial payments, then default back to using the parent payment.
			if (partialPayments.isEmpty())
			{
				success = process(affiliateId, parentPayment, amount, timeZone);
			}
			else
			{
				success = processPartialPaymentsList(parentPayment, affiliateId, partialPayments, amount);
			}

			if (success)
			{
				log.info("Stripe refund successful for {}", parentPayment.getReference());
				completeRefundProcessing(parentPayment, amount, true, timeZone);
			}
		}
		else if (amount.compareTo(BigDecimal.ZERO) == 0)
		{
			log.debug("The amount is 0, no refund needed returning true.");
			success = true;
		}

		return success;
	}
	
	@Override
	public boolean process(int affiliateId, Payments payment, BigDecimal amount, String timeZone)
	{
		log.info("Processing Stripe refund for reference {} and transaction ID {}", payment.getReference(), payment.getTransactionId());
		boolean success = false;
		if (amount.compareTo(BigDecimal.ZERO) > 0)
		{
			Refund refund = stripePaymentIntentProcessor.stripeRefundPayment(affiliateId, amount, payment.getTransactionId());

			if (refund != null && "succeeded".equals(refund.getStatus()))
			{
				completeRefundProcessing(payment, amount, false, timeZone);
				success = true;
			}
		}
		else
		{
			log.debug("Refund not required as amount is zero or less.");
			success = true;
		}
		return success;
	}
	
	private boolean processPartialPaymentsList(Payments parentPayment, int affiliateId,
			List<PartialPayments> partialPayments, BigDecimal amount)
	{
		boolean success = false;
		BigDecimal amountLeftToBeRefunded = amount;

		for (PartialPayments partialPayment : partialPayments)
		{
			if (amountLeftToBeRefunded.compareTo(BigDecimal.ZERO) == 0)
			{
				break;
			}

			BigDecimal partialPaymentAmount = partialPayment.getAmount();
			BigDecimal partialPaymentRefundedAmount = new BigDecimal(partialPayment.getAmountRefunded());
			if (partialPaymentAmount.compareTo(partialPaymentRefundedAmount) == 0)
			{
				continue;
			}

			int partialPaymentId = partialPayment.getId();
			// Get the amount left that can be refunded on the partial payment.
			BigDecimal partialAmountLeftToBeRefunded = partialPaymentAmount.subtract(partialPaymentRefundedAmount);
			// If the amount left to be refunded is more than what can be refunded on the partial payment,
			// get the value of what can be refunded, else refund the full amount left.
			BigDecimal partialRefundAmount =
					partialAmountLeftToBeRefunded.compareTo(amountLeftToBeRefunded) > 0 ? amountLeftToBeRefunded
							: partialAmountLeftToBeRefunded;
			log.debug("Refunding: {}, from partial payment: {}", partialRefundAmount.toPlainString(), partialPaymentId);

			// Refund the amount.
			Refund refund = stripePaymentIntentProcessor.stripeRefundPayment(affiliateId, partialRefundAmount,
					partialPayment.getTransactionId());

			if (refund != null && "succeeded".equals(refund.getStatus()))
			{
				// The refund was successful insert a refund in the DB and update the payment.
				processPartialPaymentRefund(partialPayment, partialRefundAmount);
				amountLeftToBeRefunded = amountLeftToBeRefunded.subtract(partialRefundAmount);
				success = true;
			}
			else
			{
				log.info("Refund for transaction: {} failed.", partialPayment.getTransactionId());
				break;
			}
		}
		return success;
	}
	
	private void completeRefundProcessing(Payments payment, BigDecimal amount, boolean isPartialPaymentRefund, String timeZone)
	{
		Refunds refund = new Refunds();
		refund.setId(0);
		refund.setPaymentId(payment.getId());
		refund.setType(payment.getType());
		refund.setReference(payment.getReference());
		refund.setAmount(amount.toString());
		refund.setCreated(Timestamp.valueOf(DateUtil.nowLocalDateTime(timeZone)));
		if (paymentService.insertRefund(refund))
		{
			paymentService.setAmountRefunded(payment, amount);

			if (isPartialPaymentRefund)
			{
				PartialPaymentAmount partialPaymentAmount = new PartialPaymentAmount();
				partialPaymentAmount.setAmount("-" + amount.toString());
				partialPaymentAmount.setReference(payment.getReference());
				partialPaymentAmount.setRefundId(refund.getId());
				paymentService.savePartialPaymentAmount(partialPaymentAmount);
			}
		}
		else
		{
			log.error("Error inserting refund for {}, the refund was successful with Stripe but the insert to the DB failed", payment.getReference());
		}
	}

	private void processPartialPaymentRefund(PartialPayments partialPayment, BigDecimal amount)
	{
		partialPayment.setAmountRefunded(new BigDecimal(partialPayment.getAmountRefunded()).add(amount).toString());
		paymentService.savePartialPayment(partialPayment);
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.STRIPE;
	}
}
