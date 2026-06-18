package com.kmp.aeroparker.subscription.security.utils;

import java.security.Key;

public class MyKey implements Key
{
	private static final long serialVersionUID = 1L;
	private final byte[] keyBytes;
	private final String alg;

	public MyKey(String alg, String keyStr)
	{
		this.alg = alg;
		this.keyBytes = SecurityUtil.hexFromString(keyStr);
	}

	public String getAlgorithm()
	{
		return alg;
	}

	public String getFormat()
	{
		return "RAW";
	}

	public byte[] getEncoded()
	{
		return (byte[]) keyBytes.clone();
	}
}