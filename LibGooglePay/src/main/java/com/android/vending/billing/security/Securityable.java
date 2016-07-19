package com.android.vending.billing.security;

public interface Securityable {

	public String getOrderSign(String srcStr, String key);
	public String getCheckSign(String srcStr, String extraStr, String key);

	public String encryption(String srcStr, String key);
	public String decryption(String encryStr, String key);



}
