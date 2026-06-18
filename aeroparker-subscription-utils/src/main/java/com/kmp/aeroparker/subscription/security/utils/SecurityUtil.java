/** 
 * Copyright KMP Associates Ltd. 10-Jan-05. 
 * 
 * All rights are reserved. Reproduction or transmission in whole or in part, in 
 * any form or by any means, electronic, mechanical or otherwise, is prohibited 
 * without the prior written consent of the copyright owner. 
 *  
 */
package com.kmp.aeroparker.subscription.security.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import cryptix.jce.provider.CryptixCrypto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SecurityUtil
{
	private static final char[] HEX_DIGITS =
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static Cipher encryptCipher = null;
	private static Cipher decryptCipher = null;
	private static boolean initialized = false;
	
	private SecurityUtil()
	{
	}

	public static String tosha256(final String dataString)
	{
		String returnString = "";
		try
		{
			MessageDigest md = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
			md.update(dataString.getBytes(StandardCharsets.UTF_8));
			byte[] mdbytes = md.digest();
			returnString = DatatypeConverter.printHexBinary(mdbytes);
		}
		catch (NoSuchAlgorithmException e)
		{
			log.error("Error creating sha256 {}, {}", e.getMessage(), e);
		}
		return returnString;
	}

	public static String encodeStringBase64(final String string)
	{
		String encodedString = "";
		if (!StringUtil.isEmpty(string))
		{
			encodedString = Base64.getEncoder()
					.encodeToString(string.getBytes());
		}
		return encodedString;
	}

	public static void init()
	{
		log.debug("Initializing Security Util");
		String provider = "CryptixCrypto";
		String transformation = "Blowfish/ECB/PKCS#5";

		try
		{
			addProviderCryptix();
			encryptCipher = Cipher.getInstance(transformation, provider);
			decryptCipher = Cipher.getInstance(transformation, provider);

			MyKey key = new MyKey("Blowfish", "53869A3EE1234443333344DDEEEDFDDDDDDDDDDD3343333333333333");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
			initialized = true;
			log.debug("Security Util Initialized");
		}
		catch (InvalidKeyException e)
		{
			log.error(e.getMessage(), e);
		}
		catch (NoSuchAlgorithmException e)
		{
			log.error(e.getMessage(), e);
		}
		catch (NoSuchProviderException e)
		{
			log.error(e.getMessage(), e);
		}
		catch (NoSuchPaddingException e)
		{
			log.error(e.getMessage(), e);
		}
	}

	private static boolean addProviderCryptix()
	{
		// First add the provider (in this case cryptix) dynamicly to the
		// provider list. If this should be staticly edit the java.security
		// file and add the line:
		// security.provider.X=cryptix.jce.provider.Cryptix
		// where X specifies the desired priority of the provider in the
		// provider list.

		// create a new Cryptix provider.
		Provider cryptix_provider = new CryptixCrypto();

		// Result either returns the position of the provider or -1 if the
		// provider already exists.
		int result = Security.addProvider(cryptix_provider);

		if (result == -1)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public static String encryptString(String stringToEncrypt)
	{
		log.debug("String to encrypt: " + stringToEncrypt);
		if (!initialized)
		{
			log.debug("SecurityUtil not initialized");
			init();
		}

		byte[] enc = null;
		try
		{
			enc = encryptCipher.doFinal(stringToEncrypt.getBytes());
		}
		catch (Exception e)
		{
			log.error("Error encrypting string: " + stringToEncrypt, e);
			// For some reason we need to reinit after this has happened
			initialized = false;
		}

		String ret = (enc == null) ? "" : toString(enc);
		log.debug("Encrypted String: " + ret);
		return ret;
	}

	public static String decryptString(String stringToDecrypt)
	{
		if (!initialized)
		{
			init();
		}

		byte[] dec = null;

		try
		{
			dec = decryptCipher.doFinal(hexFromString(stringToDecrypt));
		}
		catch (Exception e)
		{
			// For some reason we need to reinit after this has happened
			initialized = false;
		}

		String ret = (dec == null) ? "" : new String(dec);
		return ret;
	}

	static String toString(byte[] ba)
	{
		int length = ba.length;
		char[] buf = new char[length * 2];
		for (int i = 0, j = 0, k; i < length;)
		{
			k = ba[i++];
			buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
			buf[j++] = HEX_DIGITS[k & 0x0F];
		}
		return new String(buf);
	}

	static byte[] hexFromString(String hex)
	{
		byte[] buf = null;
		try
		{
			int len = hex.length();
			buf = new byte[((len + 1) / 2)];

			int i = 0, j = 0;
			if ((len % 2) == 1)
			{
				buf[j++] = (byte) fromDigit(hex.charAt(i++));
			}

			while (i < len)
			{
				buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) | fromDigit(hex.charAt(i++)));
			}

		}
		catch (RuntimeException e)
		{
			log.info("SecurityUtil::hexFromString: RuntimeException:" + e);
		}
		return buf;
	}

	static int fromDigit(char ch)
	{
		if (ch >= '0' && ch <= '9')
		{
			return ch - '0';
		}
		if (ch >= 'A' && ch <= 'F')
		{
			return ch - 'A' + 10;
		}
		if (ch >= 'a' && ch <= 'f')
		{
			return ch - 'a' + 10;
		}
		return 0;
	}
}