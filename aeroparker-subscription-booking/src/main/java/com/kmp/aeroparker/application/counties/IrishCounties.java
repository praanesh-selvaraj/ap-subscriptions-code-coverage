package com.kmp.aeroparker.application.counties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class IrishCounties
{
	private IrishCounties()
	{
	}

	private static Map<String, String> irishCounties = new HashMap<String, String>();

	static
	{
		irishCounties.put("Antrim", "Antrim");
		irishCounties.put("Armagh", "Armagh");
		irishCounties.put("Carlow", "Carlow");
		irishCounties.put("Cavan", "Cavan");
		irishCounties.put("Clare", "Clare");
		irishCounties.put("Cork", "Cork");
		irishCounties.put("Derry", "Derry");
		irishCounties.put("Donegal", "Donegal");
		irishCounties.put("Down", "Down");
		irishCounties.put("Dublin", "Dublin");
		irishCounties.put("Fermanagh", "Fermanagh");
		irishCounties.put("Galway", "Galway");
		irishCounties.put("Kerry", "Kerry");
		irishCounties.put("Kildare", "Kildare");
		irishCounties.put("Kilkenny", "Kilkenny");
		irishCounties.put("Laois", "Laois");
		irishCounties.put("Leitrim", "Leitrim");
		irishCounties.put("Limerick", "Limerick");
		irishCounties.put("Longford", "Longford");
		irishCounties.put("Louth", "Louth");
		irishCounties.put("Mayo", "Mayo");
		irishCounties.put("Meath", "Meath");
		irishCounties.put("Monaghan", "Monaghan");
		irishCounties.put("Offaly", "Offaly");
		irishCounties.put("Roscommon", "Roscommon");
		irishCounties.put("Sligo", "Sligo");
		irishCounties.put("Tipperary", "Tipperary");
		irishCounties.put("Tyrone", "Tyrone");
		irishCounties.put("Waterford", "Waterford");
		irishCounties.put("Westmeath", "Westmeath");
		irishCounties.put("Wexford", "Wexford");
		irishCounties.put("Wicklow", "Wicklow");
		irishCounties = Collections.unmodifiableMap(irishCounties);
	}

	public static Map<String, String> getIrishCounties()
	{
		return irishCounties;
	}
}