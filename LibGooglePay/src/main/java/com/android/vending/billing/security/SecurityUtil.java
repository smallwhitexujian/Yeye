package com.android.vending.billing.security;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {

	public static String encryptXor(String plainText, String secretKey) {
		String encryption = "";
		try {
			plainText = new String(plainText.getBytes("UTF-8"), "iso-8859-1");
			secretKey = new String(secretKey.getBytes("UTF-8"), "iso-8859-1");
		} catch (Exception e) {
		}
		char[] cipher = new char[plainText.length()];
		for (int i = 0, j = 0; i < plainText.length(); i++, j++) {
			if (j == secretKey.length()){
				j = 0;
			}
			cipher[i] = (char) (plainText.charAt(i) ^ secretKey.charAt(j));
			String strCipher = Integer.toHexString(cipher[i]);
			if (strCipher.length() == 1) {
				encryption += "0" + strCipher;
			}
			else {
				encryption += strCipher;
			}
		}
		return encryption;
	}
  

	public static String decryptXor(String encryption, String secretKey) {
		try {
			secretKey = new String(secretKey.getBytes("UTF-8"), "iso-8859-1");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		char[] decryption = new char[encryption.length() / 2];
		for (int i = 0, j = 0; i < encryption.length() / 2; i++, j++) {
			if (j == secretKey.length()){
				j = 0;
			}
			char n = (char) (int) Integer.valueOf(encryption.substring(i * 2, i * 2 + 2), 16);
			decryption[i] = (char) (n ^ secretKey.charAt(j));
		}
		String decoding = "";
		try {
			decoding = new String(String.valueOf(decryption).getBytes("iso-8859-1"), "UTF-8");
		} catch (Exception e) {
		}
		return decoding;
	}


	//des 加密
	public static byte[] encryptDES(String encryptString, String encryptKey) throws Exception {
		if(encryptString == null || encryptKey == null || encryptKey.length() != 8){
			throw  new RuntimeException("illegal argument");
		}
		IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
		return encryptedData;
	}

	//des 解密
	public static String decryptDES(byte[] encryptedData, String decryptKey) throws Exception {
		if(encryptedData == null || encryptedData.length <=0 || decryptKey == null || decryptKey.length() != 8){
			throw  new RuntimeException("illegal argument");
		}
		IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(encryptedData);
		return new String(decryptedData);
	}

	public static String uuid(){
		
		
		 return UUID.randomUUID().toString();
	}
	
	
	
	
	
}
