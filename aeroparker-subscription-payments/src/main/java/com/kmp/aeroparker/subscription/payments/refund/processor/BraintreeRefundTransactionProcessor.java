package com.kmp.aeroparker.subscription.payments.refund.processor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.ValidationError;
import com.braintreegateway.ValidationErrorCode;
import com.braintreegateway.ValidationErrors;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BraintreeRefundTransactionProcessor
{
	public Result<Transaction> processFailedRefund(Result<Transaction> result, final Payments payment, final BraintreeGateway gateway)
	{
		ValidationErrors errors = result.getErrors();
		List<ValidationError> validationErrors = errors.getAllDeepValidationErrors();
		if (validationErrors.size() > 0)
		{
			// Log the errors from Braintree.
			for (ValidationError error : validationErrors)
			{
				log.info("Braintree refund error for " + payment.getReference() + ": " + error.getMessage() + " (" + error.getCode() + ")");
			}
			if (validationErrors.stream()
					.anyMatch(p -> p.getCode() == ValidationErrorCode.TRANSACTION_CANNOT_REFUND_UNLESS_SETTLED))
			{
				// The original transaction was made before it was
				// settled so the customer had not paid anything
				// yet,
				// for now void the original transaction which means
				// the customer loses no money at all.
				log.info("The transaction has not been settled attempting to void Braintree transaction for " + payment.getReference());
				result = gateway.transaction()
						.voidTransaction(payment.getTransactionId());
			}
		}
		return result;
	}

	public Result<Transaction> processRefund(final BraintreeGateway gateway, final String transactionId, final BigDecimal amount)
	{
		return gateway.transaction()
				.refund(transactionId, amount);
	}
}