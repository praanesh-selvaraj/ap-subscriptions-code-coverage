package com.kmp.aeroparker.subscription.payments.refund.processor;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.interfaces.RefundProcessor;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequestBuilder;
import com.kmp.aeroparker.subscription.payments.klix.KlixRequestHandler;
import com.kmp.aeroparker.subscription.payments.klix.KlixResponse;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KlixRefundProcessor extends AbstractRefundProcessor implements RefundProcessor
{
	private static final String APPROVED_REFUND = "success";
	private static final String PENDING_REFUND = "pending_refund";

	private final KlixRequestHandler requestHandler;
	private final KlixRequestBuilder requestBuilder;

	public KlixRefundProcessor(PaymentService paymentService, KlixRequestHandler requestHandler,
			KlixRequestBuilder requestBuilder, KlixRequestBuilder requestBuilder2)
	{
		super(paymentService);
		this.requestHandler = requestHandler;
		this.requestBuilder = requestBuilder2;
	}

	@Override
	public boolean process(int affId, Payments payment, BigDecimal amount, String timeZone)
	{
		boolean status = false;

		if (payment != null)
		{
			log.info("Attempting refund for klix payment {}", payment.getId());

			if (amount.compareTo(BigDecimal.ZERO) > 0)
			{
				KlixCredentials credentials =
						(KlixCredentials) paymentService.fetchPaymentCredentials(affId, 0, PaymentGatewayType.KLIX);

				if (credentials != null)
				{
					JsonObject request = requestBuilder.generateJsonForRefund(amount);
					KlixResponse response = requestHandler.sendRefundRequest(credentials, payment.getTransactionId(),
							payment.getReference(), affId, request);

					if (response != null)
					{
						String paymentStatus = response.getStatus();
						if (StringUtil.isEqual(APPROVED_REFUND, paymentStatus)
								|| StringUtil.isEqual(PENDING_REFUND, paymentStatus))
						{
							status = processRefund(payment, amount, timeZone);
						}
					}
				}
			}
			else if (amount.compareTo(BigDecimal.ZERO) == 0)
			{
				log.debug("The amount is 0, no refund needed returning true.");
				status = true;
			}
			else
			{
				log.debug("The refund amount was negative");
			}
		}
		else
		{
			log.info("Klix refund request didn't have a Payment");
		}
		return status;
	}

	@Override
	public PaymentGatewayType getType()
	{
		return PaymentGatewayType.KLIX;
	}
}
