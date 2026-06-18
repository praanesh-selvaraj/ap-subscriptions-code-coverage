package com.kmp.aeroparker.application.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.availability.PriceDetails;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;
import com.kmp.aeroparker.l10n.Localise;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class BasketTest
{
	private final Basket basket = new Basket();

	@Test
	void testGetGrandTotalWithoutBookingFee()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.getGrandTotalWithoutBookingFee()).isNotNull()
				.isEqualTo(purchaseRequest.getProductPrice());
	}

	@Test
	void testGetGrandTotalWithoutBookingFee_Negative()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal(-1));
		agreement.setPriceDetails(priceDetails);
		purchaseRequest.setPurchaseAgreement(agreement);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.getGrandTotalWithoutBookingFee()).isNotNull()
				.isEqualTo(BigDecimal.ZERO);
	}

	@Test
	void testGetGrandTotal()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.getGrandTotal()).isNotNull()
				.isEqualTo(purchaseRequest.getGrandTotal());
	}

	@Test
	void testGetGrandTotal_Price_Zero()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal(-1));
		agreement.setPriceDetails(priceDetails);
		purchaseRequest.setPurchaseAgreement(agreement);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.getGrandTotal()).isZero();
	}

	@Test
	void testGetAllProductIds()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.getAllProductIds()).isNotEmpty()
				.hasSize(purchaseRequestList.size());
	}

	@Test
	void testGetGrandTotalVatAmount()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal(20.25));
		agreement.setPriceDetails(priceDetails);
		agreement.setStateTax(BigDecimal.ZERO);
		agreement.setBookingFee(BigDecimal.ZERO);
		agreement.setVatRate(BigDecimal.TEN);
		purchaseRequest.setPurchaseAgreement(agreement);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.getGrandTotalVatAmount()).isNotNull()
				.isEqualTo("1.84");
	}

	@Test
	void testGetTotalBookingFee()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.getTotalBookingFee()).isNotNull()
				.isEqualTo(purchaseRequest.getBookingFee());
	}

	@Test
	void testIsRecurring()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequest.setPeriodType(SubscriptionPeriodType.RECURRING);
		purchaseRequestList.add(purchaseRequest);
		SubscriptionPurchaseRequest purchaseRequestTwo = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequestTwo.setPeriodType(SubscriptionPeriodType.FIXED);
		purchaseRequestList.add(purchaseRequestTwo);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.isRecurring()).isTrue();
	}

	@Test
	void testInitialize()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		Localise localise = mock(Localise.class);
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
		basket.initialize(localise);
		assertThat(basket.getPriceIncludePennyPlaceholdersHtml()).isNotNull()
				.isEqualTo("&#8364;0<span class='booking-summary__item__val--total__pence'>.00</span>");
	}

	@Test
	void testPromoCodeGetterSetter()
	{
		basket.setPromoCode("SAVE10");
		assertThat(basket.getPromoCode()).isEqualTo("SAVE10");
	}

	@Test
	void testPromoDiscountGetterSetter()
	{
		BigDecimal discount = new BigDecimal("25.00");
		basket.setPromoDiscount(discount);
		assertThat(basket.getPromoDiscount()).isEqualByComparingTo(discount);
	}

	@Test
	void testGetOriginalGrandTotal()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.getOriginalGrandTotal()).isNotNull()
				.isEqualTo(purchaseRequest.getOriginalGrandTotal());
	}

	@Test
	void testGetOriginalGrandTotal_NegativePrice()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal(-10));
		agreement.setPriceDetails(priceDetails);
		purchaseRequest.setPurchaseAgreement(agreement);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.getOriginalGrandTotal()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	void testGetGrandTotal_WithPromoDiscount()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);

		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal("100.00"));
		agreement.setPriceDetails(priceDetails);
		agreement.setBookingFee(new BigDecimal("5.00"));
		agreement.setPromoDiscount(new BigDecimal("10.00"));
		agreement.setPromoCode("SAVE10");

		purchaseRequest.setPurchaseAgreement(agreement);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		basket.setPromoDiscount(new BigDecimal("10.00"));
		basket.setPromoCode("SAVE10");

		assertThat(basket.getGrandTotal()).isEqualByComparingTo(new BigDecimal("95.00"));
	}

	@Test
	void testInitialize_WithPromoDiscount()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);

		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal("100.00"));
		agreement.setPriceDetails(priceDetails);
		agreement.setBookingFee(new BigDecimal("5.00"));
		agreement.setPromoDiscount(new BigDecimal("10.00"));
		agreement.setPromoCode("SAVE10");

		purchaseRequest.setPurchaseAgreement(agreement);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		basket.setPromoDiscount(new BigDecimal("10.00"));
		basket.setPromoCode("SAVE10");

		Localise localise = mock(Localise.class);
		when(localise.priceIncludePennyPlaceholders(anyFloat(), anyBoolean()))
				.thenReturn("95");

		basket.initialize(localise);

		assertThat(basket.getPriceIncludePennyPlaceholdersHtml()).isNotNull()
				.contains("95");
	}

	@Test
	void testGetAllProductIds_EmptyList()
	{
		basket.setPurchaseRequestList(new PurchaseRequestList());
		assertThat(basket.getAllProductIds()).isEmpty();
	}

	@Test
	void testGetTotalBookingFee_MultiplePurchaseRequests()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();

		SubscriptionPurchaseRequest purchaseRequest1 = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		SubscriptionPurchaseAgreement agreement1 = new SubscriptionPurchaseAgreement();
		agreement1.setBookingFee(new BigDecimal("5.00"));
		purchaseRequest1.setPurchaseAgreement(agreement1);

		SubscriptionPurchaseRequest purchaseRequest2 = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		SubscriptionPurchaseAgreement agreement2 = new SubscriptionPurchaseAgreement();
		agreement2.setBookingFee(new BigDecimal("3.00"));
		purchaseRequest2.setPurchaseAgreement(agreement2);

		purchaseRequestList.add(purchaseRequest1);
		purchaseRequestList.add(purchaseRequest2);
		basket.setPurchaseRequestList(purchaseRequestList);

		assertThat(basket.getTotalBookingFee()).isEqualByComparingTo(new BigDecimal("8.00"));
	}

	@Test
	void testIsRecurring_NoRecurringTickets()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);
		purchaseRequest.setPeriodType(SubscriptionPeriodType.FIXED);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		assertThat(basket.isRecurring()).isFalse();
	}

	@Test
	void testGetOriginalGrandTotal_WithPromoDiscount()
	{
		PurchaseRequestList purchaseRequestList = new PurchaseRequestList();
		SubscriptionPurchaseRequest purchaseRequest = EnhancedRandom.random(SubscriptionPurchaseRequest.class);

		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal("100.00"));
		agreement.setPriceDetails(priceDetails);
		agreement.setBookingFee(new BigDecimal("5.00"));
		agreement.setPromoDiscount(new BigDecimal("10.00"));
		agreement.setPromoCode("SAVE10");

		purchaseRequest.setPurchaseAgreement(agreement);
		purchaseRequestList.add(purchaseRequest);
		basket.setPurchaseRequestList(purchaseRequestList);
		basket.setPromoDiscount(new BigDecimal("10.00"));
		basket.setPromoCode("SAVE10");

		// Original grand total should NOT include promo discount
		assertThat(basket.getOriginalGrandTotal()).isEqualByComparingTo(new BigDecimal("105.00"));
		// Grand total SHOULD include promo discount
		assertThat(basket.getGrandTotal()).isEqualByComparingTo(new BigDecimal("95.00"));
	}
}