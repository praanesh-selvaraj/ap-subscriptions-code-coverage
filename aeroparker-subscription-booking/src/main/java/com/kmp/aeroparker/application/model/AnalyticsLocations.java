package com.kmp.aeroparker.application.model;

public enum AnalyticsLocations
{
	SUBSCRIPTION_CONFIRMATION(39, "Subscription Confirmation"),
	SUBSCRIPTION_PAYMENT_DETAIL(40, "Subscription Payment Detail"),
	UNKNOWN(Integer.MIN_VALUE, "Unknown");

	private int id;
	private String label;

	/**
	 * @param id
	 * @param label
	 * @return
	 */
	private AnalyticsLocations(int id, String label)
	{
		this.id = id;
		this.label = label;
	}

	public int getId()
	{
		return id;
	}

	public String getLabel()
	{
		return label;
	}

	public static AnalyticsLocations fromId(int id)
	{
		AnalyticsLocations location = UNKNOWN;
		for (AnalyticsLocations value : values())
		{
			if (value.getId() == id)
			{
				location = value;
			}
		}
		return location;
	}
}
