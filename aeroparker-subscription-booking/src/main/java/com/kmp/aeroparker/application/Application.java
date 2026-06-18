package com.kmp.aeroparker.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.kmp.aeroparker.l10n", "com.kmp.aeroparker.application", "com.kmp.aeroparker.i18n", "com.kmp.aeroparker.subscription.payments" })
public class Application
{
	public static void main(final String[] args)
	{
		SpringApplication.run(Application.class, args);
	}
}