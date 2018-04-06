package com.socket.cipher;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
 
public class AESCipher {
 
	private final static String secretKey   = "aBpLKju890$!Fd00094pvvgisfe**4$!"; //32bit
	private static String IV = secretKey.substring(0, 16);

	public static Cipher getCipher(int CipherMode) throws InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {

		byte[] keyData = secretKey.getBytes();
		SecretKey secureKey = new SecretKeySpec(keyData, "AES");
		
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(CipherMode, secureKey, new IvParameterSpec(IV.getBytes("UTF-8")));
		
		return c;
	}

	//암호화
	public static String Encode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{

		byte[] bt = str.getBytes("UTF-8");
		String enStr = new String(Encode(bt));
				
		return enStr;
		 
	}

	public static byte[] Encode(byte[] bt) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{

		Cipher c = getCipher(Cipher.ENCRYPT_MODE);
		
		byte[] encrypted = c.doFinal(bt);
		
		return Base64.encodeBase64(encrypted);
		 
	}
	
	 //복호화
	public static String Decode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		
		byte[] bt = str.getBytes();
		
		return new String(Decode(bt), "UTF-8");
		
	}

	public static byte[] Decode(byte[] bt) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		
		Cipher c = getCipher(Cipher.DECRYPT_MODE);
		
		byte[] byteStr = Base64.decodeBase64(bt);
		 
		return c.doFinal(byteStr);
	}
	
}
