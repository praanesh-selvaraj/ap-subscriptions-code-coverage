package com.kmp.aeroparker.application.model.enums;

public enum SubscriptionMinimumTerm
{
	RECURRING(0),
	ONE_MONTH(1),
	TWO_MONTHS(2),
	THREE_MONTHS(3),
	FOUR_MONTHS(4),
	FIVE_MONTHS(5),
	SIX_MONTHS(6),
	SEVEN_MONTHS(7),
	EIGHT_MONTHS(8),
	NINE_MONTHS(9),
	TEN_MONTHS(10),
	ELEVEN_MONTHS(11),
	TWELVE_MONTHS(12);

	private int intValue;

	private SubscriptionMinimumTerm(final int intValue)
	{
		this.intValue = intValue;
	}

	public int getIntValue()
	{
		return intValue;
	}
}