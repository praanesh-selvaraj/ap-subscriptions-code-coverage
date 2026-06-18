package com.kmp.aeroparker.application.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.PurchaseQuery;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.l10n.location.Location;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class SubscriptionBasketBuilderTest
{
	@Mock
	private Localise localise;
	@Mock
	private SubscriptionService service;
	@Mock
	private PurchaseRequestBuilder purchaseRequestbuilder;
	@InjectMocks
	private SubscriptionBasketBuilder builder;

	@Test
	void testBuild()
	{
		Location location = mock(Location.class);
		when(location.getDateformat()).thenReturn("dd/MM/yyyy");
		when(localise.getLocation()).thenReturn(location);
		Affiliates affiliate = mock(Affiliates.class);
		List<PurchaseQuery> basketUpdateQueries = new ArrayList<>();
		PurchaseQuery purchaseQuery = mock(PurchaseQuery.class);
		when(purchaseQuery.getProductId()).thenReturn(1);
		when(purchaseQuery.getStartDate()).thenReturn("13/08/2019");
		basketUpdateQueries.add(purchaseQuery);
		List<SubscriptionProduct> products = EnhancedRandom.randomListOf(1, SubscriptionProduct.class);
		when(service.fetchSubscriptionProductByIds(any())).thenReturn(products);
		when(purchaseRequestbuilder.build(any(), anyInt(), anyInt(), any(), any(), any()))
				.thenReturn(EnhancedRandom.random(SubscriptionPurchaseRequest.class));
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean())).thenReturn("&#8364;10{pennies}.00{/pennies}");
		assertThat(builder.build(basketUpdateQueries, 1, 1, affiliate, mock(AffiliateSubscriptionSettings.class)));
		verify(service).fetchSubscriptionProductByIds(any());
		verify(purchaseRequestbuilder).build(any(), anyInt(), anyInt(), any(), any(), any());
		verifyNoMoreInteractions(service, purchaseRequestbuilder);
	}

	@Test
	void testGetType()
	{
		assertThat(builder.getType()).isEqualTo(BuilderType.BASKET);
	}
}