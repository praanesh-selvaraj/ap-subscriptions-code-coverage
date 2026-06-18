package com.kmp.aeroparker.application.builder;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.kmp.aeroparker.application.model.ConfirmationDetails;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ConfirmationDetailsBuilder
{
	private final Localise localise;
	private final LanguageFieldsList languageFieldsList;

	public ConfirmationDetails build(final SubscriptionBookingRecord bookingRecord, final Affiliates affiliate,
			final HashMap<Integer, String> confirmationMessages)
	{
		ConfirmationDetails confirmationDetails = new ConfirmationDetails();
		SubscriptionBooking booking = bookingRecord.getBooking();
		
		if (booking != null)
		{
			log.info("Building confirmation details, reference: {}", booking.getReference());
			confirmationDetails.setBookingId(booking.getId());
			confirmationDetails.setReference(booking.getReference());
			confirmationDetails.setBookingDateTime(localise.longDateTranslated(booking.getCreated()
					.toLocalDateTime(), languageFieldsList));
			confirmationDetails.setTotalBookingFee(booking.getBookingFee());
			confirmationDetails.setConfirmationMessages(confirmationMessages);
			setVatAttributes(affiliate, booking.getGrandTotal(), confirmationDetails);
		}

		SubscriptionBookingCustomerDetails customerDetails = bookingRecord.getCustomerDetails();

		if (customerDetails != null)
		{
			confirmationDetails.setName(customerDetails.getFirstName() + " " + customerDetails.getLastName());
			log.info("Customer details not null, setting customer name: {}", confirmationDetails.getName());
			confirmationDetails.setEmail(customerDetails.getEmailAddress());
		}
		return confirmationDetails;
	}

	private void setVatAttributes(final Affiliates affiliate, final BigDecimal grandTotal, final ConfirmationDetails confirmationDetails)
	{
		log.info("Building confirmation VAT details, affiliate Id: {}, reference: {} ",affiliate.getId(), confirmationDetails.getReference());
		String vatNo = HtmlUtils.htmlUnescape(affiliate.getVatNo());
		String vatCompany = HtmlUtils.htmlUnescape(affiliate.getVatCompany());
		String vatAddress = HtmlUtils.htmlUnescape(affiliate.getVatAddress());
		BigDecimal vatRate = affiliate.getVatRate();
		BigDecimal stateTaxRate = affiliate.getStateTaxRate();
		BigDecimal taxDivisor = (BigDecimal.valueOf(100)
				.add(vatRate)
				.add(stateTaxRate)).divide(BigDecimal.valueOf(100));
		BigDecimal grandTotalBeforeTax = grandTotal.divide(taxDivisor, 2, BigDecimal.ROUND_HALF_UP);
		BigDecimal grandTotalTax = grandTotal.subtract(grandTotalBeforeTax);
		confirmationDetails.setVatNo(vatNo);
		confirmationDetails.setVatCompany(vatCompany);
		confirmationDetails.setVatAddress(vatAddress);
		confirmationDetails.setVatRate(vatRate);
		confirmationDetails.setStateTaxRate(stateTaxRate);
		confirmationDetails.setTaxDivisor(taxDivisor);
		confirmationDetails.setGrandTotalBeforeTax(grandTotalBeforeTax);
		confirmationDetails.setGrandTotalTax(grandTotalTax);
		confirmationDetails.setGrandTotal(grandTotal);
	}
}