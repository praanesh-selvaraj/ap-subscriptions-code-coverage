package com.kmp.aeroparker.application.factory;

import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class LocaleFactory
{
	public Locale[] getAvailableLocales()
	{
		 return Locale.getAvailableLocales();
	}
}