package com.kmp.aeroparker.application.ui.model;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class DatesStep
{
	public ProductsStep submit()
	{
		$(".btn--submit").click();
		return page(ProductsStep.class);
	}

	public void validate()
	{
		$(".booking-bar__date").find("label")
				.getText()
				.equalsIgnoreCase("Start Date");
		$(".booking-bar__col").find("input")
				.getValue()
				.equalsIgnoreCase("search");
	}
}