package com.kmp.aeroparker.subscription.payments.dao;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;

import com.kmp.aeroparker.subscription.payments.Tables;
import com.kmp.aeroparker.subscription.payments.tables.daos.PaymentGatewayTypesDao;
import com.kmp.aeroparker.subscription.payments.tables.daos.PaymentsCustomFieldsDao;
import com.kmp.aeroparker.subscription.payments.tables.daos.PaymentsCustomValuesDao;
import com.kmp.aeroparker.subscription.payments.tables.daos.PaymentsDao;
import com.kmp.aeroparker.subscription.payments.tables.daos.PaymentsProcessedTokenDao;
import com.kmp.aeroparker.subscription.payments.tables.daos.RefundsDao;
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
import com.kmp.aeroparker.subscription.payments.tables.records.PaymentTransactionRecord;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class PaymentDao
{
	private final DSLContext dsl;

	public List<SelectedPaymentGateway> fetchSelectedPaymentGateway(final int affiliateId, final int carParkId)
	{
		return dsl.selectFrom(Tables.SELECTED_PAYMENT_GATEWAY)
				.where(Tables.SELECTED_PAYMENT_GATEWAY.AFFILIATEID.eq(affiliateId)
						.or(Tables.SELECTED_PAYMENT_GATEWAY.CARPARKID.eq(carParkId)))
				.fetchInto(SelectedPaymentGateway.class);
	}

	public PaymentGatewayTypes fetchPaymentGatewayTypesById(final int id)
	{
		return new PaymentGatewayTypesDao(dsl.configuration()).fetchById(id)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public Map<String, Integer> fetchAllCustomFields()
	{
		return new PaymentsCustomFieldsDao(dsl.configuration()).findAll()
				.stream()
				.collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(field -> field.getField()))))
				.stream()
				.collect(Collectors.toMap(field -> field.getField(), id -> id.getId()));
	}

	public void saveCustomValues(final List<PaymentsCustomValues> paymentsCustomValues)
	{
		PaymentsCustomValuesDao paymentsCustomValuesDao = new PaymentsCustomValuesDao(dsl.configuration());
		paymentsCustomValuesDao.insert(paymentsCustomValues);
	}

	public boolean savePayment(final Payments payment)
	{
		return payment.save(Tables.PAYMENTS, dsl);
	}

	public Payments fetchPaymentByReference(final String bookingReference)
	{
		return new PaymentsDao(dsl.configuration()).fetchByReference(bookingReference)
				.stream()
				.findAny()
				.orElse(null);
	}

	public void insertRefund(final Refunds refund)
	{
		RefundsDao refundsDao = new RefundsDao(dsl.configuration());
		refundsDao.insert(refund);
	}

	public PaymentsProcessedToken fetchProcessedToken(final String token)
	{
		return new PaymentsProcessedTokenDao(dsl.configuration()).fetchByToken(token)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public boolean insertWirecardResponse(final PaymentsWirecardResponse paymentResponse)
	{
		return paymentResponse.save(Tables.PAYMENTS_WIRECARD_RESPONSE, dsl);
	}

	public List<PaymentsCustomValues> fetchPaymentCustomValueByPaymentId(final int paymentId)
	{
		return new PaymentsCustomValuesDao(dsl.configuration()).fetchByPaymentid(paymentId);
	}

	public Payments fetchPaymentById(final int paymentId)
	{
		return new PaymentsDao(dsl.configuration()).fetchById(paymentId)
				.stream()
				.findFirst()
				.orElse(null);
	}

	public boolean insertSubscriptionScheduledRecurringPayment(final SubscriptionScheduledRecurringPayment recurringPayment)
	{
		return recurringPayment.save(Tables.SUBSCRIPTION_SCHEDULED_RECURRING_PAYMENT, dsl);
	}
	
	public List<Payments> fetchRecurringPaymentSentByBookingReference(final String bookingReference)
	{
		return dsl.selectFrom(Tables.PAYMENTS.rightJoin(Tables.SUBSCRIPTION_RECURRING_PAYMENT_SENT)
				.on(Tables.PAYMENTS.ID.eq(Tables.SUBSCRIPTION_RECURRING_PAYMENT_SENT.PAYMENT_ID))
				.leftJoin(Tables.SUBSCRIPTION_SCHEDULED_RECURRING_PAYMENT)
				.on(Tables.SUBSCRIPTION_RECURRING_PAYMENT_SENT.SCHEDULED_PAYMENT_ID
						.eq(Tables.SUBSCRIPTION_SCHEDULED_RECURRING_PAYMENT.ID)))
				.fetchInto(Payments.class);
	}

	public boolean saveKlixSession(KlixSession session)
	{
		return session.save(Tables.KLIX_SESSION, dsl);
	}

	public KlixSession fetchKlixSessionByReferenceAndAffiliateId(String reference, int affiliateId)
	{
		return dsl.selectFrom(Tables.KLIX_SESSION)
				.where(Tables.KLIX_SESSION.AFFILIATE_ID.eq(affiliateId))
				.and(Tables.KLIX_SESSION.BOOKING_REFERENCE.eq(reference))
				.orderBy(Tables.KLIX_SESSION.ID.desc())
				.limit(1)
				.fetchOneInto(KlixSession.class);
	}

	public Affiliates fetchAffiliatesbyId(int affiliateId)
	{
		return dsl.selectFrom(Tables.AB_AFFILIATES)
				.where(Tables.AB_AFFILIATES.ID.eq(affiliateId))
				.fetchOneInto(Affiliates.class);
	}
	
	public PartialPayments fetchPartialPaymentsByTransactionIdReferenceAndType(final String transactionId,
			final String reference, final int type) throws DataAccessException, SQLException
	{
		return dsl.selectFrom(Tables.PARTIAL_PAYMENTS)
				.where(Tables.PARTIAL_PAYMENTS.TRANSACTION_ID.eq(transactionId))
				.and(Tables.PARTIAL_PAYMENTS.REFERENCE.eq(reference))
				.and(Tables.PARTIAL_PAYMENTS.TYPE.eq(type))
				.fetchOneInto(PartialPayments.class);
	}
	
	public boolean savePartialPayment(PartialPayments partialPayment)
	{
		return partialPayment.save(Tables.PARTIAL_PAYMENTS, dsl);
	}

	public boolean savePartialPaymentAmount(PartialPaymentAmount partialPaymentAmount)
	{
		return partialPaymentAmount.save(Tables.PARTIAL_PAYMENT_AMOUNT, dsl);
	}

	public boolean savePartialPaymentVersion(PartialPaymentVersion partialPaymentVersion)
	{
		return partialPaymentVersion.save(Tables.PARTIAL_PAYMENT_VERSION, dsl);
	}

	public int fetchLatestPartialPaymentAmountIdByReference(String reference)
	{
		return dsl.select(Tables.PARTIAL_PAYMENT_AMOUNT.ID)
				.from(Tables.PARTIAL_PAYMENT_AMOUNT)
				.where(Tables.PARTIAL_PAYMENT_AMOUNT.REFERENCE.eq(reference))
				.orderBy(Tables.PARTIAL_PAYMENT_AMOUNT.ID.desc())
				.limit(1)
				.fetchOptionalInto(Integer.class)
				.orElse(0);
	}
	
	public int fetchLatestPartialPaymentVersionByReference(String reference)
	{
		return dsl.select(Tables.PARTIAL_PAYMENT_VERSION.VERSION)
				.from(Tables.PARTIAL_PAYMENT_VERSION)
				.where(Tables.PARTIAL_PAYMENT_VERSION.REFERENCE.eq(reference))
				.orderBy(Tables.PARTIAL_PAYMENT_VERSION.VERSION.desc())
				.limit(1)
				.fetchOptionalInto(Integer.class)
				.orElse(0);
	}

	public boolean savePaymentCustomValues(PaymentsCustomValues customValues, String field)
	{
		return dsl.insertInto(Tables.PAYMENTS_CUSTOM_VALUES)
				.set(Tables.PAYMENTS_CUSTOM_VALUES.FIELD,
						dsl.select(Tables.PAYMENTS_CUSTOM_FIELDS.ID)
								.from(Tables.PAYMENTS_CUSTOM_FIELDS)
								.where(Tables.PAYMENTS_CUSTOM_FIELDS.FIELD.eq(field)))
				.set(Tables.PAYMENTS_CUSTOM_VALUES.VALUE, customValues.getValue())
				.set(Tables.PAYMENTS_CUSTOM_VALUES.PAYMENTID,
						customValues.getPaymentId())
				.set(Tables.PAYMENTS_CUSTOM_VALUES.PARTIALPAYMENTID,
						customValues.getPartialPaymentId())
				.execute() > 0;
	}
	
	public List<PartialPayments> fetchAllPartialPaymentsByReferenceAndType(final String reference, final int type)
			throws DataAccessException, SQLException
	{
		return dsl.selectFrom(Tables.PARTIAL_PAYMENTS)
				.where(Tables.PARTIAL_PAYMENTS.REFERENCE.eq(reference))
				.and(Tables.PARTIAL_PAYMENTS.TYPE.eq(type))
				.fetchInto(PartialPayments.class);
	}
	
	public boolean savePaymentTransaction(PaymentTransaction transaction)
	{
		PaymentTransactionRecord record = dsl.newRecord(Tables.PAYMENT_TRANSACTION, transaction);
		return dsl.insertInto(Tables.PAYMENT_TRANSACTION)
				.set(record)
				.onDuplicateKeyUpdate()
				.set(record)
				.execute() > 0;
	}
}