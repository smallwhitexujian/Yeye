package com.angelatech.yeyelive1;

public class PurchaseEntity {

	private String kind;
	private long purchaseTimeMillis;
	private int purchaseState;// (purchased:0 cancelled：1，我们就是依靠这个判断购买信息)
	private int consumptionState;
	private String developerPayload;
	
	
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public long getPurchaseTimeMillis() {
		return purchaseTimeMillis;
	}
	public void setPurchaseTimeMillis(long purchaseTimeMillis) {
		this.purchaseTimeMillis = purchaseTimeMillis;
	}
	public int getPurchaseState() {
		return purchaseState;
	}
	public void setPurchaseState(int purchaseState) {
		this.purchaseState = purchaseState;
	}
	public int getConsumptionState() {
		return consumptionState;
	}
	public void setConsumptionState(int consumptionState) {
		this.consumptionState = consumptionState;
	}
	public String getDeveloperPayload() {
		return developerPayload;
	}
	public void setDeveloperPayload(String developerPayload) {
		this.developerPayload = developerPayload;
	}
	
	
	

}
