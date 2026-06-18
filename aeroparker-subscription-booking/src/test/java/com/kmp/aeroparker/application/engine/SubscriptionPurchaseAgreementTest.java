package com.kmp.aeroparker.application.engine;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.availability.PriceDetails;

class SubscriptionPurchaseAgreementTest
{
	private final SubscriptionPurchaseAgreement agreement = new SubscriptionPurchaseAgreement();

	@Test
	void testGetGrandTotal()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setPriceDetails(priceDetails);
		assertThat(agreement.getGrandTotal()).isEqualTo(BigDecimal.TEN.setScale(2));
	}

	@Test
	void testGetProductPrice()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setPriceDetails(priceDetails);
		assertThat(agreement.getProductPrice()).isEqualTo(BigDecimal.TEN.setScale(2));
	}

	@Test
	void testGetGrandTotal_With_BookingFee()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setBookingFee(BigDecimal.ONE);
		agreement.setPriceDetails(priceDetails);
		assertThat(agreement.getGrandTotal()).isEqualTo(new BigDecimal(11).setScale(2));
	}

	@Test
	void testGetGrandTotal_With_BookingFee_Zero()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setBookingFee(BigDecimal.ZERO);
		agreement.setPriceDetails(priceDetails);
		assertThat(agreement.getGrandTotal()).isEqualTo(new BigDecimal(10).setScale(2));
	}

	@Test
	void testGetGrandTotal_With_BookingFee_Negative()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setBookingFee(new BigDecimal(-10));
		agreement.setPriceDetails(priceDetails);
		assertThat(agreement.getGrandTotal()).isEqualTo(new BigDecimal(10).setScale(2));
	}

	@Test
	void testGetVatAmount()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setPriceDetails(priceDetails);
		agreement.setStateTax(BigDecimal.ZERO);
		agreement.setVatRate(BigDecimal.TEN);
		agreement.setBookingFee(BigDecimal.ZERO);
		BigDecimal bigDecimal = new BigDecimal(0.91);
		assertThat(agreement.getVatAmount()).isEqualTo(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));
	}

	@Test
	void testGetProductPrice_WithPromoDiscount()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal("100.00"));
		agreement.setPriceDetails(priceDetails);
		agreement.setPromoDiscount(new BigDecimal("10.00"));
		agreement.setPromoCode("SAVE10");

		assertThat(agreement.getProductPrice()).isEqualByComparingTo(new BigDecimal("90.00"));
	}

	@Test
	void testGetProductPrice_PromoExceedsPrice()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal("50.00"));
		agreement.setPriceDetails(priceDetails);
		agreement.setPromoDiscount(new BigDecimal("100.00"));
		agreement.setPromoCode("SAVE100");

		assertThat(agreement.getProductPrice()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void testGetGrandTotal_WithPromoDiscount()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal("100.00"));
		agreement.setPriceDetails(priceDetails);
		agreement.setBookingFee(new BigDecimal("5.00"));
		agreement.setPromoDiscount(new BigDecimal("10.00"));
		agreement.setPromoCode("SAVE10");

		assertThat(agreement.getGrandTotal()).isEqualByComparingTo(new BigDecimal("95.00"));
	}

	@Test
	void testGetGrandTotal_WithPromoAndRecurringPrice()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal("100.00"));
		agreement.setPriceDetails(priceDetails);
		agreement.setBookingFee(new BigDecimal("5.00"));
		agreement.setRecurringProductPrice(new BigDecimal("20.00"));
		agreement.setPromoDiscount(new BigDecimal("10.00"));
		agreement.setPromoCode("SAVE10");

		assertThat(agreement.getGrandTotal()).isEqualByComparingTo(new BigDecimal("75.00"));
	}

	@Test
	void testGetGrandTotal_PromoExceedsTotal()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal("50.00"));
		agreement.setPriceDetails(priceDetails);
		agreement.setBookingFee(new BigDecimal("5.00"));
		agreement.setPromoDiscount(new BigDecimal("100.00"));
		agreement.setPromoCode("SAVE100");

		assertThat(agreement.getGrandTotal()).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Test
	void testGetOriginalGrandTotal_NotAffectedByPromo()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(new BigDecimal("100.00"));
		agreement.setPriceDetails(priceDetails);
		agreement.setBookingFee(new BigDecimal("5.00"));
		agreement.setPromoDiscount(new BigDecimal("10.00"));
		agreement.setPromoCode("SAVE10");

		assertThat(agreement.getOriginalGrandTotal()).isEqualByComparingTo(new BigDecimal("105.00"));
	}

	@Test
	void testPromoCodeGetterSetter()
	{
		agreement.setPromoCode("TESTCODE");
		assertThat(agreement.getPromoCode()).isEqualTo("TESTCODE");
	}

	@Test
	void testPromoDiscountGetterSetter()
	{
		agreement.setPromoDiscount(new BigDecimal("25.50"));
		assertThat(agreement.getPromoDiscount()).isEqualByComparingTo(new BigDecimal("25.50"));
	}

	@Test
	void testGetOriginalProductPrice()
	{
		PriceDetails priceDetails = new PriceDetails();
		priceDetails.setPrice(BigDecimal.TEN);
		agreement.setPriceDetails(priceDetails);
		assertThat(agreement.getOriginalProductPrice()).isEqualTo(BigDecimal.TEN.setScale(2));
	}
}
