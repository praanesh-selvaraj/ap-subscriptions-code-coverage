package com.kmp.aeroparker.subscription.string.utils;

public final class PhoneUtil
{
	private PhoneUtil()
	{
	}

	public static String normalise(final String phone)
	{
		if (phone == null || phone.trim().isEmpty())
		{
			return "";
		}
		return phone.trim().replaceAll("[\\s\\-()]", "");
	}

	public static boolean isValid(final String phone)
	{
		String normalised = normalise(phone);
		if (normalised.isEmpty())
		{
			return false;
		}
		return normalised.matches("^\\+?[0-9]{7,15}$");
	}

	public static String formatInternational(final String phone, final String defaultCountryCode)
	{
		String normalised = normalise(phone);
		if (normalised.isEmpty())
		{
			return "";
		}
		if (!normalised.startsWith("+"))
		{
			normalised = defaultCountryCode + normalised;
		}
		return normalised;
	}
}
