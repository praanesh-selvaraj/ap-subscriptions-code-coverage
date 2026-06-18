package com.kmp.aeroparker.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;

@Configuration
public class RequestBeanConfig
{
	@Value("${global.properties.region}")
	private String region;
	@Value("${s3bucket.region:${global.properties.region}}")
	private String s3Region;

	@Bean
	@RequestScope
	public SubscriptionConfigBean subscriptionConfigBean()
	{
		return new SubscriptionConfigBean();
	}
	
	@Bean
	@RequestScope
	public AmazonS3 s3client()
	{
		return AmazonS3ClientBuilder.standard()
				.withRegion(s3Region)
				.build();
	}

	@Bean
	public AmazonSQS amazonSQS()
	{
		return AmazonSQSClientBuilder.standard()
				.withRegion(region)
				.build();
	}
}