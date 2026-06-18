package com.kmp.aeroparker.application.model.enums;

public enum AffiliateCrmOptIn
{
	TYPE_EMAIL(1, "emailOptIn"), TYPE_SMS(2, "smsOptIn");

	private int id;
	private String name;

	private AffiliateCrmOptIn(int id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public static AffiliateCrmOptIn getById(int id)
	{
		AffiliateCrmOptIn affiliateCrmOptIn = null;
		for (AffiliateCrmOptIn crmOptIn : values())
		{
			if (crmOptIn.getId() == id)
			{
				affiliateCrmOptIn = crmOptIn;
			}
		}
		return affiliateCrmOptIn;
	}
}