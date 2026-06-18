package com.kmp.aeroparker.application.ui.model;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.conditions.Visible;

public class ProductsStep
{
	public DetailsStep selectProductAndSubmit()
	{
		$$(".subscribe-btn").get(0)
				.click();
		$(".booking-summary").shouldBe(Visible.visible)
		.shouldHave(text("€20"));
		$(".btn--submit").click();
		return page(DetailsStep.class);
	}

	public DatesStep breadcrumbDates()
	{
		$$(".breadcrumb__item").get(0)
				.click();
		return page(DatesStep.class);
	}

	public void removeProduct()
	{
		$$(".subscribe-btn").get(0)
				.click();
		$(".booking-summary").shouldHave(text("€20"));
		$(".btn-added").find("span")
				.click();
		$(".booking-summary").shouldHave(text("€0"));
		$$(".subscribe-btn").get(0)
				.click();
	}

	public void validate()
	{
		$(".booking-summary__title").exists();
		$(".navbar-left").find("h1")
				.getText()
				.equalsIgnoreCase("select product");
	}

	public void selectProductSearch()
	{
		// Open drop down
		$(".fa-angle-down").click();
		// Get all the td closer to the active one
		SelenideElement parentTr = $(".datepicker-dropdown").find("div")
				.find("table")
				.find("td.active")
				.parent();

		ElementsCollection allTd = parentTr.findAll("td");
		// Get the selected td
		SelenideElement selected = $(".datepicker-dropdown").find("div")
				.find("table")
				.find("td.active");
		// Get the position of the selected td
		int elementPos = allTd.indexOf(selected);
		// Increase the position to the next one
		int nextElementPos = elementPos + 1;
		SelenideElement element = null;
		if (nextElementPos > 6)
		{
			// we some how have to get the next tr with and choose a date
			ElementsCollection allTr = $(".datepicker-dropdown").find("div")
					.find("table")
					.find("td.active")
					.parent()
					.parent()
					.findAll("tr");
			int elementTrPos = allTr.indexOf(parentTr);
			int nextTrElementPos = elementTrPos + 1;
			element = allTr.get(nextTrElementPos)
					.findAll("td")
					.get(0);
		}
		else
		{
			// get the next td from the active one
			element = $(".datepicker-dropdown").find("div")
					.find("table")
					.find("td.active")
					.parent()
					.findAll("td")
					.get(nextElementPos);
			// click on it
		}
		element.click();

		// reopen the drop down so the date picker tags are visible
		$(".fa-angle-down").click();
		// check if the date picket has the correct date selected
		element.shouldHave(attribute("class", "active selected range-start range-end day"));
		$(".w-change-date__action").click();
	}
}