package com.kmp.aeroparker.application.email.dispatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.model.BookingRecurringTicket;
import com.kmp.aeroparker.application.model.BookingSeasonTicket;
import com.kmp.aeroparker.application.model.ReplacePlaceholdersParameters;
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
import com.kmp.aeroparker.subscription.payments.enums.CustomValue;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Sites;
import com.kmp.aeroparker.subscription.payments.tables.pojos.SubscriptionScheduledRecurringPayment;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class PlaceHolderReplacerTest
{
	@Mock
	private PaymentService paymentService;
	@InjectMocks
	private PlaceHolderReplacer placeHolderReplacer;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@Mock
	private SubscriptionBookingRecord bookingRecord;
	@Mock
	private SubscriptionEmailAppearance appearance;

	@Test
	void testReplacePlaceHolderSeasonTicket()
	{
		when(appearance.getBody()).thenReturn(" Booking reference: {booking:reference} "
				+ " Customer Name: {booking:customer-firstname} {booking:customer-lastname}" + "{booking:product-body}");
		when(appearance.getFixedSeasonBody()).thenReturn(" Product Name: {booking:product-name} " + " Car Park:  {booking:carPark} "
				+ " Start Date:  {booking:start-date} " + " End Date: {booking:end-date}");
		SubscriptionBooking booking = EnhancedRandom.random(SubscriptionBooking.class);
		SubscriptionBookingCustomerDetails bookingCustomerDetails = EnhancedRandom.random(SubscriptionBookingCustomerDetails.class);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(bookingRecord.getCustomerDetails()).thenReturn(bookingCustomerDetails);
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(EnhancedRandom.random(SubscriptionBookingVehicleDetails.class));
		when(bookingRecord.getReceiptDetails()).thenReturn(EnhancedRandom.random(SubscriptionBookingReceiptDetails.class));
		when(bookingRecord.getSubscriptionScheduledRecurringPayment())
				.thenReturn(EnhancedRandom.randomListOf(1, SubscriptionScheduledRecurringPayment.class));
		when(bookingRecord.getBookingPayment()).thenReturn(EnhancedRandom.random(SubscriptionBookingPayment.class));
		Map<String, String> customFields = new HashMap<>();
		customFields.put(CustomValue.CARD_SCHEME.toString(), "creditcard");
		when(paymentService.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(customFields);
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = EnhancedRandom.random(SubscriptionBookingItem.class);
		bookingItem.setPeriodType("FIXED");
		BookingSeasonTicket bookingSeasonTicket = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(bookingItem, bookingSeasonTicket);

		SubscriptionBookingItem bookingItem1 = EnhancedRandom.random(SubscriptionBookingItem.class);
		bookingItem1.setPeriodType("FIXED");
		BookingSeasonTicket bookingSeasonTicket1 = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(bookingItem1, bookingSeasonTicket1);
		Localise localise = mock(Localise.class);
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn("Mon, 09 September 19");
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		Affiliates affiliate = mock(Affiliates.class);
		when(affiliate.getCode()).thenReturn("aff_code");
		Sites site = mock(Sites.class);
		when(site.getTimezone()).thenReturn("Europe/London");
		ReplacePlaceholdersParameters replacePlaceholdersParameters = buildReplaceHolderParameters(localise, affiliate, site);
		assertThat(placeHolderReplacer.replacePlaceHolders(replacePlaceholdersParameters)).isNotBlank()
				.contains("Booking reference: " + booking.getReference(),
						" Customer Name: " + bookingCustomerDetails.getFirstName() + " " + bookingCustomerDetails.getLastName(),
						" Product Name: " + bookingItem.getProductDisplayName(), " End Date: " + "Mon, 09 September 19",
						" Product Name: " + bookingItem1.getProductDisplayName(), " End Date: " + "Mon, 09 September 19")
				.doesNotContain("{booking:product-body}");
	}

	/**
	 * @param localise
	 * @param affiliate
	 * @param site
	 * @return
	 */
	private ReplacePlaceholdersParameters buildReplaceHolderParameters(final Localise localise, final Affiliates affiliate, final Sites site)
	{
		ReplacePlaceholdersParameters replacePlaceholdersParameters = ReplacePlaceholdersParameters.builder()
				.withAffiliate(affiliate)
				.withAppearance(appearance)
				.withBookingRecord(bookingRecord)
				.withLanguageFieldsList(languageFieldsList)
				.withLocalise(localise)
				.withSite(site)
				.build();
		return replacePlaceholdersParameters;
	}

	@Test
	void testReplacePlaceHolderRecurringTicket()
	{
		when(appearance.getBody()).thenReturn(" Booking reference: {booking:reference} "
				+ " Customer Name: {booking:customer-firstname} {booking:customer-lastname}" + "{booking:product-body}");
		when(appearance.getRecurringBody())
				.thenReturn(" Product Name: {booking:product-name} " + " Car Park:  {booking:carPark} " + " Start Date:  {booking:start-date} ");
		SubscriptionBooking booking = EnhancedRandom.random(SubscriptionBooking.class);
		SubscriptionBookingCustomerDetails bookingCustomerDetails = EnhancedRandom.random(SubscriptionBookingCustomerDetails.class);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(bookingRecord.getCustomerDetails()).thenReturn(bookingCustomerDetails);
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(EnhancedRandom.random(SubscriptionBookingVehicleDetails.class));
		when(bookingRecord.getReceiptDetails()).thenReturn(EnhancedRandom.random(SubscriptionBookingReceiptDetails.class, "companyName"));
		when(bookingRecord.getSubscriptionScheduledRecurringPayment())
				.thenReturn(EnhancedRandom.randomListOf(1, SubscriptionScheduledRecurringPayment.class));
		when(bookingRecord.getBookingPayment()).thenReturn(EnhancedRandom.random(SubscriptionBookingPayment.class));
		Map<String, String> customFields = new HashMap<>();
		customFields.put(CustomValue.PAYMENT_TYPE.toString(), "creditcard");
		when(paymentService.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(customFields);
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = EnhancedRandom.random(SubscriptionBookingItem.class);
		bookingItem.setPeriodType("RECURRING");
		BookingRecurringTicket bookingRecurringTicket = EnhancedRandom.random(BookingRecurringTicket.class, "minimumTerm");
		bookingRecurringTicket.setMinimumTerm("TWELVE_MONTHS");
		bookingItemMap.put(bookingItem, bookingRecurringTicket);
		SubscriptionBookingItem bookingItem1 = EnhancedRandom.random(SubscriptionBookingItem.class);
		bookingItem1.setPeriodType("RECURRING");
		BookingRecurringTicket bookingRecurringTicket1 = EnhancedRandom.random(BookingRecurringTicket.class, "minimumTerm");
		bookingRecurringTicket1.setMinimumTerm("TWELVE_MONTHS");
		bookingItemMap.put(bookingItem1, bookingRecurringTicket1);
		Localise localise = mock(Localise.class);
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn("Mon, 09 September 19");
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		when(languageFieldsList.getTranslation(eq("Months"))).thenReturn("Months");
		Affiliates affiliate = mock(Affiliates.class);
		when(affiliate.getCode()).thenReturn("aff_code");
		Sites site = mock(Sites.class);
		when(site.getTimezone()).thenReturn("Europe/London");
		ReplacePlaceholdersParameters replacePlaceholdersParameters = buildReplaceHolderParameters(localise, affiliate, site);
		assertThat(placeHolderReplacer.replacePlaceHolders(replacePlaceholdersParameters)).isNotBlank()
				.contains("Booking reference: " + booking.getReference(),
						" Customer Name: " + bookingCustomerDetails.getFirstName() + " " + bookingCustomerDetails.getLastName(),
						" Product Name: " + bookingItem.getProductDisplayName(), " Product Name: " + bookingItem1.getProductDisplayName())
				.doesNotContain("{booking:product-body}");
		verify(paymentService).fetchPaymentCustomValueByPaymentId(anyInt());
		verifyNoMoreInteractions(paymentService);
	}

	@Test
	void testReplacePlaceHolderSeasonTicket_SubscriptionBookingVehicleDetails_SubscriptionBookingReceiptDetails_SubscriptionScheduledRecurringPayment()
	{
		when(appearance.getBody()).thenReturn(" Booking reference: {booking:reference} "
				+ " Customer Name: {booking:customer-firstname} {booking:customer-lastname}" + "{booking:product-body}");
		when(appearance.getFixedSeasonBody()).thenReturn(" Product Name: {booking:product-name} " + " Car Park:  {booking:carPark} "
				+ " Start Date:  {booking:start-date} " + " End Date: {booking:end-date}");
		SubscriptionBooking booking = EnhancedRandom.random(SubscriptionBooking.class);
		SubscriptionBookingCustomerDetails bookingCustomerDetails =
				EnhancedRandom.random(SubscriptionBookingCustomerDetails.class, "address1", "postcode");
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(bookingRecord.getCustomerDetails()).thenReturn(bookingCustomerDetails);
		when(bookingRecord.getSubscriptionBookingVehicleDetails()).thenReturn(null);
		when(bookingRecord.getReceiptDetails()).thenReturn(null);
		when(bookingRecord.getSubscriptionScheduledRecurringPayment()).thenReturn(Collections.emptyList());
		when(bookingRecord.getBookingPayment()).thenReturn(EnhancedRandom.random(SubscriptionBookingPayment.class));
		Map<String, String> customFields = new HashMap<>();
		customFields.put(CustomValue.CARD_SCHEME.toString(), "creditcard");
		when(paymentService.fetchPaymentCustomValueByPaymentId(anyInt())).thenReturn(customFields);
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();
		SubscriptionBookingItem bookingItem = EnhancedRandom.random(SubscriptionBookingItem.class);
		bookingItem.setPeriodType("FIXED");
		BookingSeasonTicket bookingSeasonTicket = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(bookingItem, bookingSeasonTicket);

		SubscriptionBookingItem bookingItem1 = EnhancedRandom.random(SubscriptionBookingItem.class);
		bookingItem1.setPeriodType("FIXED");
		BookingSeasonTicket bookingSeasonTicket1 = EnhancedRandom.random(BookingSeasonTicket.class);
		bookingItemMap.put(bookingItem1, bookingSeasonTicket1);
		Localise localise = mock(Localise.class);
		when(localise.longDateTranslated(any(Calendar.class), any())).thenReturn("Mon, 09 September 19");
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		Affiliates affiliate = mock(Affiliates.class);
		when(affiliate.getCode()).thenReturn("aff_code");
		Sites site = mock(Sites.class);
		when(site.getTimezone()).thenReturn("Europe/London");
		ReplacePlaceholdersParameters replacePlaceholdersParameters = buildReplaceHolderParameters(localise, affiliate, site);
		assertThat(placeHolderReplacer.replacePlaceHolders(replacePlaceholdersParameters)).isNotBlank()
				.contains("Booking reference: " + booking.getReference(),
						" Customer Name: " + bookingCustomerDetails.getFirstName() + " " + bookingCustomerDetails.getLastName(),
						" Product Name: " + bookingItem.getProductDisplayName(), " End Date: " + "Mon, 09 September 19",
						" Product Name: " + bookingItem1.getProductDisplayName(), " End Date: " + "Mon, 09 September 19")
				.doesNotContain("{booking:product-body}");
	}

	@Test
	void testReplacePlaceHolder_Null()
	{
		when(appearance.getBody()).thenReturn("");
		Localise localise = mock(Localise.class);
		ReplacePlaceholdersParameters replacePlaceholdersParameters = buildReplaceHolderParameters(localise, null, null);
		assertThat(placeHolderReplacer.replacePlaceHolders(replacePlaceholdersParameters)).isBlank();
	}

	@Test
	void testReplacePlaceHoldersActivationEmail()
	{
		Contacts contacts = mock(Contacts.class);
		when(contacts.getLastName()).thenReturn("lastname");
		when(contacts.getFirstName()).thenReturn("lastsurname");
		String code = "123";
		String body = "{contact:customer-lastname},{contact:customer-firstname},{contact:confirm-email-link}";
		assertThat(placeHolderReplacer.replacePlaceHoldersActivationEmail(body, contacts, code)).doesNotContain("contact:customer-lastnam",
				"contact:customer-firstname", "contact:confirm-email-link");
	}

	@Test
	void testReplacePlaceHoldersActivationEmail_Body_Null()
	{
		assertThat(placeHolderReplacer.replacePlaceHoldersActivationEmail("", new Contacts(), "")).isBlank();
	}
}