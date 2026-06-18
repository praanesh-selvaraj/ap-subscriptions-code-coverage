package com.kmp.aeroparker.subscription.string.utils;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.util.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StringUtil extends StringUtils
{
	private StringUtil()
	{
	}

	/**
	 * Convert a string to an int. Use the default value if parsing fails
	 * 
	 * @param parameter
	 * @param defaultValue
	 * @return
	 */
	public static int strToInt(final String parameter, final int defaultValue)
	{
		try
		{
			return Integer.parseInt(parameter);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	/**
	 * Checks that both strings are equal, if either are null this will return
	 * false
	 * 
	 * @param str1
	 *            first String to compare
	 * @param str2
	 *            second String to compare
	 * @return true if the strings are equal and not null
	 */
	public static boolean isEqual(final String str1, final String str2)
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

	/**
	 * Checks that both strings are equal once set to lower case and whitespace
	 * trimmed, if either are null this will return false.
	 * 
	 * @param str1
	 *            first String to compare
	 * @param str2
	 *            second String to compare
	 * @return true if strings are equal after being set to lowercase and
	 *         whitespace trimmed, and not null
	 */
	public static boolean isEqualIgnoreCase(final String str1, final String str2)
	{
		if (str1 != null && str2 != null)
		{
			return isEqual(str1.trim()
					.toLowerCase(),
					str2.trim()
							.toLowerCase());
		}
		return false;
	}

	/**
	 * Returns the string passed in if it is not null, otherwise returns an
	 * empty string ""
	 * 
	 * @param input
	 *            String to parse
	 * @return input if it is not null, otherwise an empty String
	 */
	public static String parse(final String input)
	{
		return parse(input, "");
	}

	/**
	 * Returns the string passed in if it is not null, otherwise it returns the
	 * value in strDefault
	 * 
	 * @param input
	 *            String to parse
	 * @param defaultVal
	 *            Default value to return if input is null
	 * @return input if it is not null, otherwise defaultVal
	 */
	public static String parse(final String input, final String defaultVal)
	{
		String value = (input == null) ? defaultVal : input;
		return value;
	}

	/**
	 * Similar Parse(String, String), but uses the default value if the input is
	 * empty
	 * 
	 * @param defaultVal
	 *            Default Value to use if input is null or empty
	 * @param strings
	 * @return String input or defaultValue depending on inputs value
	 */
	public static String coalesce(final String defaultVal, final String... strings)
	{
		String value = defaultVal;
		for (String s : strings)
		{
			if (s != null)
			{
				value = s;
				break;
			}
		}
		return value;
	}

	/**
	 * Check if the string contains the 'contains' argument. Allows case
	 * insensitivity
	 * 
	 * @param string
	 *            String to check whether the 'contains' string is within
	 * @param contains
	 * @param ignoreCase
	 *            true if case is to be ignored
	 * @return true if string contains 'contains'
	 */
	public static boolean contains(String string, String contains, final boolean ignoreCase)
	{
		boolean contained = false;
		if (string != null)
		{
			if (ignoreCase)
			{
				string = string.toLowerCase();
				contains = contains.toLowerCase();
			}
			if (string.indexOf(contains) > -1)
			{
				contained = true;
			}
		}
		return contained;
	}

	/**
	 * Checks that one of the strings in the list is equal to the given string.
	 * 
	 * @param str
	 *            first String to compare
	 * @param strList
	 *            Var args of string to compare
	 * @return
	 */
	public static boolean isEqualAtLeastOne(final String str, final String... strList)
	{
		return Arrays.asList(strList)
				.stream()
				.anyMatch(ln -> isEqualIgnoreCase(str, ln));
	}
	
	/**
	 * Convert a int to string.
	 * 
	 * @param value
	 * @return
	 */
	public static String intToStr(final int value)
	{
		return Integer.toString(value);
	}

	public static JsonObject toJsonObject(String string)
	{
		JsonObject responseJson = null;
		try
		{
			responseJson = new JsonParser().parse(string).getAsJsonObject();
		}
		catch (JsonSyntaxException e)
		{
			log.error("Error converting string to json object error {}", e.getMessage(), e);
		}
		return responseJson;
	}

	public static String trimToLength(String string, int length)
	{
		if (isEmpty(string))
		{
			string = "";
		}
		if (string.length() > length)
		{
			return string.substring(0, length);
		}
		return string;
	}

	public static BigDecimal strToBigDecimal(String input, BigDecimal defaultVal)
	{
		try
		{
			BigDecimal value = new BigDecimal(input);
			return value;
		}
		catch (Exception e)
		{
			return defaultVal;
		}
	}

	public static boolean strToBoolean(String input)
	{
		if (isEmpty(input))
		{
			return false;
		}
		input = input.toLowerCase();
		return input.equals("1") || input.equals("true") || input.equals("yes") || input.equals("on");
	}
	
	/**
	 * Check whether the supplied string is null or empty
	 * 
	 * @param input
	 * @return true if the string is null or has zero length
	 */
	public static boolean isNullOrEmpty(String input)
	{
		return !hasText(input) || input.equalsIgnoreCase("null");
	}
}