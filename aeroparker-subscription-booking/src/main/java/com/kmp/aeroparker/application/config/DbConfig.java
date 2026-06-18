package com.kmp.aeroparker.application.config;

import javax.sql.DataSource;

import org.jooq.ConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
public class DbConfig
{
	private final DataSource dataSource;

	@Bean
	@RequestScope
	public CustomConnectionProvider connectionProvider()
	{
		return new CustomConnectionProvider(dataSource);
	}

	@Bean
	@RequestScope
	public ConnectionProvider customConnectionProvider()
	{
		return connectionProvider();
	}
}