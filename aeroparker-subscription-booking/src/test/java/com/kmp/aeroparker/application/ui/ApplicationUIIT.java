package com.kmp.aeroparker.application.ui;

import static com.codeborne.selenide.Selenide.open;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kmp.aeroparker.application.base.UITestBase;
import com.kmp.aeroparker.application.container.ITMySQLContainer;
import com.kmp.aeroparker.application.ui.model.Confirmation;
import com.kmp.aeroparker.application.ui.model.DatesStep;
import com.kmp.aeroparker.application.ui.model.DetailsStep;
import com.kmp.aeroparker.application.ui.model.ProductsStep;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class ApplicationUIIT extends UITestBase
{
	@Container
	public final static ITMySQLContainer mysql = ITMySQLContainer.getInstance();
	@LocalServerPort
	private int port;

	@Test
	void testBooking() throws InterruptedException
	{
		DatesStep datesStep = open("http://localhost:" + port + "/subscriptions/snn/dates", DatesStep.class);
		ProductsStep productsStep = datesStep.submit();
		DetailsStep detailsStep = productsStep.selectProductAndSubmit();
		Confirmation confirmation = detailsStep.enterDetailsAndSubmit();
		confirmation.validate();
		// TODO Possible database checking to ensure the booking/contact have
		// been created correctly.
	}

	@Test
	void testBeadcrumpDates()
	{
		DatesStep datesStep = open("http://localhost:" + port + "/subscriptions/snn/dates", DatesStep.class);
		ProductsStep productsStep = datesStep.submit();
		datesStep = productsStep.breadcrumbDates();
		datesStep.validate();
	}

	@Test
	void testBeadcrumpSelectProduct()
	{
		DatesStep datesStep = open("http://localhost:" + port + "/subscriptions/snn/dates", DatesStep.class);
		ProductsStep productsStep = datesStep.submit();
		DetailsStep detailsStep = productsStep.selectProductAndSubmit();
		productsStep = detailsStep.breadcrumbSelectProduct();
		productsStep.validate();
	}

	@Test
	void testBackBtnSelectProduct()
	{
		DatesStep datesStep = open("http://localhost:" + port + "/subscriptions/snn/dates", DatesStep.class);
		ProductsStep productsStep = datesStep.submit();
		DetailsStep detailsStep = productsStep.selectProductAndSubmit();
		productsStep = detailsStep.backBtn();
		productsStep.validate();
	}

	@Test
	void testRemoveProduct()
	{
		DatesStep datesStep = open("http://localhost:" + port + "/subscriptions/snn/dates", DatesStep.class);
		ProductsStep productsStep = datesStep.submit();
		productsStep.removeProduct();
		productsStep.validate();
	}

	@Test
	void testBookingSearchSelectProduct()
	{
		DatesStep datesStep = open("http://localhost:" + port + "/subscriptions/snn/dates", DatesStep.class);
		ProductsStep productsStep = datesStep.submit();
		productsStep.selectProductSearch();
		productsStep.validate();
	}
}