package com.kmp.aeroparker.application.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.availability.PriceDetails;
import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetails;
import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetailsList;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductAppearance;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProductTerms;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionProductDisplayItemFactoryTest
{
	@Mock
	private LanguageFieldsList languageFieldsList;
	@Mock
	private Localise localise;
	@Mock
	private SeasonTicketDisplayItemBuilder seasonTicketDisplayItemBuilder;
	@Mock
	private RecurringTicketDisplayItemBuilder recurringTicketDisplayItemBuilder;
	@InjectMocks
	private SubscriptionProductDisplayItemFactory factory;

	@Test
	void testBuild_Season_Ticket()
	{
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean())).thenReturn("�2{pennies}.99{/pennies}");
		SubscriptionProductAvailabilityDetailsList availabilityDetailsList = new SubscriptionProductAvailabilityDetailsList();
		SubscriptionProductAvailabilityDetails availabilityDetails = mock(SubscriptionProductAvailabilityDetails.class);
		when(availabilityDetails.isSeasonTicket()).thenReturn(true);
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class);
		when(availabilityDetails.getProduct()).thenReturn(subscriptionProduct);
		SubscriptionProductAppearance subscriptionProductAppearance = mock(SubscriptionProductAppearance.class);
		when(subscriptionProductAppearance.getBulletPoint()).thenReturn("{line1:bulletPoints}");
		when(subscriptionProductAppearance.getMoreInfo()).thenReturn("moreInfo");
		when(availabilityDetails.getProductAppearance()).thenReturn(subscriptionProductAppearance);
		SubscriptionProductTerms subscriptionProductTerms = mock(SubscriptionProductTerms.class);
		when(availabilityDetails.getProductTerms()).thenReturn(subscriptionProductTerms);
		PriceDetails priceDetails = mock(PriceDetails.class);
		when(priceDetails.getPrice()).thenReturn(BigDecimal.TEN);
		when(availabilityDetails.getPriceDetails()).thenReturn(priceDetails);
		when(seasonTicketDisplayItemBuilder.build(any())).thenReturn(new SeasonTicketDisplayItem());
		availabilityDetailsList.add(availabilityDetails);
		assertThat(factory.build(availabilityDetailsList)).isNotEmpty();
		verify(seasonTicketDisplayItemBuilder).build(any());
		verifyNoMoreInteractions(seasonTicketDisplayItemBuilder);
	}

	@Test
	void testBuild_Recurring_Ticket()
	{
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean())).thenReturn("�2{pennies}.99{/pennies}");
		SubscriptionProductAvailabilityDetailsList availabilityDetailsList = new SubscriptionProductAvailabilityDetailsList();
		SubscriptionProductAvailabilityDetails availabilityDetails = mock(SubscriptionProductAvailabilityDetails.class);
		when(availabilityDetails.isSeasonTicket()).thenReturn(false);
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class);
		when(availabilityDetails.getProduct()).thenReturn(subscriptionProduct);
		when(availabilityDetails.isRecurringTicket()).thenReturn(true);
		SubscriptionProductAppearance subscriptionProductAppearance = mock(SubscriptionProductAppearance.class);
		when(subscriptionProductAppearance.getBulletPoint()).thenReturn("{line1:bulletPoints}");
		when(subscriptionProductAppearance.getMoreInfo()).thenReturn("moreInfo");
		when(availabilityDetails.getProductAppearance()).thenReturn(subscriptionProductAppearance);
		SubscriptionProductTerms subscriptionProductTerms = mock(SubscriptionProductTerms.class);
		when(availabilityDetails.getProductTerms()).thenReturn(subscriptionProductTerms);
		PriceDetails priceDetails = mock(PriceDetails.class);
		when(priceDetails.getPrice()).thenReturn(BigDecimal.TEN);
		when(availabilityDetails.getPriceDetails()).thenReturn(priceDetails);
		when(recurringTicketDisplayItemBuilder.build(any())).thenReturn(new RecurringTicketDisplayItem());
		availabilityDetailsList.add(availabilityDetails);
		when(languageFieldsList.getTranslation(anyString())).thenReturn("mo");
		assertThat(factory.build(availabilityDetailsList)).isNotEmpty();
		verify(seasonTicketDisplayItemBuilder, times(0)).build(any());
		verify(recurringTicketDisplayItemBuilder).build(any());
		verifyNoMoreInteractions(seasonTicketDisplayItemBuilder);
	}

	@Test
	void testBuild_Bullet_Point_Empty()
	{
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean())).thenReturn("�2{pennies}.99{/pennies}");
		SubscriptionProductAvailabilityDetailsList availabilityDetailsList = new SubscriptionProductAvailabilityDetailsList();
		SubscriptionProductAvailabilityDetails availabilityDetails = mock(SubscriptionProductAvailabilityDetails.class);
		when(availabilityDetails.isSeasonTicket()).thenReturn(true);
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class);
		when(availabilityDetails.getProduct()).thenReturn(subscriptionProduct);
		SubscriptionProductAppearance subscriptionProductAppearance = mock(SubscriptionProductAppearance.class);
		when(subscriptionProductAppearance.getBulletPoint()).thenReturn("");
		when(availabilityDetails.getProductAppearance()).thenReturn(subscriptionProductAppearance);
		SubscriptionProductTerms subscriptionProductTerms = mock(SubscriptionProductTerms.class);
		when(availabilityDetails.getProductTerms()).thenReturn(subscriptionProductTerms);
		PriceDetails priceDetails = mock(PriceDetails.class);
		when(priceDetails.getPrice()).thenReturn(BigDecimal.TEN);
		when(availabilityDetails.getPriceDetails()).thenReturn(priceDetails);
		when(seasonTicketDisplayItemBuilder.build(any())).thenReturn(mock(SubscriptionProductDisplayItem.class));
		availabilityDetailsList.add(availabilityDetails);
		assertThat(factory.build(availabilityDetailsList)).flatExtracting(SubscriptionProductDisplayItem::getBulletPoints)
				.isEmpty();
		verify(seasonTicketDisplayItemBuilder).build(any());
		verifyNoMoreInteractions(seasonTicketDisplayItemBuilder);
	}

	@Test
	void testBuild_Bullet_Point_JSONException()
	{
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean())).thenReturn("�2{pennies}.99{/pennies}");
		SubscriptionProductAvailabilityDetailsList availabilityDetailsList = new SubscriptionProductAvailabilityDetailsList();
		SubscriptionProductAvailabilityDetails availabilityDetails = mock(SubscriptionProductAvailabilityDetails.class);
		when(availabilityDetails.isSeasonTicket()).thenReturn(true);
		SubscriptionProduct subscriptionProduct = EnhancedRandom.random(SubscriptionProduct.class);
		when(availabilityDetails.getProduct()).thenReturn(subscriptionProduct);
		SubscriptionProductAppearance subscriptionProductAppearance = mock(SubscriptionProductAppearance.class);
		when(subscriptionProductAppearance.getBulletPoint()).thenReturn("line1:bulletPoints");
		when(availabilityDetails.getProductAppearance()).thenReturn(subscriptionProductAppearance);
		SubscriptionProductTerms subscriptionProductTerms = mock(SubscriptionProductTerms.class);
		when(availabilityDetails.getProductTerms()).thenReturn(subscriptionProductTerms);
		PriceDetails priceDetails = mock(PriceDetails.class);
		when(priceDetails.getPrice()).thenReturn(BigDecimal.TEN);
		when(availabilityDetails.getPriceDetails()).thenReturn(priceDetails);
		when(seasonTicketDisplayItemBuilder.build(any())).thenReturn(mock(SubscriptionProductDisplayItem.class));
		availabilityDetailsList.add(availabilityDetails);
		assertThat(factory.build(availabilityDetailsList)).flatExtracting(SubscriptionProductDisplayItem::getBulletPoints)
				.isEmpty();
		verify(seasonTicketDisplayItemBuilder).build(any());
		verifyNoMoreInteractions(seasonTicketDisplayItemBuilder);
	}
}