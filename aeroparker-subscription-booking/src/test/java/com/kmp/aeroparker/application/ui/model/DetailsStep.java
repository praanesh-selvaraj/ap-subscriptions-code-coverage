package com.kmp.aeroparker.application.ui.model;

import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;
import static com.codeborne.selenide.Selenide.switchTo;

public class DetailsStep
{
	public Confirmation enterDetailsAndSubmit()
	{
		if ($("#title").exists())
		{
			$("#title").selectOptionByValue("Mr");
			$("#title").shouldHave(value("Mr"));
		}

		if ($("#firstName").exists())
		{
			$("#firstName").setValue("test_first_name");
			$("#firstName").shouldHave(value("test_first_name"));
		}

		if ($("#lastName").exists())
		{
			$("#lastName").setValue("test_last_name");
			$("#lastName").shouldHave(value("test_last_name"));
		}
		if ($("#email").exists())
		{
			$("#email").setValue("test@aeroparker.com");
			$("#email").shouldHave(value("test@aeroparker.com"));
		}
		if ($("#contactNo").exists())
		{
			$("#contactNo").setValue("0123456789");
			$("#contactNo").shouldHave(value("0123456789"));
		}
		if ($("#addr1").exists())
		{
			$("#addr1").setValue("test_address_1");
			$("#addr1").shouldHave(value("test_address_1"));
		}
		if ($("#addr2").exists())
		{
			$("#addr2").setValue("test_address_2");
			$("#addr2").shouldHave(value("test_address_2"));
		}
		if ($("#town").exists())
		{
			$("#town").setValue("test_town");
			$("#town").shouldHave(value("test_town"));
		}
		if ($("#county").exists())
		{
			$("#county").selectOption(1);
			$("#county").shouldHave(value("Antrim"));
		}
		if ($("#postcode").exists())
		{
			$("#postcode").setValue("test_postcode");
			$("#postcode").shouldHave(value("test_postcode"));
		}
		switchTo().frame("braintree-hosted-field-number");
		$("#credit-card-number").setValue("4111111111111111");
		$("#credit-card-number").shouldHave(value("4111 1111 1111 1111"));
		switchTo().defaultContent()
				.switchTo()
				.frame("braintree-hosted-field-expirationDate");
		$("#expiration").setValue("1225");
		$("#expiration").shouldHave(value("12 / 25"));
		switchTo().defaultContent()
				.switchTo()
				.frame("braintree-hosted-field-cvv");
		$("#cvv").setValue("111");
		$("#cvv").shouldHave(value("111"));
		switchTo().defaultContent();
		if ($("#terms").exists())
		{
			$("#terms").click();
			$("#terms").shouldBe(checked);
		}
		$(".btn--submit").click();
		return page(Confirmation.class);
	}

	public ProductsStep breadcrumbSelectProduct()
	{
		$$(".breadcrumb__item").get(1)
				.click();
		return page(ProductsStep.class);
	}

	public ProductsStep backBtn()
	{
		$(".btn-backbtn").click();
		return page(ProductsStep.class);
	}
}