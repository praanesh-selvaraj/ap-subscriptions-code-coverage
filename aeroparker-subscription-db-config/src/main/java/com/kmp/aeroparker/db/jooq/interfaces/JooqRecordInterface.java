package com.kmp.aeroparker.db.jooq.interfaces;

import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataChangedException;

/**
 * @author glyn.pritchard
 * 
 * Interface to be implemented by Jooq Records
 *
 */
public interface JooqRecordInterface
{
	public default Integer getId()
	{
		throw new UnsupportedOperationException("Method 'getId()' not implemented by Record.");
	}

	public default Integer setId()
	{
		throw new UnsupportedOperationException("Method 'setId()' not implemented by Record.");
	}

	default int insert() throws DataAccessException
	{
		throw new UnsupportedOperationException("Method 'insert()' not implemented by Record.");
	}

	default int update() throws DataAccessException, DataChangedException
	{
		throw new UnsupportedOperationException("Method 'update()' not implemented by Record.");
	}

	/**
	 * Save this record, returns the Id
	 * 
	 * @return
	 * @throws DataAccessException
	 * @throws DataChangedException
	 */
	public default int save() throws DataAccessException, DataChangedException
	{
		if (getId() == null || getId() == 0)
		{
			insert();
		}
		else
		{
			update();
		}

		return getId();
	}
}
