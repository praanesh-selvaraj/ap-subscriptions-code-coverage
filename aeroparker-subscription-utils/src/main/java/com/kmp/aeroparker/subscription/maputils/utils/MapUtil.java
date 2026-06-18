package com.kmp.aeroparker.subscription.maputils.utils;

import java.util.Map;
import java.util.Map.Entry;

public final class MapUtil
{
	private MapUtil()
	{
	}
	
	public static <K, V> Entry<K, V> getEntryByValue(Map<K, V> map, V value)
	{
		Entry<K, V> foundEntry = null;
		if (map == null)
		{
			return foundEntry;
		}
		
		if (map.containsValue(value))
		{
			foundEntry = map.entrySet().stream().filter(x -> x.getValue().equals(value)).findFirst().orElse(null);
		}
		return foundEntry;
	}
}