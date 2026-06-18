package com.kmp.aeroparker.application.processor;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.barcode.sender.AncillaryBarcodeGenerator;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.db.service.AncillaryBarcodeService;
import com.kmp.aeroparker.application.model.interfaces.IBookingTicket;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SiteAncillaryBarcodeConfiguration;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingItem;

@ExtendWith(MockitoExtension.class)
class AncillaryBarcodeProcessorTest
{
	@Mock
	private AncillaryBarcodeService service;
	@Mock
	private AncillaryBarcodeGenerator barcodeGenerator;
	@Mock
	private SubscriptionBookingRecord bookingRecord;
	@Mock
	private SubscriptionBooking booking;
	@Mock
	private SubscriptionBookingItem bookingItem;
	@InjectMocks
	private AncillaryBarcodeProcessor processor;

	@BeforeEach
	void init()
	{
		Map<SubscriptionBookingItem, IBookingTicket> bookingItemMap = new HashMap<>();

		bookingItemMap.put(bookingItem, mock(IBookingTicket.class));

		when(bookingRecord.getBooking()).thenReturn(booking);
		when(booking.getReference()).thenReturn("ref");
		when(bookingRecord.getBookingItemMap()).thenReturn(bookingItemMap);
		when(bookingItem.getProductId()).thenReturn(1);
	}

	@Test
	void testGenerateNewAncillaryBarcode()
	{
		List<SiteAncillaryBarcodeConfiguration> configurationList = new ArrayList<>();
		SiteAncillaryBarcodeConfiguration configuration = mock(SiteAncillaryBarcodeConfiguration.class);

		configurationList.add(configuration);

		when(service.fetchAncillaryBarcodeConfigurationBySubscriptionProductId(anyInt())).thenReturn(configurationList);

		processor.generateNewAncillaryBarcode(bookingRecord);

		verify(barcodeGenerator).generate(anyString(), anyInt());
	}

	@Test
	void testGenerateNewAncillaryBarcode_NoConfiguration()
	{
		processor.generateNewAncillaryBarcode(bookingRecord);

		verify(service).fetchAncillaryBarcodeConfigurationBySubscriptionProductId(anyInt());
		verifyNoInteractions(barcodeGenerator);
	}

	@Test
	void testGenerateNewAncillaryBarcode_NullProductId()
	{
		when(bookingItem.getProductId()).thenReturn(null);

		processor.generateNewAncillaryBarcode(bookingRecord);

		verifyNoInteractions(service);
		verifyNoInteractions(barcodeGenerator);
	}
}
