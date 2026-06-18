package com.kmp.aeroparker.application.model.enums;

public enum SubscriptionPeriodType
{
	FIXED("Fixed", "Season Ticket"),
	RECURRING("Monthly auto-renew", "Recurring Ticket");

	private String name;
	private String type;

	SubscriptionPeriodType(final String name, final String type)
	{
		this.type = type;
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public static boolean isFixedTicket(final String name)
	{
		boolean result = false;
		SubscriptionPeriodType periodType = SubscriptionPeriodType.valueOf(name);

		if (periodType != null)
		{
			result = periodType.equals(SubscriptionPeriodType.FIXED);
		}
		return result;
	}
}