package com.kmp.aeroparker.application.ui.model;

import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Confirmation
{
	public void validate()
	{
		$(".confirmation__detail").shouldHave(text("test_first_name"), text("test_last_name"), text("Sub1"));
		$(".confirmation__item").find("h2")
				.shouldHave(matchText("Booking Reference.*SNWSC100187"));
	}
}