package com.angelatech.yeyelive.util.pay;

import com.will.common.string.security.Base64;
import com.will.common.string.security.EncrypDES;
import com.will.common.string.security.Md5;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SecurityUtil {

	public static String getOrderSign(String srcStr, String key) {
		String base64Str = Base64.encode(srcStr.getBytes());
		String md5Str = Md5.md5(base64Str);
		String sign = Md5.md5(md5Str+key);
		//DebugLogs.e("jjfly str:" + srcStr + "  base64:" + base64Str + " sign:" + sign);
		return sign;
	}

	public String getCheckSign(String srcStr,String extraKey, String key) {
		String md5Str = Md5.md5(srcStr+extraKey);
		String base64Str = Base64.encode(md5Str.getBytes());
		String sign = Md5.md5(base64Str+key);
		return sign;
	}

	public String encryption(String srcStr, String key) {
		String encryStr = null;
		return encryStr;
	}

	public String decryption(String encryStr, String key) {
		String decryStr = null;
		try {
			EncrypDES encrypDES = EncrypDES.getInstance();
			byte[] decryStrByte = encrypDES.Decryptor(encryStr.getBytes());
			decryStr = new String(decryStrByte);
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return decryStr;
	}

}
