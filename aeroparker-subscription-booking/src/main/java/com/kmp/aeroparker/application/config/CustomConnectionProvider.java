package com.kmp.aeroparker.application.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Value;

import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomConnectionProvider implements ConnectionProvider
{
	private final DataSource dataSource;
	private String schema;
	@Value("${test.schema:#{null}}")
	private String testSchema;

	@Override
	public Connection acquire() throws DataAccessException
	{
		Connection connection = null;
		try
		{
			connection = dataSource.getConnection();
			String currentSchema = StringUtil.coalesce("tenants", testSchema, schema);
			connection.setCatalog(currentSchema);
		}
		catch (final SQLException e)
		{
			log.error("Error creating DSL context for schema {}", "", e);
		}
		return connection;
	}

	@Override
	public void release(final Connection connection) throws DataAccessException
	{
		try
		{
			connection.close();
		}
		catch (final SQLException e)
		{
			log.error("Error closing connection", e);
		}
	}

	public void setSchema(final String schema)
	{
		this.schema = schema;
	}

	public String getSchema()
	{
		return StringUtil.coalesce("tenants", testSchema, schema);
	}
}