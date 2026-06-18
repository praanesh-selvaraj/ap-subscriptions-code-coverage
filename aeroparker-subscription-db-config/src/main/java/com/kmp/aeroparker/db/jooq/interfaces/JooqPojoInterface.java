package com.kmp.aeroparker.db.jooq.interfaces;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;

public interface JooqPojoInterface
{
	public default Integer getId()
	{
		throw new UnsupportedOperationException("Method 'getId()' not implemented by POJO.");
	}

	public default void setId(Integer id)
	{
		throw new UnsupportedOperationException("Method 'setId()' not implemented by POJO.");
	}

	/**
	 * Save this POJO, updates the id on insert
	 * 
	 * @param relevant
	 *            table for this POJO
	 * @throws Exception
	 */
	public default <R extends Record, T extends Table<R>> boolean save(T table, DSLContext dsl)
	{
		JooqRecordInterface record = (JooqRecordInterface) dsl.newRecord(table, this);
		setId(record.save());
		if (getId() > 0)
		{
			return true;
		}
		return false;
	}
}