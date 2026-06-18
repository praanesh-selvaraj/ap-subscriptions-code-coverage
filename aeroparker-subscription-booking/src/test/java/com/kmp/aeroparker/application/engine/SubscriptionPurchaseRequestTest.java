package com.kmp.aeroparker.application.engine;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.availability.PriceDetails;
import com.kmp.aeroparker.application.model.enums.SubscriptionPeriodType;

class SubscriptionPurchaseRequestTest
{
	private final SubscriptionPurchaseRequest purchaseRequest = new SubscriptionPurchaseRequest();

	@Test
	void testGetGrandTotal()
	{
		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setPriceDetails(priceDetails);
		purchaseRequest.setPurchaseAgreement(agreement);
		assertThat(purchaseRequest.getGrandTotal()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_HALF_UP));
	}

	@Test
	void testGetTotalPrice_SubscriptionPurchaseAgreement_Null()
	{
		assertThat(purchaseRequest.getGrandTotal()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	void testGetProductPrice()
	{
		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setPriceDetails(priceDetails);
		purchaseRequest.setPurchaseAgreement(agreement);
		assertThat(purchaseRequest.getProductPrice()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_HALF_UP));
	}

	@Test
	void testGetProductPrice_SubscriptionPurchaseAgreement_Null()
	{
		assertThat(purchaseRequest.getProductPrice()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	void testIsRecurringTicket()
	{
		SubscriptionPurchaseRequest purchaseRequest = new SubscriptionPurchaseRequest();
		purchaseRequest.setPeriodType(SubscriptionPeriodType.RECURRING);
		assertThat(purchaseRequest.isRecurringTicket()).isTrue();
		assertThat(purchaseRequest.isSeasonTicket()).isFalse();
	}

	@Test
	void testIsSeasonTicket()
	{
		SubscriptionPurchaseRequest purchaseRequest = new SubscriptionPurchaseRequest();
		purchaseRequest.setPeriodType(SubscriptionPeriodType.FIXED);
		assertThat(purchaseRequest.isSeasonTicket()).isTrue();
		assertThat(purchaseRequest.isRecurringTicket()).isFalse();
	}

	@Test
	void testPeriodType_Null()
	{
		SubscriptionPurchaseRequest purchaseRequest = new SubscriptionPurchaseRequest();
		assertThat(purchaseRequest.isRecurringTicket()).isFalse();
		assertThat(purchaseRequest.isSeasonTicket()).isFalse();
	}

	@Test
	void testGetBookingFee()
	{
		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		agreement.setBookingFee(BigDecimal.TEN);
		purchaseRequest.setPurchaseAgreement(agreement);
		assertThat(purchaseRequest.getBookingFee()).isEqualTo(agreement.getBookingFee()
				.setScale(2, BigDecimal.ROUND_HALF_UP));
	}

	@Test
	void testGetBookingFee_Null()
	{
		assertThat(purchaseRequest.getBookingFee()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	void testGetOriginalProductPrice()
	{
		SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setPriceDetails(priceDetails);
		purchaseRequest.setPurchaseAgreement(agreement);
		assertThat(purchaseRequest.getOriginalProductPrice()).isEqualTo(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_HALF_UP));
	}
}