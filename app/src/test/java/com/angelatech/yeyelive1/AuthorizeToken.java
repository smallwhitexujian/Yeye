package com.angelatech.yeyelive1;

public class AuthorizeToken {

	/**
     * token_type : Bearer
     * expires_in : 3600
     * refresh_token :
     */

    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }
	
	
}