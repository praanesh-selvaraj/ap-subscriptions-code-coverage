package com.kmp.aeroparker.subscription.payments.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.credentials.Credentials;
import com.kmp.aeroparker.subscription.payments.dao.PaymentDao;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.CredentialsFactory;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.KlixSession;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPaymentAmount;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPaymentVersion;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PartialPayments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentGatewayTypes;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentTransaction;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsCustomValues;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsProcessedToken;
import com.kmp.aeroparker.subscription.payments.tables.pojos.PaymentsWirecardResponse;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Refunds;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SelectedPaymentGateway;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentService
{
	private final PaymentDao dao;
	private final CredentialsFactory credentialsFactory;
	private final ObjectMapper objectMapper;

	public int fetchSelectedPaymentGatewayTypeByAffiliateAndCarPark(final int affId, final int carParkId)
	{
		int paymentGatewayType = 0;
		if (affId <= 0)
		{
			log.info("Affiliate ID is not greater than 0, payment gateway will not be fetched");
		}
		else
		{
			final SelectedPaymentGateway selectedPaymentGateway = fetchSelectedPaymentGateway(affId, carParkId);
			paymentGatewayType = selectedPaymentGateway != null ? selectedPaymentGateway.getType() : 0;
		}
		return paymentGatewayType;
	}

	/**
	 * Fetch both affiliate and car park(if enabled) gateway using
	 * selected_payment_gateway_view
	 * 
	 * @param affId
	 * @param carParkId
	 * @return car park gateway if enable else affiliate gateway
	 */
	public SelectedPaymentGateway fetchSelectedPaymentGateway(final int affId, final int carParkId)
	{
		log.debug("Fetching for selected payment gateway using carpark ID " + carParkId + " and affiliateId " + affId);
		List<SelectedPaymentGateway> selectedPaymentGatewaysList = new ArrayList<>();
		SelectedPaymentGateway selectedGateway = null;

		if (affId <= 0)
		{
			log.info("Affiliate ID/Carpark ID is not greater than 0, payment gateway will not be fetched");
		}
		else
		{
			selectedPaymentGatewaysList = dao.fetchSelectedPaymentGateway(affId, carParkId);

			for (final SelectedPaymentGateway gateway : selectedPaymentGatewaysList)
			{
				if (gateway.getGatewayType()
						.equals("CARPARK"))
				{
					selectedGateway = gateway;
					break;
				}
				else
				{
					selectedGateway = gateway;
				}
			}
		}
		return selectedGateway;
	}

	public Credentials fetchPaymentCredentials(final int affId, final int carParkId, final PaymentGatewayType type)
	{
		Credentials credentials = null;
		if (affId <= 0)
		{
			log.info("Affiliate ID/Carpark ID is not greater than 0, credentials will not be fetched");
		}
		else
		{
			SelectedPaymentGateway selectedGateway = fetchSelectedPaymentGateway(affId, carParkId);

			if (selectedGateway != null)
			{
				credentials = getCredentials(selectedGateway.getCredentialsJson(), type);
			}
		}
		return credentials;
	}

	@SuppressWarnings("unchecked")
	private <T extends Credentials> T getCredentials(final String credentialsJson, final PaymentGatewayType type)
	{
		Class<?> credentialsClass = credentialsFactory.getInstance(type);
		T credentials = null;
		if (!StringUtil.isEmpty(credentialsJson))
		{
			// Parse the credentials JSON into the correct class type.
			try
			{
				credentials = (T) objectMapper.readValue(HtmlUtils.htmlUnescape(credentialsJson), credentialsClass);
			}
			catch (final IOException e)
			{
				log.error("Error parsing credentials JSON: " + e.getMessage(), e);
			}
		}
		return credentials;
	}

	public PaymentGatewayTypes fetchPaymentGatewayTypesById(final int id)
	{
		PaymentGatewayTypes gatewayType = null;
		if (id <= 0)
		{
			log.info("Payment gateway type ID is not greater than 0, payment gateway type will not be fetched");
		}
		else
		{
			gatewayType = dao.fetchPaymentGatewayTypesById(id);
		}
		return gatewayType;
	}

	public boolean savePayment(final Payments payment)
	{
		boolean saved = false;
		if (payment == null)
		{
			log.info("Payment is null, payment will not be saved");
		}
		else
		{
			saved = dao.savePayment(payment);
			log.debug("Payment is saved successfully");
		}
		return saved;
	}

	public boolean saveCustomValues(final int paymentId, final Map<String, String> customValues)
	{
		boolean saved = false;
		Map<String, Integer> customFields = fetchAllCustomFields();
		List<PaymentsCustomValues> paymentsCustomValuesToSave = new ArrayList<>();
		for (Entry<String, String> customValue : customValues.entrySet())
		{
			String value = customValue.getValue();
			if (!StringUtil.isEmpty(value))
			{
				String key = customValue.getKey();
				int field = customFields.getOrDefault(key, 0);
				if (field != 0)
				{
					PaymentsCustomValues paymentsCustomValues = new PaymentsCustomValues();
					paymentsCustomValues.setField(field);
					paymentsCustomValues.setValue(value);
					paymentsCustomValues.setPaymentId(paymentId);
					paymentsCustomValuesToSave.add(paymentsCustomValues);
				}
			}
		}
		if (!paymentsCustomValuesToSave.isEmpty())
		{
			log.debug("Inserting payment custom values {}", paymentsCustomValuesToSave.toString());
			dao.saveCustomValues(paymentsCustomValuesToSave);
			saved = true;
		}
		return saved;
	}

	public Map<String, String> fetchPaymentCustomValueByPaymentId(final int paymentId)
	{
		Map<String, String> customValues = new HashMap<>();
		if (paymentId > 0)
		{
			List<PaymentsCustomValues> listCustomValues = dao.fetchPaymentCustomValueByPaymentId(paymentId);

			if (!listCustomValues.isEmpty())
			{
				Map<String, Integer> customFields = fetchAllCustomFields();
				customValues.putAll(listCustomValues.stream()
						.collect(Collectors.toMap(x -> customFields.entrySet()
								.stream()
								.filter(entry -> x.getField() == entry.getValue())
								.findFirst()
								.get()
								.getKey(), x -> x.getValue())));
			}
		}
		return customValues;
	}

	private Map<String, Integer> fetchAllCustomFields()
	{
		return dao.fetchAllCustomFields();
	}

	public Payments fetchPaymentByReference(final String bookingReference)
	{
		Payments payment = null;
		if (StringUtil.isEmpty(bookingReference))
		{
			log.info("Booking reference is empty, payment will not be fetched");
		}
		else
		{
			payment = dao.fetchPaymentByReference(bookingReference);
			log.debug("Payment is fetched successfully");
		}
		return payment;
	}

	public boolean insertRefund(final Refunds refund)
	{
		boolean saved = false;
		if (refund == null)
		{
			log.info("Refunds is null, Refunds will not be saved");
		}
		else
		{
			dao.insertRefund(refund);
			saved = true;
			log.debug("Refund is saved successfully");
		}
		return saved;
	}

	public PaymentsProcessedToken fetchProcessedToken(final String token)
	{
		PaymentsProcessedToken paymentToken = null;
		if (StringUtil.isEmpty(token))
		{
			log.info("Token is empty. No payment processed token will be fetched");
		}
		else
		{
			paymentToken = dao.fetchProcessedToken(token);
			log.debug("Payment processed token fetched successfully");
		}
		return paymentToken;
	}

	public Payments createPayment(final String transactionid, final String bookingReference, final String amount, final int type,
			final String timeZone)
	{
		log.debug("Creating payment......");
		final Payments payment = new Payments();
		payment.setId(0);
		payment.setTransactionId(transactionid);
		payment.setType(type);
		payment.setReference(bookingReference);
		payment.setAmount(amount);
		payment.setCreated(Timestamp.valueOf(DateUtil.nowLocalDateTime(timeZone)));
		return payment;
	}

	public boolean insertWirecardResponse(final PaymentsWirecardResponse paymentResponse)
	{
		boolean saved = false;
		if (paymentResponse == null)
		{
			log.info("Payment WirecardResponse is null, PaymentsWirecardResponse will not be saved");
		}
		else
		{
			saved = dao.insertWirecardResponse(paymentResponse);
			log.debug("Payment wirecard eesponse is saved successfully");
		}
		return saved;
	}

	public Payments fetchPaymentById(final int paymentId)
	{
		Payments payments = null;
		if (paymentId > 0)
		{
			payments = dao.fetchPaymentById(paymentId);
		}
		return payments;
	}

	public boolean insertSubscriptionScheduledRecurringPayment(final SubscriptionScheduledRecurringPayment recurringPayment)
	{
		boolean saved = false;
		if (recurringPayment != null)
		{
			saved = dao.insertSubscriptionScheduledRecurringPayment(recurringPayment);
		}
		return saved;
	}
	
	public List<Payments> fetchRecurringPaymentSentByBookingReference(final String bookingReference)
	{
		List<Payments> payments = new ArrayList<>();
		if (!StringUtil.isEmpty(bookingReference))
		{
			payments = dao.fetchRecurringPaymentSentByBookingReference(bookingReference);
		}
		else
		{
			log.info("Unable to fetch payment history as booking reference was null or empty.");
		}
		return payments;
	}

	public boolean saveKlixSession(KlixSession session)
	{
		boolean isSuccess = false;
		if (session != null)
		{
			isSuccess = dao.saveKlixSession(session);
		}
		else
		{
			log.debug("Unable to save KlixSession due to null parameter");
		}
		return isSuccess;
	}

	public KlixSession fetchKlixSessionByReferenceAndAffiliateId(String reference, int affiliateId)
	{
		KlixSession session = null;

		if (!StringUtils.isEmpty(reference) && affiliateId > 0)
		{
			session = dao.fetchKlixSessionByReferenceAndAffiliateId(reference, affiliateId);
		}
		else
		{
			log.debug("Unable to fetch klix session as reference or affiliateId was less than 1 or null");
		}
		return session;
	}

	public Affiliates fetchAffiliatesById(int affiliateId)
	{
		Affiliates affiliates = null;
		if (affiliateId > 0)
		{
			affiliates = dao.fetchAffiliatesbyId(affiliateId);
		}
		else
		{
			log.debug("Unable to fetch affiliate as affiliateId was less than 1");
		}
		return affiliates;
	}
	
	public PartialPayments fetchPartialPaymentsByTransactionIdReferenceAndType(final String transactionId,
			final String reference, final int type)
	{
		PartialPayments payment = null;
		if (StringUtils.hasText(transactionId) && StringUtil.hasText(reference) && type > 0)
		{
			try
			{
				payment = dao.fetchPartialPaymentsByTransactionIdReferenceAndType(transactionId, reference, type);
			}
			catch (DataAccessException | SQLException e)
			{
				log.error("Error trying to fetch partial payment of type {} with transaction id: {}"
						+ " and reference {} : {}", type, transactionId, reference, e.getMessage(), e);
			}
		}
		else
		{
			log.debug("Unable to fetch partial payment as either the transaction id: {}, reference: {} or type {}"
					+ " was invalid.", transactionId, reference, type);
		}

		return payment;
	}
	
	public boolean savePartialPayment(PartialPayments partialPayment)
	{
		boolean success = false;
		if (partialPayment != null)
		{
			success = dao.savePartialPayment(partialPayment);
		}
		else
		{
			log.debug("Unable to save partial payment as the object was null");
		}
		return success;
	}
	
	public boolean savePartialPaymentAmount(PartialPaymentAmount partialPaymentAmount)
	{
		boolean success = false;
		if (partialPaymentAmount != null)
		{
			success = dao.savePartialPaymentAmount(partialPaymentAmount);
		}
		else
		{
			log.debug("Unable to save partial payment amount as the object was null.");
		}
		if (success)
		{
			savePartialPaymentVersion(partialPaymentAmount.getReference(), false);
		}
		return success;
	}
	
	public boolean savePartialPaymentVersion(String reference, boolean isBasicAmend)
	{
		boolean success = false;
		if (StringUtils.hasText(reference))
		{
			PartialPaymentVersion partialPaymentVersion =
					createPartialPaymentVersion(reference, isBasicAmend);
			success = dao.savePartialPaymentVersion(partialPaymentVersion);
		}
		else
		{
			log.debug("Reference was empty, could not save partial payment version");
		}
		
		return success;
	}
	
	private PartialPaymentVersion createPartialPaymentVersion(String reference, boolean isBasicAmend)
	{
		PartialPaymentVersion partialPaymentVersion = new PartialPaymentVersion();

		if (StringUtils.hasText(reference))
		{
			partialPaymentVersion.setReference(reference);

			// if the version is being saved on basic amend there shouldn't be a partial payment amount id
			Integer partialPaymentAmountId = null;
			if (!isBasicAmend)
			{
				partialPaymentAmountId = fetchLatestPartialPaymentAmountIdByReference(reference);
			}
			partialPaymentVersion.setPartialPaymentAmountId(partialPaymentAmountId);

			// increment from the latest version, if not record found service returns 0 so will default to 1
			int version = fetchLatestPartialPaymentVersionByReference(reference);
			partialPaymentVersion.setVersion(version + 1);
		}
		else
		{
			log.debug("Reference is empty when trying to create partial payment version");
		}
		return partialPaymentVersion;
	}
	
	public int fetchLatestPartialPaymentAmountIdByReference(String reference)
	{
		int partialPaymentAmountId = 0;
		if (StringUtils.hasText(reference))
		{
			partialPaymentAmountId = dao.fetchLatestPartialPaymentAmountIdByReference(reference);
		}
		else
		{
			log.debug("Reference was empty, could not fetch latest partial payment amount id");
		}
		return partialPaymentAmountId;
	}
	
	public int fetchLatestPartialPaymentVersionByReference(String reference)
	{
		int latestVersion = 0;
		if (StringUtils.hasText(reference))
		{
			latestVersion = dao.fetchLatestPartialPaymentVersionByReference(reference);
		}
		else
		{
			log.debug("Reference was empty, could not save partial payment version");
		}
		return latestVersion;
	}
	
	public List<PartialPayments> fetchAllPartialPaymentsByReferenceAndType(final String reference, final int type)
	{
		List<PartialPayments> listOfPayments = new ArrayList<>();

		if (StringUtils.hasText(reference) && type > 0)
		{
			try
			{
				listOfPayments = dao.fetchAllPartialPaymentsByReferenceAndType(reference, type);
			}
			catch (DataAccessException | SQLException e)
			{
				log.error("Unable to fetch list of partial payments of type: {} with reference {}: {}", type, reference, e.getMessage(), e);
			}
		}
		else
		{
			log.debug("Unable to fetch partial payments as either the reference: {} or type: {}", reference, type);
		}

		return listOfPayments;
	}

	public boolean savePaymentCustomValues(Map<String, String> customValues, int paymentId, int partialPaymentId)
	{
		boolean success = false;
		if (!customValues.isEmpty() && paymentId > 0 && partialPaymentId > 0)
		{
			for (Entry<String, String> customValue : customValues.entrySet())
			{
				if (StringUtils.hasText(customValue.getValue()))
				{
					PaymentsCustomValues paymentsCustomValues = new PaymentsCustomValues();
					paymentsCustomValues.setValue(customValue.getValue());
					paymentsCustomValues.setPaymentId(paymentId);
					paymentsCustomValues.setPartialPaymentId(partialPaymentId);

					dao.savePaymentCustomValues(paymentsCustomValues, customValue.getKey());
				}
			}
			success = true;
		}
		else
		{
			log.debug("Unable to save payment custom values as either the customValues map is invalid, the payment id is less than 1: {}, or the partialPaymentId is less than 1: {}.",
					paymentId, partialPaymentId);
		}
		return success;
	}
	
	public boolean setAmountRefunded(final Payments payment, final BigDecimal amount)
	{
		boolean saved = false;
		if (payment == null)
		{
			log.info("Payment is null, payment will not be saved");
		}
		else
		{
			BigDecimal amountRefunded = new BigDecimal(payment.getAmountRefunded()).setScale(2, RoundingMode.HALF_UP);
			amountRefunded = amountRefunded.add(amount.setScale(2, RoundingMode.HALF_UP));
			payment.setAmountRefunded(amountRefunded.toPlainString());
			saved = dao.savePayment(payment);
			log.debug("Payment amount refunded updated successfully");
		}
		return saved;
	}
	
	public boolean savePaymentTransaction(PaymentTransaction transaction)
	{
		boolean success = false;
		
		if (transaction == null)
		{
			log.debug("Failed to save to payment transaction because given object was null.");
		}
		else
		{
			success = dao.savePaymentTransaction(transaction);
		}
		
		return success;
	}
}