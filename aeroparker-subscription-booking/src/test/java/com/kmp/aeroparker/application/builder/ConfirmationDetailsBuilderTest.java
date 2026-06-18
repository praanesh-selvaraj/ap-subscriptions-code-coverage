package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.HtmlUtils;

import com.kmp.aeroparker.application.model.ConfirmationDetails;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class ConfirmationDetailsBuilderTest
{
	@Mock
	private Localise localise;
	@Mock
	private LanguageFieldsList languageFieldsList;
	@InjectMocks
	private ConfirmationDetailsBuilder builder;
	@Mock
	private SubscriptionBookingRecord bookingRecord;
	@Mock
	private Affiliates affiliate;
	@Mock
	private SubscriptionBooking booking;

	@Test
	void testBuild()
	{
		when(localise.longDateTranslated(any(LocalDateTime.class), any())).thenReturn("18:31 Wed, 04 September 19");
		HashMap<Integer, String> confirmationMessages = new HashMap<>();
		when(affiliate.getVatNo()).thenReturn("VAT123456789");
		when(affiliate.getVatCompany()).thenReturn("vat company");
		when(affiliate.getVatAddress()).thenReturn("vat address");
		when(affiliate.getVatRate()).thenReturn(BigDecimal.ONE);
		when(affiliate.getStateTaxRate()).thenReturn(BigDecimal.ONE);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getReference()).thenReturn("reference");
		when(booking.getCreated()).thenReturn(new Timestamp(new Date().getTime()));
		when(booking.getGrandTotal()).thenReturn(BigDecimal.TEN);
		SubscriptionBookingCustomerDetails customerDetails =
				EnhancedRandom.random(SubscriptionBookingCustomerDetails.class);
		when(bookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		
		assertThat(builder.build(bookingRecord, affiliate, confirmationMessages)).isNotNull()
				.isInstanceOf(ConfirmationDetails.class);
	}
	
	@Test
	void testBuild_EscapedVatStrings()
	{
		when(localise.longDateTranslated(any(LocalDateTime.class), any())).thenReturn("18:31 Wed, 04 September 19");
		HashMap<Integer, String> confirmationMessages = new HashMap<>();
		String escapedVatNo = "\"VAT\"123456789";
		when(affiliate.getVatNo()).thenReturn(escapedVatNo);
		String escapedVatCompany = "SIA &ldquo;Mūžīgais Ābols&rdquo;";
		when(affiliate.getVatCompany()).thenReturn(escapedVatCompany);
		String escapedVatAddress = "&quot;Lidosta &quot;Rīga&quot; 10/1&quot;, Lidosta &quot;Rīga&quot;, Mārupes nov., LV-1053, Latvija";
		when(affiliate.getVatAddress()).thenReturn(escapedVatAddress);
		when(affiliate.getVatRate()).thenReturn(BigDecimal.ONE);
		when(affiliate.getStateTaxRate()).thenReturn(BigDecimal.ONE);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getReference()).thenReturn("reference");
		when(booking.getCreated()).thenReturn(new Timestamp(new Date().getTime()));
		when(booking.getGrandTotal()).thenReturn(BigDecimal.TEN);
		SubscriptionBookingCustomerDetails customerDetails =
				EnhancedRandom.random(SubscriptionBookingCustomerDetails.class);
		when(bookingRecord.getCustomerDetails()).thenReturn(customerDetails);
		
		ConfirmationDetails confirmationDetails = builder.build(bookingRecord, affiliate, confirmationMessages);
		
		assertEquals(HtmlUtils.htmlUnescape(escapedVatNo), confirmationDetails.getVatNo());
		assertEquals(HtmlUtils.htmlUnescape(escapedVatCompany), confirmationDetails.getVatCompany());
		assertEquals(HtmlUtils.htmlUnescape(escapedVatAddress), confirmationDetails.getVatAddress());
	}

	@Test
	void testBuild_SubscriptionBookingCustomerDetails_Null()
	{
		when(localise.longDateTranslated(any(LocalDateTime.class), any())).thenReturn("18:31 Wed, 04 September 19");
		HashMap<Integer, String> confirmationMessages = new HashMap<>();
		when(affiliate.getVatNo()).thenReturn("VAT123456789");
		when(affiliate.getVatCompany()).thenReturn("vat company");
		when(affiliate.getVatAddress()).thenReturn("vat address");
		when(affiliate.getVatRate()).thenReturn(BigDecimal.ONE);
		when(affiliate.getStateTaxRate()).thenReturn(BigDecimal.ONE);
		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getReference()).thenReturn("reference");
		when(booking.getCreated()).thenReturn(new Timestamp(new Date().getTime()));
		when(booking.getGrandTotal()).thenReturn(BigDecimal.TEN);
		when(bookingRecord.getCustomerDetails()).thenReturn(null);
		
		assertThat(builder.build(bookingRecord, affiliate, confirmationMessages)).isNotNull()
				.isInstanceOf(ConfirmationDetails.class);
	}

	@Test
	void testBuild_SubscriptionBooking_Null()
	{
		HashMap<Integer, String> confirmationMessages = new HashMap<>();
		when(bookingRecord.getCustomerDetails()).thenReturn(null);
		
		assertThat(builder.build(bookingRecord, affiliate, confirmationMessages)).isNotNull()
				.isInstanceOf(ConfirmationDetails.class);
	}
}