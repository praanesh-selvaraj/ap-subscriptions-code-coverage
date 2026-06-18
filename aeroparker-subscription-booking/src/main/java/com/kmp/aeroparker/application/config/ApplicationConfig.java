package com.kmp.aeroparker.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSManager;
import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSProducer;
import com.kmp.aeroparker.sqs.reports.ReportsSQSManager;
import com.kmp.aeroparker.sqs.reports.ReportsSQSProducer;

@Configuration
public class ApplicationConfig
{
	@Bean
	public FreemarkerSQSProducer getFreemarkerProducer()
	{
		return new FreemarkerSQSProducer(new FreemarkerSQSManager());
	}

	@Bean
	public ReportsSQSProducer getReportsProducer()
	{
		return new ReportsSQSProducer(new ReportsSQSManager());
	}
}
