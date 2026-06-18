package com.kmp.aeroparker.application.base;

import org.junit.jupiter.api.BeforeAll;

import com.codeborne.selenide.Configuration;

public abstract class UITestBase
{
	@BeforeAll
	static void initAll()
	{
		Configuration.headless = true;
	}
}