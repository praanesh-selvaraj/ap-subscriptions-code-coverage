package com.kmp.aeroparker.application.utils;

public class StringUtil
{
	/**
	 * Check whether the supplied string is null or empty
	 * 
	 * @param input
	 * @return true if the string is null or has zero length
	 */
	public static boolean isNullOrEmpty(String input)
	{
		return input == null || input.length() == 0 || input.equals("null");
	}

	/**
	 * Checks that both strings are equal, if either are null this will return false
	 * 
	 * @param str1 first String to compare
	 * @param str2 second String to compare
	 * @return true if the strings are equal and not null
	 */
	public static boolean isEqual(String str1, String str2)
	{
		if (str1 == null || str2 == null)
		{
			return false;
		}
		else
		{
			return str1.equals(str2);
		}
	}
}
