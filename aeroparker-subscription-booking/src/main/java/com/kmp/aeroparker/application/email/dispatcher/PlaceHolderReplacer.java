package com.kmp.aeroparker.application.email.dispatcher;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.ReplacePlaceholdersParameters;
import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingPayment;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingReceiptDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingVehicleDetails;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionEmailAppearance;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.enums.CustomValue;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class PlaceHolderReplacer extends BasePlaceHolder
{
	private final PaymentService paymentService;

	protected String replacePlaceHolders(final ReplacePlaceholdersParameters replacePlaceholdersParameters)
	{
		final SubscriptionBookingRecord bookingRecord = replacePlaceholdersParameters.getBookingRecord();
		final SubscriptionEmailAppearance appearance = replacePlaceholdersParameters.getAppearance();
		final Localise localise = replacePlaceholdersParameters.getLocalise();
		final LanguageFieldsList languageFieldsList = replacePlaceholdersParameters.getLanguageFieldsList();
		final Sites site = replacePlaceholdersParameters.getSite();
		final Affiliates affiliate = replacePlaceholdersParameters.getAffiliate();

		log.debug("Creating Email Body");
		String body = "";
		if (!StringUtil.isEmpty(appearance.getBody()))
		{
			body = appearance.getBody();
			SubscriptionBooking booking = bookingRecord.getBooking();
			SubscriptionBookingCustomerDetails bookingCustomerDetails = bookingRecord.getCustomerDetails();
			Map<String, String> placeHolders = new HashMap<String, String>();
			log.debug("Replacing place holders in template.");
			placeHolders.put("booking:customer-title", bookingCustomerDetails.getTitle());
			placeHolders.put("booking:customer-firstname", bookingCustomerDetails.getFirstName());
			placeHolders.put("booking:customer-lastname", bookingCustomerDetails.getLastName());

			placeHolders.put("booking:customer-address1",
					StringUtil.isEmpty(bookingCustomerDetails.getAddress1()) ? "N/A" : bookingCustomerDetails.getAddress1());
			placeHolders.put("booking:customer-address2", bookingCustomerDetails.getAddress2());
			placeHolders.put("booking:customer-postcode",
					StringUtil.isEmpty(bookingCustomerDetails.getPostcode()) ? "N/A" : bookingCustomerDetails.getPostcode());
			placeHolders.put("booking:town", bookingCustomerDetails.getTown());
			placeHolders.put("booking:customer-telephone", bookingCustomerDetails.getPhoneNumber());

			Calendar now = DateUtil.nowCalendar(site.getTimezone());
			placeHolders.put("booking:date", localise.date(now));
			placeHolders.put("booking:time", localise.longTime(now, languageFieldsList));
			placeHolders.put("booking:bookig-date", localise.date(DateUtil.timestampToLocalDateTime(booking.getCreated())));
			placeHolders.put("booking:booking-time", localise.longTime(DateUtil.timestampToCalendar(booking.getCreated()), languageFieldsList));

			SubscriptionBookingVehicleDetails bookingVehicleDetails = bookingRecord.getSubscriptionBookingVehicleDetails();
			if (bookingVehicleDetails != null)
			{
				placeHolders.put("booking:car-registration", bookingVehicleDetails.getCarRegistration());
				placeHolders.put("booking:car-make", bookingVehicleDetails.getCarMake());
				placeHolders.put("booking:car-model", bookingVehicleDetails.getCarModel());
				placeHolders.put("booking:car-colour", bookingVehicleDetails.getCarColour());
				placeHolders.put("booking:car-country", bookingVehicleDetails.getCarCountry());
			}

			placeHolders.put("booking:booking-fee", localise.price(booking.getBookingFee()
					.floatValue(), false));
			placeHolders.put("booking:total-price-without-booking-fee", localise.price((booking.getGrandTotal()
					.subtract(booking.getBookingFee())).floatValue(), false));

			placeHolders.put("booking:affiliate-id", StringUtil.intToStr(booking.getAffiliateId()));
			placeHolders.put("booking:affiliate-code", affiliate.getCode());
			placeHolders.put("booking:reference", booking.getReference());
			placeHolders.put("booking:total-price", booking.getGrandTotal()
					.toPlainString());

			SubscriptionBookingReceiptDetails bookingReceiptDetails = bookingRecord.getReceiptDetails();
			if (bookingReceiptDetails != null)
			{
				placeHolders.put("booking:company",
						StringUtil.isEmpty(bookingReceiptDetails.getCompanyName()) ? "N/A" : bookingReceiptDetails.getCompanyName());
			}

			List<SubscriptionScheduledRecurringPayment> scheduledRecurringPaymentList = bookingRecord.getSubscriptionScheduledRecurringPayment();
			if (!scheduledRecurringPaymentList.isEmpty())
			{
				BigDecimal charge = scheduledRecurringPaymentList.stream()
						.map(scheduledRecurringPayment -> scheduledRecurringPayment.getAmount())
						.reduce(BigDecimal.ZERO, (charge1, charge2) -> charge1.add(charge2));
				placeHolders.put("recurring:charge-amount", charge.toString());

				placeHolders.put("recurring:charge-date", localise.date(DateUtil.dateToCalendar(scheduledRecurringPaymentList.get(0)
						.getUpcomingPaymentDate())));
			}
			SubscriptionBookingPayment bookingPayment = bookingRecord.getBookingPayment();
			Map<String, String> paymentCustomValues = paymentService.fetchPaymentCustomValueByPaymentId(bookingPayment.getPaymentId());
			placeHolders.put("booking:paymentMethod",
					paymentCustomValues.getOrDefault(CustomValue.PAYMENT_TYPE.getField(), paymentCustomValues.getOrDefault(CustomValue.CARD_SCHEME.getField(), "N/A")));

			String productBody = "";
			StringJoiner stringJoiner = new StringJoiner("");
			for (Map.Entry<SubscriptionBookingItem, IBookingTicket> entryBookingItem : bookingRecord.getBookingItemMap()
					.entrySet())
			{
				SubscriptionBookingItem bookingItem = entryBookingItem.getKey();
				placeHolders.put("booking:product-name", bookingItem.getProductDisplayName());
				placeHolders.put("booking:vatAmount", bookingItem.getVatAmount()
						.toString());
				placeHolders.put("booking:carPark", bookingItem.getCarParkName());
				String ticketTypeBody = "";
				if (SubscriptionPeriodType.isFixedTicket(bookingItem.getPeriodType()))
				{
					log.debug("Creating season ticket body.");
					ticketTypeBody = appearance.getFixedSeasonBody();
					BookingSeasonTicket bookingTicket = (BookingSeasonTicket) entryBookingItem.getValue();
					setStartDate(placeHolders, bookingTicket.getStartDate(), localise, languageFieldsList);
					placeHolders.put("booking:end-date", localise.longDateTranslated(DateUtil.localDateToCalendar(bookingTicket.getEndDate()
							.toLocalDate()), languageFieldsList));
				}
				else
				{
					log.debug("Creating recurring ticket body.");
					ticketTypeBody = appearance.getRecurringBody();
					BookingRecurringTicket bookingTicket = (BookingRecurringTicket) entryBookingItem.getValue();
					setStartDate(placeHolders, bookingTicket.getStartDate(), localise, languageFieldsList);
					placeHolders.put("booking:minimum-term-date", SubscriptionMinimumTerm.valueOf(bookingTicket.getMinimumTerm())
							.getIntValue() + " " + languageFieldsList.getTranslation("Months") + " - "
							+ localise.longDateTranslated(DateUtil.localDateToCalendar(bookingTicket.getMinimumTermDate()
									.toLocalDate()), languageFieldsList));
				}

				stringJoiner.add(replacePlaceHolder(ticketTypeBody, placeHolders));
			}
			productBody = stringJoiner.toString();
			// replace the product tag with the built product body
			body = StringUtil.replace(body, "{booking:product-body}", productBody);
			// replace remaining place holders
			body = replacePlaceHolder(body, placeHolders);
		}
		return body;
	}

	private void setStartDate(final Map<String, String> placeHolders, final Date startDate, final Localise localise,
			final LanguageFieldsList languageFieldsList)
	{
		placeHolders.put("booking:start-date",
				localise.longDateTranslated(DateUtil.localDateToCalendar(startDate.toLocalDate()), languageFieldsList));
	}

	protected String replacePlaceHoldersActivationEmail(final String body, final Contacts contact, final String url)
	{
		String toReturn = "";
		log.debug("Creating Email Body");
		if (!StringUtil.isEmpty(body))
		{
			toReturn = body;

			Map<String, String> placeHolders = new HashMap<>();

			log.debug("Replacing place holders in template.");

			placeHolders.put("contact:customer-lastname", contact.getLastName());
			placeHolders.put("contact:customer-firstname", contact.getFirstName());
			placeHolders.put("contact:confirm-email-link", url);

			// replace place holders
			toReturn = replacePlaceHolder(body, placeHolders);
		}
		return toReturn;
	}
}