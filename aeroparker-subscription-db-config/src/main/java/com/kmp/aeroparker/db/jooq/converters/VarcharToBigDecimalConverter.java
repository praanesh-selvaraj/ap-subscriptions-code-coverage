package com.kmp.aeroparker.db.jooq.converters;

import java.math.BigDecimal;

import org.jooq.Converter;
import org.jooq.tools.StringUtils;

public class VarcharToBigDecimalConverter implements Converter<String, BigDecimal>
{
	private static final long serialVersionUID = 1L;

	@Override
	public BigDecimal from(String databaseObject)
	{
		if (StringUtils.isBlank(databaseObject))
		{
			return BigDecimal.ZERO;
		}
		return new BigDecimal(databaseObject);
	}

	@Override
	public String to(BigDecimal userObject)
	{
		return userObject == null ? null : userObject.toPlainString();
	}

	@Override
	public Class<String> fromType()
	{
		return String.class;
	}

	@Override
	public Class<BigDecimal> toType()
	{
		return BigDecimal.class;
	}
}
