package com.kmp.aeroparker.exceptions;

public class FilterException extends Exception
{
	private static final long serialVersionUID = 1L;

	public FilterException(final String messageDetail)
	{
		super(messageDetail);
	}
}