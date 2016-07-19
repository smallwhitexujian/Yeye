package com.angelatech.yeyelive.qqapi;

public class QQModel {
    public String ret;
    public String openid;
    public String access_token;
    public String pay_token;
    public String expires_in;
    public String pf;
    public String pfkey;
    public String msg;
    public String login_cost;
    public String query_authority_cost;
    public String authority_cost;

    @Override
    public String toString() {
        return "QQModel{" +
                "ret='" + ret + '\'' +
                ", openid='" + openid + '\'' +
                ", access_token='" + access_token + '\'' +
                ", pay_token='" + pay_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", pf='" + pf + '\'' +
                ", pfkey='" + pfkey + '\'' +
                ", msg='" + msg + '\'' +
                ", login_cost='" + login_cost + '\'' +
                ", query_authority_cost='" + query_authority_cost + '\'' +
                ", authority_cost='" + authority_cost + '\'' +
                '}';
    }
}