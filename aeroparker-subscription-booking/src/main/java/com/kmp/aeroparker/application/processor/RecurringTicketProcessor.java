package com.kmp.aeroparker.application.processor;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteSubscriptionRecurringPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Payments;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class RecurringTicketProcessor
{
	private static final boolean ENABLED = true;
	private final BookingService service;
	private final SiteService siteService;
	private final PaymentService paymentService;

	public boolean process(final int affId, final int siteId, final int contactId, final String bookingReference,
			final SubscriptionBookingItem subscriptionBookingItem, final BookingRecurringTicket bookingTicket)
	{
		log.debug("Processing recurring ticket");
		boolean saved = false;
		bookingTicket.setItemId(subscriptionBookingItem.getId());
		if (service.saveBookingRecurringTicket(bookingTicket))
		{
			if (!StringUtil.isEmpty(bookingReference))
			{
				Payments payment = paymentService.fetchPaymentByReference(bookingReference);
				if (payment != null)
				{
					log.debug("Adding scheduled recurring payment to db");
					SubscriptionScheduledRecurringPayment recurringPayment = buildSchedulePayment(affId, siteId,
							contactId, bookingReference, subscriptionBookingItem, bookingTicket, payment);

					if (service.fetchSiteSubscriptionRecurringPaymentBySiteId(siteId) == null)
					{
						SiteSubscriptionRecurringPayment siteSubscriptionRecurringPayment =
								new SiteSubscriptionRecurringPayment();
						siteSubscriptionRecurringPayment.setSiteId(siteId);
						service.insertSiteSubscriptionRecurringPayment(siteSubscriptionRecurringPayment);
					}
					saved = paymentService.insertSubscriptionScheduledRecurringPayment(recurringPayment);
				}
			}
		}
		return saved;
	}

	private SubscriptionScheduledRecurringPayment buildSchedulePayment(final int affId, final int siteId,
			final int contactId, final String bookingReference, final SubscriptionBookingItem subscriptionBookingItem,
			final BookingRecurringTicket bookingTicket, final Payments payment)
	{
		SubscriptionScheduledRecurringPayment recurringPayment = new SubscriptionScheduledRecurringPayment();
		recurringPayment.setAmount(bookingTicket.getPrice());
		recurringPayment.setSubscriptionBookingReference(bookingReference);
		Date lastPaymentDate = null;
		Date upcomingDate = bookingTicket.getStartDate();
		String timeZone = siteService.fetchSiteById(siteId)
				.getTimezone();
		if (upcomingDate.equals(DateUtil.nowDate(timeZone)))
		{
			log.debug("Updating last and upcoming payment dates.");
			lastPaymentDate = bookingTicket.getStartDate();
			LocalDate localUpcomingDate = upcomingDate.toLocalDate()
					.plusMonths(1);
			upcomingDate = DateUtil.localDateToDate(localUpcomingDate);
		}
		recurringPayment.setLastPaymentDate(lastPaymentDate);
		recurringPayment.setUpcomingPaymentDate(upcomingDate);
		recurringPayment.setPaymentId(payment.getId());
		recurringPayment.setAffiliateId(affId);
		recurringPayment.setEnabled(ENABLED);
		recurringPayment.setSiteId(siteId);
		recurringPayment.setContactId(contactId);
		recurringPayment.setSubBookingId(subscriptionBookingItem.getSubBookingId());
		return recurringPayment;
	}
}