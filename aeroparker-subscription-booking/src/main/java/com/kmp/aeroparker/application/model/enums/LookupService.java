package com.kmp.aeroparker.application.model.enums;

public enum LookupService
{
	MOTORCHECK_IE(2);

	private final int id;

	LookupService(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
}
