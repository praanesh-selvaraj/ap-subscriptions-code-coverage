package com.kmp.aeroparker.application.model.enums;

public enum SubscriptionEmailType
{
	CONFIRMATION, 
	CHARGE, 
	NOTICE_OF_TERMINATION;

	public static String getStrEnum(String strValue)
	{
		return !strValue.equals("") ? SubscriptionEmailType.valueOf(strValue.toUpperCase())
				.toString() : SubscriptionEmailType.CONFIRMATION.toString();
	}
}