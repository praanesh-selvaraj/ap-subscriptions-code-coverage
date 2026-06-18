package com.kmp.aeroparker.subscription.payments.processor.history;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.IPaymentHistoryProcessor;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionPaymentHistory;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class WirecardPaymentHistoryProcessor implements IPaymentHistoryProcessor
{
	private final PaymentService paymentService;

	@Override
	public SubscriptionPaymentHistory loadHistory(int affiliateId, int carParkId, String bookingReference)
	{
		log.debug("Fetching wirecard payment history for booking: {}", bookingReference);
		List<Payments> listOfRecurringPayments = paymentService.fetchRecurringPaymentSentByBookingReference(bookingReference);
		SubscriptionPaymentHistory paymentHistory = new SubscriptionPaymentHistory();

		for (Payments payment : listOfRecurringPayments)
		{
			LocalDateTime createdDate = DateUtil.timestampToLocalDateTime(payment.getCreated());
			BigDecimal amount = new BigDecimal(payment.getAmount());
			paymentHistory.put(createdDate, amount);
		}

		return paymentHistory;
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.WIRECARD;
	}
}
